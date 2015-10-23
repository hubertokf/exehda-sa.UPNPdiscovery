/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author huberto
 */
public class EdgeServer{
    
    private static final int ServidorBordaID = 9;
    private static final String urlLogin = "http://localhost/exehdager-teste/index.php/ci_login/logar";
    private static final String insertSensorURI = "http://localhost/exehdager-teste/index.php/cadastros/ci_sensor/gravaSensor";
    private static final String insertGatewayURI = "http://localhost/exehdager-teste/index.php/cadastros/ci_gateway/gravaGateway";
    private static ArrayList<Gateway> gatewaysCadastrados = new ArrayList<>();
        
    public static void main(String[] args) {
        // Start a user thread that runs the UPnP stack
        Thread clientThread = new Thread(new Descoberta(ServidorBordaID, urlLogin,insertSensorURI, insertGatewayURI, gatewaysCadastrados));
        clientThread.setDaemon(false);
        clientThread.start();
        
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new publicacao(gatewaysCadastrados), 0, 5, TimeUnit.MINUTES);
    }

    
}
