package org.williams.st;

public class RhumbLine {

    private static final double TOL = 0.000001;

    private static final double PI_4 = Math.PI / 4.0;

    private static final double TWO_PI = 2.0 * Math.PI;

    private Point p1;

    private Point p2;

    private static double mod(double y, double x) {
        return y - x * Math.floor(y / x);
    }

    public RhumbLine(Point p1, Point p2) {
        super();
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * The distance between the points
     * 
     * @return
     */
    public double distance() {
        final double dlon_W = mod(p2.lon - p1.lon, TWO_PI);
        final double dlon_E = mod(p1.lon - p2.lon, TWO_PI);
        final double dphi = Math.log(Math.tan(p2.lat / 2 + PI_4) / Math.tan(p1.lat / 2 + PI_4));

        double q = 0.0;
        if (Math.abs(p2.lat - p1.lat) < Math.sqrt(TOL)) {
            q = Math.cos(p1.lat);
        } else {
            q = (p2.lat - p1.lat) / dphi;
        }

        double d = 0.0;
        if (dlon_W < dlon_E) {
            // Westerly rhumb line is the shortest
            d = Math.sqrt((q * q) * (dlon_W * dlon_W) + (p2.lat - p1.lat) * (p2.lat - p1.lat));
        } else {
            d = Math.sqrt((q * q) * (dlon_E * dlon_E) + (p2.lat - p1.lat) * (p2.lat - p1.lat));
        }

        return d;
    }

    /**
     * The true course between the points
     * 
     * @return
     */
    public double course() {
        final double dlon_W = mod(p2.lon - p1.lon, TWO_PI);
        final double dlon_E = mod(p1.lon - p2.lon, TWO_PI);
        final double dphi = Math.log(Math.tan(p2.lat / 2 + PI_4) / Math.tan(p1.lat / 2 + PI_4));

        double tc = 0.0;
        if (dlon_W < dlon_E) {
            // Westerly rhumb line is the shortest
            tc = mod(Math.atan2(-dlon_W, dphi), TWO_PI);
        } else {
            tc = mod(Math.atan2(dlon_E, dphi), TWO_PI);
        }

        return tc;
    }

    /**
     * Lat/lon given radial and distance
     * 
     * @param tc
     * @param d
     * @return
     */
    public Point radialDistance(double tc, double d) {
        final double lat = p1.lat + d * Math.cos(tc);
        final double dphi = Math.log(Math.tan(lat / 2.0 + PI_4) / Math.tan(p1.lat / 2.0 + PI_4));

        double q = 0.0;
        if (Math.abs(lat - p1.lat) < Math.sqrt(TOL)) {
            q = Math.cos(p1.lat);
        } else {
            q = (lat - p1.lat) / dphi;
        }

        final double dlon = -d * Math.sin(tc) / q;
        final double lon = mod(p1.lon + dlon + Math.PI, TWO_PI) - Math.PI;

        return new Point(lat, lon);
    }
}
