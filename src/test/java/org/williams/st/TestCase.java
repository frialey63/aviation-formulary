package org.williams.st;

public class TestCase {
    
    private Point lax;
    private Point jfk;
    
    private GreatCircle gc;
    
    private RhumbLine rl;
    
    public TestCase() {
        lax = new Point(Utils.toRad(33.0, 57.0, 0.0), -Utils.toRad(118.0, 24.0, 0.0));
        jfk = new Point(Utils.toRad(40.0, 38.0, 0.0), -Utils.toRad(73.0, 47.0, 0.0));

        gc = new GreatCircle(lax, jfk);
        rl = new RhumbLine(lax, jfk);
    }

    public void distanceGC() {
        double distance = gc.distance();
        
        assert(distance == 0.6235846454638786);
        
        System.out.println("distance (nm) = " + Utils.distNm(distance));
        
        assert(distance == gc.distance(jfk));
    }
    
    public void distanceRL() {
        double distance = rl.distance();
        
        assert(distance == 0.629649547581414);
        
        System.out.println("distance (nm) = " + Utils.distNm(distance));
    }
    
    public void courseGC() {
        double course = gc.course();
        
        assert(course == 1.1500352576178878);
        
        System.out.println("course (deg) = " + Utils.toDeg(course));
    }
    
    public void courseRL() {
        double course = rl.course();
        
        assert(course == 1.384464260364723);
        
        System.out.println("course (deg) = " + Utils.toDeg(course));
    }
    
    public void latitude() {
        double latitude = gc.latitude(-2.034206);
        
        assert(latitude == 0.6041799804391857);
        
        System.out.println("latitude = " + latitude);
    }
    
    public void longitude() {
        double[] longitude = gc.longitude(0.604180);
        
        assert(longitude[0] == -2.034205944184774);
        
        System.out.println("longitude = " + longitude[0]);
    }
    
    public void radialDistanceGC() {
        Point point = gc.radialDistance(Utils.toRad(66.0), Utils.distRad(100.0));
        
        assert((point.lat == 0.6041297682735567) && (point.lon == -2.034179511399408));
        
        System.out.println("point = " + point.toString());
    }
    
    public void radialDistanceRL() {
        Point point = rl.radialDistance(Utils.toRad(79.3), Utils.distRad(2164.6));
        
        assert((point.lat == 0.7094454938886464) && (point.lon == -1.2877331474450715));
        
        System.out.println("point = " + point.toString());
    }
    
    public void intermediate() {
        Point point = gc.intermediate(gc.distance(), 0.4);
        
        assert((point.lat == 0.6749091831305527) && (point.lon == -1.7737111036158304));
        
        System.out.println("point = " + point.toString());
    }
    
    public void intermediate2() {
        gc.intermediate(gc.distance(), 0.4);
    }
    
    public void intersection() {
        Point reo = new Point(0.74351, -2.05715);
        Point bke = new Point(0.782606, -2.056103);
        
        Point intersect = GreatCircle.intersection(reo, 0.890118, bke, 2.391101);
        
        assert((intersect.lat == 0.7604730580510807) && (intersect.lon == -2.0278762495147316));
        
        System.out.println("intersect = " + intersect.toString());
    }
    
    public void crossTrackError() {
        double[] cte = gc.crossTrackError(new Point(Utils.toRad(34.0, 30.0, 0.0), -Utils.toRad(116.0, 30.0, 0.0)));
        
        assert((cte[0] == 0.0021677781652220647) && (cte[1] == 0.028969104870581967));
        
        System.out.println("xtd = " + cte[0] + " atd = " + cte[1]);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        TestCase tc = new TestCase();
        
        System.out.println("----- GC -----");
        tc.distanceGC();
        tc.courseGC();
        tc.latitude();
        tc.longitude();
        tc.radialDistanceGC();
        tc.crossTrackError();
        tc.intermediate();
        tc.intersection();
        
        System.out.println("----- RL -----");
        tc.distanceRL();
        tc.courseRL();
        tc.radialDistanceRL();
        
        System.out.println("----- FE -----");
        for (double delta = 0.0; delta < 10.0; delta += 0.1)
        {
            
            Point originRL = new Point(Utils.toRad(0.0), Utils.toRad(0.0));
            FEPoint originFE = new FEPoint(Utils.toRad(0.0), Utils.toRad(0.0));
            
            Point testRL = new Point(Utils.toRad(delta), Utils.toRad(delta));
            FEPoint testFE = new FEPoint(Utils.toRad(delta), Utils.toRad(delta));

            RhumbLine rl = new RhumbLine(originRL, testRL);
            FlatEarth fe = new FlatEarth(originFE);
            
            final double distFE = fe.distance(testFE);
            final double brngFE = Utils.toDeg(fe.bearing(testFE));
            
            final double distRL = Utils.distNm(rl.distance()) * Utils.NM_TO_M;
            final double brngRL = Utils.toDeg(rl.course());
            
            System.out.println("for " + delta + " by RL (km)  = " + distRL + " by FE (km)  = " + distFE + " %diff = " + (100.0 * Math.abs(distFE - distRL) / distRL));
            System.out.println("for " + delta + " by RL (deg) = " + brngRL + " by FE (deg) = " + brngFE + " %diff = " + (100.0 * Math.abs(brngFE - brngRL) / brngRL));
        }

        System.out.println("----- Timings for intermediate GC -----");
        {
            final long start = System.currentTimeMillis();
            
            final int NUM_ITS = 100000;
            for (int i = 0; i < NUM_ITS; i++) {
                tc.intermediate2();
            }
            
            System.out.println("time for " + NUM_ITS + " iterations is " + (System.currentTimeMillis() - start) + " mSec");
        }
        
    }

}
