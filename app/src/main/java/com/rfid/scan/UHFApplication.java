package com.rfid.scan;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;

import com.rfid.scan.series.reader.server.ReaderHelper;
import com.rfid.scan.series.utils.MusicPlayer;
import com.rfid.scan.service.BoxDataUtil;


import java.util.ArrayList;
import java.util.List;

public class UHFApplication extends Application {

    public static Context applicationContext;
    private List<Activity> activities = new ArrayList<Activity>();

    public static final String OP_Type_Series = "0";
    public static final String OP_Type_Search = "1";
    public static final String OP_Type_Recognize = "2";
    public static final String OP_Type_Change = "3";

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext=getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(applicationContext);
        MusicPlayer.getInstance();
        try {
            //实例化ReaderHelper并setContext
            ReaderHelper.setContext(applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyU8Series.getInstance().initFist(applicationContext);

        //初始化工具包数据
        BoxDataUtil.getInstance().initData(applicationContext);
    }


    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : activities) {
            try {
                activity.finish();
            } catch (Exception e) {
                ;
            }
        }

        MyU8Series.getInstance().onDestroy();
        if (BluetoothAdapter.getDefaultAdapter() != null)
            BluetoothAdapter.getDefaultAdapter().disable();
        System.exit(0);
    }


    static public void saveBeeperState(int state){
        SharedPreferences spf = applicationContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt("_state", state);
        editor.commit();
    }

    static int _SoftSound=2;
    static public int appGetSoftSound(){
        if(_SoftSound==2)
            _SoftSound=getSoftSound();
        return _SoftSound;
    }
    static public int getVeeperState(){
        SharedPreferences spf = applicationContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        //SharedPreferences.Editor editor = spf.edit();
        int state = spf.getInt("_state", 0);
        return state;
    }

    static public void saveSoftSound(int state){
        _SoftSound=state;
        SharedPreferences spf = applicationContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt("_software_sound", state);
        editor.commit();
    }

    static public int getSoftSound(){
        SharedPreferences spf =applicationContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        //SharedPreferences.Editor editor = spf.edit();
        int state = spf.getInt("_software_sound", 1);
        return state;
    }

    static public void saveSessionState(int state){
        SharedPreferences spf = applicationContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt("_session", state);
        editor.commit();
    }

    static public int getSessionState(){
        SharedPreferences spf = applicationContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        //SharedPreferences.Editor editor = spf.edit();
        int state = spf.getInt("_session", 0);
        return state;
    }
    static public void saveFlagState(int state){
        SharedPreferences spf = applicationContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putInt("_flag", state);
        editor.commit();
    }

    static public int getFlagState(){
        SharedPreferences spf = applicationContext.getSharedPreferences("setting", Activity.MODE_PRIVATE);
        //SharedPreferences.Editor editor = spf.edit();
        int state = spf.getInt("_flag", 0);
        return state;
    }
}