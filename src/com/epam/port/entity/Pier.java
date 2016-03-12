package com.epam.port.entity;

import org.apache.log4j.Logger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Pier {

    private static Logger logger = Logger.getLogger(Pier.class);
    private Warehouse warehouse;         // ссылка на общее хранилище для порта
    private int id;

    public Pier(Warehouse warehouse, int id) {
        this.id = id;
        this.warehouse = warehouse;
    }

    public boolean loadToWarehouse(Warehouse localWarehouse) throws InterruptedException {
        boolean res = false;
        boolean lockedWarehouse = false;
        Lock warehouseLock = warehouse.getLock();
        try {
            lockedWarehouse = warehouseLock.tryLock(2000, TimeUnit.MILLISECONDS);
            if (lockedWarehouse && (localWarehouse.getContainersCount() <= warehouse.getFreeSpace())) {
                res = unloadFromShip(localWarehouse);
            }
        } finally {
            if (lockedWarehouse) {
                warehouseLock.unlock();
            }
        }
        return res;
    }

    private boolean unloadFromShip(Warehouse localWarehouse) throws InterruptedException {
        boolean res = false;
        boolean lockedShip = false;
        Lock localWarehouseLock = localWarehouse.getLock();
        try {
            lockedShip = localWarehouseLock.tryLock(2000, TimeUnit.MILLISECONDS);
            if (lockedShip && warehouse.addContainers(localWarehouse.getContainersCount())) {   // кладем контейнеры в warehouse
                localWarehouse.setContainersCount(0);                                           // удаляем контейнеры из ship
                res = true;
            }
        } finally {
            if (lockedShip) {
                localWarehouseLock.unlock();
            }
        }
        return res;
    }

    public boolean loadToShip(Warehouse localWarehouse) throws InterruptedException {
        boolean res = false;
        boolean lockedWarehouse = false;
        Lock warehouseLock = warehouse.getLock();
        try {
            lockedWarehouse = warehouseLock.tryLock(2000, TimeUnit.MILLISECONDS);
            if (lockedWarehouse && (localWarehouse.getFreeSpace() <= warehouse.getContainersCount())) {
                res = unloadFromPort(localWarehouse);
            }
        } finally {
            if (lockedWarehouse) {
                warehouseLock.unlock();
            }
        }
        return res;
    }


    private boolean unloadFromPort(Warehouse localWarehouse) throws InterruptedException {
        boolean res = false;
        boolean lockedShip = false;
        Lock localWarehouseLock = localWarehouse.getLock();
        try {
            lockedShip = localWarehouseLock.tryLock(2000, TimeUnit.MILLISECONDS);
            if (lockedShip && warehouse.takeContainers(localWarehouse.getFreeSpace())) {     // берем контейнеры из warehouse
                localWarehouse.setContainersCount(localWarehouse.getWarehouseSize());        // добавляем контейнеры на корабль
                res = true;
            }
        } finally {
            if (lockedShip) {
                localWarehouseLock.unlock();
            }
        }
        return res;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
