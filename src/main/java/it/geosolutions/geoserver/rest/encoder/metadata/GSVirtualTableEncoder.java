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

import java.util.ArrayList;
import java.util.List;

import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

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
 * 	final GSVirtualTableGeomEncoder vtGeom = new GSVirtualTableGeomEncoder();
 * 	vtGeom.setup("the_geom", "MultiPolygon", "4326");
 * 
 * 	// Set-up 2 virtual table parameters
 * 	final GSVirtualTableParamEncoder vtParam1 = new GSVirtualTableParamEncoder();
 * 	vtParam1.setup("high", "100000000", "^[\\d]+$");
 * 	final GSVirtualTableParamEncoder vtParam2 = new GSVirtualTableParamEncoder();
 * 	vtParam2.setup("low", "0", "^[\\d]+$");
 * 
 * 	// sql
 * 	String sql = "select gid, state_name, the_geom from pgstates where persons between %low% and %high%";
 * 
 * 	// Set-up the virtual table
 * 	final GSVirtualTableEncoder vte = new GSVirtualTableEncoder();
 * 	vte.setup("popstates", sql, null, Arrays.asList(vtGeom),
 * 			Arrays.asList(vtParam1, vtParam2));
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
public class GSVirtualTableEncoder extends PropertyXMLEncoder {

	private String name;
	private String sql;
	private List<String> keyColumns;
	private List<GSVirtualTableGeomEncoder> geomEncList;
	private List<GSVirtualTableParamEncoder> paramEncList;

	/**
	 * Constructs a GSVirtualTableEncoder
	 */
	public GSVirtualTableEncoder() {
		super("virtualTable");
	}

	/**
	 * Set-up quickly a GSVirtualTableEncoder. This unique entry point to set-up
	 * is aimed to ensure that the order of metadata elements is well
	 * maintained, and avoid errors at publication time in Geoserver.
	 * 
	 * @param name (must be the same as the featureType nativeName)
	 * @param sql
	 * @param keyColumns
	 * @param geomEncList
	 * @param paramEncList
	 */
	public void setup(String name, String sql, List<String> keyColumns,
			List<GSVirtualTableGeomEncoder> geomEncList,
			List<GSVirtualTableParamEncoder> paramEncList) {

		this.getRoot().removeContent();
		this.keyColumns = null;
		this.geomEncList = null;
		this.paramEncList = null;

		this.name = name;
		this.sql = sql;
		set("name", this.name);
		set("sql", this.sql);

		if (keyColumns != null) {
			this.keyColumns = new ArrayList<String>();
			this.keyColumns.addAll(keyColumns);
			for (String pk : this.keyColumns) {
				add("keyColumn", pk);
			}

		}

		if (geomEncList != null) {
			this.geomEncList = new ArrayList<GSVirtualTableGeomEncoder>();
			this.geomEncList.addAll(geomEncList);
			for (GSVirtualTableGeomEncoder geomEnc : this.geomEncList) {
				addContent(geomEnc.getRoot());
			}
		}

		if (paramEncList != null) {
			this.paramEncList = new ArrayList<GSVirtualTableParamEncoder>();
			this.paramEncList.addAll(paramEncList);
			for (GSVirtualTableParamEncoder paramEnc : this.paramEncList) {
				addContent(paramEnc.getRoot());
			}
			;
		}

	}

	/**
	 * get Name
	 * 
	 * @return the name of the virtual table
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * get SQL query
	 * 
	 * @return the sql query of the virtual table
	 */
	public String getSql() {
		return this.sql;
	}

	/**
	 * Get the list of key columns
	 * 
	 * @return
	 */
	public List<String> getKeyColumnList(){
		return this.keyColumns;
	}
	
	
	/**
	 * get the list of GSVirtualTableGeomEncoder
	 * 
	 * @return
	 */
	public List<GSVirtualTableGeomEncoder> getVirtualTableGeomEncoderList() {
		return this.geomEncList;
	}

	/**
	 * get the list of VirtualTableParamEncoder
	 * 
	 * @return
	 */
	public List<GSVirtualTableParamEncoder> getVirtualTableParamEncoderList() {
		return this.paramEncList;
	}

	// TODO (eventually) utils method to check consistency between SQL view
	// params provided by the sql string
	// and the virtual table parameters set up in parallel
	public boolean checkVirtualTableParams() {
		return false;
	}

}
