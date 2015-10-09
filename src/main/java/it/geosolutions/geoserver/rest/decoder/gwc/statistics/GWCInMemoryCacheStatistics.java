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

import java.io.Serializable;

/**
 * This class is a container of all the Statistics of the CacheProvider object 
 * used by the MemoryBlobStore.
 * 
 * @author Nicola Lagomarsini, GeoSolutions
 * 
 */
public class GWCInMemoryCacheStatistics implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -1049287017217353112L;

    /** Cache hit count */
    private long hitCount = 0;

    /** Cache miss count */
    private long missCount = 0;

    /** Cache eviction count */
    private long evictionCount = 0;

    /** Cache total request count (hit + miss) */
    private long totalCount = 0;

    /** Cache hit rate */
    private double hitRate = 0;

    /** Cache miss rate */
    private double missRate = 0;

    /** Cache current memory occupation */
    private double currentMemoryOccupation = 0;

    /** Cache total size */
    private long totalSize = 0;

    /** Cache actual size */
    private long actualSize = 0;

    public GWCInMemoryCacheStatistics() {
    }

    // Copy Constructor
    public GWCInMemoryCacheStatistics(GWCInMemoryCacheStatistics stats) {
        this.setEvictionCount(stats.getEvictionCount());
        this.setHitCount(stats.getHitCount());
        this.setMissCount(stats.getMissCount());
        this.setTotalCount(stats.getRequestCount());
        this.setHitRate(stats.getHitRate());
        this.setMissRate(stats.getMissRate());
        this.setCurrentMemoryOccupation(stats.getCurrentMemoryOccupation());
        this.setActualSize(stats.getActualSize());
        this.setTotalSize(stats.getTotalSize());
    }

    /**
     * @return the cache hit count
     */
    public long getHitCount() {
        return hitCount;
    }

    /**
     * Setter for cache hit count
     * 
     * @param hitCount
     */
    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    /**
     * @return the cache miss count
     */
    public long getMissCount() {
        return missCount;
    }

    /**
     * Setter for cache miss count
     * 
     * @param missCount
     */
    public void setMissCount(long missCount) {
        this.missCount = missCount;
    }

    /**
     * @return the cache eviction count
     */
    public long getEvictionCount() {
        return evictionCount;
    }

    /**
     * Setter for cache eviction count
     * 
     * @param evictionCount
     */
    public void setEvictionCount(long evictionCount) {
        this.evictionCount = evictionCount;
    }

    /**
     * @return the cache total request count
     */
    public long getRequestCount() {
        return totalCount;
    }

    /**
     * Setter for cache total count
     * 
     * @param totalCount
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the cache hit rate
     */
    public double getHitRate() {
        return hitRate;
    }

    /**
     * Setter for cache hit rate
     * 
     * @param hitRate
     */
    public void setHitRate(double hitRate) {
        this.hitRate = hitRate;
    }

    /**
     * @return the cache miss rate
     */
    public double getMissRate() {
        return missRate;
    }

    /**
     * Setter for cache miss rate
     * 
     * @param missRate
     */
    public void setMissRate(double missRate) {
        this.missRate = missRate;
    }

    /**
     * @return the cache current memory occupation
     */
    public double getCurrentMemoryOccupation() {
        return currentMemoryOccupation;
    }

    /**
     * Setter for cache memory occupation
     * 
     * @param currentMemoryOccupation
     */
    public void setCurrentMemoryOccupation(double currentMemoryOccupation) {
        this.currentMemoryOccupation = currentMemoryOccupation;
    }

    /**
     * @return the cache current total size
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Setter for cache total size
     * 
     * @param totalSize
     */
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * @return the cache current actual size
     */
    public long getActualSize() {
        return actualSize;
    }

    /**
     * Setter for cache actual size
     * 
     * @param actualSize
     */
    public void setActualSize(long actualSize) {
        this.actualSize = actualSize;
    }
}
