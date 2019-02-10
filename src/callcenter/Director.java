/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callcenter;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nelson
 */
public class Director implements Runnable  {
    public String name; 
    private final BlockingQueue<Call> directorQueue;   
    
    Director(int i, BlockingQueue<Call> d) {
        name = Integer.toString(i);;
        directorQueue = d;
    }

    @Override
    public void run() {
        System.out.println("Director " + name + " created.");
        // duracion de llamada 
        Random duration = new Random(); 
        
        // ciclo para que hebra vuelva a buscar por llamadas para responder
        while(!CallCenter.callExServ.isTerminated()){
            try {
                if (!directorQueue.isEmpty()) {
                    try {
                        Call currentCall = directorQueue.take();
                        System.out.println("Director " + name + " taking call " + currentCall.id + ".");
                        currentCall.answer();
                        Thread.sleep(duration.nextInt((CallCenter.CALL_MAX_DURATION - CallCenter.CALL_MIN_DURATION) + 1));
                        System.out.println("Director " + name + " solved call " + currentCall.id + ".");
                        currentCall.solve();
                    } catch (InterruptedException ex) {
                        System.out.println("Director " + name + " couldn't take call.");
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Director.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        System.out.println("Director " + name + " disconnected."); 
    }
    
}
