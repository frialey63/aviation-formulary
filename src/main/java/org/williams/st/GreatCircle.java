package org.williams.st;

/*
 * @see http://williams.best.vwh.net/avform.htm
 */
/**
 * @author Paul J Parlett
 *
 */
public class GreatCircle {

    private static final double EPS = 0.001;

    private static final double TOL = 0.000001;

    private static final double TWO_PI = 2.0 * Math.PI;

    private static double acos(double x) {
        final double safeX = (Math.max(-1.0, Math.min(x, 1.0)));

        return Math.atan2(Math.sqrt(1.0 - safeX * safeX), safeX);
    }

    private static double asin(double x) {
        final double safeX = (Math.max(-1.0, Math.min(x, 1.0)));

        return Math.atan2(safeX, Math.sqrt(1.0 - safeX * safeX));
    }

    private static double mod(double y, double x) {
        return y - x * Math.floor(y / x);
    }

    private Point p1;

    private Point p2;

    private double sinp1lat;

    private double cosp1lat;

    private double sinp1lon;

    private double cosp1lon;

    private double sinp2lat;

    private double cosp2lat;

    private double sinp2lon;

    private double cosp2lon;

    public GreatCircle(Point p1, Point p2) {
        super();
        this.p1 = p1;
        this.p2 = p2;

        sinp1lat = Math.sin(p1.lat);
        cosp1lat = Math.cos(p1.lat);

        sinp1lon = Math.sin(p1.lon);
        cosp1lon = Math.cos(p1.lon);

        sinp2lat = Math.sin(p2.lat);
        cosp2lat = Math.cos(p2.lat);

        sinp2lon = Math.sin(p2.lon);
        cosp2lon = Math.cos(p2.lon);
    }

    /**
     * Distance between points
     * 
     * @return
     */
    public double distance() {
        return acos(sinp1lat * sinp2lat + cosp1lat * cosp2lat * Math.cos(p1.lon - p2.lon));
    }

    /**
     * Distance between points
     * 
     * @param p
     * @return
     */
    public double distance(Point p) {
        return acos(sinp1lat * Math.sin(p.lat) + cosp1lat * Math.cos(p.lat) * Math.cos(p1.lon - p.lon));
    }

    /**
     * Course between points
     * 
     * @return
     */
    public double course() {
        double result = 0.0;

        if (cosp1lat < EPS) {
            result = (p1.lat > 0.0) ? Math.PI : TWO_PI;
        } else {
            result = mod(Math.atan2(Math.sin(p1.lon - p2.lon) * cosp2lat, cosp1lat * sinp2lat - sinp1lat * cosp2lat
                    * Math.cos(p1.lon - p2.lon)), TWO_PI);
        }

        return result;
    }

    /**
     * Course between points
     * 
     * @param p
     * @return
     */
    public double course(Point p) {
        double result = 0.0;

        if (cosp1lat < EPS) {
            result = (p1.lat > 0.0) ? Math.PI : TWO_PI;
        } else {
            result = mod(Math.atan2(Math.sin(p1.lon - p.lon) * Math.cos(p.lat), cosp1lat * Math.sin(p.lat) - sinp1lat
                    * Math.cos(p.lat) * Math.cos(p1.lon - p.lon)), TWO_PI);
        }

        return result;
    }

    /**
     * Latitude of point on GC
     * 
     * @param lon
     * @return
     */
    public double latitude(double lon) {
        double result = 0.0;
        
        lon = -lon;

        if (Math.sin(p1.lon - p2.lon) == 0.0) {
            throw new IllegalArgumentException();
        } else {
            result = Math.atan((sinp1lat * cosp2lat * Math.sin(lon - p2.lon) - sinp2lat * cosp1lat * Math.sin(lon - p1.lon))
                    / (cosp1lat * cosp2lat * Math.sin(p1.lon - p2.lon)));
        }

        return result;
    }

    /**
     * Longitude of point on GC
     * 
     * @param lat
     * @return
     */
    public double[] longitude(double lat) {
        double[] result = null;

        final double l12 = p1.lon - p2.lon;

        final double A = sinp1lat * cosp2lat * Math.cos(lat) * Math.sin(l12);
        final double B = sinp1lat * cosp2lat * Math.cos(lat) * Math.cos(l12) - cosp1lat * sinp2lat * Math.cos(lat);
        final double C = cosp1lat * cosp2lat * Math.sin(lat) * Math.sin(l12);

        if (Math.abs(C) > Math.sqrt(A * A + B * B)) {
            throw new IllegalArgumentException();
        } else {
            final double lon = Math.atan2(B, A);
            final double dlon = acos(C / Math.sqrt(A * A + B * B));

            result = new double[2];
            result[0] = -(mod(p1.lon + dlon + lon + Math.PI, TWO_PI) - Math.PI);
            result[1] = -(mod(p1.lon - dlon + lon + Math.PI, TWO_PI) - Math.PI);
        }

        return result;
    }

    /**
     * Lat/lon given radial and distance
     * 
     * @param tc
     * @param d
     * @return
     */
    public Point radialDistance(double tc, double d) {
        final double cosd = Math.cos(d);
        final double sind = Math.sin(d);

        final double lat = asin(sinp1lat * cosd + cosp1lat * sind * Math.cos(tc));
        final double dlon = Math.atan2(Math.sin(tc) * sind * cosp1lat, cosd - sinp1lat * Math.sin(lat));
        final double lon = mod(p1.lon - dlon + Math.PI, TWO_PI) - Math.PI;

        return new Point(lat, lon);
    }

    /**
     * Intermediate points on a great circle
     * 
     * @param d
     * @param f
     * @return
     */
    public Point intermediate(double d, double f) {
        Point result = null;

        if ((p1.lat + p2.lat == 0.0) && (Math.abs(p1.lon - p2.lon) == Math.PI)) {
            throw new IllegalArgumentException();
        } else {
            final double A = Math.sin((1.0 - f) * d) / Math.sin(d);
            final double B = Math.sin(f * d) / Math.sin(d);

            final double x = A * cosp1lat * cosp1lon + B * cosp2lat * cosp2lon;
            final double y = A * cosp1lat * sinp1lon + B * cosp2lat * sinp2lon;
            final double z = A * sinp1lat + B * sinp2lat;

            final double lat = Math.atan2(z, Math.sqrt(x * x + y * y));
            final double lon = Math.atan2(y, x);

            result = new Point(lat, lon);
        }

        return result;
    }

    /**
     * Intersecting radials
     * 
     * @param p1
     * @param crs13
     * @param p2
     * @param crs23
     * @return
     */
    public static Point intersection(Point p1, double crs13, Point p2, double crs23) {
        Point result = null;
        
        final double cosp1lat = Math.cos(p1.lat);
        final double cosp2lat = Math.cos(p2.lat);

        final double sinp1lat = Math.sin(p1.lat);
        final double sinp2lat = Math.sin(p2.lat);

        final double dst12 = 2.0 * asin(Math.sqrt(Math.pow(Math.sin((p1.lat - p2.lat) / 2.0), 2) + cosp1lat
                * cosp2lat * Math.pow(Math.sin((p1.lon - p2.lon) / 2.0), 2)));

        double crs12 = 0.0;
        if (Math.sin(p2.lon - p1.lon) < 0.0) {
            crs12 = acos((sinp2lat - sinp1lat * Math.cos(dst12)) / (Math.sin(dst12) * cosp1lat));
        } else {
            crs12 = TWO_PI
                    - acos((sinp2lat - sinp1lat * Math.cos(dst12)) / (Math.sin(dst12) * cosp1lat));
        }

        double crs21 = 0.0;
        if (Math.sin(p1.lon - p2.lon) < 0.0) {
            crs21 = acos((sinp1lat - sinp2lat * Math.cos(dst12)) / (Math.sin(dst12) * cosp2lat));
        } else {
            crs21 = TWO_PI
                    - acos((sinp1lat - sinp2lat * Math.cos(dst12)) / (Math.sin(dst12) * cosp2lat));
        }

        double ang1 = mod(crs13 - crs12 + Math.PI, TWO_PI) - Math.PI;
        double ang2 = mod(crs21 - crs23 + Math.PI, TWO_PI) - Math.PI;

        if (Math.sin(ang1) * Math.sin(ang2) <= Math.sqrt(TOL)) {
            throw new IllegalArgumentException();
        } else {
            ang1 = Math.abs(ang1);
            ang2 = Math.abs(ang2);
            
            final double ang3 = acos(-Math.cos(ang1) * Math.cos(ang2) + Math.sin(ang1) * Math.sin(ang2) * Math.cos(dst12));
            final double dst13 = asin(Math.sin(ang2) * Math.sin(dst12) / Math.sin(ang3));

            final double lat3 = asin(sinp1lat * Math.cos(dst13) + cosp1lat * Math.sin(dst13) * Math.cos(crs13));
            final double lon3 = mod(p1.lon - asin(Math.sin(crs13) * Math.sin(dst13) / Math.cos(lat3)) + Math.PI, TWO_PI)
                    - Math.PI;
            
            result = new Point(lat3, lon3);
        }

        return result;
    }

    /**
     * Cross track error
     * 
     * @param D
     * @return
     */
    public double[] crossTrackError(Point D) {

        final double crs_AB = course();
        final double crs_AD = course(D);
        final double dist_AD = distance(D);

        final double xtd = asin(Math.sin(dist_AD) * Math.sin(crs_AD - crs_AB));
        final double atd = asin(Math.sqrt(Math.pow(Math.sin(dist_AD), 2) - Math.pow(Math.sin(xtd), 2)) / Math.cos(xtd));

        return new double[] { xtd, atd };
    }

    /**
     * Point(s) known distance from a great circle
     * 
     * @param D
     * @param d
     * @return
     */
    public Point[] knownDistance(Point D, double d) {
        Point[] result = null;

        final double crs_AB = course();
        final double crs_AD = course(D);
        final double dist_AD = distance(D);

        final double A = crs_AD - crs_AB;
        final double b = dist_AD;

        final double r = Math.sqrt(Math.pow(Math.cos(b), 2) + Math.pow(Math.sin(b), 2) * Math.pow(Math.cos(A), 2));
        final double p = Math.atan2(Math.sin(b) * Math.cos(A), Math.cos(b));

        if (Math.pow(Math.cos(d), 2) > r * r) {
            throw new IllegalArgumentException();
        } else {
            result = new Point[2];

            result[0] = radialDistance(crs_AB, p + acos(Math.cos(d) / r));
            result[1] = radialDistance(crs_AB, p - acos(Math.cos(d) / r));
        }

        return result;
    }
}
