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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */
public abstract class ElementUtils {
	/**
	 * Default logger
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(ElementUtils.class);

	/**
	 * 
	 * @param root the root where to start searching to element to remove
	 * @param el the element to remove (will be set to null since this node is unusable after removal)
	 * @return true if the element EQUALS to the 'el' starting from root
	 *         (including) is found, false if object 'el' is not found
	 * 
	 */
	public static boolean remove(final Element root, Element el) throws IllegalArgumentException {
		if (root == null || el == null) {
			throw new IllegalArgumentException("Bad arguments: root=" + root
					+ " element=" + el);
		}
		// root is the element to remove
		// note: equals checks references on Element type
		if (root.equals(el)) {
			// detach
			el.detach();
			// removing all child
			el.removeContent();			
			
			el=null;

			return true;
		}

		// search for the element to remove
		final Element search = ElementUtils.contains(root, el);
		if (search != null) {
			return remove(search, el);
		}
		return false;
	}

	/**
	 * 
	 * @param root
	 * @param filter
	 * @param depth
	 *            the max depth to search. Use {@link contains(final Element
	 *            root, final Filter filter)} for an infinite depth search
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static List<Element> search(final Element root,
			final Filter filter, final int depth)
			throws IllegalArgumentException {

		if (root == null || filter == null) {
			throw new IllegalArgumentException("Bad arguments: root=" + root
					+ " filter=" + filter + " depth=" + depth);
		}
		final List<Element> ret = new ArrayList<Element>();
		// if match add myself
		if (filter.matches(root)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("LOCATED-> name:" + root.getName() + " text:"
						+ root.getText());
			ret.add(root);
		}
		// check my children
		if (depth != 0) {
			final List<?> childrenList = root.getContent();
			final Iterator<?> it = childrenList.iterator();
			while (it.hasNext()) {
				final Object obj = it.next();
				if (obj instanceof Element) {
					final Element childEl = (Element) obj;
					ret.addAll(search(childEl, filter, depth - 1));
				}
			}
		}
		return ret;
	}

	public static List<Element> search(final Element root, final Filter filter) {
		if (root == null || filter == null) {
			throw new IllegalArgumentException("Bad arguments: root=" + root
					+ " filter=" + filter);
		}
		final List<Element> ret = new ArrayList<Element>();
		// if match add myself
		if (filter.matches(root)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("LOCATED-> name:" + root.getName() + " text:"
						+ root.getText());
			ret.add(root);
		}
		// navigate through children
		final Iterator<?> it = root.getDescendants(filter);
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof Element) {
				Element el = (Element) obj;

				if (LOGGER.isDebugEnabled())
					LOGGER.debug("LOCATED-> name:" + el.getName() + " text:"
							+ el.getText());

				ret.add(el);
			}
		}
		return ret;
	}

	/**
	 * @param root
	 * @param name
	 * @param val
	 * @return the FIRST element matching the passed filter or null
	 */
	public static Element contains(final Element root, final Filter filter) throws IllegalArgumentException {
		if (root == null || filter == null ) {
			throw new IllegalArgumentException("Bad arguments: root=" + root
					+ " name=" + filter);
		}
		final Iterator<Element> it = search(root, filter).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}
	
	/**
	 * @param root
	 * @param name
	 * @param val
	 * @return the FIRST element EQUALS to a node with name and a text value
	 *         starting from root or null
	 */
	public static Element contains(final Element root, final String name,
			final String val) throws IllegalArgumentException {
		if (root == null || name == null || val == null) {
			throw new IllegalArgumentException("Bad arguments: root=" + root
					+ " name=" + name + " val=" + val);
		}
		final Filter filter = new Filter() {
			private static final long serialVersionUID = 1L;

			public boolean matches(Object obj) {
				if (obj instanceof Element) {
					final Element el = ((Element) obj);
					if (el.getName().equals(name) && el.getText().equals(val)) {
						return true;
					}
				}
				return false;
			}
		};
		final Iterator<Element> it = search(root, filter).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	
	/**
         * return the FIRST element with name equals to the passed key
         * 
         * @param root
         * @param name
         * @return
         */
        public static Element contains(final Element root, final String name){
            return contains(root, name, -1);
        }
	
	/**
	 * return the FIRST element with name equals to the passed key
	 * 
	 * @param root
	 * @param name
	 * @return
	 */
	public static Element contains(final Element root, final String name, final int deep)
			throws IllegalArgumentException {
		if (root == null || name == null) {
			throw new IllegalArgumentException("Bad arguments: root=" + root
					+ " name=" + name);
		}

		final Filter filter = new Filter() {
			private static final long serialVersionUID = 1L;

			public boolean matches(Object obj) {
				if (obj instanceof Element) {
					final Element el = ((Element) obj);
					if (el.getName().equals(name)) {
						return true;
					}
				}
				return false;
			}
		};
		final Iterator<Element> it = search(root, filter, deep).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	/**
	 * @param root
	 * @param el
	 * @return the FIRST element EQUALS to the 'el' starting from root or null
	 *         This tests for equality of this Content object to the supplied
	 *         object. Content items are considered equal only if they are
	 *         referentially equal (i.e. the same object).
	 */
	public static Element contains(final Element root, final Element el)
			throws IllegalArgumentException {
		if (root == null || el == null) {
			throw new IllegalArgumentException("Bad arguments: root=" + root
					+ " element=" + el);
		}
		final Filter filter = new Filter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean matches(Object obj) {
				if (obj instanceof Element) {
					final Element element = ((Element) obj);
					if (element.equals(el)) {
						return true;
					}
				}
				return false;
			}
		};
		final Iterator<Element> it = search(root, filter).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}
}
