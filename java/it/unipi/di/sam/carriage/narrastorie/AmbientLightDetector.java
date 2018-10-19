package it.unipi.di.sam.carriage.narrastorie;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AmbientLightDetector implements SensorEventListener
{

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private OnLowLightListener onLowLightListener;

    private final int LOW_LIGHT_THRESHOLD = 20; // LUX

    public AmbientLightDetector(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        startDetecting();
    }

    public void setOnLowLightListener(OnLowLightListener onLowLightListener)
    {
        this.onLowLightListener = onLowLightListener;
    }

    public void startDetecting()
    {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopDetecting()
    {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() != Sensor.TYPE_LIGHT) { return; }

        float luxes = event.values[0];
        if (luxes < LOW_LIGHT_THRESHOLD)
        {
            if (onLowLightListener != null)
            {
                onLowLightListener.onLowLight();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    { }

    public interface OnLowLightListener
    {
        void onLowLight();
    }
}
