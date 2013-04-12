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
 * GSVirtualTableParamEncoder - Encodes a metadata VirtualTable parameter for a
 * GeoServer featureType, as follows:
 * 
 * <pre>
 * { @code
 * 	final VTParameterEncoder vtParam = new VTParameterEncoder();
 * 	vtParam.setVirtualTableParamMember(VTParameter.name, "fieldname");
 * 	vtParam.setVirtualTableParamMember(VTParameter.defaultValue,
 * 			"default_value");
 * 	vtParam.setVirtualTableParamMember(VTParameter.regexpValidator,
 * 			"^[\\w\\d\\s]+$");
 * }
 * </pre>
 * 
 * For this example, the XML output is:
 * 
 * <pre>
 * { @code
 * <parameter>
 * 	<name>fieldname</name>
 * 	<defaultValue>default_value</defaultValue>
 * 	<regexpValidator>^[\w\d\s]+$</regexpValidator>
 * </parameter>
 * }
 * </pre>
 * 
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
 *         emmanuel.blondel@fao.org
 * 
 */
public class VTParameterEncoder extends XmlElement {

	/** 
	 * A class to filter the VirtualTable parameters by name
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
					.getChild(VTParameter.name.toString());
			if (el != null && el.getTextTrim().equals(key)) {
				return true;
			}
			return false;
		}
	}

	/** 
	 * Get a Filter using the VTParameter name
	 * 
	 * @param name
	 * @return the filter
	 */
	public static Filter getFilterByName(String name) {
		return new filterByName(name);
	}
	
	/**
	 * Constructs a GSVirtualTableParamEncoder
	 */
	public VTParameterEncoder() {
		super("parameter");
	}

	/**
	 * Constructs quickly a VTParameterEncoder
	 * 
	 * @param name
	 * @param defaultValue
	 * @param regexpValidator
	 */
	public VTParameterEncoder(String name, String defaultValue, String regexpValidator){
		super("parameter");
		this.setup(name, defaultValue, regexpValidator);
	}
	
	/**
	 * Set-up quickly a VirtualTable parameter
	 * 
	 * @param name
	 * @param defaultValue
	 * @param regexpValidator
	 */
	protected void setup(String name, String defaultValue, String regexpValidator) {
		set(VTParameter.name.name(), name);
		set(VTParameter.defaultValue.name(), defaultValue);
		set(VTParameter.regexpValidator.name(), regexpValidator);
	}

	/**
	 * Set-up a VirtualTable parameter
	 * 
	 * @param vtParamMembers
	 */
	public void setup(Map<VTParameter, String> vtParamMembers) {
		for (Entry<VTParameter, String> vtParamMember : vtParamMembers
				.entrySet()) {
			set(vtParamMember.getKey().toString(), vtParamMember.getValue());
		}
	}

	/**
	 * Set a VirtualTableParameter member
	 * 
	 * @param type
	 * @param value
	 */
	public void setVirtualTableParamMember(VTParameter type,
			String value) {
		set(type.toString(), value);
	}

	/**
	 * Deletes a VirtualTableParameter member
	 * 
	 * @param type
	 * @return
	 */
	public boolean delVirtualTableParamMember(VTParameter type) {
		return ElementUtils.remove(this.getRoot(), this.getRoot().getChild(type.toString()));
	}

	/**
	 * Get a VirtualTableParameter member
	 * 
	 * @param type
	 * @return
	 */
	public String getVirtualTableParamMember(VTParameter type) {
		Element el = getRoot().getChild(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}
}