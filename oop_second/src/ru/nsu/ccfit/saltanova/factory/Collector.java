package ru.nsu.ccfit.saltanova.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.saltanova.threadpool.ThreadPool;
import static ru.nsu.ccfit.saltanova.AppController.*;

public class Collector {

    private static int numberOfCar = 0;
    private static final Logger log = LogManager.getLogger("logger");
    private Stock<Body> bodyStock;
    private Stock<Engine> engineStock;
    private Stock<Accessory> accessoryStock;
    private Stock<Car> carStock;

    private int queueSize;
    private int poolSize;

    ThreadPool workers;

    Collector(int queueS, int poolS, Stock<Body> b, Stock<Engine> e, Stock<Accessory> a, Stock<Car> c) {
        queueSize = queueS;
        poolSize = poolS;
        bodyStock = b;
        engineStock = e;
        accessoryStock = a;
        carStock = c;
    }

    Stock getCarStock() {
        return carStock;
    }

    void collect() {
        workers = new ThreadPool(queueSize, poolSize);
    }

    void createCar() throws InterruptedException {
        workers.addTask(new NewCar());
    }

    class NewCar implements Runnable {
        @Override
        public void run () {
            try {
                Car newCar = new Car(bodyStock.get(), accessoryStock.get(), engineStock.get(), numberOfCar++);
                carStock.put(newCar);
                setBody(bodyStock.getSize());
                setEngine(engineStock.getSize());
                setAccessory((accessoryStock.getSize()));
                setCar(carStock.getSize());
                setCarTotal(numberOfCar.get());
            } catch (InterruptedException e) {
                log.info(Thread.currentThread().getName() + " was interrupted");
            }
        }
    }
}
