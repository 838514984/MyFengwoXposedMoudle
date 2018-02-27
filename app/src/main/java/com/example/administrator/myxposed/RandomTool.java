package com.example.administrator.myxposed;

import android.os.BatteryManager;

import java.util.Random;

/**
 * Created by Administrator on 2018/2/6 0006.
 */

public class RandomTool {

    public static int getRandomLevel(){

        return 1+new Random().nextInt(95);

    }

    public static int getRandomStatus(){
        int status=BatteryManager.BATTERY_STATUS_DISCHARGING;
        switch (new Random().nextInt(3)){
            case 0:
                status=BatteryManager.BATTERY_STATUS_CHARGING;
                break;
            case 1:
                status=BatteryManager.BATTERY_STATUS_NOT_CHARGING;
                break;
            case 2:
                status=BatteryManager.BATTERY_STATUS_DISCHARGING;
                break;
        }
        return status;
    }

    public static int getRandomHealth(){
        int status=BatteryManager.BATTERY_HEALTH_GOOD;
        switch (new Random().nextInt(3)){
            case 0:
                status=BatteryManager.BATTERY_HEALTH_GOOD;
                break;
            case 1:
                status=BatteryManager.BATTERY_HEALTH_COLD;
                break;
            case 2:
                status=BatteryManager.BATTERY_HEALTH_OVERHEAT;
                break;
        }
        return status;
    }
}
