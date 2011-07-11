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

import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSMetadataEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.TextNodeListEncoder;

/**
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public abstract class GSResourceEncoder<T extends GSDimensionInfoEncoder> extends PropertyXMLEncoder {
	
	final private GSMetadataEncoder<T> metadata=new GSMetadataEncoder<T>();
	

	public void addMetadata(String key, T dimensionInfo) {
		metadata.add(key, dimensionInfo);
	}

	private TextNodeListEncoder keywordsListEncoder = new TextNodeListEncoder("keywords");

	/**
	 * NONE, REPROJECT_TO_DECLARED, FORCE_DECLARED
	 */
	public enum ProjectionPolicy {
		REPROJECT_TO_DECLARED, FORCE_DECLARED, NONE
	}
	
	protected GSResourceEncoder(final String rootName) {
		super(rootName);
		add("enabled", "true");

        // Link members to the parent
		addContent(metadata.getElement());
		addContent(keywordsListEncoder.getElement());
	}
	
	public void setName(final String name) {
		add("name", name);
    }
	
	public void setTitle(final String title) {
		add("title", title);
    }

	public void setSRS(final String srs) {
		add("srs", srs);
	}

	public void setLatLonBoundingBox(double minx, double maxy, double maxx,
			double miny, final String crs) {
		add("latLonBoundingBox/minx", String.valueOf(minx));
		add("latLonBoundingBox/maxy", String.valueOf(maxy));
		add("latLonBoundingBox/maxx", String.valueOf(maxx));
		add("latLonBoundingBox/miny", String.valueOf(miny));
		add("latLonBoundingBox/crs", crs);
	}

	public void setNativeBoundingBox(double minx, double maxy, double maxx,
			double miny, final String crs) {
		add("nativeBoundingBox/minx", String.valueOf(minx));
		add("nativeBoundingBox/maxy", String.valueOf(maxy));
		add("nativeBoundingBox/maxx", String.valueOf(maxx));
		add("nativeBoundingBox/miny", String.valueOf(miny));
		add("nativeBoundingBox/crs", crs);
	}

	/**
	 * NONE, REPROJECT_TO_DECLARED, FORCE_DECLARED
	 */
	public void setProjectionPolicy(ProjectionPolicy policy) {
		add("projectionPolicy", policy.toString());
	}


	public void addKeyword(String keyword) {
		keywordsListEncoder.add("string", keyword);
	}

}
