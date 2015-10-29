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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author huberto
 */
public class Publicacao {
    private Integer servidorborda;
    private Integer sensor;
    private Timestamp datacoleta;
    private Timestamp datapublicacao;
    private Float valorcoletado;

    public Publicacao(Integer servidorborda, Integer sensor, Timestamp datacoleta, Timestamp datapublicacao, Float valorcoletado) {
        this.servidorborda = servidorborda;
        this.sensor = sensor;
        this.datacoleta = datacoleta;
        this.datapublicacao = datapublicacao;
        this.valorcoletado = valorcoletado;
    }
    
    public void publica(String urlLogin, String urlInsertDado) throws Exception{
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        HTTPClient http = new HTTPClient();
        
        List<NameValuePair> postp = new ArrayList<>();
        postp.add(new BasicNameValuePair("login", "huberto"));
        postp.add(new BasicNameValuePair("password", "99766330"));
        
        http.sendPost(urlLogin, postp);
        
        List<NameValuePair> GatewayParams = new ArrayList<>();
        GatewayParams.add(new BasicNameValuePair("publicacao_servidorborda", Integer.toString(this.servidorborda)));
        GatewayParams.add(new BasicNameValuePair("publicacao_sensor", Integer.toString(this.sensor)));
        GatewayParams.add(new BasicNameValuePair("publicacao_datacoleta", this.datacoleta.toString()));
        GatewayParams.add(new BasicNameValuePair("publicacao_datapublicacao", this.datapublicacao.toString()));
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

    public Timestamp getDatapublicacao() {
        return datapublicacao;
    }

    public void setDatapublicacao(Timestamp datapublicacao) {
        this.datapublicacao = datapublicacao;
    }

    public Float getValorcoletado() {
        return valorcoletado;
    }

    public void setValorcoletado(Float valorcoletado) {
        this.valorcoletado = valorcoletado;
    }
    
    
}
