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

package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * 
 * Encode a GeoServer resource.
 * 
 * @see GSDimensionInfoEncoder
 * @see GSFeatureDimensionInfoEncoder
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public abstract class GSResourceEncoder
		extends PropertyXMLEncoder {
	public final static String NAME = "name";
	public final static String METADATA="metadata";
	public final static String KEYWORDS="keywords";

	final private GSMetadataEncoder metadata = new GSMetadataEncoder();
	final private Element keywordsListEncoder = new Element(KEYWORDS);
	
	private class GSMetadataEncoder extends NestedElementEncoder{
		public GSMetadataEncoder() {
			super(METADATA);
		}
	}

	/**
	 * @param rootName
	 *            Actually 'feature' or 'coverage'
	 * @see GSFeatureTypeEncoder
	 * @see GSCoverageEncoder
	 */
	protected GSResourceEncoder(final String rootName) {
		super(rootName);
		add("enabled", "true");

		// Link members to the parent
		addContent(metadata.getRoot());
		addContent(keywordsListEncoder);
	}

	public void setEnabled(boolean enabled) {
		set("enabled", (enabled) ? "true" : "false");
	}

	// TODO MetadataLink
//	public void setMetadata(String key, String url){
//		metadata.set(key, url);
//	}
	
	/**
	 * @param key
	 * @param dimensionInfo
	 */
	protected void addMetadata(String key, XmlElement dimensionInfo) {
		metadata.add(key, dimensionInfo.getRoot());
	}

	public void setMetadata(String key, XmlElement dimensionInfo) {
		metadata.set(key, dimensionInfo.getRoot());
	}
	

	/**
	 * @param key
	 *            the name of the metadata to add (f.e.: elevation, time)
	 * @return true if something is removed, false otherwise
	 */
	public boolean delMetadata(String key) {
		return metadata.remove(key);
	}


	public void addKeyword(String keyword) {
		final Element el = new Element("string");
		el.setText(keyword);
		keywordsListEncoder.addContent(el);
	}

	/**
	 * delete a keyword from the list
	 * 
	 * @param keyword
	 * @return true if something is removed, false otherwise
	 */
	public boolean delKeyword(final String keyword) {
		final Element el = new Element("string");
		el.setText(keyword);
		return (keywordsListEncoder.removeContent(new Filter() {
			private static final long serialVersionUID = 1L;

			public boolean matches(Object obj) {
				if (((Element) obj).getText().equals(keyword)) {
					return true;
				}
				return false;
			}
		})).size() == 0 ? false : true;
	}

    /**
     * Reprojection policy for a published layer. One of:
     * <ul>
     * <li>{@link #REPROJECT_TO_DECLARED} Reproject from native to declared CRS
     * <li>{@link #FORCE_DECLARED} Use the declared CRS (ignore native)
     * <li>{@link #NONE} No reprojection (use native CRS)
     * </ul>
     */
	public enum ProjectionPolicy {
        /** Reproject from native to declared CRS */
        REPROJECT_TO_DECLARED,
        /** Use the declared CRS (ignore native) */
        FORCE_DECLARED,
        /** Keep native */
        NONE
	}

	private final static String PROJECTIONPOLICY = "projectionPolicy";

	/**
	 * NONE, REPROJECT_TO_DECLARED, FORCE_DECLARED
	 * 
	 */
	protected void addProjectionPolicy(ProjectionPolicy policy) {
		add(PROJECTIONPOLICY, policy.toString());
	}

	/**
	 * NONE, REPROJECT_TO_DECLARED, FORCE_DECLARED
	 */
	public void setProjectionPolicy(ProjectionPolicy policy) {
		set(PROJECTIONPOLICY, policy.toString());
	}

	/**
	 * Add the 'name' node with a text value from 'name'
	 * 
	 * @note REQUIRED to configure a resource
	 */
	protected void addName(final String name) {
		add(NAME, name);
	}

	/**
	 * Set or modify the 'name' node with a text value from 'name'
	 * 
	 * @note REQUIRED to configure a resource
	 */
	public void setName(final String name) {
		set(NAME, name);
	}

	public String getName() {
		final Element nameNode = get(NAME);
		if (nameNode != null)
			return nameNode.getText();
		else
			return null;
	}

	private final static String TITLE = "title";

	/**
	 * Add the 'title' node with a text value from 'title'
	 * 
	 */
	protected void addTitle(final String title) {
		add(TITLE, title);
	}

	/**
	 * Set or modify the 'title' node with a text value from 'title'
	 */
	public void setTitle(final String title) {
		set(TITLE, title);
	}

	private final static String SRS = "srs";

	/**
	 * Add the 'SRS' node with a text value from 'srs'
	 */
	protected void addSRS(final String srs) {
		add(SRS, srs);
	}

	/**
	 * Set or modify the 'SRS' node with a text value from 'srs'
	 */
	public void setSRS(final String srs) {
		set(SRS, srs);
	}
	
	private final static String NATIVECRS = "nativeCRS";

	/**
	 * Add the 'nativeCRS' node with a text value from 'nativeCRS'
	 */
	protected void addNativeCRS(final String nativeCRS) {
		add(NATIVECRS, nativeCRS);
	}

	/**
	 * Set or modify the 'nativeCRS' node with a text value from 'nativeCRS'
	 */
	public void setNativeCRS(final String nativeCRS) {
		set(NATIVECRS, nativeCRS);
	}

	private final static String LATLONBBMINX = "latLonBoundingBox/minx";
	private final static String LATLONBBMAXX = "latLonBoundingBox/maxx";
	private final static String LATLONBBMINY = "latLonBoundingBox/miny";
	private final static String LATLONBBMAXY = "latLonBoundingBox/maxy";
	private final static String LATLONBBCRS = "latLonBoundingBox/crs";

	/**
	 * 
	 * @param minx
	 * @param maxy
	 * @param maxx
	 * @param miny
	 * @param crs
	 */
	protected void addLatLonBoundingBox(double minx, double miny, double maxx,
			double maxy, final String crs) {
		add(LATLONBBMINX, String.valueOf(minx));
		add(LATLONBBMINY, String.valueOf(miny));
		add(LATLONBBMAXY, String.valueOf(maxy));
		add(LATLONBBMAXX, String.valueOf(maxx));
		add(LATLONBBCRS, crs);
	}

	public void setLatLonBoundingBox(double minx, double miny, double maxx,
			double maxy, final String crs) {
		set(LATLONBBMINX, String.valueOf(minx));
		set(LATLONBBMAXY, String.valueOf(maxy));
		set(LATLONBBMAXX, String.valueOf(maxx));
		set(LATLONBBMINY, String.valueOf(miny));
		set(LATLONBBCRS, crs);
	}

	private final static String NATIVEBBMINX = "nativeBoundingBox/minx";
	private final static String NATIVEBBMAXX = "nativeBoundingBox/maxx";
	private final static String NATIVEBBMINY = "nativeBoundingBox/miny";
	private final static String NATIVEBBMAXY = "nativeBoundingBox/maxy";
	private final static String NATIVEBBCRS = "nativeBoundingBox/crs";

	/**
	 * @param minx
	 * @param maxy
	 * @param maxx
	 * @param miny
	 * @param crs
	 */
	protected void addNativeBoundingBox(double minx, double miny, double maxx,
			double maxy, final String crs) {
		add(NATIVEBBMINX, String.valueOf(minx));
		add(NATIVEBBMAXY, String.valueOf(maxy));
		add(NATIVEBBMAXX, String.valueOf(maxx));
		add(NATIVEBBMINY, String.valueOf(miny));
		add(NATIVEBBCRS, crs);
	}

	public void setNativeBoundingBox(double minx, double miny, double maxx,
			double maxy, final String crs) {
		set(NATIVEBBMINX, String.valueOf(minx));
		set(NATIVEBBMAXY, String.valueOf(maxy));
		set(NATIVEBBMAXX, String.valueOf(maxx));
		set(NATIVEBBMINY, String.valueOf(miny));
		set(NATIVEBBCRS, crs);
	}

}
