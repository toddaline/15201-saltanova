package ru.nsu.ccfit.saltanova;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Config {

    private static final String PROPERTIES_FILE = "src/config.properties";

    public static int PORT1;
    public static int PORT2;
    public static int HISTORY_LENGTH;
    public static boolean LOGGING;

    private static final Logger log = LogManager.getLogger("log");
    static {


        Properties properties = new Properties();
        FileInputStream propertiesFile = null;

        try {
            propertiesFile = new FileInputStream(PROPERTIES_FILE);
            properties.load(propertiesFile);
            PORT1 = Integer.parseInt(properties.getProperty("PORT1"));
            PORT2 = Integer.parseInt(properties.getProperty("PORT2"));
            HISTORY_LENGTH = Integer.parseInt(properties.getProperty("HISTORY_LENGTH"));
            LOGGING = Boolean.parseBoolean(properties.getProperty("LOGGING"));
        } catch (FileNotFoundException e) {
            log.info("no config file");
        } catch (IOException e) {
            log.info("file reading error");
        } finally {
            try {
                assert propertiesFile != null;
                propertiesFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}