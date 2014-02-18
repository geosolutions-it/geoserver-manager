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
package it.geosolutions.geoserver.rest.encoder.dimensions;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * GSCoverageDimension - encodes a CoverageDimension for a given GeoServer Resource (feature type /coverage), as follows:
 * 
 * <pre>
 * {@code
 * final GSCoverageDimensionEncoder gsCoverageDimensionEncoder = 
 *      new GSCoverageDimensionEncoder("GRAY_INDEX", "GridSampleDimension[-2.147483648E9,-2.147483648E9]", 
 *      String.valueOf(Integer.MIN_VALUE), String.valueOf(Integer.MAX_VALUE), "dobson units³", "REAL_32BITS");
 *  coverageEncoder.addCoverageDimensionInfo(gsCoverageDimensionEncoder);
 * }
 * </pre>
 * 
 * For this example, the XML output is:
 * 
 * <pre>
 *  {@code
 * <coverageDimension>
 * 	<name>GRAY_INDEX</name>
 * 	<description>GridSampleDimension[-2.147483648E9,2.147483648E9]</description>
 * 	<range>
 * 		<min>-2.147483648E9</min>
 * 		<max>2.147483647E9</max>
 * 	</range>
 * 	<unit>dobson units³</unit>
 * 	<dimensionType>
 *  		<name>REAL_32BITS</name>
 *  	</dimensionType>
 * </coverageDimension>
 *  }
 * </pre>
 * 
 * @author Henry Rotzoll (henry.rotzoll@dlr.de)
 * 
 */
public class GSCoverageDimensionEncoder extends XmlElement {

    /**
     * A class to filter the GSCoverageDimension by content
     * 
     * 
     */
    private static class filterByContent implements Filter {

        final private String key;

        public filterByContent(String content) {
            this.key = content;
        }

        private static final long serialVersionUID = 1L;

        public boolean matches(Object obj) {
            Element el = ((Element) obj).getChild("name");
            if (el != null && el.getTextTrim().equals(key)) {
                return true;
            }
            return false;
        }
    }

    /**
     * Get a Filter using the GSCoverageDimensionEncoder content (GSCoverageDimensionEncoder name)
     * 
     * @param content
     * @return the filter
     */
    public static Filter getFilterByContent(String content) {
        return new filterByContent(content);
    }

    /**
     * Constructs a new GSCoverageDimensionEncoder
     * 
     */
    public GSCoverageDimensionEncoder() {
        super("coverageDimension");
    }

    /**
     * Constructs quickly a GSCoverageDimensionEncoder info
     * 
     * @param name
     * @param description
     * @param rangeMin
     * @param rangeMax
     * @param unit
     * @param dimensionTypeName
     */
    public GSCoverageDimensionEncoder(String name, String description, String rangeMin,
            String rangeMax, String unit, String dimensionTypeName) {
        super("coverageDimension");
        this.setup(name, description, rangeMin, rangeMax, unit, dimensionTypeName);
    }

    /**
     * Set-up quickly a GSCoverageDimensionEncoder info
     * 
     * @param name
     * @param description
     * @param rangeMin
     * @param rangeMax
     * @param unit
     * @param dimensionTypeName
     */
    protected void setup(String name, String description, String rangeMin, String rangeMax,
            String unit, String dimensionTypeName) {
        // name
        setName(name);

        // description
        setDescription(description);

        // range
        setRange(rangeMin, rangeMax);

        // unit
        setUnit(unit);

        // dimension Type
        setDimensionType(dimensionTypeName);
    }

    /**
     * Get the value of the GSCoverageDimensionEncoder member
     * 
     * @param memberName
     * @return the value of the GSCoverageDimensionEncoder member
     */
    protected String getMember(String memberName) {
        Element el = this.getRoot().getChild(memberName.toString());
        if (el != null)
            return el.getTextTrim();
        else
            return null;
    }

    /**
     * Deletes a GSCoverageDimensionEncoder member
     * 
     * @param memberName
     * @return true if the GSCoverageDimensionEncoder member is removed
     */
    protected boolean delMemberIfExists(String memberName) {
        if (ElementUtils.contains(getRoot(), memberName) != null) {
            return ElementUtils.remove(this.getRoot(),
                    this.getRoot().getChild(memberName.toString()));
        }

        return false;
    }

    /**
     * Set a GSCoverageDimensionEncoder member
     * 
     * @param memberName
     * @param memberValue
     */
    protected void setMember(String memberName, String memberValue) {
        if (memberName != null && !memberName.isEmpty() && memberValue != null
                && !memberValue.isEmpty()) {
            delMemberIfExists(memberName); // delete the element if it already exists
            addMember(memberName.toString(), memberValue);
        }
    }

    /**
     * adds a GSCoverageDimensionEncoder member
     * 
     * @param memberName
     * @param memberValue
     */
    protected void addMember(String memberName, String memberValue) {
        if (memberName != null && !memberName.isEmpty() && memberValue != null
                && !memberValue.isEmpty()) {
            set(memberName.toString(), memberValue);
        }
    }

    /**
     * Set the name
     * 
     * @param name
     */
    public void setName(String name) {
        setMember("name", name);
    }

    /**
     * Deletes the name
     * 
     * @param name
     * @return true if removed
     */
    public boolean delName() {
        return this.delMemberIfExists("name");
    }

    /**
     * Get the description
     * 
     * @return description
     */
    public String getName() {
        return this.getMember("name");
    }

    /**
     * Set the description
     * 
     * @param description
     */
    public void setDescription(String description) {
        setMember("description", description);
    }

    /**
     * Deletes the description
     * 
     * @param description
     * @return true if removed
     */
    public boolean delDescription() {
        return this.delMemberIfExists("description");
    }

    /**
     * Get the description
     * 
     * @return description
     */
    public String getDescription() {
        return this.getMember("description");
    }

    /**
     * Set the range
     * 
     * @param range
     */
    public void setRange(String rangeMin, String rangeMax) {
        if (rangeMin != null && !rangeMin.isEmpty() && rangeMax != null && !rangeMax.isEmpty()) {
            remove("range");

            final Element rangeElement = new Element("range");
            final Element rangeMinElement = new Element("min");
            rangeMinElement.setText(rangeMin);
            final Element rangeMaxElement = new Element("max");
            rangeMaxElement.setText(rangeMax);
            rangeElement.addContent(rangeMinElement);
            rangeElement.addContent(rangeMaxElement);
            addContent(rangeElement);
        }
    }

    /**
     * Deletes the range
     * 
     * @param range
     * @return true if removed
     */
    public boolean delRange() {
        return this.delMemberIfExists("range");
    }

    /**
     * Get the range min
     * 
     * @return range min
     */
    public String getRangeMin() {
        final Element range = this.getRoot().getChild("range");
        if (range != null) {
            return range.getChildText("min");
        }

        return null;
    }

    /**
     * Get the range max
     * 
     * @return range max
     */
    public String getRangeMax() {
        final Element range = this.getRoot().getChild("range");
        if (range != null) {
            return range.getChildText("max");
        }

        return null;
    }

    /**
     * Set the unit
     * 
     * @param unit
     */
    public void setUnit(String unit) {
        setMember("unit", unit);
    }

    /**
     * Deletes the type
     * 
     * @param type
     * @return true if removed
     */
    public boolean delUnit() {
        return this.delMemberIfExists("unit");
    }

    /**
     * Get the unit
     * 
     * @return unit
     */
    public String getUnit() {
        return this.getMember("unit");
    }

    /**
     * Set the dimensionType
     * 
     * @param dimensionType
     */
    public void setDimensionType(String dimensionTypeName) {
        if (dimensionTypeName != null && !dimensionTypeName.isEmpty()) {
            remove("dimensionType");

            final Element dimensionTypeElement = new Element("dimensionType");
            final Element dimensionNameElement = new Element("name");
            dimensionNameElement.setText(dimensionTypeName);
            dimensionTypeElement.addContent(dimensionNameElement);
            addContent(dimensionTypeElement);
        }
    }

    /**
     * Deletes the dimensionType
     * 
     * @param dimensionType
     * @return true if removed
     */
    public boolean delDimensionType() {
        return this.delMemberIfExists("dimensionType");
    }

    /**
     * Get the dimensionType name
     * 
     * @return dimensionType name
     */
    public String getDimensionTypeName() {
        final Element dimensionType = this.getRoot().getChild("dimensionType");
        if (dimensionType != null) {
            return dimensionType.getChildText("name");
        }

        return null;
    }
}