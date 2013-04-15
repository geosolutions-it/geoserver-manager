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

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * VTGeometryEncoder - Encodes a metadata VirtualTable geometry for a
 * GeoServer featureType, as follows:
 * 
 * <pre>
 * {
 * 	@code
 * 	final VTGeometryEncoder vtGeom = new VTGeometryEncoder();
 * 	vtGeom.setName("the_geom");
 * 	vtGeom.setType("MultiPolygon");
 * 	vtGeom.setSrid("4326");
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
	private static class filterByName implements Filter {

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
	 * @param name (required)
	 * @param geometryType (required)
	 * @param srid (required)
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
		setName(name);
		setType(geometryType);
		setSrid(srid);
	}

	/**
	 * Set a VirtualTable Geometry member
	 * 
	 * @param type
	 * @param value
	 */
	protected void setMember(VTGeometry type,
			String value) {
		set(type.toString(), value);
	}

	/**
	 * Set a geometry name
	 * 
	 * @param name
	 */
	public void setName(String name){
		this.setMember(VTGeometry.name, name);
	}
	
	/**
	 * Set a geometry type
	 * 
	 * @param type
	 */
	public void setType(String type){
		this.setMember(VTGeometry.type, type);
	}
	
	/**
	 * Set a geometry srid
	 * 
	 * @param srid
	 */
	public void setSrid(String srid){
		this.setMember(VTGeometry.srid, srid);
	}
	
	/**
	 * Deletes a VirtualTableGeometry member
	 * 
	 * @param type
	 * @return true if removed, false otherwise
	 */
	protected boolean delMember(VTGeometry type) {
		return ElementUtils.remove(this.getRoot(), this.getRoot().getChild(type.toString()));
	}
	
	/**
	 * Deletes the name
	 * 
	 * @return true if removed, false otherwise
	 */
	public boolean delName(){
		return this.delMember(VTGeometry.name);
	}
	
	/**
	 * Deletes the type
	 * 
	 * @return true if removed, false otherwise
	 */
	public boolean delType(){
		return this.delMember(VTGeometry.type);
	}

	/**
	 * Deletes the srid
	 * 
	 * @return true if removed, false otherwise
	 */
	public boolean delSrid(){
		return this.delMember(VTGeometry.srid);
	}
	
	/**
	 * Get the VirtualTableGeometry member value
	 * 
	 * @param type
	 * @return
	 */
	protected String getMember(VTGeometry type) {
		Element el = this.getRoot().getChild(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}
	
	/**
	 * Get the geometry column name
	 * 
	 * @return
	 */
	public String getName(){
		return this.getMember(VTGeometry.name);
	}
	
	/**
	 * Get the geometry column type
	 * 
	 * @return
	 */
	public String getType(){
		return this.getMember(VTGeometry.type);
	}
	
	/**
	 * Get the geometry column srid
	 * 
	 * @return
	 */
	public String getSrid(){
		return this.getMember(VTGeometry.srid);
	}
}