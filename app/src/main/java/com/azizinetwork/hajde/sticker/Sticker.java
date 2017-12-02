package com.azizinetwork.hajde.sticker;

import android.graphics.Matrix;

public class Sticker {
    public Sticker() {
        mMatrix = new Matrix();
    }

    protected Matrix mMatrix;

    protected boolean focused;

    protected float[] mapPointsSrc;

    protected float[] mapPointsDst = new float[10];

    protected float scaleSize = 1.0f;

    protected float rotate = 0f;

    public Matrix getmMatrix() {
        return mMatrix;
    }

    public void setmMatrix(Matrix mMatrix) {
        this.mMatrix = mMatrix;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public float[] getMapPointsSrc() {
        return mapPointsSrc;
    }

    public void setMapPointsSrc(float[] mapPointsSrc) {
        this.mapPointsSrc = mapPointsSrc;
    }

    public float[] getMapPointsDst() {
        return mapPointsDst;
    }

    public void setMapPointsDst(float[] mapPointsDst) {
        this.mapPointsDst = mapPointsDst;
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(float scaleSize) {
        this.scaleSize = scaleSize;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }
}
