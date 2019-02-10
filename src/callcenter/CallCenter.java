package callcenter;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nelson
 */
public class CallCenter implements Runnable{
    // Cantidad de 
    public static int RESPONDENTS = 5;
    public static int MANAGERS = 4;
    public static int DIRECTORS = 3;
    public static int CALLS = 70;
    // tiempos minimo y maximo entre llamadas (ms)
    public static int CALL_MIN_TIME_BETWEEN_CREATION = 100;
    public static int CALL_MAX_TIME_BETWEEN_CREATION = 300; 
    // duracion llamadas (ms)
    public static int CALL_MIN_DURATION = 500;    
    public static int CALL_MAX_DURATION = 3000;
    // grupos de hebras
    public static ExecutorService respondentExServ;
    public static ExecutorService managerExServ;
    public static ExecutorService directorExServ;
    public static ExecutorService callExServ;
    // cola de llamadas
    public static LinkedBlockingQueue callQueue;

    public static void main(String[] args) throws InterruptedException {
        CallCenter callcenter = new CallCenter();
        callcenter.run();        
    }

    @Override
    public void run() {
        try {
            // asignar tamaño a grupos de hebras
            respondentExServ = Executors.newFixedThreadPool(RESPONDENTS);
            managerExServ = Executors.newFixedThreadPool(MANAGERS);
            directorExServ = Executors.newFixedThreadPool(DIRECTORS);
            callQueue = new LinkedBlockingQueue<>(CALLS);
            callExServ = new ThreadPoolExecutor(CALLS, CALLS, 9L, TimeUnit.DAYS, callQueue);
            // Cola de llamadas de respondents, managers and directors
            // Colas se comparten entre clases
            BlockingQueue<Call> respondentQueue = new LinkedBlockingQueue<>(CALLS);
            BlockingQueue<Call> managerQueue = new LinkedBlockingQueue<>(CALLS);
            BlockingQueue<Call> directorQueue = new LinkedBlockingQueue<>(CALLS);
            
            for (int i = 0; i < RESPONDENTS; i++) {
                // respondent con acceso a la cola de respondents, managers y directors
                respondentExServ.execute(new Respondent(i, respondentQueue, managerQueue, directorQueue));
            }
            
            Thread.sleep(500);
            System.out.println("");
            
            for (int i = 0; i < MANAGERS; i++) {
                // manager con acceso a la cola de managers y directors
                managerExServ.execute(new Manager(i, managerQueue, directorQueue));
            }
            
            Thread.sleep(500);
            System.out.println("");
            
            for (int i = 0; i < DIRECTORS; i++) {
                // director con acceso a la cola de directors
                directorExServ.execute(new Director(i, directorQueue));
            }
            
            Thread.sleep(500);
            System.out.println("");
            
            Random random = new Random();            
            for (int i = 0; i < CALLS; i++) {
                // se crea una llamada
                callExServ.execute(new Call(i, respondentQueue));
                // tiempo entre llamadas
                Thread.sleep(random.nextInt((CALL_MAX_TIME_BETWEEN_CREATION - CALL_MIN_TIME_BETWEEN_CREATION) + 1));
            }
            
            // para esperar que completen sus tareas
            respondentExServ.awaitTermination(1, TimeUnit.SECONDS);
            managerExServ.awaitTermination(1, TimeUnit.SECONDS);
            directorExServ.awaitTermination(1, TimeUnit.SECONDS);
            callExServ.awaitTermination(1, TimeUnit.SECONDS);
            
            // se cierran los grupos de hebras
            respondentExServ.shutdown();            
            managerExServ.shutdown();
            directorExServ.shutdown();
            callExServ.shutdown();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(CallCenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
