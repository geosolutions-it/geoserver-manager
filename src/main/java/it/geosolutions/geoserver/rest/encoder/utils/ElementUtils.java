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

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */
public abstract class ElementUtils {

	/**
	 * 
	 * @param root
	 * @param el
	 * @return true if the FIRST element EQUALS to the 'el' starting from root
	 *         is found AND can be deleted. If it is the root true is returned
	 *         and all of its child are removed.
	 * 
	 */
	public static boolean remove(Element root, final Element el) {
		if (root.equals(el)) {
			if (!el.isRootElement()) {
				root = el.getParentElement();
				// removing all child
				el.removeContent();
				root.removeContent(el);
			} else {
				// log warn this is root!
				// removing all child
				el.removeContent();
			}
		} else if ((root = ElementUtils.contains(root, el)) != null) {
			return remove(root, el);
		} else {
			return false;
		}

		return true;
	}

	/**
	 * @param root
	 * @param el
	 * @return the FIRST element EQUALS to the 'el' starting from root or null
	 */
	public static Element contains(final Element root, final Element el) {

		if (root != null && el != null) {
			if (root.equals(el))
				return root;

			final List<Element> childrenList = root.getChildren();
			if (childrenList.size() > 0) {
				Iterator<Element> it = childrenList.iterator();
				while (it.hasNext()) {
					final Element ret;
					if ((ret = contains(it.next(), el)) != null)
						return ret;
				}
			}
		}
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
			final String val) {
		if (root != null && name != null && val != null) {
			if (root.getName().equals(name) && root.getText().equals(val))
				return root;

			final List<Element> childrenList = root.getChildren();
			if (childrenList.size() > 0) {
				Iterator<Element> it = childrenList.iterator();
				while (it.hasNext()) {
					final Element ret;
					if ((ret = contains(it.next(), name, val)) != null)
						return ret;
				}
			}
		}
		return null;
	}

	/**
	 * return the FIRST element with name equals to the passed key
	 * 
	 * @param root
	 * @param name
	 * @return
	 */
	public static Element contains(final Element root, final String name) {
		if (root != null && name != null) {
			if (root.getName().equals(name))
				return root;

			final List<Element> childrenList = root.getChildren();
			if (childrenList.size() > 0) {
				Iterator<Element> it = childrenList.iterator();
				while (it.hasNext()) {
					final Element ret;
					if ((ret = contains(it.next(), name)) != null)
						return ret;
				}
			}
		}
		return null;
	}

}
