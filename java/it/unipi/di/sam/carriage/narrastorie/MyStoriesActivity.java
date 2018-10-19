package it.unipi.di.sam.carriage.narrastorie;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

public class MyStoriesActivity extends Activity
        implements View.OnClickListener, MediaPlayer.OnCompletionListener,
        ConfirmationDialogFragment.ConfirmationDialogListener
{

    private TextView placeholderMessage;
    private ListView myStoriesListView;

    private MediaPlayer mediaPlayer;
    private View lastClickedButton;
    private View clickedDeleteButton;
    private boolean audioPaused;

    private StoriesDatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private ConfirmationDialogFragment confirmationDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stories);

        databaseHelper = new StoriesDatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        Cursor cursor = databaseHelper.getDefaultCursor();

        if (cursor.moveToFirst())
        {
            // Nascondo il messaggio he indica la non disponibilità di storie
            placeholderMessage = (TextView) findViewById(R.id.my_stories_unavailable_label);
            placeholderMessage.setVisibility(View.GONE);

            // Mostro la listview
            myStoriesListView = (ListView) findViewById(R.id.my_stories_listview);
            myStoriesListView.setVisibility(View.VISIBLE);
            myStoriesListView.setAdapter(new MyStoriesAdapter(this, cursor, 0));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mediaPlayer == null)
        {
            mediaPlayer = new MediaPlayer();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (lastClickedButton != null)
        {
            setPlayIcon(lastClickedButton);
            lastClickedButton = null;
        }

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void playAudio(String filepath)
    {
        try
        {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filepath);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnCompletionListener(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        mediaPlayer.start();
    }

    private void pauseAudio()
    {
        mediaPlayer.pause();
    }

    private void resumeAudio()
    {
        mediaPlayer.start();
    }

    private void stopAudio()
    {
        mediaPlayer.stop();
    }

    @Override
    public void onClick(View clickedButton)
    {
        if (clickedButton.getId() == R.id.my_stories_row_play_button)
        {
            // Cliccato un qualche bottone 'play' o 'pausa'

            if (lastClickedButton == null)
            {
                // Cliccato un bottone 'play' mentre nessun audio era in riproduzione

                // Cambio l'icona del bottone premuto
                setPauseIcon(clickedButton);

                // Disabilito il bottone per cancellare l'audio
                disableDeleteButton(clickedButton);

                // Riproduco l'audio
                playAudio(getAudioFilePath(clickedButton));

                lastClickedButton = clickedButton;
            }
            else
            {
                if (clickedButton == lastClickedButton)
                {
                    if (audioPaused)
                    {
                        // Cliccato play su un audio messo in pausa in precedenza

                        // Cambio l'icona del bottone premuto
                        setPauseIcon(clickedButton);

                        // Disabilito il bottone per cancellare l'audio
                        disableDeleteButton(clickedButton);

                        // Faccio ripartire l'audio
                        resumeAudio();

                        audioPaused = false;
                    }
                    else
                    {
                        // Cliccato il bottone pausa sull' audio in riproduzione

                        // Metto in pausa la riproduzione
                        pauseAudio();

                        // Cambio l'icona del bottone
                        setPlayIcon(clickedButton);

                        // Abilito il bottone per cancellare l'audio
                        enableDeleteButton(clickedButton);

                        audioPaused = true;
                    }
                }
                else
                {
                    // Cliccato il bottone play su un audio non in riproduzione

                    // Interrompo la riproduzione
                    stopAudio();

                    // Cambio l'icona del bottone relativo all'audio che era in riproduzione
                    setPlayIcon(lastClickedButton);

                    // Abilito il bottone per cancellare l'audio
                    enableDeleteButton(lastClickedButton);


                    // Cambio l'icona del bottone premuto
                    setPauseIcon(clickedButton);

                    // Disabilito il bottone per cancellare l'audio
                    disableDeleteButton(clickedButton);

                    // Faccio partire il nuovo audio
                    playAudio(getAudioFilePath(clickedButton));

                    lastClickedButton = clickedButton;
                }
            }
        }
        else if (clickedButton.getId() == R.id.my_stories_row_delete_button)
        {
            // Cliccato un qualche bottone 'elimina'

            // Salvo il riferimento al bottone cliccato per sapere poi quale storia eliminare
            clickedDeleteButton = clickedButton;

            showConfirmationDialog(getResources().getString(R.string.wanna_delete_story));
        }

    }

    private String getAudioFilePath(View clickedButton)
    {
        TextView storyFilepathTextView =
            (TextView) ((View) clickedButton.getParent()).findViewById(R.id.my_stories_row_filepath);
        return String.valueOf(storyFilepathTextView.getText());
    }

    private void setPlayIcon(View button)
    {
        ImageButton imageButton = (ImageButton) button;
        imageButton.setImageResource(R.drawable.play);
    }

    private void setPauseIcon(View button)
    {
        ImageButton imageButton = (ImageButton) button;
        imageButton.setImageResource(R.drawable.pause);
    }

    private void enableDeleteButton(View clickedButton)
    {
        ImageButton deleteButton =
                (ImageButton) ((View) clickedButton.getParent()).findViewById(R.id.my_stories_row_delete_button);
        deleteButton.setEnabled(true);
    }

    private void disableDeleteButton(View clickedButton)
    {
        ImageButton deleteButton =
            (ImageButton) ((View) clickedButton.getParent()).findViewById(R.id.my_stories_row_delete_button);
        deleteButton.setEnabled(false);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        // Cambio l'icona del bottone che era stato premuto per far partire la riproduzione
        setPlayIcon(lastClickedButton);

        // Abilito il bottone per cancellare l'audio
        enableDeleteButton(lastClickedButton);

        // Riavvolgo
        mediaPlayer.seekTo(0);

        lastClickedButton = null;
    }

    public void showConfirmationDialog(String message)
    {
        confirmationDialogFragment = ConfirmationDialogFragment.newInstance(message);
        confirmationDialogFragment.show(getFragmentManager(), "ConfirmationDialog");
    }

    @Override
    public void onConfirm(boolean confirmed)
    {
        if (confirmed)
        {
            TextView storyIDTextView =
                (TextView) ((View) clickedDeleteButton.getParent()).findViewById(R.id.my_stories_row_story_id);

            // Elimino dal database la storia
            database.delete(
                StoriesDatabaseHelper.TABLE_STORIES,
                StoriesDatabaseHelper.STORY_ID + " = ?",
                new String[] { String.valueOf(storyIDTextView.getText()) }
            );


            CursorAdapter listViewAdapter = (CursorAdapter) myStoriesListView.getAdapter();

            // Rinfresco la listview
            listViewAdapter.changeCursor(databaseHelper.getDefaultCursor());
            listViewAdapter.notifyDataSetChanged();

            if (myStoriesListView.getCount() == 0)
            {
                placeholderMessage.setVisibility(View.VISIBLE);
            }

            clickedDeleteButton = null;
        }
    }

    public class MyStoriesAdapter extends CursorAdapter
    {

        public MyStoriesAdapter(Context context, Cursor cursor, int flags)
        {
            super(context, cursor, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            return getLayoutInflater().inflate(R.layout.my_stories_row_template, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            TextView titleLabel = (TextView) view.findViewById(R.id.my_stories_row_title_label);
            TextView subtitleLabel = (TextView) view.findViewById(R.id.my_stories_row_subtitle_label);
            TextView storyIDLabel = (TextView) view.findViewById(R.id.my_stories_row_story_id);
            TextView filepathLabel = (TextView) view.findViewById(R.id.my_stories_row_filepath);
            ImageButton playButton = (ImageButton) view.findViewById(R.id.my_stories_row_play_button);
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.my_stories_row_delete_button);

            titleLabel.setText(cursor.getString(cursor.getColumnIndex(StoriesDatabaseHelper.STORY_NAME)));
            subtitleLabel.setText(cursor.getString(cursor.getColumnIndex(StoriesDatabaseHelper.STORY_CHARACTERS)));
            storyIDLabel.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(StoriesDatabaseHelper.STORY_ID))));
            filepathLabel.setText(cursor.getString(cursor.getColumnIndex(StoriesDatabaseHelper.STORY_FILEPATH)));
            playButton.setOnClickListener(MyStoriesActivity.this);
            deleteButton.setOnClickListener(MyStoriesActivity.this);
        }
    }
}


