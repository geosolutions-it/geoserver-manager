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

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;

/**
 * GSVirtualTableGeomEncoder - Encodes a metadata VirtualTable geometry for a
 * GeoServer featureType, as follows:
 * 
 * <pre>
 * {
 * 	@code
 * 	final GSVirtualTableGeomEncoder vtGeom = new GSVirtualTableGeomEncoder();
 * 	vtGeom.setup("the_geom", "MultiPolygon", "4326");
 * }
 * </pre>
 * 
 * For this example, the XML output is:
 * 
 * <pre>
 * {@code
 * <geometry>
 * 	<name>the_geom</name>
 * 	<type>MultiPolygon</type>
 * 	<srid>4326</srid>
 * </geometry>
 * }
 * </pre>
 * 
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
 *         emmanuel.blondel@fao.org
 * 
 */
public class GSVirtualTableGeomEncoder extends PropertyXMLEncoder {

	/**
	 * Constructs a GSVirtualTableGeomEncoder
	 * 
	 */
	public GSVirtualTableGeomEncoder() {
		super("geometry");
	}

	/**
	 * Set-up quickly a GSVirtualTableGeomEncoder
	 * 
	 * @param name
	 * @param geometryType
	 * @param srid
	 */
	public void setup(String name, String geometryType, String srid) {
		set(VirtualTableGeometry.name.name(), name);
		set(VirtualTableGeometry.type.name(), geometryType);
		set(VirtualTableGeometry.srid.name(), srid);
	}

	/**
	 * Set-up a GSVirtualTableGeomEncoder
	 * 
	 * @param vtGeometryMembers
	 */
	public void setup(Map<VirtualTableGeometry, String> vtGeometryMembers) {
		for (Entry<VirtualTableGeometry, String> vtGeomMember : vtGeometryMembers
				.entrySet()) {
			set(vtGeomMember.getKey().toString(), vtGeomMember.getValue());
		}
	}

	/**
	 * Set a VirtualTable Geometry member
	 * 
	 * @param type
	 * @param value
	 */
	public void setVirtualTableGeometryMember(VirtualTableGeometry type,
			String value) {
		set(type.toString(), value);
	}

	/**
	 * Deletes a VirtualTableGeometry member
	 * 
	 * @param type
	 * @return
	 */
	public boolean delVirtualTableGeometryMember(VirtualTableGeometry type) {
		return ElementUtils.remove(this.getRoot(), get(type.toString()));
	}

	/**
	 * Get the VirtualTableGeometry member value
	 * 
	 * @param type
	 * @return
	 */
	public String getVirtualTableGeometryMember(VirtualTableGeometry type) {
		Element el = get(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}
}