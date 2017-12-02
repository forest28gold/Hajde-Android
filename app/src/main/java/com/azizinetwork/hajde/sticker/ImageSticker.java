package com.azizinetwork.hajde.sticker;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageSticker extends Sticker {

    private Bitmap bgBitmap;


    public ImageSticker(Bitmap bgBitmap) {
        this.bgBitmap = bgBitmap;
        int x = bgBitmap.getWidth();
        int y = bgBitmap.getHeight();
        Log.i("-- img --", "w,h =" + x + "," + y);
        this.mapPointsSrc = new float[]{0, 0, x, 0, x, y, 0, y, x / 2, y / 2};

        this.mMatrix.postTranslate(50, 50);
    }

    public Bitmap getBgBitmap() {
        return bgBitmap;
    }

    public void setBgBitmap(Bitmap bgBitmap) {
        this.bgBitmap = bgBitmap;
    }

}
