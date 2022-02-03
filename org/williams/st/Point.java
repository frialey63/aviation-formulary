package org.williams.st;

public class Point {

    public double lat;
    public double lon;
    
    // use for GC and RL
    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = -lon;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
    
        buffer.append("Point[");
        buffer.append("[" + super.toString() + "],");
        buffer.append("lat=").append(lat);
        buffer.append(",lon=").append(lon);
        buffer.append("]");
    
        return buffer.toString();
    }
    
}
