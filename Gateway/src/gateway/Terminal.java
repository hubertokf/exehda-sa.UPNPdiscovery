/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author huberto
 */
public class Terminal {

    private  ProcessBuilder pro;
    private Process process;
    private Runtime run = Runtime.getRuntime();
    private InputStreamReader input;
    private BufferedReader r;


    public Terminal(){
       // run = Runtime.getRuntime();
    } 

    public String execute(String s){
        try {

          process =  run.exec(s);	         
          input = new InputStreamReader(process.getInputStream());
              r = new BufferedReader(input);
              return r.readLine();

        } catch (IOException ex) {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
            return "deu errado no script de leitura do sensor 1-wire";
        }
    }
}
