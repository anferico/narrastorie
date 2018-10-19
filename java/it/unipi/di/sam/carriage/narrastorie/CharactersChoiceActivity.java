package it.unipi.di.sam.carriage.narrastorie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

public class CharactersChoiceActivity extends Activity implements View.OnClickListener
{

    private Spinner secondCharacterSpinner;
    private Spinner thirdCharacterSpinner;
    private Spinner fourthCharacterSpinner;
    private Button confirmCharactersButton;

    private String mainCharacterName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters_choice);

        Cursor cursor = getContentResolver().query(
            ContactsContract.Data.CONTENT_URI,
            null,
            ContactsContract.Data.MIMETYPE + " = ? AND (" + ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME + " IS NOT NULL)",
            new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE },
            null
        );

        ContactsAdapter contactsAdapter = new ContactsAdapter(this, cursor, 0);

        mainCharacterName = getIntent().getStringExtra("user_name");

        secondCharacterSpinner = (Spinner) findViewById(R.id.second_character_spinner);
        secondCharacterSpinner.setAdapter(contactsAdapter);

        thirdCharacterSpinner = (Spinner) findViewById(R.id.third_character_spinner);
        thirdCharacterSpinner.setAdapter(contactsAdapter);

        fourthCharacterSpinner = (Spinner) findViewById(R.id.fourth_character_spinner);
        fourthCharacterSpinner.setAdapter(contactsAdapter);

        confirmCharactersButton = (Button) findViewById(R.id.button_confirm_characters);
        confirmCharactersButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.button_confirm_characters)
        {
            ArrayList<String> chosenCharacters = new ArrayList<>();

            chosenCharacters.add(mainCharacterName);

            View secondCharacterSpinnerView = secondCharacterSpinner.getSelectedView();
            TextView secondCharacterTextView =
                    (TextView) secondCharacterSpinnerView.findViewById(R.id.character_name_label);
            chosenCharacters.add(String.valueOf(secondCharacterTextView.getText()));

            View thirdCharacterSpinnerView = thirdCharacterSpinner.getSelectedView();
            TextView thirdCharacterTextView =
                    (TextView) thirdCharacterSpinnerView.findViewById(R.id.character_name_label);
            chosenCharacters.add(String.valueOf(thirdCharacterTextView.getText()));

            View fourthCharacterSpinnerView = fourthCharacterSpinner.getSelectedView();
            TextView fourthCharacterTextView =
                    (TextView) fourthCharacterSpinnerView.findViewById(R.id.character_name_label);
            chosenCharacters.add(String.valueOf(fourthCharacterTextView.getText()));

            Intent i = new Intent(this, StorytellingActivity.class);
            i.putStringArrayListExtra("chosenCharacters", chosenCharacters);
            startActivity(i);
        }
    }

    public class ContactsAdapter extends CursorAdapter
    {

        public ContactsAdapter(Context context, Cursor cursor, int flags)
        {
            super(context, cursor, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            return getLayoutInflater().inflate(R.layout.spinner_template, parent, false);
        }

        @Override
        public View newDropDownView(Context context, Cursor cursor, ViewGroup parent)
        {
            return getLayoutInflater().inflate(R.layout.spinner_dropdown_template, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            if (view.getId() == R.id.spinner_template)
            {
                TextView characterNameLabel = (TextView) view.findViewById(R.id.character_name_label);
                characterNameLabel.setText(cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));

                ImageView characterThumbnail = (ImageView) view.findViewById(R.id.character_thumbnail);

                String thumbnailUri = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.StructuredName.PHOTO_URI));
                if (thumbnailUri != null)
                {
                    characterThumbnail.setImageURI(Uri.parse(thumbnailUri));
                }
                else
                {
                    characterThumbnail.setImageResource(R.drawable.account_circle_black_192x192);
                }
            }
            else if (view.getId() == R.id.spinner_dropdown_template)
            {
                TextView characterDropDownNameLabel =
                        (TextView) view.findViewById(R.id.character_dropdown_name_label);
                characterDropDownNameLabel.setText(cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));

                ImageView characterDropDownThumbnail =
                        (ImageView) view.findViewById(R.id.character_dropdown_thumbnail);

                String thumbnailUri = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredName.PHOTO_URI));
                if (thumbnailUri != null)
                {
                    characterDropDownThumbnail.setImageURI(Uri.parse(thumbnailUri));
                }
                else
                {
                    characterDropDownThumbnail.setImageResource(R.drawable.ic_account_circle_black_48dp);
                }

            }

        }

    }

}
