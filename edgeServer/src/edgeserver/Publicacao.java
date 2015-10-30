/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.io.Serializable;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author huberto
 */
public class Publicacao implements Serializable{
    private Integer servidorborda;
    private Integer sensor;
    private Timestamp datacoleta;
    private Float valorcoletado;

    public Publicacao(Integer servidorborda, Integer sensor, Timestamp datacoleta, Float valorcoletado) {
        this.servidorborda = servidorborda;
        this.sensor = sensor;
        this.datacoleta = datacoleta;
        this.valorcoletado = valorcoletado;
    }
    
    public void publica(String urlLogin, String urlInsertDado) throws Exception{
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());
        
        Date datapublicacao = new Date();

        HTTPClient http = new HTTPClient();
        
        List<NameValuePair> postp = new ArrayList<>();
        postp.add(new BasicNameValuePair("login", "huberto"));
        postp.add(new BasicNameValuePair("password", "99766330"));
        
        http.sendPost(urlLogin, postp);
        
        List<NameValuePair> GatewayParams = new ArrayList<>();
        GatewayParams.add(new BasicNameValuePair("publicacao_servidorborda", Integer.toString(this.servidorborda)));
        GatewayParams.add(new BasicNameValuePair("publicacao_sensor", Integer.toString(this.sensor)));
        GatewayParams.add(new BasicNameValuePair("publicacao_datacoleta", this.datacoleta.toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_datapublicacao", new Timestamp(datapublicacao.getTime()).toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_valorcoletado", Float.toString(this.valorcoletado)));
        
        String result = http.GetPageContent(urlInsertDado, GatewayParams);
    }

    public Integer getServidorborda() {
        return servidorborda;
    }

    public void setServidorborda(Integer servidorborda) {
        this.servidorborda = servidorborda;
    }

    public Integer getSensor() {
        return sensor;
    }

    public void setSensor(Integer sensor) {
        this.sensor = sensor;
    }

    public Timestamp getDatacoleta() {
        return datacoleta;
    }

    public void setDatacoleta(Timestamp datacoleta) {
        this.datacoleta = datacoleta;
    }

    public Float getValorcoletado() {
        return valorcoletado;
    }

    public void setValorcoletado(Float valorcoletado) {
        this.valorcoletado = valorcoletado;
    }
    
    public String toString(){
        String string = null;
        
        string = this.servidorborda+"::"+this.sensor+"::"+this.datacoleta+"::"+this.valorcoletado;
        
        return string;
    }
}
