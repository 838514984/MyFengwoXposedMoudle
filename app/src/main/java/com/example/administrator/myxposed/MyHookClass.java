package com.example.administrator.myxposed;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class MyHookClass implements IXposedHookLoadPackage {
    Class<?> VipAdResultInfoClz;
    Class<?> batteryServiceClz;
    Object mBatteryService;


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        batteryServiceClz = XposedHelpers.findClass("com.android.server.BatteryService", loadPackageParam.classLoader);


        //hook battery
        XposedHelpers.findAndHookMethod("android.content.Intent", loadPackageParam.classLoader, "getIntExtra", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Intent intent = (Intent) param.thisObject;
                final String action = intent.getAction();
                if (!TextUtils.isEmpty(action)){
                    if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
//                    if (BatteryManager.EXTRA_LEVEL.equals(param.args[0] + "")) {
//                        param.setResult(1);
//                    } else if ("status".equals(param.args[0] + "")) {
//                        XposedBridge.log("status");
//                        param.setResult(BatteryManager.BATTERY_STATUS_NOT_CHARGING);
//                    }
                        intent.putExtra(BatteryManager.EXTRA_LEVEL, RandomTool.getRandomLevel());
                        intent.putExtra(BatteryManager.EXTRA_STATUS, RandomTool.getRandomStatus());
                        intent.putExtra(BatteryManager.EXTRA_HEALTH, RandomTool.getRandomHealth());


                    }
                }

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

            }
        });


//        //hook batteryService
        XposedHelpers.findAndHookMethod(batteryServiceClz, "processValuesLocked" , new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                mBatteryService = param.thisObject;




                Field plug = batteryServiceClz.getDeclaredField("mPlugType");
                plug.setAccessible(true);
                plug.setInt(mBatteryService,0);

                XposedBridge.log("-------hook batteryService:  param.thisObject: "+mBatteryService);

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

            }
        });


        //hook fengwo
        if (loadPackageParam.packageName.equals("com.cyjh.gundam")) {

            XposedBridge.log("hhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            final Class<?> c = XposedHelpers.findClass("com.cyjh.gundam.fengwoscript.model.ScriptRunPermModel", loadPackageParam.classLoader);
            XposedHelpers.findAndHookMethod(c, "getVipAdResultInfo", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    //XposedBridge.log("bbbbbbbbbbbbbbbbefore "+param.getResult().getClass().getName());

                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object vipinfo = param.getResult();
                    VipAdResultInfoClz = XposedHelpers.findClass("com.cyjh.gundam.vip.bean.VipAdResultInfo", loadPackageParam.classLoader);
                    Field useTime = VipAdResultInfoClz.getDeclaredField("EachTryTime");
                    useTime.setInt(vipinfo, 9999999);

                    Field RunPermF = VipAdResultInfoClz.getDeclaredField("RunPerm");
                    Object RunpermO = RunPermF.get(vipinfo);

                    Class<?> RunperClz = XposedHelpers.findClass("com.cyjh.gundam.vip.bean.VipRunPermInfo", loadPackageParam.classLoader);

                    Field KickedOutF = RunperClz.getDeclaredField("KickedOut");
                    KickedOutF.setBoolean(RunpermO, false);

                    Field BanRunF = RunperClz.getDeclaredField("BanRun");
                    BanRunF.setBoolean(RunpermO, false);

                    Field RunF = RunperClz.getDeclaredField("Run");
                    RunF.setBoolean(RunpermO, true);

                    Field Try = RunperClz.getDeclaredField("Try");
                    Try.setBoolean(RunpermO, true);

                    Field TryExpiredF = RunperClz.getDeclaredField("TryExpired");
                    TryExpiredF.setBoolean(RunpermO, false);

                    XposedBridge.log("xxxxxxxxxxxxxxxxxxxxxxx usetime: " + useTime.getInt(vipinfo));
                    XposedBridge.log("xxxxxxxxxxxxxxxxxxxxxxxxkickedOut" + KickedOutF.getBoolean(RunpermO));
                    XposedBridge.log("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxbanRun: " + BanRunF.getBoolean(RunpermO));
                    XposedBridge.log("Try: " + Try.getBoolean(RunpermO));
                    XposedBridge.log("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxRunF: " + RunF.getBoolean(RunpermO));
                    XposedBridge.log("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxTryExpired: " + TryExpiredF.getBoolean(RunpermO));
                    param.setResult(vipinfo);

                }
            });


            //hook是否为vip
            XposedHelpers.findAndHookMethod("com.cyjh.gundam.manager.LoginManager", loadPackageParam.classLoader, "isVip", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult(1);
                    XposedBridge.log("TTTTTTTTTTTTTTTTTTTThe vip value is: " + param.getResult());
                }
            });
            //替换Onclick方法
            XposedHelpers.findAndHookMethod("com.cyjh.gundam.fengwoscript.presenter.ScriptInfoPresenter", loadPackageParam.classLoader, "onClick", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("xxxxxxxxxxxxxxxxxxxxxxx替换方法");
                    Class<?> clz = XposedHelpers.findClass("com.cyjh.gundam.fengwoscript.script.model.manager.ScriptManager", loadPackageParam.classLoader);
                    Method method = clz.getDeclaredMethod("getInstance");
                    Object obj = method.invoke(null);
                    Method runMethod = clz.getDeclaredMethod("runScript");
                    runMethod.invoke(obj);


                    return null;
                }
            });


            //hook最终判断checkRunperm方法
            XposedHelpers.findAndHookMethod("com.cyjh.gundam.fengwoscript.model.manager.HeartAndPermManager", loadPackageParam.classLoader, "checkRunPerm", VipAdResultInfoClz, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object vipinfo = param.args[0];
                    Field useTime = VipAdResultInfoClz.getDeclaredField("EachTryTime");
                    useTime.setInt(vipinfo, 9999999);

                    Field RunPermF = VipAdResultInfoClz.getDeclaredField("RunPerm");
                    Object RunpermO = RunPermF.get(vipinfo);

                    Class<?> RunperClz = XposedHelpers.findClass("com.cyjh.gundam.vip.bean.VipRunPermInfo", loadPackageParam.classLoader);

                    Field KickedOutF = RunperClz.getDeclaredField("KickedOut");
                    KickedOutF.setBoolean(RunpermO, false);

                    Field BanRunF = RunperClz.getDeclaredField("BanRun");
                    BanRunF.setBoolean(RunpermO, false);

                    Field RunF = RunperClz.getDeclaredField("Run");
                    RunF.setBoolean(RunpermO, true);

                    Field Try = RunperClz.getDeclaredField("Try");
                    Try.setBoolean(RunpermO, true);

                    Field TryExpiredF = RunperClz.getDeclaredField("TryExpired");
                    TryExpiredF.setBoolean(RunpermO, false);


                }
            });


            //hook心跳包判断
            Class<?> Event$HeartModelCallBackEventClz = XposedHelpers.findClass("com.cyjh.gundam.fengwoscript.event.Event$HeartModelCallBackEvent", loadPackageParam.classLoader);
            XposedHelpers.findAndHookMethod("com.cyjh.gundam.fengwoscript.model.manager.HeartAndPermManager", loadPackageParam.classLoader, "onEventMainThread", Event$HeartModelCallBackEventClz, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("ssssssssssssssssssssssssssssss替换第二个 判断心跳");
                    Class<?> HeartAndPermManagerClz = XposedHelpers.findClass("com.cyjh.gundam.fengwoscript.model.manager.HeartAndPermManager", loadPackageParam.classLoader);
                    Method getInstance = HeartAndPermManagerClz.getDeclaredMethod("getInstance");
                    Object HeartAndPermManagerInstance = getInstance.invoke(null);
                    Field mRunPermModel = HeartAndPermManagerClz.getDeclaredField("mRunPermModel");
                    Object RunPermModel = mRunPermModel.get(HeartAndPermManagerInstance);
                    Method load = RunPermModel.getClass().getDeclaredMethod("load");
                    load.invoke(RunPermModel);
                    return null;
                }
            });


        }


        if (loadPackageParam.packageName.equals("com.example.administrator.mybehookmoudle")) {
            final Class<?> myClass = XposedHelpers.findClass("com.example.administrator.mybehookmoudle.MyClass", loadPackageParam.classLoader);
            XposedHelpers.findAndHookMethod("com.example.administrator.mybehookmoudle.MainActivity", loadPackageParam.classLoader, "showP", myClass, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Field field = myClass.getDeclaredField("age");
                    field.setInt(param.args[0], 98765432);
                    XposedBridge.log("98765432 update the age");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {


                }
            });
        }


    }
}
