package com.calebtrevino.tallystacker.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author Ritesh Shakya
 */
@SuppressWarnings("unused")
public class Utils {

    private final Context _context;

    // constructor
    public Utils(Context context) {
        this._context = context;
    }

    /*
      * getting screen width
      */
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            display.getSize(point);
        }
        columnWidth = point.x;
        return columnWidth;
    }


    public float dpToPx(int i) {
        Resources r = _context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics());
    }


}