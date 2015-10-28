/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gateway;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.*;
import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;

import java.io.IOException;

/**
 *
 * @author huberto
 */
public class Gateway implements Runnable{

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread serverThread = new Thread(new Gateway());
        serverThread.setDaemon(false);
        serverThread.start();
    }

    public void run() {
        try {

             final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    createDevice()
            );

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
    // Metodo para criação do dispositivo
    LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {
        
        //Cria uma identidade para o dispositivo
        DeviceIdentity identity = new DeviceIdentity(
                        // Identificação unica para o dispositivo
                        UDN.uniqueSystemIdentifier("LupsGateway")
                    );
        
        // Cria um tipo de dispositivo/versão
        DeviceType type = new UDADeviceType("Gateway", 1);
        
        //Adiciona detalhes ao dispositivo
        DeviceDetails details =
                new DeviceDetails(
                        //nome amigável
                        "Gateway LUPS",
                        //fabricante
                        new ManufacturerDetails("LUPS"),
                        //detalhes do modelo [nome, descrição, versão
                        new ModelDetails(
                                "Gateway LUPS",
                                "Um gateway do laboratório de pesquisa LUPS",
                                "v1"
                        )
                );
        
        //incorpora uma imagem ao dispositivo, png 48x48
//        Icon icon =
//                new Icon(
//                        "image/ico", 48, 48, 8,
//                        getClass().getResource("X.ico")
//                );
        //serviços(Nodos) do servidor de borda
        
        LocalService<NodoTemp> NodoServiceTemp = new AnnotationLocalServiceBinder().read(NodoTemp.class);
        NodoServiceTemp.setManager( new DefaultServiceManager(NodoServiceTemp, NodoTemp.class) );
        
        //LocalService<NodoHumi> NodoServiceHumi = new AnnotationLocalServiceBinder().read(NodoHumi.class);
        //NodoServiceHumi.setManager( new DefaultServiceManager(NodoServiceHumi, NodoHumi.class) );        
        
        //envia o novo dispositivo para ser de fato criado e cadastrado
        //return new LocalDevice(identity, type, details, new LocalService[] {NodoServiceTemp, NodoServiceHumi});

        /* 
        Varios serviços podem ser vinculados ao mesmo dispositivo:
        return new LocalDevice(identity, type, details, icon, new LocalService[] {switchPowerService, myOtherService});
        
        Ou apenas um:
                 */
        return new LocalDevice(identity, type, details, NodoServiceTemp);
       
    }
    
}
