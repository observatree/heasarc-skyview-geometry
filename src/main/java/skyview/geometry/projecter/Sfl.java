package skyview.geometry.projecter;

import skyview.geometry.*;

import static java.lang.Double.NaN;
import static java.lang.Math.*;

import skyview.geometry.Projecter;
import skyview.geometry.Deprojecter;
import skyview.geometry.Transformer;


/** This class implements the Sanson-Flamsteed (Sinusoidal)
 *  projection.
 */

public final class Sfl extends Projecter {
    
    /** Get the name of the compontent */
    public String getName() {
	return "Sfl";
    }
    
    /** Return a description of the component */
    public String getDescription() {
	return "Transform from the celestial sphere to the sinusoidal projection";
    }
    
    /** Get the inverse transformation */
    public Deprojecter inverse() {
	return new  Sfl.SflDeproj();
    }
    
    /** Is this an inverse of some other transformation? */
    public boolean isInverse(Transformer t) {
	return t.getName().equals("SflDeproj");
    }
    
    /** Do the transfromation */
    public final void transform(double[] sphere, double[] plane) {
	
	if (Double.isNaN(sphere[2]) ) {
	    plane[0] = NaN;
	    plane[1] = NaN;
	} else {
	    plane[1] = atan2(sphere[2],
			     sqrt(sphere[0]*sphere[0]+sphere[1]*sphere[1]));
	    plane[0] = atan2(sphere[1], sphere[0])*Math.cos(plane[1]);
	}
    }
    
    public boolean validPosition(double[] plane) {
	return super.validPosition(plane) &&
            abs(plane[1]) <= PI/2 &&
	    abs(plane[0]) <= PI*cos(plane[1]);
    }
    
    public class SflDeproj extends Deprojecter {
        /** Get the name of the compontent */
        public String getName() {
	    return "SflDeproj";
        }
    
        /** Is this an inverse of some other transformation? */
        public boolean isInverse(Transformer t) {
	    return t.getName().equals("Sfl");
        }
	
        /** Return a description of the component */
        public String getDescription() {
	    return "Transform from a sinusoidal projection to the corresponding unit vector.";
	}
	
	/** Get the inverse */
	public Projecter inverse() {
	    return Sfl.this;
	}
    
        /** Deproject a point from the plane to the sphere.
         *  @param plane a double[2] vector in the tangent plane.
         *  @param spehre a preallocated double[3] vector.
         */
        public final void transform(double[] plane, double[] sphere) {
	
	    if (!validPosition(plane)) {
	        sphere[0] = NaN;
	        sphere[1] = NaN;
	        sphere[2] = NaN;
		
	    } else {
		
		double dec = plane[1];
		double sd  = sin(dec);
		double cd  = cos(dec);
		if (abs(dec) <= PI/2 && abs(plane[0]) <= PI*cd) {
		    double ra = plane[0];
		    if (cd > 0) {
			ra /= cd;
		    }
	            double sr = sin(ra);
	            double cr = cos(ra);
		    
	            sphere[0] = cr*cd;
	            sphere[1] = sr*cd;
	            sphere[2] = sd;
		}
	    }
	}
    }
}
