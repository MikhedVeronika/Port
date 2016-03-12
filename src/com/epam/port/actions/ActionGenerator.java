package com.epam.port.actions;

import java.util.Random;

public class ActionGenerator {

    public ActionGenerator() {
    }

    public ActionType generateAction() {
        int num = new Random().nextInt(2);
        if (num == 0) {
            return ActionType.LOADING_ON_SHIP;
        } else {
            return ActionType.UNLOADING_FROM_SHIP;
        }
    }

}
