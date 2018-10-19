package it.unipi.di.sam.carriage.narrastorie;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, InputDialogFragment.InputDialogListener
{
    public static String PACKAGE_NAME;

    private Button tellMeAStoryButton;
    private Button myStoriesButton;
    private Button aboutButton;

    private InputDialogFragment inputDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PACKAGE_NAME = getPackageName();

        tellMeAStoryButton = (Button) findViewById(R.id.button_tell_me_a_story);
        tellMeAStoryButton.setOnClickListener(this);

        myStoriesButton = (Button) findViewById(R.id.button_my_stories);
        myStoriesButton.setOnClickListener(this);

        aboutButton = (Button) findViewById(R.id.button_about);
        aboutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_tell_me_a_story:
                showInputDialog(
                    getResources().getString(R.string.enter_your_name),
                    getResources().getString(R.string.your_name)
                );
                break;

            case R.id.button_my_stories:
                startActivity(new Intent(this, MyStoriesActivity.class));
                break;

            case R.id.button_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;

            default:
                break;
        }
    }

    public void showInputDialog(String message, String inputHint)
    {
        inputDialogFragment = InputDialogFragment.newInstance(message, inputHint);
        inputDialogFragment.show(getFragmentManager(), "InputDialog");
    }

    @Override
    public void onInputProvided(String input)
    {
        Intent intent = new Intent(this, CharactersChoiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("user_name", input);

        startActivity(intent);
    }
}
