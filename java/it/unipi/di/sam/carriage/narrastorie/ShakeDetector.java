package it.unipi.di.sam.carriage.narrastorie;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener
{

    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD = 2000;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private OnShakeListener onShakeListener;

    public ShakeDetector(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        startDetecting();
    }

    public void setOnShakeListener(OnShakeListener onShakeListener)
    {
        this.onShakeListener = onShakeListener;
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

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastUpdate) > 100)
        {
            long diffTime = (currentTime - lastUpdate);
            lastUpdate = currentTime;

            float speed = Math.abs(x - lastX + y - lastY + z - lastZ) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD)
            {
                if (onShakeListener != null)
                {
                    onShakeListener.onShake();
                }
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    { }

    public interface OnShakeListener
    {
        void onShake();
    }
}
