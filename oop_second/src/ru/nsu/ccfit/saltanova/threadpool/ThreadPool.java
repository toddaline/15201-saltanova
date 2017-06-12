package ru.nsu.ccfit.saltanova.threadpool;

import ru.nsu.ccfit.saltanova.factory.BlockingQueue;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ThreadPool {

    private BlockingQueue<Runnable> queue;
    private Thread[] pool;

    public ThreadPool(int queueSize, int poolSize) {
        queue = new BlockingQueue<>(queueSize);
        pool = new Thread[poolSize];
        ThreadPoolRunnable workers = new ThreadPoolRunnable();
        for (int i = 0; i < poolSize; i++) {
            pool[i] = new Thread(workers);
            pool[i].setName("worker[" + i + "]");
            pool[i].start();
        }
    }

    public Thread[] getPool() {
        return pool;
    }

    public int getSizeOfQueue() {
        return queue.getSize();
    }

    public int getCapacityOfQueue() {
        return queue.getCapacity();
    }

    public void addTask(Runnable task) throws InterruptedException {
        queue.enqueue(task);
    }

    class ThreadPoolRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Runnable task = queue.dequeue();
                    task.run();
                }
            }
            catch (InterruptedException e) {
            }
        }
    }
}
