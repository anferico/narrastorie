package it.unipi.di.sam.carriage.narrastorie;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class ConfirmationDialogFragment extends DialogFragment implements View.OnClickListener
{

    private String message;

    private TextView confirmationDialogMessage;
    private ImageButton confirmationDialogOkButton;
    private ImageButton getConfirmationDialogCancelButton;

    public ConfirmationDialogFragment()
    {

    }

    public static ConfirmationDialogFragment newInstance(String message)
    {
        ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();

        Bundle args = new Bundle();
        args.putString("message", message);

        confirmationDialogFragment.setArguments(args);
        return confirmationDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.confirmation_dialog_template, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        confirmationDialogMessage = (TextView) view.findViewById(R.id.confirmation_dialog_message);
        confirmationDialogMessage.setText(args.getString("message"));

        confirmationDialogOkButton = (ImageButton) view.findViewById(R.id.confirmation_dialog_ok_button);
        confirmationDialogOkButton.setOnClickListener(this);

        getConfirmationDialogCancelButton = (ImageButton) view.findViewById(R.id.confirmation_dialog_cancel_button);
        getConfirmationDialogCancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.confirmation_dialog_ok_button)
        {
            try
            {
                ConfirmationDialogListener listener = (ConfirmationDialogListener) getActivity();
                listener.onConfirm(true);
            }
            catch (Exception e) { }
            finally
            {
                dismiss();
            }
        }
        else if (v.getId() == R.id.confirmation_dialog_cancel_button)
        {
            try
            {
                ConfirmationDialogListener listener = (ConfirmationDialogListener) getActivity();
                listener.onConfirm(false);
            }
            catch (ClassCastException e) { }
            finally
            {
                dismiss();
            }
        }
    }

    public interface ConfirmationDialogListener
    {
        void onConfirm(boolean confirmed);
    }
}
