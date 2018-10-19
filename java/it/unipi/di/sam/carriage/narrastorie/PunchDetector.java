package it.unipi.di.sam.carriage.narrastorie;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PunchDetector implements SensorEventListener
{

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private OnPunchListener onPunchListener;

    private final int Z_ACCELERATION_THRESHOLD = -15;
    private float gravityZ;

    public PunchDetector(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        startDetecting();
    }

    public void setOnPunchListener(OnPunchListener onPunchListener)
    {
        this.onPunchListener = onPunchListener;
    }

    public void startDetecting()
    {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopDetecting()
    {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) { return; }

        final float alpha = 0.8f;

        float rawZAcceleration = event.values[2];
        gravityZ = alpha * gravityZ + (1 - alpha) * rawZAcceleration;

        float filteredZAcceleration = rawZAcceleration - gravityZ;
        if (filteredZAcceleration < Z_ACCELERATION_THRESHOLD)
        {
            if (onPunchListener != null)
            {
                onPunchListener.onPunch();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    { }

    public interface OnPunchListener
    {
        void onPunch();
    }

}
