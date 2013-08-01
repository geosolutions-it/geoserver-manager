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

import java.util.List;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * GSVirtualTableEncoder - Encodes a metadata VirtualTable for a GeoServer
 * featureType.
 * 
 * Example (based on the example provided in the Geoserver documentation - see
 * {@link http://docs.geoserver.org/latest/en/user/data/database/sqlview.html#
 * parameterizing-sql-views}):
 * 
 * <pre>
 * {
 * 	@code
 * 	// Set-up the vtGeom
 * 	final VTGeometryEncoder vtGeom = new VTGeometryEncoder();
 * 	vtGeom.setName("the_geom");
 * 	vtGeom.setType("MultiPolygon");
 * 	vtGeom.setSrid("4326");
 * 
 * 	// Set-up 2 virtual table parameters
 * 	final VTParameterEncoder vtParam1 = new VTParameterEncoder("high",
 * 			"100000000", "^[\\d]+$");
 * 	final VTParameterEncoder vtParam2 = new VTParameterEncoder("low", "0",
 * 			"^[\\d]+$");
 * 
 * 	// sql
 * 	String sql = "select gid, state_name, the_geom from pgstates where persons between %low% and %high%";
 * 
 * 	// Set-up the virtual table
 * 	final GSVirtualTableEncoder vte = new GSVirtualTableEncoder();
 * 	vte.setName("popstates");
 * 	vte.setSql("select gid, state_name, the_geom from pgstates where persons between %low% and %high%");
 * 	vte.addKeyColumn("gid");
 * 	vte.addVirtualTableGeometry(vtGeom);
 * 	vte.addVirtualTableParameter(vtParam1);
 * 	vte.addVirtualTableParameter(vtParam2);
 * }
 * </pre>
 * 
 * For this example, the XML output is:
 * 
 * <pre>
 * {@code
 * <virtualTable>
 * 	<name>popstates</name>
 * 	<sql>select gid, state_name, the_geom from pgstates where persons between %low% and %high%</sql>
 * 	<geometry>
 * 		<name>the_geom</name>
 * 		<type>MultiPolygon</type>
 * 		<srid>4326</srid>
 * 	</geometry>
 *  <keyColumn>gid</keyColumn>
 * 	<parameter>
 * 		<name>high</name>
 * 		<defaultValue>100000000</defaultValue>
 * 		<regexpValidator>^[\d]+$</regexpValidator>
 * 	</parameter>
 * 	<parameter>
 * 		<name>low</name>
 * 		<defaultValue>0</defaultValue>
 * 		<regexpValidator>^[\d]+$</regexpValidator>
 * 	</parameter>
 * </virtualTable>
 * }
 * </pre>
 * 
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
 *         emmanuel.blondel@fao.org
 * 
 */
public class GSVirtualTableEncoder extends XmlElement {

	public final static String NAME = "name";
	public final static String SQL = "sql";
	public final static String KEYCOLUMN = "keyColumn";

	/**
	 * Constructs a GSVirtualTableEncoder
	 */
	public GSVirtualTableEncoder() {
		super("virtualTable");	
	}

	/**
	 * Constructs directly a GSVirtualTableEncoder
	 * 
	 * @param name (required)
	 * @param sql (required)
	 * @param keyColumns (optional)
	 * @param geomEncList (optional for geometryless sql view)
	 * @param paramEncList (optional)
	 */
	public GSVirtualTableEncoder(String name, String sql, List<String> keyColumns,
			List<VTGeometryEncoder> geomEncList,
			List<VTParameterEncoder> paramEncList){
		super("virtualTable");
		this.setup(name, sql, keyColumns, geomEncList, paramEncList);
	}		
	
	/**
	 * Set-up quickly a GSVirtualTableEncoder
	 * 
	 * @param name (must be the same as the featureType nativeName)
	 * @param sql
	 * @param keyColumns
	 * @param geomEncList
	 * @param paramEncList
	 */
	protected void setup(String name, String sql, List<String> keyColumns,
			List<VTGeometryEncoder> geomEncList,
			List<VTParameterEncoder> paramEncList) {

		setName(name);
		setSql(sql);

		if (keyColumns != null) {	
			for (String pk : keyColumns) {
				addKeyColumn(pk);
			}
		}

		if (geomEncList != null) {
			for (VTGeometryEncoder geomEnc : geomEncList) {
				addVirtualTableGeometry(geomEnc);
			}
		}

		if (paramEncList != null) {
			for(VTParameterEncoder paramEnc : paramEncList){
				addVirtualTableParameter(paramEnc);
			}
		}
	}

	/**
	 * Add the 'name' node with a text value from 'name'
	 * 
	 * @note REQUIRED to configure a virtual table
	 */
	protected void addName(final String name){
		add(NAME, name);
	}
	
	/**
	 * Set or modify the 'name' node with a text value from 'name'
	 * 
	 * @note REQUIRED to configure a virtual table
	 */
	public void setName(final String name) {
		set(NAME, name);
	}	
	
	/** 
	 * Get the name of the virtual table
	 * 
	 * @return
	 */
	public String getName() {
		final Element nameNode = ElementUtils.contains(getRoot(), NAME, 1);
		if (nameNode != null)
			return nameNode.getText();
		else
			return null;
	}
	
	/**
	 * Add the 'sql' node with a text value from 'sql'
	 * 
	 * @note REQUIRED to configure a virtual table
	 */
	protected void addSql(final String sql){
		add(SQL, sql);
	}
	
	/**
	 * Set or modify the 'sql' node with a text value from 'sql'
	 * 
	 * @note REQUIRED to configure a virtual table
	 */
	public void setSql(final String sql) {
		set(SQL, sql);
	}	
	
	/** 
	 * Get the sql query
	 * 
	 * @return
	 */
	public String getSql() {
		final Element sqlNode = ElementUtils.contains(getRoot(), SQL, 1);
		if (sqlNode != null)
			return sqlNode.getText();
		else
			return null;
	}

	/**
	 * Adds a keyColumn
	 * 
	 * @param keycolumn
	 */
	public void addKeyColumn(String keycolumn){
		final Element el = new Element(KEYCOLUMN);
		el.setText(keycolumn);
		this.getRoot().addContent(el);
	}
	
	/**
	 * Deletes a keyColumn
	 * 
	 * @param keycolumn
	 * @return true if the keycolumn was removed
	 */
	public boolean delKeyColumn(final String keycolumn){
		final Element el = new Element(KEYCOLUMN);
		el.setText(keycolumn);
		return (this.getRoot().removeContent(new Filter() {
			private static final long serialVersionUID = 1L;

			public boolean matches(Object obj) {
				if (((Element) obj).getText().equals(keycolumn)) {
					return true;
				}
				return false;
			}
		})).size() == 0 ? false : true;
	}
	
	/**
	 * Adds a geometry to the VirtualTable
	 * 
	 * @param geomEnc
	 */
	public void addVirtualTableGeometry(VTGeometryEncoder geomEnc){
		this.getRoot().addContent(geomEnc.getRoot());
	}
	
	/**
	 * Adds quickly a geometry to the virtual table
	 * 
	 * @param name
	 * @param geometryType
	 * @param srid
	 */
	public void addVirtualTableGeometry(String name, String geometryType, String srid){
		final VTGeometryEncoder gEnc = new VTGeometryEncoder();
		gEnc.setup(name, geometryType, srid);
		this.getRoot().addContent(gEnc.getRoot());
	}
	
	/**
	 * Deletes a geometry from the VirtualTable
	 * 
	 * @param name
	 * @return true if the geometry was removed, otherwise false
	 */
	public boolean delVirtualTableGeometry(String name){
		return (this.getRoot()
				.removeContent(VTGeometryEncoder
						.getFilterByName(name))).size() == 0 ? false
				: true;
	}
	
	/**
	 * Adds a parameter to the VirtualTable
	 * 
	 * @param paramEnc
	 */
	public void addVirtualTableParameter(VTParameterEncoder paramEnc){
		this.getRoot().addContent(paramEnc.getRoot());
	}

	/**
	 * Adds quickly a parameter to the VirtualTable
	 * 
	 * @param name
	 * @param defaultValue
	 * @param regexpValidator
	 */
	public void addVirtualTableParameter(String name, String defaultValue, String regexpValidator){
		final VTParameterEncoder pEnc = new VTParameterEncoder();
		pEnc.setup(name, defaultValue, regexpValidator);
		this.getRoot().addContent(pEnc.getRoot());
	}
	
	/**
	 * Deletes a parameter from the VirtualTable
	 * 
	 * @param name
	 * @return
	 */
	public boolean delVirtualTableParameter(String name){
		return (this.getRoot()
				.removeContent(VTParameterEncoder
						.getFilterByName(name))).size() == 0 ? false
				: true;
	}
	
	
	/*
	 * a method to check either the virtual parameters as specified in the sql
	 * query map those encoded in VTParameterEncoder, i.e. check the number and
	 * names of parameters.
	 */
	public boolean validate() throws IllegalStateException{
		 throw new UnsupportedOperationException("not yet implemented");
	}

}
