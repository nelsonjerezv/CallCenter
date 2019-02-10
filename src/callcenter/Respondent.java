package callcenter;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Nelson
 */
public class Respondent implements Runnable  {
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
        // random de tiempo de duracion de llamada
        Random duration = new Random(); 
        // random si logra resolver la llamada
        Random solved = new Random(); 
        
        try {
            Thread.sleep(7000);
            // ciclo para terminar esta hebra
            while(!CallCenter.directorExServ.isTerminated()){
                // ciclo para responder llamadas
                while(!respondentQueue.isEmpty()){
                    // saca llamada de la cola
                    Call currentCall = respondentQueue.take();
                    System.out.println("Respondent " + name + " taking call " + currentCall.id + ".");
                    currentCall.answer();
                    // tiempo que esta ocupado en la llamada
                    Thread.sleep(duration.nextInt((CallCenter.CALL_MAX_DURATION - CallCenter.CALL_MIN_DURATION) + 1));
                    if(solved.nextBoolean()){
                        System.out.println("Respondent " + name + " solved call " + currentCall.id + ".");
                        currentCall.solve();
                    } else{
                        System.out.println("Respondent " + name + " could not solve call " + currentCall.id + ", escalating.");
                        // su cantidad de managers ocupados menor a cantidad de managers
                        if (Manager.busy < CallCenter.MANAGERS) {
                            System.out.println("There are managers available to take respondent " + name + "'s call " + currentCall.id + ".");
                            // aumenta cantidad de managers ocupados y luego pone llamada en su cola
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
