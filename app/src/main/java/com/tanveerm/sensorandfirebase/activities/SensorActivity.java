package com.tanveerm.sensorandfirebase.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tanveerm.sensorandfirebase.R;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private TextView mTextViewResults;

    private SensorManager sensorManager;

    private Sensor mAccelerometerSensor;
    private Sensor mProximitySensor;
    private Sensor mLightSensor;
    private Sensor mStepCounterSensor;
    private Sensor mTempSensor;
    private Sensor mGyroscopeSensor;

    private int mCurrentSensor;

    private long mLastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mTextViewResults = findViewById(R.id.textViewResult);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mStepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mGyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

    }

    public boolean checkSensorAvailability(int sensorType) {
        boolean isSensor = false;
        if (sensorManager.getDefaultSensor(sensorType) != null) {
            isSensor = true;
        }
        return isSensor;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.e("TAG", "Sensor: " + mCurrentSensor);

        if (sensorEvent.sensor.getType() == mCurrentSensor) {

            if (mCurrentSensor == Sensor.TYPE_LIGHT) {
                float valueZ  = sensorEvent.values[0];
                mTextViewResults.setText("Brightness: " + valueZ);
            } else if (mCurrentSensor == Sensor.TYPE_PROXIMITY) {
                float distance = sensorEvent.values[0];
                mTextViewResults.setText("Proximity: " + distance);
            } else if (mCurrentSensor == Sensor.TYPE_ACCELEROMETER) {
                /* sensorEvent.values[]
                 * [0] - Acceleration force along the X axis
                 * [1] - Acceleration force along the y axis
                 * [2] - Acceleration force along the z axis
                 */
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                long curTime = System.currentTimeMillis();

                if ((curTime - mLastUpdate) > 100) {
                    long diffTime = (curTime - mLastUpdate);
                    mLastUpdate = curTime;

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                    
                    if (speed > SHAKE_THRESHOLD) {
                        Toast.makeText(this, "(Accelerometer)Your phone just shook", Toast.LENGTH_SHORT).show();
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            } else if (mCurrentSensor == Sensor.TYPE_GYROSCOPE) {
                if (sensorEvent.values[2] > 0.5f) {
                    mTextViewResults.setText("Anti Clock");
                } else if (sensorEvent.values[2] < -0.5f) {
                    mTextViewResults.setText("Clock");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void accelerometerSensorOnClick(View view) {
        if (checkSensorAvailability(Sensor.TYPE_ACCELEROMETER)) {
            mCurrentSensor = Sensor.TYPE_ACCELEROMETER;
        } else {
            mTextViewResults.setText("Accelerometer is not available");
        }

    }

    public void proximitySensorOnClick(View view) {
        if (checkSensorAvailability(Sensor.TYPE_PROXIMITY)) {
            mCurrentSensor = Sensor.TYPE_PROXIMITY;
        } else {
            mTextViewResults.setText("Promixity Sensor is not available");
        }

    }

    public void gyroscopeSensorOnClick(View view) {
        if (checkSensorAvailability(Sensor.TYPE_GYROSCOPE)) {
            mCurrentSensor = Sensor.TYPE_GYROSCOPE;
        } else {
            mTextViewResults.setText("Gyroscope Sensor is not available");
        }

    }

    public void lightSensorOnClick(View view) {
        if (checkSensorAvailability(Sensor.TYPE_LIGHT)) {
            mCurrentSensor = Sensor.TYPE_LIGHT;
        } else {
            mTextViewResults.setText("Light Sensor is not available");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Register a listener for the sensor.
        sensorManager.registerListener(this, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mLightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mProximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mGyroscopeSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /* unregister the sensor event listener,
         *  to save battery power because system will not
         *  disable automatically when the screen turns off*/
        sensorManager.unregisterListener(this);
    }
}
