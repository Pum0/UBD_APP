package com.ubd_app.Ride.Guide;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ubd_app.Main;
import com.ubd_app.R;

public class InfoDrawable {

    private Main main;

    private Drawable Straght;
    private Drawable Left;
    private Drawable Right;
    private Drawable Crosswalk;
    private Drawable LeftCrosswalk;
    private Drawable RightCrosswalk;
    private Drawable Finish;

    public InfoDrawable(Main main) {
        this.main = main;
        setValuse();
    }

    private void setValuse() {
        this.Straght = main.getResources().getDrawable(R.mipmap.straight_arrow);
        this.Left = main.getResources().getDrawable(R.mipmap.left_arrow);
        this.Right = main.getResources().getDrawable(R.mipmap.right_arrow);
        this.Crosswalk = main.getResources().getDrawable(R.mipmap.crosswalk);
        this.LeftCrosswalk = main.getResources().getDrawable(R.mipmap.cross_left);
        this.RightCrosswalk = main.getResources().getDrawable(R.mipmap.cross_right);
        this.Finish = main.getResources().getDrawable(R.drawable.finish,main.getTheme());
    }


    public Drawable getDrawable(int i) {
        if (i == 12) {
            return Left;
        } else if (i == 13) {
            return Right;
        } else if (i == 211) {
            return Crosswalk;
        } else if (i == 212 || i == 214 || i == 215) {
            return LeftCrosswalk;
        } else if (i == 213 || i == 216 || i == 217) {
            return RightCrosswalk;
        } else if (i == 201) {
            return Finish;
        } else {
            return Straght;
        }
    }
}
