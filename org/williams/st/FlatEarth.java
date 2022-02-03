package org.williams.st;

public class FlatEarth {

    private static final double TWO_PI = 2.0 * Math.PI;

    private static final double a = 6378137.0;

    private static final double f = 1.0 / 298.257223563;

    private FEPoint p0;

    private double R1;
    private double R2;

    private static double mod(double y, double x) {
        return y - x * Math.floor(y / x);
    }

    public FlatEarth(FEPoint p1) {
        super();
        this.p0 = p1;

        final double e2 = f * (2.0 - f);

        final double sinp1lat = Math.sin(p1.lat);

        R1 = a * (1.0 - e2) / Math.pow((1.0 - e2 * (sinp1lat * sinp1lat)), (3 / 2));
        R2 = a / Math.sqrt(1.0 - e2 * (sinp1lat * sinp1lat));
    }

    /**
     * Distance
     * 
     * @param p
     * @return
     */
    public double distance(FEPoint p) {
        final double dlat = p.lat - p0.lat;
        final double dlon = p.lon - p0.lon;

        final double distance_North = R1 * dlat;
        final double distance_East = R2 * Math.cos(p0.lat) * dlon;

        return Math.sqrt(distance_North * distance_North + distance_East * distance_East);
    }

    /**
     * Bearing
     * 
     * @param p
     * @return
     */
    public double bearing(FEPoint p) {
        final double dlat = p.lat - p0.lat;
        final double dlon = p.lon - p0.lon;

        final double distance_North = R1 * dlat;
        final double distance_East = R2 * Math.cos(p0.lat) * dlon;

        return mod(Math.atan2(distance_East, distance_North), TWO_PI);
    }

}
