/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callcenter;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Nelson
 */
public class Respondent implements Runnable  {

//    static void answerTheCall(Respondent r, Call llamada) {
//        r.free = false;
//        System.out.println("Respondent " + r.name + " took call " + llamada.id + ".");
//        llamada.answered = true;
//    }
    
//    public boolean free = false; 
    public String name;
    private final BlockingQueue<Call> respondentQueue;
    private final BlockingQueue<Call> managerQueue;
    private final BlockingQueue<Call> directorQueue;

    Respondent(int i, BlockingQueue<Call> r, BlockingQueue<Call> m, BlockingQueue<Call> d) {
        name = Integer.toString(i);
        respondentQueue = r;
        managerQueue = m;
        directorQueue = d;
    }

    @Override
    public void run() {
        System.out.println("Respondent " + name + " created.");
        Random duration = new Random(); 
        Random solved = new Random(); 
        
        try {
            Thread.sleep(7000);
            while(!CallCenter.directorExServ.isTerminated()){
                while(!respondentQueue.isEmpty()){
                    Call currentCall = respondentQueue.take();
                    System.out.println("Respondent " + name + " taking call " + currentCall.id + ".");
                    currentCall.answer();
                    Thread.sleep(duration.nextInt((CallCenter.CALL_MAX_DURATION - CallCenter.CALL_MIN_DURATION) + 1));
                    if(solved.nextBoolean()){
//                    if(false){
                        System.out.println("Respondent " + name + " solved call " + currentCall.id + ".");
                        currentCall.solve();
                    } else{
                        System.out.println("Respondent " + name + " could not solve call " + currentCall.id + ", escalating.");
                        if (Manager.busy < CallCenter.MANAGERS) {
                            System.out.println("There are managers available to take respondent " + name + "'s call " + currentCall.id + ".");
                            Manager.busy = Manager.busy + 1;
                            managerQueue.put(currentCall);
                        } else{
                            System.out.println("There aren't managers available to take respondent " + name + "'s call " + currentCall.id + ". Sending to a director.");
                            directorQueue.put(currentCall);
                        }
                    }  
                    Thread.sleep(100);
                }            
            }
        } catch (InterruptedException ex) {
            System.out.println("************* Ex R1");
        }
        System.out.println("Respondent " + name + " disconnected.");
    }
}
