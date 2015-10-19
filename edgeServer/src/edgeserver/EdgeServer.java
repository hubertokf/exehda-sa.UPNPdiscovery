/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
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
                    Collection<Device> devices = upnpService.getRegistry().getDevices();
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
                    
                    Gateway teste = new Gateway(upnpService, device);
                    System.out.println(teste.getNome());
                    
                    
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
