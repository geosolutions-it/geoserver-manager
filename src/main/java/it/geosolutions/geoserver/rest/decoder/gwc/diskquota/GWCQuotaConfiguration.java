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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Holds the quota configuration for all the registered layers as well as the instance wide settings
 * such as cache disk block size, maximum number of concurrent cache clean ups, etc.
 * 
 * @author groldan
 * 
 */
public class GWCQuotaConfiguration implements Cloneable, Serializable {

    private static final long serialVersionUID = 4376471696761297546L;

    @Deprecated
    static final int DEFAULT_DISK_BLOCK_SIZE = 4096;

    static final int DEFAULT_CLEANUP_FREQUENCY = 10;

    static final TimeUnit DEFAULT_CLEANUP_UNITS = TimeUnit.SECONDS;

    static final int DEFAULT_MAX_CONCURRENT_CLEANUPS = 2;

    static GWCExpirationPolicy DEFAULT_GLOBAL_POLICY_NAME = GWCExpirationPolicy.LFU;
    
    private Boolean enabled;

    @Deprecated
    private transient Integer diskBlockSize;

    private Integer cacheCleanUpFrequency;

    private TimeUnit cacheCleanUpUnits;

    private Integer maxConcurrentCleanUps;

    private GWCExpirationPolicy globalExpirationPolicyName;

    private GWCQuota globalQuota;

    private transient Date lastCleanUpTime;

    private List<GWCLayerQuota> layerQuotas;
    
    private String quotaStore;
    
    public void setDefaults() {
        if (enabled == null) {
            enabled = Boolean.FALSE;
        }
        if (diskBlockSize == null) {
            diskBlockSize = DEFAULT_DISK_BLOCK_SIZE;
        }
        if (cacheCleanUpFrequency == null) {
            cacheCleanUpFrequency = DEFAULT_CLEANUP_FREQUENCY;
        }

        if (maxConcurrentCleanUps == null) {
            maxConcurrentCleanUps = DEFAULT_MAX_CONCURRENT_CLEANUPS;
        }
        if (cacheCleanUpUnits == null) {
            cacheCleanUpUnits = DEFAULT_CLEANUP_UNITS;
        }
        if (globalExpirationPolicyName == null) {
            globalExpirationPolicyName = DEFAULT_GLOBAL_POLICY_NAME;
        }
        if (globalQuota == null) {
            globalQuota = new GWCQuota(500, GWCStorageUnit.MiB);
        }
    }

    void setFrom(GWCQuotaConfiguration other) {
        this.cacheCleanUpFrequency = other.cacheCleanUpFrequency;
        this.cacheCleanUpUnits = other.cacheCleanUpUnits;
        this.diskBlockSize = other.diskBlockSize;
        this.enabled = other.enabled;
        this.globalExpirationPolicyName = other.globalExpirationPolicyName;
        this.globalQuota = other.globalQuota;
        this.layerQuotas = other.layerQuotas == null ? null : new ArrayList<GWCLayerQuota>(
                other.layerQuotas);
        this.maxConcurrentCleanUps = other.maxConcurrentCleanUps;
        this.quotaStore = other.quotaStore;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Deprecated
    public Integer getDiskBlockSize() {
        return diskBlockSize;
    }

    @Deprecated
    public void setDiskBlockSize(int blockSizeBytes) {
        if (blockSizeBytes <= 0) {
            throw new IllegalArgumentException("Block size shall be a positive integer");
        }
        this.diskBlockSize = blockSizeBytes;
    }

    public Integer getCacheCleanUpFrequency() {
        return cacheCleanUpFrequency;
    }

    public void setCacheCleanUpFrequency(int cacheCleanUpFrequency) {
        if (cacheCleanUpFrequency < 0) {
            throw new IllegalArgumentException("cacheCleanUpFrequency shall be a positive integer");
        }
        this.cacheCleanUpFrequency = cacheCleanUpFrequency;
    }

    public TimeUnit getCacheCleanUpUnits() {
        return cacheCleanUpUnits;
    }

    public void setCacheCleanUpUnits(TimeUnit cacheCleanUpUnit) {
        if (cacheCleanUpUnit == null) {
            throw new IllegalArgumentException("cacheCleanUpUnits can't be null");
        }
        this.cacheCleanUpUnits = cacheCleanUpUnit;
    }

    /**
     * @return the configured layer quotas, or {@code null} if not set
     */
    public List<GWCLayerQuota> getLayerQuotas() {
        return layerQuotas == null ? null : new ArrayList<GWCLayerQuota>(layerQuotas);
    }

    public void setLayerQuotas(List<GWCLayerQuota> layerQuotas) {
        this.layerQuotas = layerQuotas == null ? null : new ArrayList<GWCLayerQuota>(layerQuotas);
    }

    public void addLayerQuota(GWCLayerQuota quota) {
        assert quota != null;
        assert quota.getQuota() != null;
        if (layerQuotas == null) {
            layerQuotas = new ArrayList<GWCLayerQuota>();
        }
        this.layerQuotas.add(quota);
    }

    /**
     * @return The layer quota for the given layer or {@code null} if no quota is being tracked for
     *         that layer
     */
    public GWCLayerQuota layerQuota(final String layerName) {
        if (layerQuotas != null) {
            for (GWCLayerQuota lq : layerQuotas) {
                if (lq.getLayer().equals(layerName)) {
                    return lq;
                }
            }
        }

        return null;
    }

    public void remove(final GWCLayerQuota lq) {
        if (layerQuotas != null) {
            for (Iterator<GWCLayerQuota> it = layerQuotas.iterator(); it.hasNext();) {
                if (it.next().getLayer().equals(lq.getLayer())) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("[");
        if (null != getLayerQuotas()) {
            for (GWCLayerQuota lq : getLayerQuotas()) {
                sb.append("\n\t").append(lq);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public Integer getMaxConcurrentCleanUps() {
        return maxConcurrentCleanUps;
    }

    public void setMaxConcurrentCleanUps(int nThreads) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException(
                    "maxConcurrentCleanUps shall be a positive integer: " + nThreads);
        }
        this.maxConcurrentCleanUps = nThreads;
    }

    /**
     * @return the global quota, or {@code null} if not set
     */
    public GWCQuota getGlobalQuota() {
        return this.globalQuota;
    }

    /**
     * @param newQuota
     *            the new global quota, or {@code null} to unset
     */
    public void setGlobalQuota(final GWCQuota newQuota) {
        if (newQuota == null) {
            this.globalQuota = null;
        } else {
            this.globalQuota = new GWCQuota(newQuota);
        }
    }

    public GWCExpirationPolicy getGlobalExpirationPolicyName() {
        return this.globalExpirationPolicyName;
    }

    public void setGlobalExpirationPolicyName(GWCExpirationPolicy policy) {
        this.globalExpirationPolicyName = policy;
    }

    public void setLastCleanUpTime(Date date) {
        this.lastCleanUpTime = date;
    }

    public Date getLastCleanUpTime() {
        return this.lastCleanUpTime;
    }

    public Set<String> layerNames() {
        Set<String> names = new HashSet<String>();
        if (null != getLayerQuotas()) {
            for (GWCLayerQuota lq : getLayerQuotas()) {
                names.add(lq.getLayer());
            }
        }
        return names;
    }

    @Override
    public GWCQuotaConfiguration clone() {
        GWCQuotaConfiguration clone;
        try {
            clone = (GWCQuotaConfiguration) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.lastCleanUpTime = lastCleanUpTime;
        clone.globalQuota = globalQuota == null ? null : new GWCQuota(globalQuota);
        clone.layerQuotas = layerQuotas == null ? null : new ArrayList<GWCLayerQuota>(layerQuotas);
        return clone;
    }

    /**
     * Returns the quota store name
     * @return
     */
    public String getQuotaStore() {
        return quotaStore;
    }

    /**
     * Sets the quota store name
     * @param quotaStore
     */
    public void setQuotaStore(String quotaStore) {
        this.quotaStore = quotaStore;
    }
}
