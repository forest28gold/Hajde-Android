package com.azizinetwork.hajde.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.azizinetwork.hajde.R;

import java.util.ArrayList;
import java.util.List;

public class StickerView extends View {
    public static String TAG = "<<-- StickerView -->>";

    private Bitmap mControllerBitmap, mDeleteBitmap, bgBitmap;
    private float mControllerWidth, mControllerHeight, mDeleteWidth, mDeleteHeight;
    private List<Sticker> stickers = new ArrayList<>();
    private int focusStickerPosition = -1;
    private Paint mBorderPaint;
    private TextPaint mTextPaint;
    private RectF mViewRect;
    private boolean mInController, mInMove, mInDelete = false;
    private float mLastPointX, mLastPointY;
    private float mTouchDownX, mTouchDownY;
    private boolean isPreFocused;
    private float deviation;
    Typeface typeface;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        typeface = Typeface.createFromAsset(getContext().getAssets(),"fonts/AdobeGothicStd-Bold.otf");
        mControllerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.editmode_scale);
        mControllerWidth = mControllerBitmap.getWidth();
        mControllerHeight = mControllerBitmap.getHeight();

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.editmode_delete);
        mDeleteWidth = mDeleteBitmap.getWidth();
        mDeleteHeight = mDeleteBitmap.getHeight();

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setFilterBitmap(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(1.0f);
        mBorderPaint.setColor(Color.WHITE);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL);
        Log.d(TAG, "mControllerBitmap w,h={" + mControllerWidth + "," + mControllerHeight + "} mDeleteBitmap={" + mDeleteWidth + "," + mDeleteHeight + "}");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, 0);

        if (stickers.size() <= 0) {
            return;
        }

        for (int i = 0; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            sticker.getmMatrix().mapPoints(sticker.getMapPointsDst(), sticker.getMapPointsSrc());

            if (sticker instanceof ImageSticker) {
                canvas.drawBitmap(((ImageSticker) sticker).getBgBitmap(), stickers.get(i).getmMatrix(), null);

            } else {
                if (sticker instanceof TextSticker) {
                    float[] dst = sticker.getMapPointsDst();
                    mTextPaint.setTextSize(((TextSticker) sticker).getFontSize() * sticker.getScaleSize());
                    mTextPaint.setTypeface(typeface);

                    Log.i("-- TextSticker size", "size=" + ((TextSticker) sticker).getFontSize() + ", scaleSize=" + sticker.getScaleSize() );
                    Path path = new Path();
                    path.moveTo((dst[6] + dst[0]) / 2, (dst[7] + dst[1]) / 2);
                    path.lineTo((dst[4] + dst[2]) / 2, (dst[5] + dst[3]) / 2);

                    path.close();

                    Rect rect = new Rect();
                    mTextPaint.getTextBounds(((TextSticker) sticker).getText(), 0, ((TextSticker) sticker).getText().length(), rect);
                    float ascent = mTextPaint.getFontMetrics().ascent;
                    float decent = mTextPaint.getFontMetrics().descent;
                    float leading = mTextPaint.getFontMetrics().leading;
                    float top = mTextPaint.getFontMetrics().top;
                    float bottom = mTextPaint.getFontMetrics().bottom;

                    float dy = (bottom - top) / 2 - decent - (bottom - decent);

                    Log.i("-- TextSticker ondraw", "ascent=" + ascent + ",decent:" + decent + ",leading=" + leading + ",top=" + top + ",bottom=" + bottom + ",height=" + rect.height() + ", bottom-top =" + (bottom - top) + " , dy=" + dy);
//                  canvas.drawTextOnPath(((TextSticker) sticker).getText(), path, 0, dy, mTextPaint);
//                    canvas.drawLine((dst[6] + dst[0]) / 2, (dst[7] + dst[1]) / 2, (dst[4] + dst[2]) / 2, (dst[5] + dst[3]) / 2, mTextPaint);
                    canvas.save();
                    canvas.translate(dst[0] + 10, dst[1] +10);
                    canvas.rotate((float) MathUtil.caculateAngle(dst[2], dst[3], dst[0], dst[1]));

                    StaticLayout staticLayout = new StaticLayout(((TextSticker) sticker).getText(), mTextPaint, 10000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
                    staticLayout.draw(canvas, path, mTextPaint,0);
//                    Log.i("--ondraw size static", staticLayout.getWidth() + ", " + staticLayout.getHeight() + ", " + staticLayout.getTopPadding() + "," + getPaddingBottom());
                    canvas.restore();
                }
            }

            if (sticker.isFocused()) {
                drawFocusedStickerBorder(canvas, sticker);
            }
        }
        Paint p = new Paint();
        p.setColor(Color.TRANSPARENT);
        p.setStrokeWidth(5);
        p.setStyle(Paint.Style.FILL);

        canvas.drawPoint(mLastPointX, mLastPointY, p);
        Log.i("--- isPointInSameSide", mLastPointX + "," + mLastPointY);;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mViewRect == null) {
            mViewRect = new RectF(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
            Log.i(TAG, "Measured: " + getMeasuredWidth() + "," + getMeasuredHeight() + ", real：" + getWidth() + "," + getHeight());
        }

        if (stickers.size() < 0) {
            return true;
        }
        float x = event.getX();
        float y = event.getY();

        Log.i(TAG, "touch event x,y = (" + x + " , " + y + ")");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInController(x, y)) {
                    mInController = true;
//                    T("click controll0..");

                    mLastPointX = x;
                    mLastPointY = y;


                    float[] dst = stickers.get(focusStickerPosition).getMapPointsDst();
                    float nowLength = (float) MathUtil.caculateDistance(dst[0], dst[1], dst[8], dst[9]);
                    float touchLength = (float) MathUtil.caculateDistance(x, y, dst[8], dst[9]);
                    deviation = touchLength - nowLength;
                    break;
                }

                if (isInDelete(x, y)) {
                    mInDelete = true;
                    break;
                }

                if (isFocusSticker(x, y)) {
                    mTouchDownX = x;
                    mTouchDownY = y;

                    mLastPointY = y;
                    mLastPointX = x;
                    mInMove = true;
                    invalidate();
                } else {
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mInDelete && isInDelete(x, y)) {
                    doDeleteSticker();
                } else if (isPreFocused && MathUtil.caculateDistance(x, y, mTouchDownX, mTouchDownY) < 2
                        && getFocusSticker() instanceof TextSticker) {

//                    doInputText();
                }
                mInMove = false;
                mInDelete = false;
                mInController = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mInMove = false;
                mInDelete = false;
                mInController = false;
//                T("ACTION_CANCEL");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInController) {
                    float centerX = stickers.get(focusStickerPosition).getMapPointsDst()[8];
                    float centerY = stickers.get(focusStickerPosition).getMapPointsDst()[9];
                    double nowAngle = MathUtil.caculateAngle(x, y, centerX, centerY);

                    double beforeAngle = MathUtil.caculateAngle(mLastPointX, mLastPointY, centerX, centerY);

                    Log.i("--- angle -- ", "now =" + nowAngle + ", pre=" + beforeAngle + ", rotate=" + (nowAngle - beforeAngle));

                    stickers.get(focusStickerPosition).getmMatrix().postRotate((float) (nowAngle - beforeAngle), centerX, centerY);

                    stickers.get(focusStickerPosition).setRotate(stickers.get(focusStickerPosition).getRotate() + (float) (nowAngle - beforeAngle));

                    float[] dst = stickers.get(focusStickerPosition).getMapPointsDst();
                    float nowLength = (float) MathUtil.caculateDistance(dst[0], dst[1], dst[8], dst[9]);
                    float touchLength = (float) MathUtil.caculateDistance(x, y, dst[8], dst[9]) - deviation;
                    if (nowLength != touchLength) {
                        float scale = touchLength / nowLength;
                        stickers.get(focusStickerPosition).getmMatrix().postScale(scale, scale, dst[8], dst[9]);

                        float newScale = stickers.get(focusStickerPosition).getScaleSize() * scale;
                        stickers.get(focusStickerPosition).setScaleSize(newScale);
                    }

                    mLastPointX = x;
                    mLastPointY = y;
                    postInvalidate();
                    break;
                }


                if (mInMove) {
                    float cX = x - mLastPointX;
                    float cY = y - mLastPointY;
                    if (Math.sqrt(cX * cX + cY * cY) > 2.0f) {
                        stickers.get(focusStickerPosition).getmMatrix().postTranslate(cX, cY);

                        mLastPointX = x;
                        mLastPointY = y;
                        postInvalidate();
                    }
                }
                break;
        }


        return true;
    }


    private boolean isInController(float x, float y) {
        if (focusStickerPosition < 0)
            return false;
        float[] dst = stickers.get(focusStickerPosition).getMapPointsDst();

        float ltX = dst[4];
        float ltY = dst[5];

        RectF rectF = new RectF(ltX - mDeleteWidth / 2, ltY - mDeleteHeight / 2, ltX + mDeleteWidth / 2, ltY + mDeleteHeight / 2);
        return rectF.contains(x, y);
    }

    private boolean isInDelete(float x, float y) {
        if (focusStickerPosition < 0)
            return false;
        float[] dst = stickers.get(focusStickerPosition).getMapPointsDst();

        float ltX = dst[0];
        float ltY = dst[1];

        RectF rectF = new RectF(ltX - mDeleteWidth / 2, ltY - mDeleteHeight / 2, ltX + mDeleteWidth / 2, ltY + mDeleteHeight / 2);
        return rectF.contains(x, y);
    }

    private boolean isFocusSticker(float x, float y) {
        for (int i = stickers.size() - 1; i >= 0; i--) {
            Sticker sticker = stickers.get(i);
            if (isPointInSticker(x, y, sticker)) {
                isPreFocused = sticker.isFocused();
                setFocusStickerPosition(i);
                return true;
            }
        }
        setFocusStickerPosition(-1);
        isPreFocused = false;
        return false;
    }

    private Sticker getFocusSticker() {
        return stickers.get(focusStickerPosition);
    }

    private boolean isPointInSticker(float x, float y, Sticker sticker) {
        float[] dsts = sticker.getMapPointsDst();
        Log.i(TAG, "stiker rect p1(" + dsts[0] + " , " + dsts[1] + "), " +
                " p2 (" + dsts[2] + " , " + dsts[3] + "), " +
                " p3(" + dsts[4] + " , " + dsts[5] + "), " +
                " p4(" + dsts[6] + " , " + dsts[7] + ") ");
        return MathUtil.isPointInRectangle(new PointF(dsts[0], dsts[1]), new PointF(dsts[2], dsts[3]), new PointF(dsts[4], dsts[5]), new PointF(dsts[6], dsts[7]), new PointF(x, y));
    }

    private void drawFocusedStickerBorder(Canvas canvas, Sticker sticker) {

        float[] dst = sticker.getMapPointsDst();

        canvas.drawLine(dst[0], dst[1], dst[2], dst[3], mBorderPaint);
        canvas.drawLine(dst[2], dst[3], dst[4], dst[5], mBorderPaint);
        canvas.drawLine(dst[4], dst[5], dst[6], dst[7], mBorderPaint);
        canvas.drawLine(dst[6], dst[7], dst[0], dst[1], mBorderPaint);

        canvas.drawBitmap(mDeleteBitmap, dst[0] - mDeleteWidth / 2, dst[1] - mDeleteHeight / 2, null);
        canvas.drawBitmap(mControllerBitmap, dst[4] - mControllerWidth / 2, dst[5] - mControllerHeight / 2, null);
    }

    private void setFocusStickerPosition(int position) {

        for (int i = 0; i < stickers.size(); i++) {
            if (i == position) {
                stickers.get(i).setFocused(true);
            } else {
                stickers.get(i).setFocused(false);
            }
        }
        if (position >= 0) {
            Sticker sticker = stickers.remove(position);
            stickers.add(sticker);
            position = stickers.size() - 1;
        }
        focusStickerPosition = position;
    }

    private int caculateFontWidth(String text, float fontSize) {
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize(fontSize);
        paint.getTextBounds(text,0,text.length(),rect);

        return rect.width() + 10;
    }


    private void doDeleteSticker() {
//        T("triger delete event");
        stickers.remove(focusStickerPosition);
        postInvalidate();
        focusStickerPosition = -1;
    }

    public void setBgBitmap(Bitmap bitmap) {
        this.bgBitmap = bitmap;
    }

    public void addSticker(Bitmap bitmap) {
        ImageSticker imageSticker = new ImageSticker(bitmap);
        addSticker(imageSticker);
    }

    public void addSticker(Sticker sticker) {
        stickers.add(sticker);
        focusStickerPosition = stickers.size() - 1;
        setFocusStickerPosition(focusStickerPosition);
        postInvalidate();
    }

    public void updateFocusStickerText(String text) {
        if(getFocusSticker() instanceof TextSticker) {
            ((TextSticker) getFocusSticker()).setText(text);

            TextSticker sticker =  (TextSticker) getFocusSticker();

            float[] dst = sticker.getMapPointsDst();
            float fontSize = sticker.getFontSize() * sticker.getScaleSize();
            float preLength = (float)MathUtil.caculateDistance(dst[0],dst[1],dst[2],dst[3]);
            float nowLength = caculateFontWidth(text, fontSize);
//
//
//            float sacle = nowLength / preLength;
//            sticker.getmMatrix().postScale(sacle,1,(dst[0] + dst[7]) /2,(dst[1] + dst[8]) /2);
//            sticker.setFontSize(fontSize);
//            sticker.setScaleSize(1);
//            sticker.setScaleSize(1);

            stickers.remove(sticker);
            focusStickerPosition = -1;
            TextSticker textSticker = new TextSticker(text,fontSize);
            textSticker.setFontSize(fontSize);
            textSticker.setScaleSize(1);
            textSticker.setRotate(sticker.getRotate());

//            textSticker.setmMatrix(sticker.getmMatrix());
//
//            double degree = MathUtil.caculateAngle(dst[2],dst[3],dst[0],dst[1]);
            textSticker.getmMatrix().postTranslate(dst[8] - textSticker.getMapPointsSrc()[8],dst[9] - textSticker.getMapPointsSrc()[9]);
            textSticker.getmMatrix().mapPoints(textSticker.getMapPointsDst(), textSticker.getMapPointsSrc());
            float centerX = textSticker.getMapPointsDst()[8];
            float centerY = textSticker.getMapPointsDst()[9];
            textSticker.getmMatrix().postRotate(sticker.getRotate(),centerX,centerY);
            addSticker(textSticker);

//            Log.i("-- updateFocusStickerText", "pre= " + preLength + ", now = " + nowLength + "sacle = " + sacle);

            postInvalidate();
        }
    }

    public boolean updateStickerText(String text) {

        for (int i = 0; i < stickers.size(); i++) {
            Sticker _sticker = stickers.get(i);
            _sticker.getmMatrix().mapPoints(_sticker.getMapPointsDst(), _sticker.getMapPointsSrc());

            if (_sticker instanceof TextSticker) {
                ((TextSticker) _sticker).setText(text);
                TextSticker sticker = (TextSticker) _sticker;
                float[] dst = sticker.getMapPointsDst();
                float fontSize = sticker.getFontSize() * sticker.getScaleSize();

                stickers.remove(sticker);
                focusStickerPosition = -1;
                TextSticker textSticker = new TextSticker(sticker.getText(), fontSize);
                textSticker.setFontSize(fontSize);
                textSticker.setScaleSize(1);
                textSticker.setRotate(sticker.getRotate());

                textSticker.getmMatrix().postTranslate(dst[8] - textSticker.getMapPointsSrc()[8],dst[9] - textSticker.getMapPointsSrc()[9]);
                textSticker.getmMatrix().mapPoints(textSticker.getMapPointsDst(), textSticker.getMapPointsSrc());
                float centerX = textSticker.getMapPointsDst()[8];
                float centerY = textSticker.getMapPointsDst()[9];
                textSticker.getmMatrix().postRotate(sticker.getRotate(),centerX,centerY);
                addSticker(textSticker);

                postInvalidate();

                return true;
            }
        }

        return false;
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

//    public void T(String msg) {
//        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
//    }
}
