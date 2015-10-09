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
package it.geosolutions.geoserver.rest.decoder.gwc.diskquota;

import java.io.Serializable;

/**
 * 
 * @author groldan
 * 
 */
public final class GWCLayerQuota implements Serializable {

    private static final long serialVersionUID = 5726170502452942487L;

    private String layer;

    private GWCExpirationPolicy expirationPolicyName;

    private GWCQuota quota;

    /**
     * @deprecated usage quota no longer tracked here but on the quota store. This field is
     *             temporarily left here to avoid XStram parsing problems for older versions
     */
    @Deprecated
    private transient GWCQuota usedQuota;

    GWCLayerQuota() {
        //
    }

    public GWCLayerQuota(final String layer, final GWCExpirationPolicy expirationPolicyName) {
        this(layer, expirationPolicyName, null);
    }

    public GWCLayerQuota(final String layer, final GWCExpirationPolicy expirationPolicyName, GWCQuota quota) {
        this.layer = layer;
        this.expirationPolicyName = expirationPolicyName;
        this.quota = quota;
        readResolve();
    }

    /**
     * Supports initialization of instance variables during XStream deserialization
     * 
     * @return
     */
    private Object readResolve() {
        return this;
    }

    public GWCExpirationPolicy getExpirationPolicyName() {
        return expirationPolicyName;
    }

    public String getLayer() {
        return layer;
    }

    /**
     * @return The layer's configured disk quota, or {@code null} if it has no max quota set
     */
    public GWCQuota getQuota() {
        return quota;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append("[layer: ").append(layer)
                .append(", Expiration policy: '").append(expirationPolicyName).append("', quota:")
                .append(quota).append("]").toString();
    }

}
