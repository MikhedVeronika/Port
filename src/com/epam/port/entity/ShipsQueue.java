package com.epam.port.entity;

import org.apache.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Random;

public class ShipsQueue {

    private static Logger logger = Logger.getLogger(ShipsQueue.class);
    private ArrayDeque<Ship> shipsQueue;
    private Port port;

    public ShipsQueue(int shipsCount, int piersCount, int warehouseSize, int currentContainersCount) {
        this.shipsQueue = new ArrayDeque<Ship>(shipsCount);
        this.port = Port.getInstance(warehouseSize, piersCount);
        init(currentContainersCount, shipsCount);
    }

    public void init(int currentContainersCount, int shipsCount) {
        port.loadToWarehouse(currentContainersCount);
        for (int i = 0; i < shipsCount; i++) {
            Ship ship = new Ship(this.port, i + 1, new Random().nextInt(25) + 5);
            ship.putContainers(1 + new Random().nextInt(ship.getLocalWarehouseSize() - 2));
            shipsQueue.addLast(ship);
        }
    }

    public void serviceShips() {
        int shipsCount = shipsQueue.size();

        for (int j = 0; j < shipsCount; j++) {
            if (port.getHasFreePiers()) {
                logger.info("NEW THREAD CREATED");
                new Thread(shipsQueue.removeFirst()).start();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    logger.error("Thread was interrupted");
                }
            } else {
                while (!port.getHasFreePiers()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("Thread was interrupted");
                    }
                    logger.info("WAITING");
                }
                new Thread(shipsQueue.removeFirst()).start();
            }
        }
    }
}
