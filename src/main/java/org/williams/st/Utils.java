package org.williams.st;
public class Utils {
    
    public static final double NM_TO_M = 1852.0;

    public static double toRad(double deg, double min, double sec) {
        return toRad(deg + (min / 60.0) + (sec / 3600.0));
    }

    public static double toDeg(double deg, double min, double sec) {
        return (deg + (min / 60.0) + (sec / 3600.0));
    }

    public static double toRad(double deg) {
        return Math.PI * deg / 180.0;
    }

    public static double toDeg(double rad) {
        return 180.0 * rad / Math.PI;
    }

    public static double distNm(double rad) {
        return ((180.0 * 60.0) / Math.PI) * rad;
    }

    public static double distRad(double nm) {
        return (Math.PI / (180.0 * 60.0)) * nm;
    }
}
