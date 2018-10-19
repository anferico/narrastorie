package it.unipi.di.sam.carriage.narrastorie;

import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import javax.crypto.ExemptionMechanismException;

public class InteractionDialogFragment extends DialogFragment
        implements View.OnClickListener, ShakeDetector.OnShakeListener,
        ProximityDetector.ProximityListener, BlowingDetector.OnBlowListener,
        AmbientLightDetector.OnLowLightListener, PunchDetector.OnPunchListener
{

    private TextView message;
    private TextView hint;
    private LinearLayout hintHiddenBox;
    private ImageButton hintButton;

    private ShakeDetector shakeDetector;
    private ProximityDetector proximityDetector;
    private BlowingDetector blowingDetector;
    private AmbientLightDetector ambientLightDetector;
    private PunchDetector punchDetector;

    private int interactionType;
    private final int SHAKING = 0;
    private final int PROXIMITY = 1;
    private final int BLOWING = 2;
    private final int LIGHT = 3;
    private final int PUNCH = 4;

    public InteractionDialogFragment()
    {

    }

    public static InteractionDialogFragment newInstance(StoryFragment storyFragment)
    {
        InteractionDialogFragment interactionDialogFragment = new InteractionDialogFragment();

        Map<String, Object> endOptions = storyFragment.getEndOptions();

        Bundle args = new Bundle();
        args.putString("message", (String) endOptions.get("message"));
        args.putString("interactionType", (String) endOptions.get("interactionType"));
        args.putString("hint", (String) endOptions.get("hint"));

        interactionDialogFragment.setArguments(args);
        return interactionDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.interaction_dialog_template, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        message = (TextView) view.findViewById(R.id.interaction_dialog_message);
        hint = (TextView) view.findViewById(R.id.interaction_dialog_hint_text);
        hintHiddenBox = (LinearLayout) view.findViewById(R.id.interaction_dialog_hidden_hint_box);
        hintButton = (ImageButton) view.findViewById(R.id.interaction_dialog_hint_button);

        Bundle args = getArguments();

        // Popolo opportunamente le TextView nel dialog
        message.setText(args.getString("message"));
        hint.setText(args.getString("hint"));

        // Registro il listener per il click del bottone
        hintButton.setOnClickListener(this);

        // Registro il listener a seconda del tipo di interazione richiesta
        switch (args.getString("interactionType"))
        {
            case "Shaking":
                if (isAccelerometerPresent())
                {
                    startListeningForShaking();
                    interactionType = SHAKING;
                }
                else
                {
                    // Il dispositivo non ha l'accelerometro. Agisco come se l'interazione
                    // fosse stata completata
                    closeDialog();
                }
                break;

            case "Proximity":
                if (isProximitySensorPresent())
                {
                    startListeningForProximity();
                    interactionType = PROXIMITY;
                }
                else
                {
                    // Il dispositivo non ha il sensore di prossimità. Agisco come se
                    // l'interazione fosse stata completata
                    closeDialog();
                }
                break;

            case "Blowing":
                if (isMicrophonePresent())
                {
                    startListeningForBlowing();
                    interactionType = BLOWING;
                }
                else
                {
                    // Il dispositivo non ha il microfono. Agisco come se l'interazione
                    // fosse stata completata
                    closeDialog();
                }
                break;

            case "Light":
                if (isLightSensorPresent())
                {
                    startListeningForLight();
                    interactionType = LIGHT;
                }
                else
                {
                    // Il dispositivo non ha il sensore di luminosità. Agisco come se
                    // l'interazione fosse stata completata
                    closeDialog();
                }
                break;

            case "Punch":
                if (isAccelerometerPresent())
                {
                    startListeningForPunch();
                    interactionType = PUNCH;
                }
                else
                {
                    // Il dispositivo non ha l'accelerometro. Agisco come se l'interazione
                    // fosse stata completata
                    closeDialog();
                }
                break;

            default:
                break;
        }
    }

    private void startListeningForShaking()
    {
        if (shakeDetector == null)
        {
            shakeDetector = new ShakeDetector(getActivity());
            shakeDetector.setOnShakeListener(this);
        }
        shakeDetector.startDetecting();
    }

    private void stopListeningForShaking()
    {
        if (shakeDetector != null)
        {
            shakeDetector.stopDetecting();
        }
    }

    private void startListeningForProximity()
    {
        if (proximityDetector == null)
        {
            proximityDetector = new ProximityDetector(getActivity());
            proximityDetector.setProximityListener(this);
        }
        proximityDetector.startDetecting();
    }

    private void stopListeningForProximity()
    {
        if (proximityDetector != null)
        {
            proximityDetector.stopDetecting();
        }
    }

    private void startListeningForBlowing()
    {
        if (blowingDetector == null)
        {
            blowingDetector = new BlowingDetector();
            blowingDetector.setOnBlowListener(this);
        }
        blowingDetector.startDetecting();
    }

    private void stopListeningForBlowing()
    {
        if (blowingDetector != null)
        {
            blowingDetector.stopDetecting();
        }
    }

    private void startListeningForLight()
    {
        if (ambientLightDetector == null)
        {
            ambientLightDetector = new AmbientLightDetector(getActivity());
            ambientLightDetector.setOnLowLightListener(this);
        }
        ambientLightDetector.startDetecting();
    }

    private void stopListeningForLight()
    {
        if (ambientLightDetector != null)
        {
            ambientLightDetector.stopDetecting();
        }
    }

    private void startListeningForPunch()
    {
        if (punchDetector == null)
        {
            punchDetector = new PunchDetector(getActivity());
            punchDetector.setOnPunchListener(this);
        }
        punchDetector.startDetecting();
    }

    private void stopListeningForPunch()
    {
        if (punchDetector != null)
        {
            punchDetector.stopDetecting();
        }
    }

    private boolean isAccelerometerPresent()
    {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
    }

    private boolean isProximitySensorPresent()
    {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
    }

    private boolean isMicrophonePresent()
    {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private boolean isLightSensorPresent()
    {
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
    }

    @Override
    public void onResume()
    {
        switch (interactionType)
        {
            case SHAKING:
                startListeningForShaking();
                break;

            case PROXIMITY:
                startListeningForProximity();
                break;

            case BLOWING:
                startListeningForBlowing();
                break;

            case LIGHT:
                startListeningForLight();
                break;

            case PUNCH:
                startListeningForPunch();
                break;

            default:
                break;
        }

        super.onResume();
    }

    @Override
    public void onPause()
    {
        switch (interactionType)
        {
            case SHAKING:
                stopListeningForShaking();
                break;

            case PROXIMITY:
                stopListeningForProximity();
                break;

            case BLOWING:
                stopListeningForBlowing();
                break;

            case LIGHT:
                stopListeningForLight();
                break;

            case PUNCH:
                stopListeningForPunch();
                break;

            default:
                break;
        }

        super.onPause();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.interaction_dialog_hint_button)
        {
            hintHiddenBox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onShake()
    {
        closeDialog();
        stopListeningForShaking();
    }

    @Override
    public void onNear()
    {
        closeDialog();
        stopListeningForProximity();
    }

    @Override
    public void onBlow()
    {
        closeDialog();
        stopListeningForBlowing();
    }

    @Override
    public void onLowLight()
    {
        closeDialog();
        stopListeningForLight();
    }

    @Override
    public void onPunch()
    {
        closeDialog();
        stopListeningForPunch();
    }

    private void closeDialog()
    {
        try
        {
            InteractionDialogListener listener = (InteractionDialogListener) getActivity();
            listener.onInteractionCompleted();
        }
        catch (ClassCastException e) { }
        finally { dismiss(); }
    }

    public interface InteractionDialogListener
    {
        void onInteractionCompleted();
    }
}
