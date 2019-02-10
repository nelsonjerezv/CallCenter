/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callcenter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nelson
 */
public class Call implements Runnable {
    public boolean answered;
    public boolean solved;
    public int id;
    private final BlockingQueue<Call> queue;

    Call(int i, BlockingQueue<Call> q) {
        id = i;
        queue = q;
    }
    
//    Call(int i) {
//        id = i;
//    }

    @Override
    public void run() {
        this.answered = false;
        this.solved = false;
        System.out.println("Call " + id + " ringing.");
        try {
            queue.put(this);
            do{
                Thread.sleep(100);
            }while(!this.solved);
        } catch (InterruptedException ex) {
            System.out.println("Call ex1");
        }
        System.out.println("Call " + id + " ended.");
    }

    void answer() {
        System.out.println("Call " + id + " answered.");
        answered = true;
    }

    void solve() {
        System.out.println("Call " + id + " solved.");
        solved = true;
    }
}
