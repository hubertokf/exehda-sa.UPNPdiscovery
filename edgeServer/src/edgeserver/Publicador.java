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
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author huberto
 */
public class Publicador implements Runnable {
    private final int ServidorBordaID;
    private final String urlLogin;
    private final String urlInsertDado;
    private final Date date;
    private ArrayList<Gateway> gatewaysCadastrados = new ArrayList<>();
    private ArrayList<Publicacao> filaPublicacoes = new ArrayList<>();

    Publicador(ArrayList<Publicacao> filaPublicacoes, ArrayList<Gateway> gatewaysCadastrados, EdgeServer edgeServer) {
        this.filaPublicacoes = filaPublicacoes;
        this.gatewaysCadastrados = gatewaysCadastrados;
        this.ServidorBordaID = edgeServer.getServidorBordaID();
        this.urlLogin = edgeServer.getUrlLogin();
        this.urlInsertDado = edgeServer.getUrlInsertDado();
        this.date = new Date();
    }

    @Override
    public void run() {
        System.out.println("Inicializando Publicador.");
        System.out.println("Verificando contexão com o Servidor de Contexto: ");
        try {
            this.testServer();
        } catch (Exception ex) {
            System.out.print("Fail");
            System.out.println("Conexão com o servidor de Contexto não estabelecida. Armazenando publicação.");
            
            
            Logger.getLogger(Publicador.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.print("OK");
            
            synchronized(filaPublicacoes){
                if (!filaPublicacoes.isEmpty()){
                    this.publicaFila(filaPublicacoes);
                }
            }
            System.out.println("Publicando fila de publicações:");
        }
        
        
        synchronized (gatewaysCadastrados) {
            System.out.println("Total de "+Integer.toString(gatewaysCadastrados.size())+" gateways a serem publicados.");
            gatewaysCadastrados.stream().forEach((gateway) -> {
                gateway.getSensores().stream().forEach((sensor) -> {
                    try {
                        publicaDado(sensor);
                    } catch (Exception ex) {
                        Logger.getLogger(Publicador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            });
        }
    }
    
    private String testServer() throws Exception{
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        HTTPClient http = new HTTPClient();
        
        List<NameValuePair> GatewayParams = new ArrayList<>();

        String result = http.GetPageContent(this.urlInsertDado, GatewayParams);
        
        return result;
    }
    
    private void publicaFila(ArrayList<Publicacao> filaPublicacoes){
        filaPublicacoes.stream().forEach((publicacao) -> {
            try {
                // PAREI AQUI!!
                publicacao.publica(this.urlLogin, this.urlInsertDado);
            } catch (Exception ex) {
                Logger.getLogger(Publicador.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private void publicaDado(Sensor sensor) throws Exception{
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        HTTPClient http = new HTTPClient();
        
        List<NameValuePair> postp = new ArrayList<>();
        postp.add(new BasicNameValuePair("login", "huberto"));
        postp.add(new BasicNameValuePair("password", "99766330"));
        
        http.sendPost(this.urlLogin, postp);
        
        sensor.updateDado();
        
        List<NameValuePair> GatewayParams = new ArrayList<>();
        GatewayParams.add(new BasicNameValuePair("publicacao_servidorborda", Integer.toString(this.ServidorBordaID)));
        GatewayParams.add(new BasicNameValuePair("publicacao_sensor", Integer.toString(sensor.getId())));
        GatewayParams.add(new BasicNameValuePair("publicacao_datacoleta", new Timestamp(date.getTime()).toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_datapublicacao", new Timestamp(date.getTime()).toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_valorcoletado", Float.toString(sensor.getDado())));

        String result = http.GetPageContent(this.urlInsertDado, GatewayParams);
    }
    
}
