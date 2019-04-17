package com.example.sudokusolver;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class ProximaView extends AppCompatTextView implements OwnFont {
    public ProximaView(Context context) {
        super(context);
        setFont();
    }
    public ProximaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public ProximaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    public void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Kano.otf");
        setTypeface(font, Typeface.NORMAL);
    }
}