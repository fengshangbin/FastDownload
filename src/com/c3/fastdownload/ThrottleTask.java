package com.c3.fastdownload;

import java.util.Timer;
import java.util.TimerTask;

public class ThrottleTask {
    private Timer timer;
    private Long delay;
    private Runnable runnable;
    private boolean needWait=false;

    public ThrottleTask(Runnable runnable,  Long delay) {
        this.runnable = runnable;
        this.delay = delay;
        this.timer = new Timer();
    }

    public static ThrottleTask build(Runnable runnable, Long delay){
        return new ThrottleTask(runnable, delay);
    }

    public void run(){
        if(!needWait){
            needWait=true;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    needWait=false;
                    runnable.run();
                }
            }, delay);
        }
    }

    public static void main(String[] args) {
        //System.out.println(Math.ceil(1.1));
        ThrottleTask task = ThrottleTask.build(new Runnable() {
            public void run() {
                System.out.println("do task: "+System.currentTimeMillis());
            }
        },1000L);
        while (true){
            System.out.println("call task: "+System.currentTimeMillis());
            task.run();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
