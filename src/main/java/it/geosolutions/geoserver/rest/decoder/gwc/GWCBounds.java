/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2015 GeoSolutions S.A.S.
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

package it.geosolutions.geoserver.rest.decoder.gwc;

import it.geosolutions.geoserver.rest.Util;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

/**
 * Parse <TT>Bounds</TT>s returned as XML REST objects.
 *
 * <P>This is the XML Schema representation:
 * <PRE>
 * {@code
    <xs:complexType name="Bounds">
      <xs:sequence>
        <xs:element name="coords" type="gwc:coords"/>
      </xs:sequence>
      
      <xs:complexType name="coords">
        <xs:sequence>
          <xs:element maxOccurs="4" minOccurs="4" name="double" type="xs:double"/>
        </xs:sequence>
      </xs:complexType>
    </xs:complexType>
 * }</PRE>
 * @author Nazzareno Sileno
 */
public class GWCBounds {
    
    private final Element boundsElem;

    public static GWCBounds build(String response) {
        if (response == null) {
            return null;
        }

        Element pb = JDOMBuilder.buildElement(response);
        if (pb != null) {
            return new GWCBounds(pb);
        } else {
            return null;
        }
    }

    public GWCBounds(Element boundsElem) {
        this.boundsElem = boundsElem;
    }

    public List<Double> getCoords() {
        List<Double> coords = null;
        final Element coordsRoot = boundsElem.getChild("coords");
        if(coordsRoot != null){
            coords = new ArrayList<Double>(4);
            for (Element listItem : (List<Element>) Util.safeList(coordsRoot.getChildren())) {
                coords.add(Util.getDoubleValueFromText(listItem.getChildText("double")));
            }
        }
        return coords;
    }
    
    public double getMinX() {
        return this.getCoords().get(0);
    }

    public double getMinY() {
        return this.getCoords().get(1);    
    }
    
    public double getMaxX() {
        return this.getCoords().get(2);
    }
    
    public double getMaxY() {
        return this.getCoords().get(3);
    }
    
    public double getWidth() {
        return this.getCoords().get(2) - this.getCoords().get(0);
    }

    public double getHeight() {
        return this.getCoords().get(3) - this.getCoords().get(1);
    }
    
    /**
     * Minimal sanity check
     * 
     * @return whether min x < max x, min y < max y
     */
    public boolean isSane() {
        return (getMinX() < getMaxX() && getMinY() < getMaxY());
    }

    public boolean isNull() {
        return (getMinX() > getMaxX() || getMinY() > getMaxY());
    }
    
    /**
     * Outputs a string suitable for logging and other human-readable tasks
     * 
     * @return a readable string
     */
    public String getReadableString() {
        return "Min X: " + getMinX() + " Min Y: " + getMinY() + 
                " Max X: " + getMaxX() + " Max Y: " + getMaxY();
    }
    
    public String toKMLLatLonBox() {
        return "<LatLonBox>" + toKMLBox() + "</LatLonBox>";
    }

    public String toKMLLatLonAltBox() {
        return "<LatLonAltBox>" + toKMLBox() + "</LatLonAltBox>";
    }
    
    private String toKMLBox() {
        return "<north>" + Double.toString(getMaxY()) + "</north>" + "<south>"
                + Double.toString(getMinY()) + "</south>" + "<east>" + Double.toString(getMaxX())
                + "</east>" + "<west>" + Double.toString(getMinX()) + "</west>";
    }
    
}
