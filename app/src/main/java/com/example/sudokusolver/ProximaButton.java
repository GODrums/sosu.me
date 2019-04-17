package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Generate a button with the Proxima font.
 */
public class ProximaButton extends AppCompatButton implements OwnFont {
    public ProximaButton(Context context) {
        super(context);
        setFont();
    }

    public ProximaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public ProximaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    public void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kano.otf");
        setTypeface(font, Typeface.NORMAL);
    }
}
