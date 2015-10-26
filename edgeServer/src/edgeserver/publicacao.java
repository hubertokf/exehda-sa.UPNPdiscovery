/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author huberto
 */
public class publicacao implements Runnable {
    private ArrayList<Gateway> gatewaysCadastrados = new ArrayList<>();
    private final int ServidorBordaID;
    private final String urlLogin;
    private final String urlInsertDado;

    publicacao(ArrayList<Gateway> gatewaysCadastrados, int ServidorBordaID, String urlLogin, String urlInsertDado) {
        this.gatewaysCadastrados = gatewaysCadastrados;
        this.ServidorBordaID = ServidorBordaID;
        this.urlLogin = urlLogin;
        this.urlInsertDado = urlInsertDado;
    }

    @Override
    public void run() {
        synchronized (gatewaysCadastrados) {
            System.out.println("Total de "+Integer.toString(gatewaysCadastrados.size())+" gateways a serem publicados.");
            gatewaysCadastrados.stream().forEach((gateway) -> {
                gateway.getSensores().stream().forEach((sensor) -> {
                    try {
                        publicaDado(sensor);
                    } catch (Exception ex) {
                        Logger.getLogger(publicacao.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            });
        }
    }
    
    private void publicaDado(Sensor sensor) throws Exception{
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        HTTPClient http = new HTTPClient();
        
        List<NameValuePair> postp = new ArrayList<>();
        postp.add(new BasicNameValuePair("login", "huberto"));
        postp.add(new BasicNameValuePair("password", "99766330"));
        
        http.sendPost(this.urlLogin, postp);
        java.util.Date date= new java.util.Date();
        
        sensor.updateDado();
        
        List<NameValuePair> GatewayParams = new ArrayList<>();
        GatewayParams.add(new BasicNameValuePair("publicacao_servidorborda", Integer.toString(this.ServidorBordaID)));
        GatewayParams.add(new BasicNameValuePair("publicacao_sensor", Integer.toString(sensor.getId())));
        GatewayParams.add(new BasicNameValuePair("publicacao_datacoleta", new Timestamp(date.getTime()).toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_datapublicacao", new Timestamp(date.getTime()).toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_valorcoletado", Float.toString(sensor.getDado())));
        
//        GatewayParams.add(new BasicNameValuePair("publicacao_servidorborda", "9"));
//        GatewayParams.add(new BasicNameValuePair("publicacao_sensor", "13"));
//        GatewayParams.add(new BasicNameValuePair("publicacao_datacoleta", new Timestamp(date.getTime()).toString()));
//        GatewayParams.add(new BasicNameValuePair("publicacao_datapublicacao", new Timestamp(date.getTime()).toString()));
//        GatewayParams.add(new BasicNameValuePair("publicacao_valorcoletado", "0"));


        String result = http.GetPageContent(this.urlInsertDado, GatewayParams);
    }
    
}
