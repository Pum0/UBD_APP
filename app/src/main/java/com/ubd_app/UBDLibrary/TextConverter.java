package com.ubd_app.UBDLibrary;

import android.widget.TextView;

public class TextConverter {

    public static String ConverterDistance(double dis) {
        if (dis > 1000) {
            return String.format("%.2f Km", (double) (dis / 1000));
        } else {
            return String.format("%d m", (int) dis);
        }
    }

    public static String ConverterDistance(int dis) {
        if (dis > 1000) {
            return String.format("%.2f Km", (double) (dis / 1000));
        } else {
            return String.format("%d m", (int) dis);
        }
    }

}
