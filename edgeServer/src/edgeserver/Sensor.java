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
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;

/**
 *
 * @author huberto
 */
class Sensor {
    private String nome;
    private String descricao;
    private String modelo;
    private String precisao;
    private String tipo;
    private String gateway;
    
    Sensor(String nome, String descricao, String modelo, String precisao, String tipo, String gateway) {
        this.nome = nome;
        this.descricao = descricao;
        this.modelo = modelo;
        this.precisao = precisao;
        this.tipo = tipo;
        this.gateway = gateway;
    }
    
    Sensor(UpnpService upnpService, Service sensor, String gateway){
        this.nome = this.getData(upnpService, sensor, "GetNome", "ResultNome");
        this.descricao = this.getData(upnpService, sensor, "GetDescricao", "ResultDescricao");
        this.modelo = this.getData(upnpService, sensor, "GetModelo", "ResultModelo");
        this.precisao = this.getData(upnpService, sensor, "GetPrecisao", "ResultPrecisao");
        this.tipo = this.getData(upnpService, sensor, "GetTipo", "ResultTipo");
        this.gateway = gateway;
    }
    
    private String getData(UpnpService upnpService, Service sensor, String action, String output){
        Action getStatusAction = sensor.getAction(action);
        ActionInvocation getDataInvocation = new ActionInvocation(getStatusAction);
        String str;

        new ActionCallback.Default(getDataInvocation, upnpService.getControlPoint()).run();

        str = getDataInvocation.getOutput(output).getValue().toString();
        
        return str;
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
        return gateway;
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
        this.gateway = gateway;
    }
    
    
}
