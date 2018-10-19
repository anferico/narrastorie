package it.unipi.di.sam.carriage.narrastorie;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

public class BlowingDetector
{

    private int minimumBufferSize;
    private boolean isRecording;
    private boolean didBlow;
    private AudioRecord audioRecord;
    private RecordingTask recordingTask;
    private OnBlowListener onBlowListener;

    private static final int BLOW_VOLUME_THRESHOLD = 31000;

    public BlowingDetector()
    {
        this.minimumBufferSize = AudioRecord.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        );

        this.audioRecord = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            8000, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minimumBufferSize
        );

        startDetecting();
    }

    public void setOnBlowListener(OnBlowListener onBlowListener)
    {
        this.onBlowListener = onBlowListener;
    }

    public void startDetecting()
    {
        if (!isRecording)
        {
            audioRecord.startRecording();

            if (recordingTask == null || recordingTask.getStatus() == AsyncTask.Status.FINISHED)
            {
                recordingTask = new RecordingTask();
            }
            recordingTask.execute();
        }
        isRecording = true;
    }

    public void stopDetecting()
    {
        audioRecord.stop();
        isRecording = false;
    }

    public interface OnBlowListener
    {
        void onBlow();
    }

    public class RecordingTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            short[] buffer = new short[minimumBufferSize];

            while(isRecording)
            {
                audioRecord.read(buffer, 0, buffer.length);
                for (short s : buffer)
                {
                    int blowValue = Math.abs(s);
                    if (blowValue > BLOW_VOLUME_THRESHOLD)
                    {
                        didBlow = true;
                        return null;
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            if (didBlow && onBlowListener != null)
            {
                stopDetecting();
                didBlow = false;
                onBlowListener.onBlow();
            }
        }
    }

}
