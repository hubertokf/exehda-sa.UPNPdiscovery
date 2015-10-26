/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.util.ArrayList;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;

/**
 *
 * @author huberto
 */
public class Gateway {
    private int id;
    private String nome;
    private String uid;
    private String tipo;
    private String fabricante;
    private String detalhes;
    private ArrayList<Sensor> Sensores;
    
    Gateway(UpnpService upnpservice, RemoteDevice device){
        nome = device.getDetails().getFriendlyName();
        uid = device.getIdentity().getUdn().toString();
        tipo = device.getType().getType();
        fabricante = device.getDetails().getManufacturerDetails().getManufacturer();
        detalhes = device.getDetails().getModelDetails().getModelDescription();
        Sensores = new ArrayList();
        int i = 0;
        for (RemoteService service : device.findServices()) {
            Sensor teste = new Sensor(upnpservice, service, uid);
            Sensores.add(teste);
        }
    }
    
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getUid() {
        return uid;
    }

    public String getTipo() {
        return tipo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public ArrayList<Sensor> getSensores() {
        return Sensores;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setFabricante(String Fabricante) {
        this.fabricante = Fabricante;
    }

    public void setDetalhes(String Detalhes) {
        this.detalhes = Detalhes;
    }

    public void setSensores(ArrayList<Sensor> Sensor) {
        this.Sensores = Sensor;
    }
    
    
}
