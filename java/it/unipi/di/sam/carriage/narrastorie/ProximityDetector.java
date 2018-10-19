package it.unipi.di.sam.carriage.narrastorie;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ProximityDetector implements SensorEventListener
{

    private SensorManager sensorManager;
    private Sensor proximitySensor;

    private ProximityListener proximityListener;

    public ProximityDetector(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        startDetecting();
    }

    public void setProximityListener(ProximityListener proximityListener)
    {
        this.proximityListener = proximityListener;
    }

    public void startDetecting()
    {
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopDetecting()
    {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.values[0] == 0)
        {
            if (proximityListener != null)
            {
                proximityListener.onNear();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    { }

    public interface ProximityListener
    {
        void onNear();
    }
}
