package com.example.owner.swingringer;

import android.app.ProgressDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    SwingDetector mSwingDetector = new SwingDetector();
    TextView mAccTextView;
    TextView mGyroTextView;
    TextView mMagTextView;
    TextView mCountTextView;
    int mSwingCount = 0;
    SoundRinger mSoundRinger;
    int[] mSoundIDList = {R.raw.swish, R.raw.coin, R.raw.magic};
    static ProgressDialog mWaitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                // 効果音に応じてアイコンを変更
                switch (mSoundRinger.switchSound()) {
                    case 0:
                        fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                        Toast.makeText(view.getContext(), "no sound", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        fab.setImageResource(android.R.drawable.ic_lock_idle_charging);
                        break;
                    case 2:
                        fab.setImageResource(android.R.drawable.ic_menu_compass);
                        break;
                    case 3:
                        fab.setImageResource(android.R.drawable.ic_popup_sync);
                        break;
                    default:
                        //DO NOTHING
                        break;
                }
                // 設定した音を鳴らす
                mSoundRinger.ring();
            }
        });

        // 待ち表示設定
        mWaitDialog = new ProgressDialog(this);
        mWaitDialog.setMessage("ネットワーク接続中...");
        mWaitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // センサの設定
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccTextView = (TextView) findViewById(R.id.accTextView);
        mGyroTextView = (TextView) findViewById(R.id.gyroTextView);
        mMagTextView = (TextView) findViewById(R.id.magTextView);
        mCountTextView = (TextView) findViewById(R.id.swingCountTextView);
        mCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWaitDialog.show();
                int totalDataNum = CloudAccessor.getDataNum();
                String msg;
                if (totalDataNum == -1) {
                    msg = "!! fail to get data from cloud DB !!";
                } else {
                    msg = "your total swing : " + String.valueOf(totalDataNum);
                }
                mWaitDialog.dismiss();
                Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });
        // Parserの作成
        CloudAccessor.initialize(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mCountTextView.setText(String.valueOf(mSwingCount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (acc != null) {
            mSensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_FASTEST);
        }
        Sensor gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyro != null) {
            mSensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST);
        }
        Sensor mag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mag != null) {
            mSensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_FASTEST);
        }
//        Sensor prs = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
//        if (acc != null) {
//            mSensorManager.registerListener(this, prs, SensorManager.SENSOR_DELAY_FASTEST);
//        }

        mSoundRinger = new SoundRinger(mSoundIDList, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mSoundRinger.release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        // センサ値の更新
        mSwingDetector.setSensorData(event);
        // センサ値表示の更新
        if (mSwingDetector.isNewAccData()) {
            mAccTextView.setText(mSwingDetector.getAccText());
        }
        if (mSwingDetector.isNewGyroData()) {
            mGyroTextView.setText(mSwingDetector.getGyroText());
        }
        if (mSwingDetector.isNewMagData()) {
            mMagTextView.setText(mSwingDetector.getMagText());
        }

        // スイング判定
        if (mSwingDetector.isSwing()) {
            mSoundRinger.ring();
            // スイングの情報をDBに保存
            CloudAccessor.addSwingData(mSwingDetector.getIMUData(), mSwingDetector.getOldIMUData());
            mSwingCount++;
            mCountTextView.setText(String.valueOf(mSwingCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        // DO NOTHING
    }
}
