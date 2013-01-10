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

package it.geosolutions.geoserver.rest.encoder.utils;

import org.jdom.Element;

/**
 * Creates an XML document by mapping properties to XML nodes.<br/>
 * You can set the root element name in the constructor. Any key/value pair will
 * be encoded as {@code <key>value</key>} node. <br/>
 * <br/>
 * 
 * <h4>Nested nodes</h4> Any key containing one or more slash ("/") will be
 * encoded as nested nodes; <br/>
 * e.g.:
 * 
 * <PRE>
 * {@code 
 *          key = "k1/k2/k3", value = "value" }
 * </pre>
 * 
 * will be encoded as
 * 
 * <PRE>
 * {@code        <k1><k2><k3>value</k3></k2></k1> }
 * </pre>
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class PropertyXMLEncoder extends XmlElement {

	public PropertyXMLEncoder(final String rootName) {
		super(rootName);
	}

	protected Element get(final String key, int deep) {
            return get(getRoot(), key);
        }
	
	protected Element get(final String key) {
		return get(getRoot(), key);
	}
	
	private Element get(final Element el, final String key) {
		if (el==null)
			return null;
		if (key.contains("/")) {
			final int i = key.indexOf("/");
			final String parentName = key.substring(0, i);
			final String newkey = key.substring(i + 1);
			return get(ElementUtils.contains(el, parentName),newkey);
		} else {
			return ElementUtils.contains(el, key);
		}
	}

	protected void set(final String key, final String value) {
		if (key != null && value != null) {
			set(getRoot(), key, value);
		}
	}

	private void set(final Element e, final String key, final String value) {
		if (key.contains("/")) {
			final int i = key.indexOf("/");
			final String childName = key.substring(0, i);
			final String newkey = key.substring(i + 1);

			Element child = e.getChild(childName);
			if (child == null) {
				child = new Element(childName);
				e.addContent(child);
				add(child, newkey, value);
			}
			set(child, newkey, value);
		} else {
			Element pp = null;
			if ((pp = ElementUtils.contains(e, key)) == null)
				add(e, key, value);
			else {
				ElementUtils.remove(pp, pp);
				add(e, key, value);
			}
		}
	}

	protected void add(final String key, final String value) {
		if (key != null && value != null) {
			add(this.getRoot(), key, value);
		}
	}

	private void add(Element e, String key, String value) {
		if (key.contains("/")) {
			final int i = key.indexOf("/");
			final String childName = key.substring(0, i);
			final String newkey = key.substring(i + 1);

			Element child = e.getChild(childName);
			if (child == null) {
				child = new Element(childName);
				e.addContent(child);
			}

			add(child, newkey, value);

		} else {
			e.addContent(new Element(key).setText(value));
		}

	}

	// public void set(final String key, final String value) {
	// if (key != null && value != null) {
	// set(getRoot(), key, value);
	// }
	// }
	//
	// private void set(final Element e, final String key, final String value){
	// if (!key.contains("/")) {
	// Element pp = null;
	// if ((pp = contains(key)) == null)
	// add(e,key, value);
	// else {
	// remove(pp);
	// add(e,key, value);
	// }
	// } else {
	// final int i = key.indexOf("/");
	// final String childName = key.substring(0, i);
	// final String newkey = key.substring(i + 1);
	//
	// Element child = e.getChild(childName);
	// if (child == null) {
	// child = new Element(childName);
	// e.addContent(child);
	// add(child,newkey,value);
	// }
	// set(child, newkey, value);
	// }
	// }
	//
	// public void add(final String key, final String value) {
	// if (key != null && value != null) {
	// add(this.getRoot(), key, value);
	// }
	// }
	//
	// private void add(Element e, String key, String value) {
	// if (!key.contains("/")) {
	// e.addContent(new Element(key).setText(value));
	// } else {
	// final int i = key.indexOf("/");
	// final String childName = key.substring(0, i);
	// final String newkey = key.substring(i + 1);
	//
	// Element child = e.getChild(childName);
	// if (child == null) {
	// child = new Element(childName);
	// e.addContent(child);
	// }
	//
	// add(child, newkey, value);
	// }
	//
	// }

}
