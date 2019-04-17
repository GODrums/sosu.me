package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

public class ProximaCheckBox extends AppCompatCheckBox implements OwnFont {
    public ProximaCheckBox(Context context) {
        super(context);
        setFont();
    }

    public ProximaCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public ProximaCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    public void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kano.otf");
        setTypeface(font, Typeface.NORMAL);
    }
}
