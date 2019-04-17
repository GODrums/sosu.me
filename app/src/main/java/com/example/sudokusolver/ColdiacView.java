package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class ColdiacView extends AppCompatTextView implements OwnFont {
    public ColdiacView(Context context) {
        super(context);
        setFont();
    }
    public ColdiacView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public ColdiacView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    public void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Romanesque.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
