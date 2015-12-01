package com.example.owner.swingringer;

import android.content.Context;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


/**
 * Created by owner on 2015/11/20.
 */
public class CloudAccessor {

    private boolean mIsInitialized;
    private int mDataNum;
    private IMUData mImuData;
    private IMUData mOldImuData;

    CloudAccessor() {
        mDataNum = 0;
        mIsInitialized = false;
        mImuData = new IMUData();
        mOldImuData = new IMUData();
    }

    private Thread setService;

    public void initialize(Context context, String apiKey, String cliantKey) {
        // Parseの初期化
        if (mIsInitialized == false) {
            Parse.enableLocalDatastore(context);
            Parse.initialize(context, apiKey, cliantKey);
            mIsInitialized = true;
            setDataNum();
        }
    }

    public void setSwingData(IMUData data, IMUData oldData) {
        mImuData = data;
        mOldImuData = oldData;
    }

    private void addSwingData() {
        if (mIsInitialized) {
            ParseObject swingData = new ParseObject("SwingData");
            swingData.put("accX", String.valueOf(mImuData.acc.x));
            swingData.put("accY", String.valueOf(mImuData.acc.y));
            swingData.put("accZ", String.valueOf(mImuData.acc.z));
            swingData.put("gyroR", String.valueOf(mImuData.gyro.r));
            swingData.put("gyroP", String.valueOf(mImuData.gyro.p));
            swingData.put("gyroY", String.valueOf(mImuData.gyro.y));
            swingData.put("magX", String.valueOf(mImuData.mag.x));
            swingData.put("magY", String.valueOf(mImuData.mag.y));
            swingData.put("magZ", String.valueOf(mImuData.mag.z));
            swingData.put("pre_accX", String.valueOf(mOldImuData.acc.x));
            swingData.put("pre_accY", String.valueOf(mOldImuData.acc.y));
            swingData.put("pre_accZ", String.valueOf(mOldImuData.acc.z));
            swingData.put("pre_gyroR", String.valueOf(mOldImuData.gyro.r));
            swingData.put("pre_pre_gyroP", String.valueOf(mOldImuData.gyro.p));
            swingData.put("pre_gyroY", String.valueOf(mOldImuData.gyro.y));
            swingData.put("pre_magX", String.valueOf(mOldImuData.mag.x));
            swingData.put("pre_magY", String.valueOf(mOldImuData.mag.y));
            swingData.put("pre_magZ", String.valueOf(mOldImuData.mag.z));
            swingData.put("interval", String.valueOf(mImuData.ts.getTime() - mOldImuData.ts.getTime()));
            swingData.saveInBackground();
        }
    }

    public int getDataNum() {
        return mDataNum;
    }

    private boolean setDataNum() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SwingData");
        try {
            mDataNum = query.count();
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public void startAccess() {
        setService = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mImuData.ts.getTime() != 0) {
                    addSwingData();
                }
                setDataNum();
            }
        });
        setService.start();
    }
}
