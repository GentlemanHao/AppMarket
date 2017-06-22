package com.greatest.appmarket.manager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by WangHao on 2017.04.08  0008.
 */

public class ThreadManager {

    private static ThreadPool threadPool;

    public static ThreadPool getThreadPool() {
        if (threadPool == null) {
            synchronized (ThreadManager.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPool(5, 5, 1L);
                }
            }
        }
        return threadPool;
    }

    //线程池
    public static class ThreadPool {

        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;

        private ThreadPoolExecutor threadPoolExecutor;

        public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        //执行任务
        public void execute(Runnable runnable) {
            if (threadPoolExecutor == null) {
                threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, //核心线程数，最大线程数
                        keepAliveTime, TimeUnit.SECONDS, //线程休息时间，单位
                        new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory(),//默认线程工厂
                        new ThreadPoolExecutor.AbortPolicy());//默认出错处理
            }
            threadPoolExecutor.execute(runnable);
        }

        //取消任务
        public void cancel(Runnable runnable) {
            if (threadPoolExecutor != null) {
                //从线程队列中移除任务
                threadPoolExecutor.getQueue().remove(runnable);
            }
        }
    }
}
