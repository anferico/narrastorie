package it.unipi.di.sam.carriage.narrastorie;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class SquareScrollView extends ScrollView
{
    public SquareScrollView(Context context)
    {
        super(context);
    }

    public SquareScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public SquareScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;

        double actualSize = size * 0.8;

        setMeasuredDimension((int) actualSize, (int) actualSize);
    }
}
