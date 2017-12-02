package com.azizinetwork.hajde.sticker;

import android.graphics.PointF;
import android.util.Log;

public class MathUtil {
    public static boolean isPointInRectangle(PointF a, PointF b, PointF c, PointF d, PointF p) {
        long t1 = System.currentTimeMillis();
        boolean r1 = isPointInTriangle(a, b, d, p);
        boolean r2 = isPointInTriangle(b,d,c,p);
        long t2 = System.currentTimeMillis();
        Log.i("-- isPointInSameSide",(t2 - t1) / 1000.0 + " s");
        return (r1 || r2);
    }

    public static boolean isPointInTriangle(PointF a, PointF b, PointF c, PointF p) {
        boolean lineAB = isPointInSameSide(a, b, c, p);
        boolean lineBC = isPointInSameSide(b, c, a, p);
        boolean lineAC = isPointInSameSide(a, c, b, p);
        return lineAB && lineBC && lineAC;
    }

    public static boolean isPointInSameSide(PointF a, PointF b, PointF c, PointF p) {

        float A = b.y - a.y;
        float B = a.x - b.x;
        float C = b.x * a.y - a.x * b.y;

        float resultC = A * c.x + B * c.y + C;
        float resultP = A * p.x + B * p.y + C;

        Log.i("-- isPointInSameSide","result =" + resultC + "," + resultP);
        return ((resultC >= 0 && resultP >= 0)) || (resultC < 0 && resultP < 0);
    }

    public static double caculateDistance(float x1, float y1, float x2 ,float y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return Math.sqrt(x * x + y * y);
    }

    public static double caculateAngle(float x, float y, float originX, float originY) {

        float newX = x - originX;
        float newY = y - originY;

        double r = Math.atan2(newY, newX);
        return Math.toDegrees(r);
    }
}
