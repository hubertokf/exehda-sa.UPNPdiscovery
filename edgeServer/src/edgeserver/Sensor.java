/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;

/**
 *
 * @author huberto
 */
class Sensor {
    private int id;
    private String nome;
    private String descricao;
    private String modelo;
    private String precisao;
    private String tipo;
    private String gatewayUID;
    private String urlPublicacao;
    private Float dado;
    private UpnpService upnpService;
    private Service sensorService;
    
    Sensor(String nome, String descricao, String modelo, String precisao, String tipo, String gateway) {
        this.nome = nome;
        this.descricao = descricao;
        this.modelo = modelo;
        this.precisao = precisao;
        this.tipo = tipo;
        this.gatewayUID = gateway;
    }
    
    Sensor(UpnpService upnpService, Service sensor, String gateway){
        this.upnpService = upnpService;
        this.sensorService = sensor;
        this.nome = this.getData(this.upnpService, this.sensorService, "GetNome", "ResultNome");
        this.descricao = this.getData(this.upnpService, this.sensorService, "GetDescricao", "ResultDescricao");
        this.modelo = this.getData(this.upnpService, this.sensorService, "GetModelo", "ResultModelo");
        this.precisao = this.getData(this.upnpService, this.sensorService, "GetPrecisao", "ResultPrecisao");
        this.tipo = this.getData(this.upnpService, this.sensorService, "GetTipo", "ResultTipo");
        this.dado = Float.parseFloat(this.getData(this.upnpService, this.sensorService, "GetDado", "ResultDado"));
        this.gatewayUID = gateway;
    }
    
    private String getData(UpnpService upnpService, Service sensor, String action, String output){
        Action getStatusAction = sensor.getAction(action);
        ActionInvocation getDataInvocation = new ActionInvocation(getStatusAction);
        String str;

        new ActionCallback.Default(getDataInvocation, upnpService.getControlPoint()).run();

        str = getDataInvocation.getOutput(output).getValue().toString();
        
        return str;
    }
    
    public void updateDado(){
        this.dado = Float.parseFloat(this.getData(this.upnpService, this.sensorService, "GetDado", "ResultDado"));
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getModelo() {
        return modelo;
    }

    public String getPrecisao() {
        return precisao;
    }

    public String getTipo() {
        return tipo;
    }

    public String getGateway() {
        return gatewayUID;
    }

    public Float getDado() {
        return dado;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setPrecisao(String precisao) {
        this.precisao = precisao;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setGateway(String gateway) {
        this.gatewayUID = gateway;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlPublicacao() {
        return urlPublicacao;
    }

    public void setUrlPublicacao(String urlPublicacao) {
        this.urlPublicacao = urlPublicacao;
    }
    
    
}
