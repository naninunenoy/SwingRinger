package com.example.owner.swingringer;

import java.sql.Timestamp;
import java.lang.Math;

/**
 * Created by owner on 2015/11/14.
 */
public class IMUData implements Cloneable {
    protected class LinerData implements Cloneable{
        Timestamp ts;
        float x;
        float y;
        float z;

        LinerData() {
            ts = new Timestamp(0);
            x = 0.0f;
            y = 0.0f;
            z = 0.0f;
        }
        LinerData(final Timestamp ts_in, final float x_in, final float y_in,final float z_in) {
            ts = ts_in;
            x = x_in;
            y = y_in;
            z = z_in;
        }
        protected float getNorm(){
            return (float)Math.sqrt(x*x + y*y + z*z);
        }
        protected LinerData getNormalizedData(){
            return new LinerData(ts, x/getNorm(), y/getNorm(), z/getNorm());
        }
        public String toString(){
            return  "  <ts>" + ts + "</ts>\n"
                    + "  <x>" + x + "</x>\n"
                    + "  <y>" + y + "</y>\n"
                    + "  <z>" + z + "</z>\n";
        }
        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e.toString());
            }
        }
    }

    protected class RotateData  implements Cloneable{
        Timestamp ts;
        float r;    /** <@brief ロール角 */
        float p;    /** <@brief ピッチ角 */
        float y;    /** <@brief ヨー角 */

        RotateData() {
            ts = new Timestamp(0);
            r = 0.0f;
            p = 0.0f;
            y = 0.0f;
        }
        RotateData(final Timestamp ts_in, final float r_in, final float p_in,final float y_in) {
            ts = ts_in;
            r = r_in;
            p = p_in;
            y = y_in;
        }
        protected RotateData getDeg() {
            final float rad2deg = 180.0f/(float)Math.PI;
            return new RotateData(ts, r*rad2deg, p*rad2deg, y*rad2deg);
        }
        public String toString(){
            return  "  <ts>" + ts + "</ts>\n"
                    + "  <roll>" + r + "</roll>\n"
                    + "  <pitch>" + p + "</pitch>\n"
                    + "  <yaw>" + y + "</yaw>\n";
        }
        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e.toString());
            }
        }
    }

    protected Timestamp ts;
    protected LinerData acc;
    protected RotateData gyro;
    protected LinerData mag;

    IMUData() {
        ts = new Timestamp(0);
        acc = new LinerData();
        gyro = new RotateData();
        mag = new LinerData();
    }
    public void setAcc(final float[] acc_in) {
        Timestamp tmp = new Timestamp(System.currentTimeMillis());
        ts = tmp;
        acc = new LinerData(tmp, acc_in[0], acc_in[1],  acc_in[2]);
    }
    public void setAcc(final float[] acc_in, final LinerData old, final float weight) {
        float[] filteredVal = new float[3];
        filteredVal[0] = weight*old.x + (1.0f - weight)*acc_in[0];
        filteredVal[1] = weight*old.y + (1.0f - weight)*acc_in[1];
        filteredVal[2] = weight*old.z + (1.0f - weight)*acc_in[2];
        setAcc(filteredVal);
    }
    public void setGyro(final float[] gyro_in) {
        Timestamp tmp = new Timestamp(System.currentTimeMillis());
        ts = tmp;
        gyro = new RotateData(tmp, gyro_in[0], gyro_in[1],  gyro_in[2]);
    }
    public void setGyro(final float[] gyro_in, final RotateData old, final float weight) {
        float[] filteredVal = new float[3];
        filteredVal[0] = weight*old.r + (1.0f - weight)*gyro_in[0];
        filteredVal[1] = weight*old.p + (1.0f - weight)*gyro_in[1];
        filteredVal[2] = weight*old.y + (1.0f - weight)*gyro_in[2];
        setGyro(filteredVal);
    }
    public void setMag(final float[] mag_in) {
        Timestamp tmp = new Timestamp(System.currentTimeMillis());
        ts = tmp;
        mag = new LinerData(tmp, mag_in[0], mag_in[1],  mag_in[2]);
    }
    public void setMag(final float[] mag_in, final LinerData old, final float weight) {
        float[] filteredVal = new float[3];
        filteredVal[0] = weight*old.x + (1.0f - weight)*mag_in[0];
        filteredVal[1] = weight*old.y + (1.0f - weight)*mag_in[1];
        filteredVal[2] = weight*old.z + (1.0f - weight)*mag_in[2];
        setMag(filteredVal);
    }
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }
}
