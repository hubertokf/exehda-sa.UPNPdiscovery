/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.*;
import org.fourthline.cling.model.action.*;
import org.fourthline.cling.model.message.*;
import org.fourthline.cling.model.message.header.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.registry.*;

/**
 *
 * @author huberto
 */
public class EdgeServer  implements Runnable {
    
    private final int ServidorBordaID = 9;
    private final String urlLogin = "http://localhost/exehdager-teste/index.php/ci_login/logar";
    private final String insertSensorURI = "http://localhost/exehdager-teste/index.php/cadastros/ci_sensor/gravaSensor";
    private final String insertGatewayURI = "http://localhost/exehdager-teste/index.php/cadastros/ci_gateway/gravaGateway";
    private ArrayList<Gateway> gatewaysCadastrados = new ArrayList<>();
        
    public static void main(String[] args) {
        // Start a user thread that runs the UPnP stack
        Thread clientThread = new Thread(new EdgeServer());
        clientThread.setDaemon(false);
        clientThread.start();
    }

    public void run() {
        try {

            UpnpService upnpService = new UpnpServiceImpl();

            // Adiciona um verificador de novos registros de dispositivos UPNP / Add a listener for device registration events
            upnpService.getRegistry().addListener(
                    createRegistryListener(upnpService)
            );

            // Envia a mensagem de busca de novos dispositivos UPNP para todos os dispositivos da rede / Broadcast a search message for all devices
            // Acontece só uma vez
            upnpService.getControlPoint().search(
                    new STAllHeader()
            );
            
            Scanner scanner = new Scanner(System.in);
            String command = "";
            
            while (!"quit".equals(command)){
                command = scanner.next();
                
                // leitor de comandos do servidor de borda
                
                if ("getDevices".equals(command)){
                    Collection<Device> devices = upnpService.getRegistry().getDevices();
                    int devicessize = devices.size();
                    
                    System.out.printf("Esses são os dispositivos locais ( %d )\n", devicessize);
                    
                    System.out.println(Arrays.toString(upnpService.getRegistry().getDevices().toArray()));
                    
                    RemoteDevice newdevice;
                    
                    for(int i = 0 ; i < devices.size() ; i++){
                        newdevice = (RemoteDevice) devices.toArray()[i];
                        System.out.println(newdevice.getDetails().getFriendlyName());
                    }
                    
                }else if("exec".equals(command)){
                    /*Collection<Device> devices = upnpService.getRegistry().getDevices();
                    ServiceId serviceId = new UDAServiceId("NodoTemp");
                    RemoteDevice newdevice;
                    
                    for(int i = 0 ; i < devices.size() ; i++){
                        newdevice = (RemoteDevice) devices.toArray()[i];
                        
                        Service edgeServer;
                        if ((edgeServer = newdevice.findService(serviceId)) != null) {

                            //AÇÃO A SER EXECUTADA QUANDO ENCONTRADO DISPOSITIVO

                            //ADICIONAR O DISPOSITIVO EM ALGUM LUGAR PRA MONITORAMENTO
                            executeAction(upnpService, edgeServer);

                        }
                    }*/
                    
                }
            }
            
            System.exit(0);
            

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            System.exit(1);
        }
    }
    
    RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener() {

            // CRIA UM NOVO TIPO DE SERVIÇO A SER BUSCADO
            ServiceId serviceId = new UDAServiceId("NodoTemp");
            DeviceType type = new UDADeviceType("Gateway");
            
            //SEMPRE QUE UM DISPOSITIVO FOR ENCONTRADO E ADICIONADO EXECUTA ISSO
            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
                String nome = device.getDetails().getFriendlyName();
                String uid = device.getIdentity().getUdn().toString();
                String tipo = device.getType().getType();
                String Fabricante = device.getDetails().getManufacturerDetails().getManufacturer();
                String Detalhes = device.getDetails().getModelDetails().getModelDescription();
                // SE ESSE DISPOSITIVO TIVER UM SERVIÇO ESPECÍFICO
//                Service NodoTemp;
//                if ((NodoTemp = device.findService(serviceId)) != null) {
//
//                    System.out.println("Service discovered: " + NodoTemp);
                
                if (device.getType().equals(type) == true){
                    System.out.println("------------------------------------------------------------------");
                    System.out.println("                       GATEWAY DESCOBERTO:                        ");
                    System.out.println("------------------------------------------------------------------");
                    System.out.println(" Nome: "+device.getDetails().getFriendlyName());
                    System.out.println(" UID (unique ID): "+device.getIdentity().getUdn());
                    System.out.println(" Tipo: "+device.getType().getType());
                    System.out.println(" Fabricante: "+device.getDetails().getManufacturerDetails().getManufacturer());
                    System.out.println(" Detalhes: "+device.getDetails().getModelDetails().getModelDescription());
                    System.out.println(" Sensores (serviços): ");
                    for (RemoteService service : device.findServices()) {
                        System.out.println("       * "+service.getServiceId().getId());
                    }
                    System.out.println("------------------------------------------------------------------");
                    
                    Gateway gateway = new Gateway(upnpService, device);
                    
                    //System.out.println(gateway.getUid());
                    
                    try {
                        publicaGateway(gateway);
                        
                        for(Sensor sensor : gateway.getSensores()){
                            publicaSensor(gateway, sensor);
                        }
                        
                        gatewaysCadastrados.add(gateway);
                    } catch (Exception ex) {
                        Logger.getLogger(EdgeServer.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("DEU PAU!");
                    }
                    
                    
                }
                
                    
                
                    //AÇÃO A SER EXECUTADA QUANDO ENCONTRADO DISPOSITIVO
                    
                    //ADICIONAR O DISPOSITIVO EM ALGUM LUGAR PRA MONITORAMENTO
                    //executeAction(upnpService, switchPower);

                //}

            }

            //SEMPRE QUE UM DISPOSITIVO FOR REMOVIDO EXECUTA ISSO
            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                if ((device.getType()) == type){
                    System.out.println("Dispositivo Descoberto: ");
                }
            }

        };
    }
    
    private void publicaGateway(Gateway gateway) throws Exception{
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        HTTPClient http = new HTTPClient();

        List<NameValuePair> postp = new ArrayList<>();
        postp.add(new BasicNameValuePair("login", "huberto"));
        postp.add(new BasicNameValuePair("password", "99766330"));

        http.sendPost(this.urlLogin, postp);
        
        List<NameValuePair> GatewayParams = new ArrayList<>();
        GatewayParams.add(new BasicNameValuePair("gateway_nome", gateway.getNome()));
        GatewayParams.add(new BasicNameValuePair("gateway_servidorborda", Integer.toString(this.ServidorBordaID)));
        GatewayParams.add(new BasicNameValuePair("gateway_uid", (String)gateway.getUid()));

        String result = http.GetPageContent(this.insertGatewayURI, GatewayParams);
        
        gateway.setId(Integer.parseInt(result));
    }
    
    private void publicaSensor(Gateway gateway, Sensor sensor) throws Exception{
        
        
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        HTTPClient http = new HTTPClient();

        List<NameValuePair> postp = new ArrayList<>();
        postp.add(new BasicNameValuePair("login", "huberto"));
        postp.add(new BasicNameValuePair("password", "99766330"));

        http.sendPost(this.urlLogin, postp);
        
        List<NameValuePair> SensorParams = new ArrayList<>();
        SensorParams.add(new BasicNameValuePair("sensor_nome", sensor.getNome()));
        SensorParams.add(new BasicNameValuePair("sensor_desc", sensor.getDescricao()));
        SensorParams.add(new BasicNameValuePair("sensor_modelo", sensor.getModelo()));
        SensorParams.add(new BasicNameValuePair("sensor_precisao", sensor.getPrecisao()));
        SensorParams.add(new BasicNameValuePair("sensor_tipo", sensor.getTipo()));
        SensorParams.add(new BasicNameValuePair("sensor_servidorborda", Integer.toString(this.ServidorBordaID)));
        SensorParams.add(new BasicNameValuePair("sensor_gateway", Integer.toString(gateway.getId())));

        String result = http.GetPageContent(this.insertSensorURI, SensorParams);
        
        sensor.setId(Integer.parseInt(result));
    }
    
    void executeAction(UpnpService upnpService, Service NodoTemp) {
        
        Action getStatusAction = NodoTemp.getAction("GetStatus");
        ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);

        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                ActionArgumentValue status  = invocation.getOutput("ResultStatus");

                assert status != null;

                System.out.println(status.toString());
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                System.err.println(defaultMsg);
            }
        };

        upnpService.getControlPoint().execute(getStatusCallback);

    }
}
