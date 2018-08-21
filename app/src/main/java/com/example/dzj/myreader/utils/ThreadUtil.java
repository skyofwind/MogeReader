package com.example.dzj.myreader.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private ExecutorService executor;
    private static volatile ThreadUtil threadUtil;
    private ThreadUtil(){
        executor = Executors.newCachedThreadPool();
    }

    public static ThreadUtil getInstance(){
        ThreadUtil t = threadUtil;
        if(t == null){
            synchronized (ThreadUtil.class){
                t = new ThreadUtil();
                threadUtil = t;
            }
        }
        return threadUtil;
    }

    public void execute(Runnable runnable){
        executor.execute(runnable);
    }

    public void shutdow(){
        executor.shutdown();
    }

    public void shutdowNow(){
        executor.shutdownNow();
    }

    public void destory(){
        if(!executor.isShutdown()){
            executor.shutdownNow();
        }
        executor = null;
        threadUtil = null;
    }

    public static boolean isEmpty(){
        if (threadUtil == null){
            return true;
        }
        return false;
    }

}
