package it.unipi.di.sam.carriage.narrastorie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class SquareLayout extends RelativeLayout
{

    public SquareLayout(Context context)
    {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public SquareLayout(Context context, AttributeSet attrs)
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

        setMeasuredDimension(size, size);
    }
}
