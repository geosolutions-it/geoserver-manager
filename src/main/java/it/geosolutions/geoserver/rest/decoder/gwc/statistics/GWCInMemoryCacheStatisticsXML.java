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
package it.geosolutions.geoserver.rest.decoder.gwc.statistics;

import it.geosolutions.geoserver.rest.Util;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import java.io.Serializable;
import org.jdom.Element;

/**
 * This class is a container of all the Statistics of the CacheProvider object
 * used by the MemoryBlobStore.
 * 
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 */
public class GWCInMemoryCacheStatisticsXML implements Serializable {

    private static final long serialVersionUID = 7000603154441802797L;

    private final Element inMemoryCacheStatisticsElem;

    public static GWCInMemoryCacheStatisticsXML build(String response) {
        if (response == null) {
            return null;
        }

        Element pb = JDOMBuilder.buildElement(response);
        if (pb != null) {
            return new GWCInMemoryCacheStatisticsXML(pb);
        } else {
            return null;
        }
    }

    public GWCInMemoryCacheStatisticsXML(Element inMemoryCacheStatisticsElem) {
        this.inMemoryCacheStatisticsElem = inMemoryCacheStatisticsElem;
    }

    /**
     * @return the cache hit count
     */
    public Long getHitCount() {
        return Util.getLongValueFromText(this.inMemoryCacheStatisticsElem.getChildText("hitCount"));
    }

    /**
     * @return the cache miss count
     */
    public Long getMissCount() {
        return Util.getLongValueFromText(this.inMemoryCacheStatisticsElem.getChildText("missCount"));
    }

    /**
     * @return the cache eviction count
     */
    public Long getEvictionCount() {
        return Util.getLongValueFromText(this.inMemoryCacheStatisticsElem.getChildText("evictionCount"));
    }

    /**
     * @return the cache total request count
     */
    public Long getRequestCount() {
        return Util.getLongValueFromText(this.inMemoryCacheStatisticsElem.getChildText("totalCount"));
    }

    /**
     * @return the cache hit rate
     */
    public Double getHitRate() {
        return Util.getDoubleValueFromText(this.inMemoryCacheStatisticsElem.getChildText("hitRate"));
    }

    /**
     * @return the cache miss rate
     */
    public Double getMissRate() {
        return Util.getDoubleValueFromText(this.inMemoryCacheStatisticsElem.getChildText("missRate"));
    }

    /**
     * @return the cache current memory occupation
     */
    public Double getCurrentMemoryOccupation() {
        return Util.getDoubleValueFromText(this.inMemoryCacheStatisticsElem.getChildText("currentMemoryOccupation"));
    }

    /**
     * @return the cache current total size
     */
    public Long getTotalSize() {
        return Util.getLongValueFromText(this.inMemoryCacheStatisticsElem.getChildText("totalSize"));
    }

    /**
     * @return the cache current actual size
     */
    public Long getActualSize() {
        return Util.getLongValueFromText(this.inMemoryCacheStatisticsElem.getChildText("actualSize"));
    }

}
