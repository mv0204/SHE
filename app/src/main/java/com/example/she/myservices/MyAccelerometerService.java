package com.example.she.myservices;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyAccelerometerService extends Service implements SensorEventListener {

    public static final String SHAKE_UPDATE_ACTION = "shake_update";
    Sensor accelerometerSensor;
    SensorManager sensorManager;
    float xAxis, yAxis, zAxis, xLastAxis, yLastAxis, zLastAxis;
    float shakeThreshold = 10.0f;
    boolean firstUpdate = true;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        updateAxisValues(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);


        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt(xAxis * xAxis + yAxis * yAxis + zAxis * zAxis);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;
        if (mAccel > 12) {
            sendShakeBroadcast(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);

        }


    }



    private void updateAxisValues(float newXAxis, float newYAxis, float newZAxis) {

        if (firstUpdate) {
            xLastAxis = newXAxis;
            yLastAxis = newYAxis;
            zLastAxis = newZAxis;
            firstUpdate = false;
        } else {
            xLastAxis = xAxis;
            yLastAxis = yAxis;
            zLastAxis = zAxis;
        }
        xAxis = newXAxis;
        yAxis = newYAxis;
        zAxis = newZAxis;


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void sendShakeBroadcast(float value, float v, float value1) {
        Intent i = new Intent(SHAKE_UPDATE_ACTION);
        i.putExtra("a",value+"");
        i.putExtra("b",v+"");
        i.putExtra("c",value1+"");
        sendBroadcast(i);
    }
}
