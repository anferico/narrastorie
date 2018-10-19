package it.unipi.di.sam.carriage.narrastorie;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StorytellingActivity extends Activity
    implements SwipeDialogFragment.SwipeDialogListener,
    InteractionDialogFragment.InteractionDialogListener,
    ConfirmationDialogFragment.ConfirmationDialogListener,
    InputDialogFragment.InputDialogListener, Runnable,
    TextToSpeech.OnInitListener
{
    private TextView currentStoryFragmentText;

    private Story story;
    private StoryFragment currentFragment;
    private List<String> storyCharacters;
    private TextToSpeech textToSpeech;
    private boolean speechInterrupted;
    private boolean savingAudioFile;

    private SwipeDialogFragment swipeDialogFragment;
    private InteractionDialogFragment interactionDialogFragment;
    private ConfirmationDialogFragment confirmationDialogFragment;
    private InputDialogFragment inputDialogFragment;

    private HashMap<String, String> normalUtteranceParametersMap;
    private HashMap<String, String> savingUtteranceParametersMap;

    private String NORMAL_UTTERANCE_ID = "normal_utterance_id";
    private String SAVING_UTTERANCE_ID = "saving_utterance_id";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storytelling);

        // Ottengo i personaggi della storia che andrò a raccontare
        Intent receivedIntent = getIntent();
        storyCharacters = receivedIntent.getStringArrayListExtra("chosenCharacters");

        story = new Story(getResources(), storyCharacters);

        currentFragment = story.firstFragment();

        currentStoryFragmentText = (TextView) findViewById(R.id.story_sheet_overlying_text);
        currentStoryFragmentText.setText(currentFragment.getText());

        setupTTS();
    }

    private void setupTTS()
    {
        textToSpeech = new TextToSpeech(getApplicationContext(), this);

        normalUtteranceParametersMap = new HashMap<>();
        normalUtteranceParametersMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, NORMAL_UTTERANCE_ID);
    }

    private void speakStoryFragment(StoryFragment storyFragment)
    {
        textToSpeech.speak(storyFragment.getText(), TextToSpeech.QUEUE_FLUSH, normalUtteranceParametersMap);
    }

    private void onSpeechEnded()
    {
        switch (currentFragment.getEndType())
        {
            case "Continue":
                runOnUiThread(this);
                break;

            case "Swipe":
                showSwipeDialog();
                break;

            case "Interaction":
                showInteractionDialog();
                break;

            case "End":
                showConfirmationDialog();
                break;
        }
    }

    private void showSwipeDialog()
    {
        swipeDialogFragment = SwipeDialogFragment.newInstance(currentFragment);
        swipeDialogFragment.show(getFragmentManager(), "SwipeDialog");
    }

    private void showInteractionDialog()
    {
        interactionDialogFragment = InteractionDialogFragment.newInstance(currentFragment);
        interactionDialogFragment.show(getFragmentManager(), "InteractionDialog");
    }

    private void showConfirmationDialog()
    {
        confirmationDialogFragment = ConfirmationDialogFragment.newInstance(
            getResources().getString(R.string.wanna_save_story)
        );
        confirmationDialogFragment.show(getFragmentManager(), "ConfirmationDialog");
    }

    private void showInputDialog()
    {
        inputDialogFragment = InputDialogFragment.newInstance(
            getResources().getString(R.string.enter_story_name),
            getResources().getString(R.string.title)
        );
        inputDialogFragment.show(getFragmentManager(), "InputDialog");
    }

    @Override
    protected void onPause()
    {
        if (textToSpeech != null)
        {
            if (textToSpeech.isSpeaking())
            {
                textToSpeech.stop();
                speechInterrupted = true;
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (speechInterrupted)
        {
            if (currentFragment != null)
            {
                speakStoryFragment(currentFragment);
            }
            speechInterrupted = false;
        }
    }

    @Override
    protected void onDestroy()
    {
        if (textToSpeech != null)
        {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status)
    {
        if (status != TextToSpeech.ERROR)
        {
            textToSpeech.setLanguage(Locale.ITALY);
            textToSpeech.setPitch(1.0f);
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onStart(String utteranceId)
                { }

                @Override
                public void onDone(String utteranceId)
                {
                    if (utteranceId.equals(NORMAL_UTTERANCE_ID))
                    {
                        // La voce si è placata

                        if (!speechInterrupted)
                        {
                            // onDone() NON è stato chiamato a seguito dell'interruzione
                            // della voce, ma al completamento del discorso
                            onSpeechEnded();
                        }
                    }
                    else if (utteranceId.equals(SAVING_UTTERANCE_ID))
                    {
                        // Il salvataggio del file audio è stato completato

                        // Torno alla pagina principale
                        Intent intent = new Intent(StorytellingActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        // Mostro il toast
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(
                                    StorytellingActivity.this,
                                    R.string.story_saved,
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(String utteranceId)
                { }
            });
        }

        speakStoryFragment(currentFragment);
    }

    @Override
    public void onSwipePerformed(boolean leftSwipe)
    {
        currentFragment = story.nextFragment(currentFragment, leftSwipe);
        currentStoryFragmentText.setText(currentFragment.getText());
        speakStoryFragment(currentFragment);
    }

    @Override
    public void onInteractionCompleted()
    {
        currentFragment = story.nextFragment(currentFragment);
        currentStoryFragmentText.setText(currentFragment.getText());
        speakStoryFragment(currentFragment);
    }

    @Override
    public void onConfirm(boolean confirmed)
    {
        if (confirmed)
        {
            showInputDialog();
        }
        else
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onInputProvided(String input)
    {
        StoriesDatabaseHelper databaseHelper = new StoriesDatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        String storyFilepath = getFilesDir().getPath() + "/" + input;

        ContentValues values = new ContentValues();
        values.put(StoriesDatabaseHelper.STORY_NAME, input);
        values.put(StoriesDatabaseHelper.STORY_CHARACTERS, TextUtils.join(", ", storyCharacters));
        values.put(StoriesDatabaseHelper.STORY_FILEPATH, storyFilepath);

        try
        {
            // Salvo le informazioni della storia nel DB
            database.insertOrThrow(
                StoriesDatabaseHelper.TABLE_STORIES,
                null,
                values
            );

            savingUtteranceParametersMap = new HashMap<>();
            savingUtteranceParametersMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, SAVING_UTTERANCE_ID);

            // Salvo il file audio su disco
            textToSpeech.synthesizeToFile(
                story.getFullStoryText(),
                savingUtteranceParametersMap,
                storyFilepath
            );

            Toast.makeText(
                StorytellingActivity.this,
                R.string.saving_story,
                Toast.LENGTH_LONG
            ).show();

        }
        catch (SQLException e)
        {
            // Violato il vincolo di unicità per il nome delle storie

            Toast.makeText(this, R.string.story_already_present, Toast.LENGTH_LONG).show();
            showInputDialog();
            return;
        }
    }

    @Override
    public void run()
    {
        currentFragment = story.nextFragment(currentFragment);
        currentStoryFragmentText.setText(currentFragment.getText());
        speakStoryFragment(currentFragment);
    }
}
