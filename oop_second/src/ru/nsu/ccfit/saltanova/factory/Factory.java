package ru.nsu.ccfit.saltanova.factory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import ru.nsu.ccfit.saltanova.AppController;
import ru.nsu.ccfit.saltanova.SimpleGUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Factory {

    private Config conf;

    private BodySupplier bodySupplier;
    private EngineSupplier engineSupplier;
    private AccessorySupplier accessorySupplier;
    private Dealer dealer;

    private Thread bodyThread;
    private Thread engineThread;
    private Thread carThread;
    private Thread[] accessoryThread;
    private Thread[] dealerThread;
    private SimpleGUI app;

    private int timeout = 0;
    private int dealersTimeout = 0;
    private CarStock carStock;
    private Collector collector;

    Factory (){
        try (FileInputStream fin = new FileInputStream("config.properties")) {
            conf = new Config(fin);
        }
        catch (IOException e){
            System.out.println("No config file");
            System.exit(1);
        }

        Logger logger = LogManager.getRootLogger();
        if (!conf.isLogSale()){
            Configurator.setLevel(logger.getName(), Level.OFF);
        }

        Stock<Body> bodyStock = new Stock<>(conf.getStorageBodySize());
        Stock<Engine> engineStock = new Stock<>(conf.getStorageEngineSize());
        Stock<Accessory> accessoryStock = new Stock<>(conf.getStorageAccessorySize());
        carStock = new CarStock(conf.getStorageCarSize());

        collector = new Collector(conf.getQueueSize(), conf.getCollectors(), bodyStock, engineStock, accessoryStock, carStock);  //redo
        Controller controller = new Controller(collector);
        carStock.setController(controller);

        bodySupplier = new BodySupplier(timeout, bodyStock);
        engineSupplier = new EngineSupplier(timeout, engineStock);
        accessorySupplier = new AccessorySupplier(timeout, accessoryStock);
        dealer = new Dealer(dealersTimeout, carStock);

        bodyThread = new Thread(bodySupplier);
        engineThread = new Thread(engineSupplier);
        accessoryThread = new Thread[conf.getAccessorySuppliers()];
        for (int i = 0; i < conf.getAccessorySuppliers(); i++) {
            accessoryThread[i] = new Thread(accessorySupplier);
            accessoryThread[i].setName("accessorySupplier[" + i + "]");
        }

        carThread = new Thread(controller);
        carThread.setName("controllerThread");

        dealerThread = new Thread[conf.getDealers()];
        for (int i = 0; i < conf.getDealers(); i++) {
            dealerThread[i] = new Thread(dealer);
            dealerThread[i].setName("<" + i + ">");
        }
    }

    void start() {
        OutputStreamWriter fout = null;
        app = new SimpleGUI(fout, this);
        new AppController(app);

        bodyThread.start();
        engineThread.start();
        for (int i = 0; i < conf.getAccessorySuppliers(); i++) {
            accessoryThread[i].start();
        }
        collector.collect();
        carThread.start();

        for (int i = 0; i < conf.getDealers(); i++) {
            dealerThread[i].start();
        }
    }

    public BodySupplier getBodySupplier() {
        return bodySupplier;
    }

    public EngineSupplier getEngineSupplier() {
        return engineSupplier;
    }

    public AccessorySupplier getAccessorySupplier() {
        return accessorySupplier;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public void finish() {
        bodyThread.interrupt();
        engineThread.interrupt();
        for (Thread t : accessoryThread) {
            t.interrupt();
        }
        carThread.interrupt();
        for (Thread t : collector.workers.getPool()) {
            t.interrupt();
        }
        for (Thread t : dealerThread) {
            t.interrupt();
        }
    }
}