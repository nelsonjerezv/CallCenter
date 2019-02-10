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
public class Manager implements Runnable  {public String name;
    public static int busy = 0;
    private final BlockingQueue<Call> managerQueue;
    private final BlockingQueue<Call> directorQueue;

    Manager(int i, BlockingQueue<Call> m, BlockingQueue<Call> d) {        
        name = Integer.toString(i);
        managerQueue = m;
        directorQueue = d;
    }

    @Override
    public void run() {
        System.out.println("Manager " + name + " created.");
        Random duration = new Random(); 
        Random solved = new Random(); 
        while(!CallCenter.callExServ2.isTerminated()){
            try {
                if (!managerQueue.isEmpty()) {
                    try {
                        Call currentCall = managerQueue.take();
                        System.out.println("Manager " + name + " taking call " + currentCall.id + ".");
                        currentCall.answer();
                        Thread.sleep(duration.nextInt((CallCenter.CALL_MAX_DURATION - CallCenter.CALL_MIN_DURATION) + 1));
                        if(solved.nextBoolean()){
                            System.out.println("Manager " + name + " solved call " + currentCall.id + ".");
                            currentCall.solve();
                            busy = busy - 1;
                        } else{
                            System.out.println("Manager " + name + " could not solve call " + currentCall.id + ", sending to director.");
                            directorQueue.put(currentCall);
                            busy = busy - 1;
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("Manager " + name + " couldn't take call.");
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Manager " + name + " couldn't poll.");
            }
        }
        System.out.println("Manager " + name + " disconnected.");
    }
}
