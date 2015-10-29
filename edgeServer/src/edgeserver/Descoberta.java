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
import java.util.stream.Collectors;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

/**
 *
 * @author huberto
 */
public class Descoberta implements Runnable {
    private final int ServidorBordaID;
    private final String urlLogin;
    private final String insertSensorURI;
    private final String insertGatewayURI;
    private final String toggleGateway;
    private ArrayList<Gateway> gatewaysCadastrados = new ArrayList<>();
    

    public Descoberta(int ServidorBordaID, String urlLogin, String insertSensorURI, String insertGatewayURI, ArrayList<Gateway> gatewaysCadastrados, String toggleGateway) {
        this.ServidorBordaID = ServidorBordaID;
        this.urlLogin = urlLogin;
        this.insertSensorURI = insertSensorURI;
        this.insertGatewayURI = insertGatewayURI;
        this.gatewaysCadastrados = gatewaysCadastrados;
        this.toggleGateway = toggleGateway;
    }
    
    @Override
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
                }else if("countCadGateways".equals(command)){
                    synchronized (gatewaysCadastrados) {
                        System.out.println(gatewaysCadastrados.size());
                    }
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
            DeviceType nativeType = new UDADeviceType("GatewayNativo");
            DeviceType virtualType = new UDADeviceType("GatewayVirtual");
            DeviceType proprietaryType = new UDADeviceType("GatewayProprietario");
            
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
                
                if (device.getType().equals(nativeType) == true || device.getType().equals(virtualType) == true || device.getType().equals(proprietaryType) == true){
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
                    System.out.println("Publicando no Servidor de Contexto:");
                    Gateway gateway = new Gateway(upnpService, device);
                    
                    //System.out.println(gateway.getUid());
                    
                    try {
                        publicaGateway(gateway);
                        for(Sensor sensor : gateway.getSensores()){
                            publicaSensor(gateway, sensor);
                        }
                        
                        synchronized (gatewaysCadastrados) {
                            gatewaysCadastrados.add(gateway);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EdgeServer.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("DEU PAU!");
                    }
                    
                    System.out.println("------------------------------------------------------------------");
                    
                    
                }
                
                    
                
                    //AÇÃO A SER EXECUTADA QUANDO ENCONTRADO DISPOSITIVO
                    
                    //ADICIONAR O DISPOSITIVO EM ALGUM LUGAR PRA MONITORAMENTO
                    //executeAction(upnpService, switchPower);

                //}

            }

            //SEMPRE QUE UM DISPOSITIVO FOR REMOVIDO EXECUTA ISSO
            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                String deviceUID = device.getIdentity().getUdn().toString();
                if (device.getType().equals(nativeType) == true || device.getType().equals(virtualType) == true || device.getType().equals(proprietaryType) == true){
                    
                    synchronized (gatewaysCadastrados) {
                        List<Gateway> result;
                        result = gatewaysCadastrados.stream()
                                .filter(gateway -> gateway.getUid().equals(deviceUID))
                                .collect(Collectors.toList());
                    
                        result.stream().forEach((gateway) -> {
                            try {
                                System.out.println("------------------------------------------------------------------");
                                System.out.println("                        GATEWAY REMOVIDO:                         ");
                                System.out.println("------------------------------------------------------------------");
                                System.out.println(" Nome: "+device.getDetails().getFriendlyName());
                                System.out.println(" UID (unique ID): "+device.getIdentity().getUdn());
                                System.out.println("------------------------------------------------------------------");
                                System.out.println(" Desativando gateway no Servidor de Contexto:");
                                toggleGateway(gateway, "deactivate");
                                synchronized (gatewaysCadastrados) {
                                    gatewaysCadastrados.remove(gateway);
                                }
                                System.out.println("------------------------------------------------------------------");

                            } catch (Exception ex) {
                                Logger.getLogger(Descoberta.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }                    
                }
            }
        };
    }
    
    private void toggleGateway(Gateway gateway, String job) throws Exception{
        CookieHandler.setDefault(new CookieManager());

        HTTPClient http = new HTTPClient();
        
        List<NameValuePair> postp = new ArrayList<>();
        postp.add(new BasicNameValuePair("login", "huberto"));
        postp.add(new BasicNameValuePair("password", "99766330"));

        http.sendPost(this.urlLogin, postp);
        
        List<NameValuePair> GatewayParams = new ArrayList<>();
        GatewayParams.add(new BasicNameValuePair("gateway_id", Integer.toString(gateway.getId())));
        GatewayParams.add(new BasicNameValuePair("job", job));

        String result = http.GetPageContent(this.toggleGateway, GatewayParams);

        if (null != result)switch (result) {
            case "desativado":
                System.out.println("-> Gateway "+gateway.getNome()+"("+gateway.getId()+") DESATIVADO no Servidor de Contexto");
                break;        
            case "ativado":
                System.out.println("-> Gateway "+gateway.getNome()+"("+gateway.getId()+") ATIVADO no Servidor de Contexto");
                break;
        }
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
        //System.out.println(result);
        
        String publicType = result.split(":")[0];
        String gatewayID = result.split(":")[1];

        gateway.setId(Integer.parseInt(gatewayID));
        
        if (null != publicType)switch (publicType) {
            case "insert":
                System.out.println("-> Gateway "+gateway.getNome()+"("+gateway.getId()+") cadastrado no Servidor de Contexto com sucesso.");
                break;        
            case "update":
                System.out.println("-> Gateway "+gateway.getNome()+"("+gateway.getId()+") atualizado no Servidor de Contexto com sucesso.");
                toggleGateway(gateway, "activate");
                break;
        }
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
        //System.out.println(result);
        String publicType = result.split(":")[0];
        String sensorID = result.split(":")[1];
        
        sensor.setId(Integer.parseInt(sensorID));
        
        if (null != publicType)switch (publicType) {
            case "insert":
                System.out.println("-> Sensor "+sensor.getNome()+"("+sensor.getId()+") cadastrado no Servidor de Contexto com sucesso.");
                break;        
            case "update":
                System.out.println("-> Sensor "+sensor.getNome()+"("+sensor.getId()+") atualizado no Servidor de Contexto com sucesso.");
                break;
        }
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
