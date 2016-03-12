package com.epam.port.main;

import com.epam.port.entity.ShipsQueue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class Main {

    static {
        new DOMConfigurator().doConfigure("resources/log4j.xml", LogManager.getLoggerRepository());
    }
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        ShipsQueue ships = new ShipsQueue(8, 3, 150, 100);
        ships.serviceShips();
    }
}