/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
    private Date datapublicacao;
    private ArrayList<Gateway> gatewaysCadastrados = new ArrayList<>();
    private ArrayList<Publicacao> filaPublicacoes = new ArrayList<>();

    Publicador(ArrayList<Gateway> gatewaysCadastrados, EdgeServer edgeServer) {
        this.gatewaysCadastrados = gatewaysCadastrados;
        this.ServidorBordaID = edgeServer.getServidorBordaID();
        this.urlLogin = edgeServer.getUrlLogin();
        this.urlInsertDado = edgeServer.getUrlInsertDado();
    }

    @Override
    public void run() {
        System.out.println("------------------------------------------------------------------");
        System.out.println("Inicializando Publicador.");
        System.out.print("Verificando contexão com o Servidor de Contexto: ");
        try {
            this.testServer();
            System.out.println("OK");
            
            this.filaPublicacoes = this.obtemFila();
            
            
            if (!this.filaPublicacoes.isEmpty()){
                System.out.println("Publicando fila de publicações: ");
                this.filaPublicacoes = this.publicaFila();
            }            
            
            synchronized (gatewaysCadastrados) {
                if(!gatewaysCadastrados.isEmpty()){
                    System.out.println("Efetuando novas publicações: ");

                    this.datapublicacao = new Date();
                    gatewaysCadastrados.stream().forEach((gateway) -> {
                        gateway.getSensores().stream().forEach((sensor) -> {
                            try {
                                System.out.print("-> Publicando sensor "+sensor.getNome()+": ");
                                publicaDado(sensor);
                                System.out.println("OK");
                            } catch (Exception ex) {
                                System.out.println("Fail");
                                System.out.print("Armazenando dado para publicação futura: ");
                                this.filaPublicacoes.add(new Publicacao(this.ServidorBordaID, sensor.getId(), new Timestamp(this.datapublicacao.getTime()), sensor.getDado()));
                                System.out.println("OK");
                            }
                        });
                    });
                }else
                    System.out.println("Nenhuma publicação a ser realizada.");
            }
        } catch (Exception ex) {
            System.out.println("Fail");
            synchronized(gatewaysCadastrados){
                gatewaysCadastrados.stream().forEach((gateway) -> {
                    gateway.getSensores().stream().forEach((sensor) -> {
                            System.out.print("Armazenando dados para publicações futuras: ");
                            this.filaPublicacoes.add(new Publicacao(this.ServidorBordaID, sensor.getId(), new Timestamp(this.datapublicacao.getTime()), sensor.getDado()));
                            System.out.println("OK");
                    });
                });
            }
        }     
                        //this.writeObjectsToFile(filaPublicacoes);

        
        try {
            this.armazenaFila(filaPublicacoes);
        } catch (IOException ex) {
            Logger.getLogger(Publicador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Finalizando Publicador.");
        System.out.println("------------------------------------------------------------------");
    }
    
    private String testServer() throws Exception{
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        HTTPClient http = new HTTPClient();
        
        List<NameValuePair> GatewayParams = new ArrayList<>();

        String result = http.GetPageContent(this.urlInsertDado, GatewayParams);
        
        return result;
    }
    
    private ArrayList publicaFila(){
        ArrayList<Publicacao> publicacoes = new ArrayList<>();
        filaPublicacoes.stream().forEach((publicacao) -> {
            try {
                System.out.print("-> Publicando sensor "+publicacao.getSensor()+" coletado em "+publicacao.getDatacoleta()+": ");
                publicacao.publica(this.urlLogin, this.urlInsertDado);
                System.out.println("OK");

            } catch (Exception ex) {
                System.out.println("Fail");
                publicacoes.add(publicacao);
                System.out.println("   '-> Publicação armazenada para a próxima publicação");
            }
        });
        
        return publicacoes;
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
        GatewayParams.add(new BasicNameValuePair("publicacao_datacoleta", new Timestamp(this.datapublicacao.getTime()).toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_datapublicacao", new Timestamp(this.datapublicacao.getTime()).toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_valorcoletado", Float.toString(sensor.getDado())));

        String result = http.GetPageContent(this.urlInsertDado, GatewayParams);
    }
    
    private static void armazenaFila(ArrayList filaPublicacoes) throws IOException{
        new PrintWriter("fila.txt").close();
        filaPublicacoes.stream().forEach((publicacao) -> {
            BufferedWriter bw = null;
            try {
                // APPEND MODE SET HERE
                bw = new BufferedWriter(new FileWriter("fila.txt", true));
                bw.write(publicacao.toString());
                bw.newLine();
                bw.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {                       // always close the file
                if (bw != null) try {
                    bw.close();
                } catch (IOException ioe2) {
                    // just ignore it
                }
            }
        });
        
    }

    private static ArrayList obtemFila() throws IOException, ClassNotFoundException{
        ArrayList<Publicacao> fila = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("fila.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("::");
                fila.add(new Publicacao(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Timestamp.valueOf(parts[2]), Float.parseFloat(parts[3])));
            }
        }
        
        return fila;
    }
    
}
