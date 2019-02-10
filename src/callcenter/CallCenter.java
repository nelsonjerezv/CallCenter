/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callcenter;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nelson
 */
public class CallCenter implements Runnable{

    public static int RESPONDENTS = 5;
    public static int MANAGERS = 4;
    public static int DIRECTORS = 3;
    public static int CALLS = 70;
    public static int CALL_MIN_TIME_BETWEEN_CREATION = 100;
    public static int CALL_MAX_TIME_BETWEEN_CREATION = 300;    
    public static int CALL_MIN_DURATION = 500;    
    public static int CALL_MAX_DURATION = 3000;    
    public static ExecutorService respondentExServ;
    public static ExecutorService managerExServ;
    public static ExecutorService directorExServ;
    public static ExecutorService callExServ;
    public static ExecutorService callExServ2;
    public static LinkedBlockingQueue callQueue;
    public static LinkedBlockingQueue respondentQueue;

    public static void main(String[] args) throws InterruptedException {
        CallCenter callcenter = new CallCenter();
        callcenter.run();        
    }

    @Override
    public void run() {
        try {
            respondentExServ = Executors.newFixedThreadPool(RESPONDENTS);
            managerExServ = Executors.newFixedThreadPool(MANAGERS);
            directorExServ = Executors.newFixedThreadPool(DIRECTORS);
            callQueue = new LinkedBlockingQueue<>(CALLS);
            respondentQueue = new LinkedBlockingQueue<>(RESPONDENTS);
            callExServ2 = new ThreadPoolExecutor(CALLS, CALLS, 9L, TimeUnit.DAYS, callQueue);
            BlockingQueue<Call> r = new LinkedBlockingQueue<>(CALLS);
            BlockingQueue<Call> m = new LinkedBlockingQueue<>(CALLS);
            BlockingQueue<Call> d = new LinkedBlockingQueue<>(CALLS);
            
            for (int i = 0; i < RESPONDENTS; i++) {
                respondentExServ.execute(new Respondent(i, r, m, d));
            }
            
            Thread.sleep(500);
            System.out.println("");
            
            for (int i = 0; i < MANAGERS; i++) {
                managerExServ.execute(new Manager(i, m, d));
            }
            
            Thread.sleep(500);
            System.out.println("");
            
            for (int i = 0; i < DIRECTORS; i++) {
                directorExServ.execute(new Director(i, d));
            }
            
            Thread.sleep(500);
            System.out.println("");
            
//            if (!callExServ2.isTerminated()) {
//                System.out.println("not ready");
//            }
            
            Random random = new Random();            
            for (int i = 0; i < CALLS; i++) {
//                callExServ2.execute(new Call(i));
//                q.add(callExServ2.execute(new Call(i)));
                callExServ2.execute(new Call(i, r));
                Thread.sleep(random.nextInt((CALL_MAX_TIME_BETWEEN_CREATION - CALL_MIN_TIME_BETWEEN_CREATION) + 1));
            }
            
//            Thread.sleep(1500);
            
            respondentExServ.awaitTermination(1, TimeUnit.SECONDS);
            managerExServ.awaitTermination(1, TimeUnit.SECONDS);
            directorExServ.awaitTermination(1, TimeUnit.SECONDS);
            callExServ2.awaitTermination(1, TimeUnit.SECONDS);
            
            respondentExServ.shutdown();            
            managerExServ.shutdown();
            directorExServ.shutdown();
            callExServ2.shutdown();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(CallCenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
