package it.unipi.di.sam.carriage.narrastorie;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class InputDialogFragment extends DialogFragment implements View.OnClickListener
{

    private TextView inputDialogMessage;
    private EditText inputDialogInputBox;
    private ImageButton inputDialogConfirmButton;

    public InputDialogFragment()
    {

    }

    public static InputDialogFragment newInstance(String message, String inputHint)
    {
        InputDialogFragment inputDialogFragment = new InputDialogFragment();

        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("inputHint", inputHint);

        inputDialogFragment.setArguments(args);
        return inputDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.input_dialog_template, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        inputDialogMessage = (TextView) view.findViewById(R.id.input_dialog_message);
        inputDialogMessage.setText(args.getString("message"));

        inputDialogInputBox = (EditText) view.findViewById(R.id.input_dialog_input_box);
        inputDialogInputBox.setHint(args.getString("inputHint"));

        inputDialogConfirmButton = (ImageButton) view.findViewById(R.id.input_dialog_confirm_button);
        inputDialogConfirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.input_dialog_confirm_button)
        {
            if (inputDialogInputBox.getText().length() == 0)
            {
                inputDialogInputBox.setError(getResources().getString(R.string.dont_leave_it_empty));
            }
            else
            {
                try
                {
                    InputDialogListener listener = (InputDialogListener) getActivity();
                    listener.onInputProvided(String.valueOf(inputDialogInputBox.getText()));
                }
                catch (ClassCastException e) { }
                finally
                { dismiss(); }
            }

        }
    }


    public interface InputDialogListener
    {
        void onInputProvided(String input);
    }
}
