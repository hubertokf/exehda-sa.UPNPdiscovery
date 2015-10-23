/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeserver;

import java.util.ArrayList;

/**
 *
 * @author huberto
 */
public class publicacao implements Runnable {
    private ArrayList<Gateway> gatewaysCadastrados = new ArrayList<>();

    publicacao(ArrayList<Gateway> gatewaysCadastrados) {
        this.gatewaysCadastrados = gatewaysCadastrados;
    }

    @Override
    public void run() {
        synchronized (gatewaysCadastrados) {
            System.out.println(Integer.toString(gatewaysCadastrados.size()));
        }
    }
    
}
