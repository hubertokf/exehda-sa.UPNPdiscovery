/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author huberto
 */
public class EdgeServer{    
    private int ServidorBordaID;
    private String urlLogin;
    private String insertSensorURI;
    private String insertGatewayURI;
    private String urlInsertDado;
    private String toggleGateway;
    private int tempoPublicacao;
    
    private final Properties prop = new Properties();
    private InputStream input = null;
    
    private static ArrayList<Gateway> gatewaysCadastrados = new ArrayList<>();

    public EdgeServer() {
        
        System.out.println("Lendo arquivo de configuração.");
        readConfig();
        
        this.urlLogin = this.prop.getProperty("urlLogin");
        this.insertSensorURI = this.prop.getProperty("insertSensorURI");
        this.insertGatewayURI = this.prop.getProperty("insertGatewayURI");
        this.urlInsertDado = this.prop.getProperty("urlInsertDado");
        this.toggleGateway = this.prop.getProperty("toggleGateway");
        this.ServidorBordaID = Integer.parseInt(this.prop.getProperty("ServidorBordaID"));
        this.tempoPublicacao = Integer.parseInt(this.prop.getProperty("tempoPublicacao"));
    }
        
    public static void main(String[] args){        
        
        EdgeServer edgeServer = new EdgeServer();
        
        // Start a user thread that runs the UPnP stack
        Thread clientThread = new Thread(new Descoberta(edgeServer.getServidorBordaID(), edgeServer.getUrlLogin(), edgeServer.getInsertSensorURI(), edgeServer.getInsertGatewayURI(), gatewaysCadastrados, edgeServer.getToggleGateway()));
        clientThread.setDaemon(false);
        clientThread.start();
        
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(
            new Publicador(gatewaysCadastrados, edgeServer),
            0,
            edgeServer.getTempoPublicacao(), 
            TimeUnit.MINUTES
        );
    }
    
    private void readConfig(){
        try {
            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            

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
            prop.setProperty("urlLogin", "http://localhost/exehdager-teste/index.php/ci_login/logar");
            prop.setProperty("insertSensorURI", "http://localhost/exehdager-teste/index.php/cadastros/ci_sensor/gravaSensor");
            prop.setProperty("insertGatewayURI", "http://localhost/exehdager-teste/index.php/cadastros/ci_gateway/gravaGateway");
            prop.setProperty("urlInsertDado", "http://localhost/exehdager-teste/index.php/cadastros/ci_publicacao/gravaPublicacao");
            prop.setProperty("toggleGateway", "http://localhost/exehdager-teste/index.php/cadastros/ci_gateway/toggleGateway");
            prop.setProperty("ServidorBordaID", "9");
            prop.setProperty("tempoPublicacao", "10");

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

    public int getServidorBordaID() {
        return ServidorBordaID;
    }

    public void setServidorBordaID(int ServidorBordaID) {
        this.ServidorBordaID = ServidorBordaID;
    }

    public String getUrlLogin() {
        return urlLogin;
    }

    public void setUrlLogin(String urlLogin) {
        this.urlLogin = urlLogin;
    }

    public String getInsertSensorURI() {
        return insertSensorURI;
    }

    public void setInsertSensorURI(String insertSensorURI) {
        this.insertSensorURI = insertSensorURI;
    }

    public String getInsertGatewayURI() {
        return insertGatewayURI;
    }

    public void setInsertGatewayURI(String insertGatewayURI) {
        this.insertGatewayURI = insertGatewayURI;
    }

    public String getUrlInsertDado() {
        return urlInsertDado;
    }

    public void setUrlInsertDado(String urlInsertDado) {
        this.urlInsertDado = urlInsertDado;
    }

    public String getToggleGateway() {
        return toggleGateway;
    }

    public void setToggleGateway(String toggleGateway) {
        this.toggleGateway = toggleGateway;
    }   

    public int getTempoPublicacao() {
        return tempoPublicacao;
    }

    public void setTempoPublicacao(int tempoPublicacao) {
        this.tempoPublicacao = tempoPublicacao;
    }
    
}
