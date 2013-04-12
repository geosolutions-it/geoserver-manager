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
package it.geosolutions.geoserver.rest.encoder.metadata.virtualtable;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * GSVirtualTableGeomEncoder - Encodes a metadata VirtualTable geometry for a
 * GeoServer featureType, as follows:
 * 
 * <pre>
 * {
 * 	@code
 * 	final GSVirtualTableGeomEncoder vtGeom = new GSVirtualTableGeomEncoder();
 * 	vtGeom.setVirtualTableGeometryMember(VTGeometry.name, "the_geom");
 * 	vtGeom.setVirtualTableGeometryMember(VTGeometry.type, "MultiPolygon");
 * 	vtGeom.setVirtualTableGeometryMember(VTGeometry.srid, "4326");
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
public class VTGeometryEncoder extends XmlElement {

	/** 
	 * A class to filter the VirtualTable geometries by name
	 *
	 */
	public static class filterByName implements Filter {

		final private String key;

		public filterByName(String name) {
			this.key = name;
		}

		private static final long serialVersionUID = 1L;

		public boolean matches(Object obj) {
			Element el = ((Element) obj)
					.getChild(VTGeometry.name.toString());
			if (el != null && el.getTextTrim().equals(key)) {
				return true;
			}
			return false;
		}
	}

	/** 
	 * Get a Filter using the VTGeometry name
	 * 
	 * @param name
	 * @return the filter
	 */
	public static Filter getFilterByName(String name) {
		return new filterByName(name);
	}
	
	/**
	 * Constructs a GSVirtualTableGeomEncoder
	 * 
	 */
	public VTGeometryEncoder() {
		super("geometry");
	}

	/**
	 * Constructs quickly a VTGeometryEncoder
	 * 
	 * @param name
	 * @param geometryType
	 * @param srid
	 */
	public VTGeometryEncoder(String name, String geometryType, String srid) {
		super("geometry");
		this.setup(name, geometryType, srid);
	}
	
	/**
	 * Set-up quickly a GSVirtualTableGeomEncoder
	 * 
	 * @param name
	 * @param geometryType
	 * @param srid
	 */
	protected void setup(String name, String geometryType, String srid) {
		set(VTGeometry.name.name(), name);
		set(VTGeometry.type.name(), geometryType);
		set(VTGeometry.srid.name(), srid);
	}

	/**
	 * Set-up a GSVirtualTableGeomEncoder
	 * 
	 * @param vtGeometryMembers
	 */
	public void setup(Map<VTGeometry, String> vtGeometryMembers) {
		for (Entry<VTGeometry, String> vtGeomMember : vtGeometryMembers
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
	public void setVirtualTableGeometryMember(VTGeometry type,
			String value) {
		set(type.toString(), value);
	}

	/**
	 * Deletes a VirtualTableGeometry member
	 * 
	 * @param type
	 * @return
	 */
	public boolean delVirtualTableGeometryMember(VTGeometry type) {
		return ElementUtils.remove(this.getRoot(), this.getRoot().getChild(type.toString()));
	}

	/**
	 * Get the VirtualTableGeometry member value
	 * 
	 * @param type
	 * @return
	 */
	public String getVirtualTableGeometryMember(VTGeometry type) {
		Element el = this.getRoot().getChild(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}
}