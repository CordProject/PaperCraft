package cordproject.lol.papercraft.util;

/**
 * Created by Jeff Lee on 10/29/15.
 * cord-android
 * Copyright 2015 Cord Project Inc.
 */
public class MathUtil {

    public static float toRadians(float degrees) {
        return (float) ((degrees * Math.PI)/180);
    }

    public static float toDegrees(double radians) {
        return (float) ((radians * 180)/Math.PI);
    }

    public static float lerp(float a, float b, float pct) {
        return a + pct * (b - a);
    }

    public static long millisForSec(float seconds) {
        return (long)(1000 * seconds);
    }

    public static long framesForSec(float seconds) {
        return (long)(60 * seconds);
    }
}
