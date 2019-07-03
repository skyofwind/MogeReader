package com.example.dzj.myreader.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单例线程池
 */
public class ExecutorsUtil {

    private static volatile ExecutorsUtil executorsUtil;
    private ExecutorService fixedThreadPool;

    private ExecutorsUtil(){
        fixedThreadPool = Executors.newFixedThreadPool(5);
    }

    public static ExecutorsUtil getInstance(){
        ExecutorsUtil ex = executorsUtil;
        if(ex == null){
            synchronized (ExecutorsUtil.class){
                ex = new ExecutorsUtil();
                executorsUtil = ex;
            }
        }
        return ex;
    }

    public void execute(Runnable runnable){
        if(fixedThreadPool != null){
            fixedThreadPool.execute(runnable);
        }
    }
}
