package com.epam.port.entity;

import com.epam.port.exceptions.PortException;
import org.apache.log4j.Logger;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {

    private static Logger logger = Logger.getLogger(Port.class);
    private static Port instance = null;
    private static Lock lock = new ReentrantLock();
    private Warehouse warehouse;
    private ArrayDeque<Pier> pierList;                                  // очередь из свободных причалов
    private HashMap<Ship, Pier> occupiedPiers;                          // информация о занятых пристани
    private volatile boolean hasFreePiers = true;                       // true, если есть свободные пристани

    private Port(int warehouseSize, int piersCount) {
        logger.info("Creating port with piers count = " + piersCount);
        warehouse = new Warehouse(warehouseSize);
        occupiedPiers = new HashMap<Ship, Pier>();                      // сначала все пристани свободны, коллекция пустая
        pierList = new ArrayDeque<Pier>(piersCount);
        for (int i = 0; i < piersCount; i++) {
            pierList.addLast(new Pier(warehouse, i));                   // все пристани становятся в общую очередь
        }
    }

    public static Port getInstance(int warehouseSize, int piersCount) {
        lock.lock();
        try {
            if (instance == null) {
                instance = new Port(warehouseSize, piersCount);
            }
        } finally {
            lock.unlock();
        }
        return instance;
    }

    public Pier takePier(Ship ship) throws PortException {              // получить ссылку на пристань, к которой пришвартован корабль
        if (occupiedPiers.get(ship) == null) {
            throw new PortException("Can not take pier");
        }
        return occupiedPiers.get(ship);
    }

    public boolean dockShipToPier(Ship ship) {
        lock.lock();
        boolean res = false;
        try {
            if (pierList.size() != 0) {
                Pier pier = pierList.pollFirst();
                occupiedPiers.put(ship, pier);
                logger.info("Ship №" + ship.getId() + " dock to pier №" + pier.getId());
                res = true;

                if (pierList.size() == 0) {
                    logger.info("There are no free piers");
                    hasFreePiers = false;
                }

            } else {
                logger.info("Ship №" + ship.getId() + " can not dock. There are no piers to dock");
                res = false;
            }
        } finally {
            lock.unlock();
        }
        return res;
    }

    public boolean leavePier(Ship ship) {
        lock.lock();
        logger.info("Port locked. Ship is trying to leave port");
        boolean res = false;
        try {
            if (occupiedPiers.get(ship) != null) {
                Pier pier = occupiedPiers.remove(ship);
                pierList.addLast(pier);
                logger.info("Ship №" + ship.getId() + " left pier №" + pier.getId());
                res = true;
            } else {
                logger.info("Ship №" + ship.getId() + " can not leave pier");
                res = false;
            }
            if (pierList.size() != 0) {
                logger.info("There is a free pier");
                hasFreePiers = true;
            }
        } finally {
            lock.unlock();
            logger.info("Port unlocked");
        }
        return res;
    }

    public boolean getHasFreePiers() {
        boolean res = false;
        lock.lock();
        try {
            res = hasFreePiers;
        } finally {
            lock.unlock();
        }
        return hasFreePiers;
    }

    public void loadToWarehouse(int count) {
        warehouse.addContainers(count);
    }

}