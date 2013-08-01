/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.geosolutions.geoserver.rest.encoder.metadata;

import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import java.math.BigDecimal;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * @author Simone Giannecchini, GeoSolutions
 *
 */
public class GSDimensionInfoEncoder extends XmlElement{
	public final static String DIMENSIONINFO="dimensionInfo";
	
	public final static String RESOLUTION="resolution";
	
	public final static String PRESENTATION="presentation";
	
	public final static String UNITS="units";
	
	public final static String UNIT_SYMBOL="unitSymbol";
	
	private boolean enabled;
	
	/**
	 * Enum for presentation mode
	 */
	public enum Presentation {
		LIST,
		CONTINUOUS_INTERVAL,
		DISCRETE_INTERVAL
	}
	
	/**
	 * Build a dimension
	 * @param enabled enable dimension if true
	 * @note a enabled dimension also need a presentation mode set.
	 */
	public GSDimensionInfoEncoder(final boolean enabled) {
		super(DIMENSIONINFO);
		add("enabled", (enabled)?"true":"false");
		this.enabled=enabled;
	}
	
	/**
	 * build an not enabled dimension
	 */
	public GSDimensionInfoEncoder() {
		super(DIMENSIONINFO);
		add("enabled", "false");
		this.enabled=Boolean.FALSE;
	}
	
	public void setEnabled(final boolean enabled){
		set("enabled", "true");
		this.enabled=Boolean.TRUE;
	}
	
	public void setPresentation(final Presentation pres){
		setPresentation(pres, null);
	}
	
	/**
	 * @param pres
	 * @param interval
	 */
	protected void addPresentation(final Presentation pres, final BigDecimal interval){
		if (enabled){
			add(PRESENTATION,pres.toString());
			if(pres==Presentation.DISCRETE_INTERVAL){
	                    if(pres==Presentation.DISCRETE_INTERVAL&&interval==null){
	                        throw new IllegalArgumentException("Null interval was provided while trying to set the presentation to discrete interval.");
	                    }
	                    add(RESOLUTION,String.valueOf(interval)); 
	                } else {
	                    remove(RESOLUTION);
	                }
		}
	}
	
	public void setPresentation(final Presentation pres, final BigDecimal interval){
            if (enabled){
                set(PRESENTATION,pres.toString());
                if(pres==Presentation.DISCRETE_INTERVAL){
                    if(pres==Presentation.DISCRETE_INTERVAL&&interval==null){
                        throw new IllegalArgumentException("Null interval was provided while trying to set the presentation to discrete interval.");
                    }
                    set(RESOLUTION,String.valueOf(interval)); 
                } else {
                    remove(RESOLUTION);
                }
        }
	}
	
	
	/**
	 * Set UoM for this dimension.
	 * 
	 * <code>null</code> is acceptable and leave this UoM blank.
	 * 
	 * @param unit UoM for this dimension.
	 */
	public void setUnit(final String unit){
	    if(unit==null||unit.length()<=0){
	        return;
	    }
            if (enabled){
                set(UNITS,unit);    
            }
	}
	
	/**
         * Set UoM for this dimension.
         * 
         * <code>null</code> is acceptable and leave this UoM blank.
         * 
         * @param unit UoM for this dimension.
         */
        public void setUnitSymbol(final String unitSymbol){
            if(unitSymbol==null||unitSymbol.length()<=0){
                return;
            }
            if (enabled){
                set(UNIT_SYMBOL,unitSymbol);    
            }
        }
	
}
