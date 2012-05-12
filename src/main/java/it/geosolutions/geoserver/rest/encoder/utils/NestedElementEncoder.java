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

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * Encodes lists of entries with key attribute. <br/>
 * e.g.:
 * 
 * <PRE>
 * {@code 
 *  <listName>
 *   <entry key="k1">val1</entry>
 *   <entry key="k2">val2</entry>
 *   <entry key="k3">val3</entry>
 * </listName>}
 * 
 * <PRE>
 * 
 * This can be also add compounded Elements <br/>
 * e.g.:
 * 
 * <PRE>
 * {@code 
 * <listName>
 *  <entry key="time">
 *   	<dimensionInfo>
 * 			<enabled>false</enabled>
 * 		</dimensionInfo>
 * 	</entry>
 * 	<entry key="elevation">
 * 		<dimensionInfo>
 * 			<enabled>true</enabled>
 * 			<attribute>ele</attribute>
 * 			<presentation>LIST</presentation>
 * 		</dimensionInfo>
 * 	</entry>
 * </listName>}
 * 
 * This can be also add list of compounded Elements <br/>
 * 
 * <listName>
 *  <entry>
 *   	<String>AllowMultithreading</String>
 * 		<enabled>false</enabled>
 * 	</entry>
 * 	<entry>
 * 		
 * 			<enabled>true</enabled>
 * 			<attribute>ele</attribute>
 * 			<presentation>LIST</presentation>
 * 		</dimensionInfo>
 * 	</entry>
 * </listName>}
 * 
 * <PRE>
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */
public class NestedElementEncoder extends XmlElement {
	public final static String ENTRY = "entry";
	public final static String KEY = "key";

	static class NestedElementFilter implements Filter {
		private static final long serialVersionUID = 1L;
		private final String key;
		private final String value;
		private final Element root;

		/**
		 * if key is null we only check for children name if value is null we
		 * only check for key attribute
		 * 
		 * @param root
		 * @param key
		 * @param value
		 */
		public NestedElementFilter(Element root, String key, String value) {
			this.key = key;
			this.root = root;
			this.value = value;
		}

		public boolean matches(Object obj) {
			if (obj instanceof Element) {
				final Element el = ((Element) obj);
				if (root.isAncestor(el)) {
					if (el.getName().equals(ENTRY)/* && el.getText().equals(value) */) {
						boolean keyCheck=true;
						if (key != null) {
							if (el.getAttribute(KEY).getValue().equals(key)) {
								keyCheck=true;
							} else {
								keyCheck=false;
							}
						}
						if (value != null)
							return keyCheck&&checkChilds(el, value);
						else
							return keyCheck;
					}
				}
			}
			return false;
		}

		private static boolean checkChilds(Element el, String value) {
			final List<Element> childList = el.getChildren();
			final Iterator<Element> childIt = childList.iterator();
			while (childIt.hasNext()) {
				final Element child = childIt.next();
				if (child.getName().equals(value)) {
					return true;
				}
			}
			return false;
		}
	};

	public NestedElementEncoder(String listName) {
		super(listName);
	}

	public void set(final String key, final String value) {
		// if some previous similar object is found
		final Element search;
		if ((search = ElementUtils.contains(getRoot(), new NestedElementFilter(
				getRoot(), key, null))) != null) {
			// remove it
			ElementUtils.remove(getRoot(), search);
		}
		// add the new entry
		add(key, value);
	}

	public void set(final String key, final Element value) {
		// if some previous similar object is found
		final Element search;
		if ((search = ElementUtils.contains(getRoot(), new NestedElementFilter(
				getRoot(), key, value.getName()))) != null) {
			// remove it
			ElementUtils.remove(getRoot(), search);
		}
		// add the new entry
		add(key, value);
	}

	public void add(final String key, final Element value) {
		final Element entryElem = new Element(ENTRY);
		if (key != null)
			entryElem.setAttribute(KEY, key);

		entryElem.addContent(value);

		this.addContent(entryElem);
	}

	public void add(final String key, final String value) {
		final Element entryElem = new Element(ENTRY);

		if (key != null)
			entryElem.setAttribute(KEY, key);

		entryElem.setText(value);

		this.addContent(entryElem);
	}

	public void add(final String key, final List<Element> list) {
		final Element entryElem = new Element(ENTRY);
		if (key != null)
			entryElem.setAttribute(KEY, key);

		// final Iterator<Element> it=list.iterator();
		// while (it.hasNext()){
		// final Element child=it.next();
		entryElem.addContent(list);
		// }

		this.addContent(entryElem);
	}

	public void set(final String key, final List<Element> value) {
		// if some previous similar object is found
		final Element search;
		if ((search = ElementUtils.contains(getRoot(), new NestedElementFilter(
				getRoot(), key, value.get(0).getValue()))) != null) {
			// remove it
			ElementUtils.remove(search, search);
		}
		// add the new entry
		add(key, value);
	}

	public boolean remove(final String key) {
		// if some previous similar object is found
		final Element search;
		if ((search = ElementUtils.contains(getRoot(), new NestedElementFilter(
				getRoot(), key, null))) != null) {
			return ElementUtils.remove(search, search);
		} else
			return false;
	}
}
