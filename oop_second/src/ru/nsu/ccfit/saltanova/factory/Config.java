package ru.nsu.ccfit.saltanova.factory;

import java.io.*;
import java.util.Properties;

class Config {
    private int StorageBodySize, StorageEngineSize, StorageAccessorySize, StorageCarSize, AccessorySuppliers, Collectors, QueueSize, Dealers;
    private boolean LogSale;
    Config(FileInputStream fin){
        Properties property = new Properties();
        try {
            property.load(fin);
        }
        catch (IOException e) {
            System.err.println("no config file");
        }
        StorageBodySize = Integer.valueOf(property.getProperty("StorageBodySize"));
        StorageEngineSize = Integer.valueOf(property.getProperty("StorageEngineSize"));
        StorageAccessorySize = Integer.valueOf(property.getProperty("StorageAccessorySize"));
        StorageCarSize = Integer.valueOf(property.getProperty("StorageCarSize"));
        AccessorySuppliers = Integer.valueOf(property.getProperty("AccessorySuppliers"));
        Collectors = Integer.valueOf(property.getProperty("Collectors"));
        QueueSize = Integer.valueOf(property.getProperty("QueueSize"));
        Dealers = Integer.valueOf(property.getProperty("Dealers"));
        LogSale = Boolean.valueOf(property.getProperty("LogSale"));
    }

    public int getStorageBodySize() {
        return StorageBodySize;
    }

    public int getStorageEngineSize() {
        return StorageEngineSize;
    }

    public int getStorageAccessorySize() {
        return StorageAccessorySize;
    }

    public int getStorageCarSize() {
        return StorageCarSize;
    }

    public int getAccessorySuppliers() {
        return AccessorySuppliers;
    }

    public int getCollectors() {
        return Collectors;
    }

    public int getQueueSize() {return QueueSize; }

    public int getDealers() {
        return Dealers;
    }

    public boolean isLogSale() {
        return LogSale;
    }
}