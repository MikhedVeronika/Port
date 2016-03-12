package com.epam.port.entity;

import com.epam.port.actions.ActionGenerator;
import com.epam.port.actions.ActionType;
import com.epam.port.exceptions.PortException;
import org.apache.log4j.Logger;

import java.util.Random;

public class Ship implements Runnable {

    private static Logger logger = Logger.getLogger(Ship.class);
    private boolean alive = true;                  // true, если корабль еще не обслуживался
    private Warehouse localWarehouse;              // локальное хранилище для корабля
    private Port port;
    private int id;

    public Ship(Port port, int id, int sizeOfWarehouse) {
        logger.info("Start creating ship №" + id);
        this.port = port;
        this.id = id;
        this.localWarehouse = new Warehouse(sizeOfWarehouse);
        logger.info("Ship №" + id + " created");
    }

    public boolean putContainers(int count) {  // вызвать из Main, чтобы наполнить корабли контейнерами
        return localWarehouse.addContainers(count);
    }

    public int getId() {
        return id;
    }

    public boolean unloadFromShip(Pier pier) throws InterruptedException {
        boolean res = false;
        int count = this.localWarehouse.getContainersCount();
        logger.info("Ship №" + this.id + " tries load to port " + count + " container(s)");
        res = pier.loadToWarehouse(this.localWarehouse);
        if (res) {
            logger.info("Ship №" + this.id + " loaded to port " + count + " container(s)");
        } else {
            logger.info("Ship №" + this.id + " didn't load to port " + count + " container(s)");
        }
        return res;
    }

    public boolean loadOnShip(Pier pier) throws InterruptedException {
        boolean res = false;
        int count = this.localWarehouse.getFreeSpace();
        logger.info("Ship №" + this.id + " tries unload from port " + count + " container(s)");
        res = pier.loadToShip(this.localWarehouse);
        if (res) {
            logger.info("Ship №" + this.id + " unloaded from port " + count + " container(s)");
        } else {
            logger.info("Ship №" + this.id + " didn't unload from port " + count + " container(s)");
        }
        return res;
    }

    public void doAction(ActionType actionType, Pier pier) throws InterruptedException {
        switch (actionType) {
            case LOADING_ON_SHIP:
                logger.info("Action 'loading on ship' was generated (pier №" + pier.getId() + ")");
                loadOnShip(pier);
                break;
            case UNLOADING_FROM_SHIP:
                logger.info("Action 'unloading from ship' was generated (pier №" + pier.getId() + ")");
                unloadFromShip(pier);
                break;
        }
    }

    private void dock() throws InterruptedException, PortException {      // попробовать пришвартоваться к свободной пристани
        Pier pier;
        boolean occupyPier = false;
        try {
            occupyPier = port.dockShipToPier(this);                       // true если удалось занять пристань
            if (occupyPier) {
                pier = port.takePier(this);                               // пристань, к которой удалось пришвартоваться
                ActionGenerator actionGenerator = new ActionGenerator();
                ActionType actionType = actionGenerator.generateAction();
                doAction(actionType, pier);
            }                                                             // else - нельзя пришвартоваться
        } finally {
            if (occupyPier) {
                Thread.sleep(1000 + new Random().nextInt(3000));
                port.leavePier(this);                                     // покинуть причал
                alive = false;
            }
        }
    }

    @Override
    public void run() {
        try {
            while (alive) {
                dock();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("Ship №" + this.id + " is broken");
        } catch (PortException e) {
            logger.error("Ship №" + this.id + " could not take pier");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return id == ship.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getLocalWarehouseSize() {
        return localWarehouse.getWarehouseSize();
    }
}

