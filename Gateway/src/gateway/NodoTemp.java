/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gateway;

import org.fourthline.cling.binding.annotations.*;

/**
 *
 * @author huberto
 */

@UpnpService(
    serviceId = @UpnpServiceId("NodoTempNativo"),
    serviceType = @UpnpServiceType(value = "NodoTemp", version = 1)
)
public class NodoTemp {
    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private String nome = "NodoTemp";
    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private String descricao = "Sensor de temperatura";
    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private String modelo = "dht11";
    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private String precisao = "0.1";
    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private String tipo = "temperatura";
    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private Float temperatura = (float) 0;
    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private Float dado = (float) 0;

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultNome"))
    public String getNome() {
        return nome;
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDescricao"))
    public String getDescricao() {
        return descricao;
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultModelo"))
    public String getModelo() {
        return modelo;
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultPrecisao"))
    public String getPrecisao() {
        return precisao;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultTipo"))
    public String getTipo() {
        return tipo;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultTemperatura"))
    public Float getTemperatura() {
        return temperatura;
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDado"))
    public Float getDado() {
        // If you want to pass extra UPnP information on error:
        // throw new ActionException(ErrorCode.ACTION_NOT_AUTHORIZED);
        Terminal t = new Terminal();
        
        this.dado = Float.parseFloat(t.execute("python nodoTemp.py"));
        
        return dado;
    }    
    
//    public static float gerarNumero(float minX, float maxX) {  
//        Random r = new Random();
//        
//        return r.nextFloat() * (maxX - minX) + minX;
//    } 
    
}
