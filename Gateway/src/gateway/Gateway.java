/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gateway;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.*;
import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author huberto
 */
public class Gateway implements Runnable{
    private String Name;
    private String Type;
    private String FriendlyName;
    private String ManufacturerDetails;
    private String Description;
    private String Version;
    
    private static Properties prop = new Properties();
    private static InputStream input = null;

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread serverThread = new Thread(new Gateway());
        serverThread.setDaemon(false);
        serverThread.start();
    }

    public void run() {
        System.out.println("Lendo arquivo de configuração.");
        readConfig();
        
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
                        UDN.uniqueSystemIdentifier(this.Name)
                    );
        
        // Cria um tipo de dispositivo/versão
        DeviceType type = new UDADeviceType(this.Type, Integer.parseInt(this.Version));
        
        //Adiciona detalhes ao dispositivo
        DeviceDetails details =
                new DeviceDetails(
                        //nome amigável
                        this.FriendlyName,
                        //fabricante
                        new ManufacturerDetails(this.ManufacturerDetails),
                        //detalhes do modelo [nome, descrição, versão
                        new ModelDetails(
                                this.FriendlyName,
                                this.Description,
                                this.Version
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
    
    private void readConfig(){
        try {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);
            // get the property value and print it out
            this.setName(prop.getProperty("nome"));
            this.setType(prop.getProperty("tipo"));
            this.setFriendlyName(prop.getProperty("nomeAmigavel"));
            this.setManufacturerDetails(prop.getProperty("fabricanteDetalhes"));
            this.setDescription(prop.getProperty("descricao"));
            this.setVersion(prop.getProperty("versao"));

	} catch (IOException ex) {
            //ex.printStackTrace();
            System.out.println("Arquivo de configuração não encontrado.");
            System.out.println("Criando arquivo de configuração padrão..");
            this.createDefaultConfigFile();
            this.readConfig();
	} finally {
            if (input != null) {
                try {
                    System.out.println("Arquivo de configuração lido com sucesso.");
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
    }
    
    private void createDefaultConfigFile(){
        OutputStream output = null;
        
	try {
            output = new FileOutputStream("config.properties");

            // set the properties value
            prop.setProperty("nome", "LupsNativeGateway");
            prop.setProperty("tipo", "GatewayNativo");
            prop.setProperty("nomeAmigavel", "Gateway Nativo LUPS");
            prop.setProperty("fabricanteDetalhes", "LUPS");
            prop.setProperty("descricao", "Um gateway do laboratório de pesquisa LUPS");
            prop.setProperty("versao", "1");

            // save properties to project root folder
            prop.store(output, null);

	} catch (IOException io) {
            io.printStackTrace();
	} finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
	}
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getFriendlyName() {
        return FriendlyName;
    }

    public void setFriendlyName(String FriendlyName) {
        this.FriendlyName = FriendlyName;
    }

    public String getManufacturerDetails() {
        return ManufacturerDetails;
    }

    public void setManufacturerDetails(String ManufacturerDetails) {
        this.ManufacturerDetails = ManufacturerDetails;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }
    
    
}
