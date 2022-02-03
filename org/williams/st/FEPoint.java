package org.williams.st;

public class FEPoint extends Point {

    public FEPoint(double lat, double lon) {
        super(lat, -lon);
    }

}
