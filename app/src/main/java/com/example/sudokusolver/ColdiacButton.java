package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class ColdiacButton extends AppCompatButton implements OwnFont {
    public ColdiacButton(Context context) {
        super(context);
        setFont();
    }

    public ColdiacButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public ColdiacButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    public void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Romanesque.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
