package callcenter;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

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
        // random de tiempo de duracion de llamada
        Random duration = new Random(); 
        // random si logra resolver la llamada
        Random solved = new Random(); 
        
        // ciclo para que hebra siga buscando llamadas
        while(!CallCenter.callExServ.isTerminated()){
            try {                
                if (!managerQueue.isEmpty()) {
                    try {
                        Call currentCall = managerQueue.take();
                        System.out.println("Manager " + name + " taking call " + currentCall.id + ".");
                        currentCall.answer();
                        // duración de llamada
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
                // tiempo de espera para volver a buscar llamadas para responder
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Manager " + name + " couldn't poll.");
            }
        }
        System.out.println("Manager " + name + " disconnected.");
    }
}
