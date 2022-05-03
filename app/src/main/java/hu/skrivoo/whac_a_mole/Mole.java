package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Mole {

    private static final String LOG_TAG = Mole.class.getName();
    private ImageView moleView;
    private boolean active;
    @SuppressLint("ClickableViewAccessibility")
    private final View.OnTouchListener changeColorListener = (v, event) -> {
        try {
            Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
            int color = bmp.getPixel((int) event.getX(), (int) event.getY());
            return color == Color.TRANSPARENT;
        } catch (IllegalArgumentException e) {
            Log.w(LOG_TAG, "Nagy a baj, :" + e.getMessage());
        }
        return false;
    };

    @SuppressLint("ClickableViewAccessibility")
    public <T extends View> Mole(T viewById) {
        this.moleView = (ImageView) viewById;
        moleView.setDrawingCacheEnabled(true);
        moleView.setOnTouchListener(changeColorListener);
        moleView.setVisibility(View.GONE);
        this.active = false;
    }

    public ImageView getMoleView() {
        return moleView;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
