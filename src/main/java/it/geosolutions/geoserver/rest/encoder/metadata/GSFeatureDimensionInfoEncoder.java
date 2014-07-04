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


public class GSFeatureDimensionInfoEncoder extends GSDimensionInfoEncoder {
	public final static String ATTRIBUTE="attribute";
	public final static String END_ATTRIBUTE="endAttribute";
	
	/**
	 * if this dimension is enabled this constructor should be called.
	 * @param attribute the attribute field name to use as dimension
	 */
	public GSFeatureDimensionInfoEncoder(final String attribute){
		super(true);
		add(ATTRIBUTE, attribute);
	}
	
	/**
	 * Change the attribute used as dimension
	 * @param attribute the attribute to use as dimension
	 */
	public void setAttribute(final String attribute){
		set(ATTRIBUTE, attribute);
	}
	
	/**
	 * Set the end attribute used in the dimension (optional)
	 * @param endAttribute the dimension end attribute
	 */
	public void setEndAttribute(final String endAttribute){
		set(END_ATTRIBUTE, endAttribute);
	}

}
