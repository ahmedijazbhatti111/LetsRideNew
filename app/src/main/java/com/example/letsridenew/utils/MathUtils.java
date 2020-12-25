package com.example.letsridenew.utils;

import com.google.android.gms.maps.model.LatLng;

public class  MathUtils
{
    // http://stackoverflow.com/questions/3728246/what-should-be-the-
    // epsilon-value-when-performing-double-value-equal-comparison
    // ULP = Unit in Last Place
    public static double relativeEpsilon( double a, double b )
    {
        return Math.max( Math.ulp( a ), Math.ulp( b ) );
    }

    public static boolean nearlyEqual( double a, double b )
    {
        return nearlyEqual( a, b, relativeEpsilon( a, b ) );
    }

    // http://floating-point-gui.de/errors/comparison/
    public static boolean nearlyEqual( double a, double b, double epsilon )
    {
        final double absA = Math.abs( a );
        final double absB = Math.abs( b );
        final double diff = Math.abs( a - b );

        if( a == b )
        {
            // shortcut, handles infinities
            return true;
        }
        else if( a == 0 || b == 0 || absA + absB < Double.MIN_NORMAL )
        {
            // a or b is zero or both are extremely close to it
            // relative error is less meaningful here
            // NOT SURE HOW RELATIVE EPSILON WORKS IN THIS CASE
            return diff < ( epsilon * Double.MIN_NORMAL );
        }
        else
        {
            // use relative error
            return diff / Math.min( ( absA + absB ), Double.MAX_VALUE ) < epsilon;
        }
    }
    public static boolean approximatelyEqual(double desiredValue, double actualValue, double tolerancePercentage) {
        double diff = Math.abs(desiredValue - actualValue);         //  1000 - 950  = 50
        double tolerance = tolerancePercentage/100 * desiredValue;  //  20/100*1000 = 200
        return diff < tolerance;                                   //  50<200      = true
        //33.6838655,33.6837
    }
    public static LatLng midLatLng(double lat1, double lon1, double lat2, double lon2)
    {
        double dLon = Math.toRadians(lon2-lon1);
        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1)+ Math.sin(lat2), Math.sqrt( (Math.cos(lat1)+Bx)*(Math.cos(lat1)+Bx) + By*By) );
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        return new LatLng(lat3,lon3);
    }
}
