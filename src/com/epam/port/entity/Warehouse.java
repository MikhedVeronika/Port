package com.epam.port.entity;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {

    private static Logger logger = Logger.getLogger(Warehouse.class);

    private int warehouseSize;
    private int containersCount = 0;
    private Lock lock = new ReentrantLock();

    public Warehouse(int warehouseSize) {
        lock.lock();
        try {
            logger.info("Creating warehouse with size = " + warehouseSize);
            this.warehouseSize = warehouseSize;
        } finally {
            lock.unlock();
        }
    }

    public boolean addContainers(int count) {                // пытаемся добавить count контейнеров на склад
        lock.lock();
        logger.info("Warehouse locked, trying add " + count + " containers");
        boolean successAdding = false;
        try {
            if (containersCount + count <= warehouseSize) {
                containersCount += count;
                successAdding = true;
                logger.info(count + "container(s) were added to warehouse successfully");
            }
            if (!successAdding) {
                logger.info("Can not add to warehouse " + count + " container(s)");
            }
        } finally {
            lock.unlock();
            logger.info("Warehouse unlocked");
        }
        return successAdding;
    }

    public boolean takeContainers(int count) {               // пытаемся извлечь count контейнеров
        lock.lock();
        logger.info("Warehouse locked, trying take " + count + " containers");
        boolean successTaking = false;
        try {
            if (containersCount >= count) {
                containersCount -= count;
                successTaking = true;
                logger.info(count + "container(s) were taken from warehouse successfully");
            }
            if (!successTaking) {
                logger.info("Can not take from warehouse " + count + " container(s)");
            }
        } finally {
            lock.unlock();
            logger.info("Warehouse unlocked");
        }
        return successTaking;
    }

    public int getWarehouseSize() {
        return warehouseSize;
    }

    public void setWarehouseSize(int warehouseSize) {
        this.warehouseSize = warehouseSize;
    }

    public int getContainersCount() {
        return containersCount;
    }

    public void setContainersCount(int containersCount) {
        this.containersCount = containersCount;
    }

    public int getFreeSpace() {
        return this.warehouseSize - this.containersCount;
    }

    public Lock getLock() {
        return lock;
    }
}