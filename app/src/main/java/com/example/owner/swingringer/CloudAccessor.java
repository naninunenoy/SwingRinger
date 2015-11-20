package com.example.owner.swingringer;

import android.content.Context;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


/**
 * Created by owner on 2015/11/20.
 */
public class CloudAccessor {

    CloudAccessor() {
    }

    static private boolean isInitialized = false;
    static private int dataNum = 0;


    static private Thread setService = new Thread(new Runnable() {
        @Override
        public void run() {
            //CloudAccessor.setDataNum();
        }
    });

    static public void initialize(Context context) {
        // Parseの初期化
        if (isInitialized == false) {
            Parse.enableLocalDatastore(context);
            Parse.initialize(context, "TJONUf77Q1B8qmTATtyx9Bd0RBBnnio0GJIZdGDV", "ZdmfIdCNT0vlBN32Pq5KzHdzbTZdhywFmB4sGIFb");
            isInitialized = true;
            setDataNum();
            //setService.start();
        }
    }

    static public void addSwingData(IMUData data, IMUData oldData) {
        if (isInitialized) {
            ParseObject swingData = new ParseObject("SwingData");
            swingData.put("accX", String.valueOf(data.acc.x));
            swingData.put("accY", String.valueOf(data.acc.y));
            swingData.put("accZ", String.valueOf(data.acc.z));
            swingData.put("gyroR", String.valueOf(data.gyro.r));
            swingData.put("gyroP", String.valueOf(data.gyro.p));
            swingData.put("gyroY", String.valueOf(data.gyro.y));
            swingData.put("magX", String.valueOf(data.mag.x));
            swingData.put("magY", String.valueOf(data.mag.y));
            swingData.put("magZ", String.valueOf(data.mag.z));
            swingData.put("pre_accX", String.valueOf(oldData.acc.x));
            swingData.put("pre_accY", String.valueOf(oldData.acc.y));
            swingData.put("pre_accZ", String.valueOf(oldData.acc.z));
            swingData.put("pre_gyroR", String.valueOf(oldData.gyro.r));
            swingData.put("pre_pre_gyroP", String.valueOf(oldData.gyro.p));
            swingData.put("pre_gyroY", String.valueOf(oldData.gyro.y));
            swingData.put("pre_magX", String.valueOf(oldData.mag.x));
            swingData.put("pre_magY", String.valueOf(oldData.mag.y));
            swingData.put("pre_magZ", String.valueOf(oldData.mag.z));
            swingData.put("interval", String.valueOf(data.ts.getTime() - oldData.ts.getTime()));
            swingData.saveInBackground();
//            try {
//                setService.join();
//            } catch (InterruptedException e) {
//            }
        } else {
            throw new IllegalStateException();
        }
    }

    static public int getDataNum() {
        setDataNum();
        return dataNum;
    }

    static private boolean setDataNum() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SwingData");
        try {
            dataNum = query.count();
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
