package it.unipi.di.sam.carriage.narrastorie;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

public class SwipeDialogFragment extends DialogFragment
        implements View.OnTouchListener, Animator.AnimatorListener
{

    private TextView message;
    private TextView leftSwipeMessage;
    private TextView rightSwipeMessage;
    private LinearLayout rootFrame;
    private GestureDetector gestureDetector;

    private boolean SWIPE_DIRECTION; // true = left, false = right

    public SwipeDialogFragment()
    {

    }

    public static SwipeDialogFragment newInstance(StoryFragment storyFragment)
    {
        SwipeDialogFragment swipeDialogFragment = new SwipeDialogFragment();

        Map<String, Object> endOptions = storyFragment.getEndOptions();

        Bundle args = new Bundle();
        args.putString("message", (String) endOptions.get("message"));
        args.putString("leftSwipeMessage", (String) endOptions.get("leftSwipeMessage"));
        args.putString("rightSwipeMessage", (String) endOptions.get("rightSwipeMessage"));

        swipeDialogFragment.setArguments(args);
        return swipeDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.swipe_dialog_template, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        message = (TextView) view.findViewById(R.id.swipe_dialog_message);
        leftSwipeMessage = (TextView) view.findViewById(R.id.swipe_dialog_left_swipe_message);
        rightSwipeMessage = (TextView) view.findViewById(R.id.swipe_dialog_right_swipe_message);

        Bundle args = getArguments();

        // Popolo opportunamente le TextView nel dialog
        message.setText(args.getString("message"));
        leftSwipeMessage.setText(args.getString("leftSwipeMessage"));
        rightSwipeMessage.setText(args.getString("rightSwipeMessage"));

        // Registro il listener per gli eventi swipe
        rootFrame = (LinearLayout) view.findViewById(R.id.swipe_dialog_root_frame);
        rootFrame.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (gestureDetector == null)
        {
            gestureDetector = new GestureDetector(getActivity(), new MyGestureListener());
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private ObjectAnimator getLeftSwipeAnimation()
    {
        return ObjectAnimator.ofPropertyValuesHolder(getDialog().getWindow().getDecorView(),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, -500f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f))
                .setDuration(500);
    }

    private ObjectAnimator getRightSwipeAnimation()
    {
        return ObjectAnimator.ofPropertyValuesHolder(getDialog().getWindow().getDecorView(),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f, 500f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f))
                .setDuration(500);
    }

    @Override
    public void onAnimationStart(Animator animation) { }

    @Override
    public void onAnimationCancel(Animator animation) { }

    @Override
    public void onAnimationRepeat(Animator animation) { }

    @Override
    public void onAnimationEnd(Animator animation)
    {
        try
        {
            SwipeDialogListener listener = (SwipeDialogListener) getActivity();
            listener.onSwipePerformed(SWIPE_DIRECTION);
        }
        catch (ClassCastException e) { }
        finally { dismiss(); }
    }

    public interface SwipeDialogListener
    {
        void onSwipePerformed(boolean leftSwipe);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                // Swipe a sinistra

                ObjectAnimator leftSwipeAnimation = getLeftSwipeAnimation();
                leftSwipeAnimation.addListener(SwipeDialogFragment.this);
                leftSwipeAnimation.start();

                SWIPE_DIRECTION = true;

                return true;
            }
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
            {
                // Swipe a destra

                ObjectAnimator rightSwipeAnimation = getRightSwipeAnimation();
                rightSwipeAnimation.addListener(SwipeDialogFragment.this);
                rightSwipeAnimation.start();

                SWIPE_DIRECTION = false;

                return true;
            }

            return true;
        }
    }
}
