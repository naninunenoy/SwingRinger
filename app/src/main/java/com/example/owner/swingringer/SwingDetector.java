package com.example.owner.swingringer;

import android.hardware.SensorEvent;
import android.hardware.Sensor;

import java.lang.Math;
import java.sql.Timestamp;


/**
 * Created by owner on 2015/11/14.
 */
public class SwingDetector {
    private IMUData mSensorData;
    private IMUData mPreviousSensorData;
    private final int PERIOD = 100; //[ms]
    private long mPreviousAccTimestamp;
    private final float OLD_DATA_WEIGHT = 0.8f;
    private final float FAST_MOVE_GYRO = 0.6f;
    private final float FAST_MOVE_ACC = 0.8f;
    private final long SWING_WAIT_MS = 600;

    SwingDetector() {
        mSensorData = new IMUData();
        mPreviousSensorData = new IMUData();
        mPreviousAccTimestamp = 0;
    }

    public void setSensorData(SensorEvent sensorEnvent) {
        // 現在時刻取得
        long nowTime = System.currentTimeMillis();
        switch (sensorEnvent.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            if (nowTime - mSensorData.acc.ts.getTime() >= PERIOD) {
                mPreviousSensorData.acc = (IMUData.LinerData) mSensorData.acc.clone();
                mPreviousSensorData.ts = (Timestamp) mSensorData.acc.ts.clone();
                mSensorData.setAcc(sensorEnvent.values, mPreviousSensorData.acc, OLD_DATA_WEIGHT);
            }
            break;
        case Sensor.TYPE_GYROSCOPE:
            if (nowTime - mSensorData.gyro.ts.getTime() >= PERIOD) {
                mPreviousSensorData.gyro = (IMUData.RotateData) mSensorData.gyro.clone();
                mPreviousSensorData.ts = (Timestamp) mSensorData.gyro.ts.clone();
                mSensorData.setGyro(sensorEnvent.values);
            }
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
            if (nowTime - mSensorData.mag.ts.getTime() >= PERIOD) {
                mPreviousSensorData.mag = (IMUData.LinerData) mSensorData.mag.clone();
                mPreviousSensorData.ts = (Timestamp) mSensorData.mag.ts.clone();
                mSensorData.setMag(sensorEnvent.values, mPreviousSensorData.mag, OLD_DATA_WEIGHT);
            }
            break;
        default:
            // DO NOTHING
            break;
        }
    }

    protected boolean isSwing(){
        if (mSensorData.acc.ts.getTime() > mPreviousAccTimestamp + SWING_WAIT_MS) {
            if (isFastMoveByGyro()) {
                if (isStopToFastMoveByAcc(mSensorData.acc.x, mPreviousSensorData.acc.x)) {
                    mPreviousAccTimestamp = mSensorData.acc.ts.getTime();
                    return true;
                } else if (isStopToFastMoveByAcc(mSensorData.acc.y, mPreviousSensorData.acc.y)) {
                    mPreviousAccTimestamp = mSensorData.acc.ts.getTime();
                    return true;
                } else if (isStopToFastMoveByAcc(mSensorData.acc.z, mPreviousSensorData.acc.z)) {
                    mPreviousAccTimestamp = mSensorData.acc.ts.getTime();
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isNewIMUData(){
        return (mSensorData.ts.compareTo(mPreviousSensorData.ts) > 0);
    }

    protected boolean isNewAccData(){
        return (mSensorData.acc.ts.compareTo(mPreviousSensorData.acc.ts) > 0);
    }

    protected boolean isNewGyroData(){
        return (mSensorData.gyro.ts.compareTo(mPreviousSensorData.gyro.ts) > 0);
    }

    protected boolean isNewMagData(){
        return (mSensorData.mag.ts.compareTo(mPreviousSensorData.mag.ts) > 0);
    }

    protected String getAccText(){
        return "<acc>\n"
                + mSensorData.acc.toString()
                + "</acc>\n";
    }

    protected String getGyroText(){
        return "<gyro>\n"
                + mSensorData.gyro.toString()
                + "</gyro>\n";
    }

    protected String getMagText() {
        return "<mag>\n"
                + mSensorData.mag.getNormalizedData().toString()
                + "</mag>\n";
    }

    private boolean isFastMoveByGyro() {
        return (Math.abs(mSensorData.gyro.r) > FAST_MOVE_GYRO
                || Math.abs(mSensorData.gyro.p) > FAST_MOVE_GYRO
                || Math.abs(mSensorData.gyro.y) > FAST_MOVE_GYRO);
    }

    private boolean isStopToFastMoveByAcc(final float data, final float oldData) {
        if (((oldData <= 0.0f) && (0.0f < data)) || ((data <= 0.0f) && (0.0f < oldData))) {
          if (Math.abs(data - oldData) >  FAST_MOVE_ACC) {
              return true;
          }
        }
        return false;
    }

    protected IMUData getIMUData(){ return mSensorData; }
    protected IMUData getOldIMUData(){ return mPreviousSensorData; }
}
