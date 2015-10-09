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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <b>Mutable</b> representation of the disk usage of a given cache tile set, given by a value and
 * a {@link StorageUnit storage unit}.
 * <p>
 * Instances of this class are <b>not</b> thread safe.
 * </p>
 * 
 * @author groldan
 * 
 */
public class GWCQuota implements Cloneable, Comparable<GWCQuota>, Serializable {

    private static final long serialVersionUID = -3817255124248938529L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GWCQuota.class);

    private static final NumberFormat NICE_FORMATTER = NumberFormat.getNumberInstance();
    static {
        NICE_FORMATTER.setMinimumFractionDigits(1);
        NICE_FORMATTER.setMaximumFractionDigits(2);
    }

    private Integer id;

    private String tileSetId;

    private BigInteger bytes;
    
    private GWCStorageUnit units;

    public GWCQuota() {
        this(BigInteger.ZERO);
    }

    public GWCQuota(BigInteger bytes) {
        this.bytes = bytes;
        this.units = GWCStorageUnit.B;
    }

    public GWCQuota(GWCQuota quota) {
        id = quota.id;
        tileSetId = quota.tileSetId;
        bytes = quota.getBytes();
        this.units = GWCStorageUnit.B;
    }

    public Integer getId() {
        return id;
    }

    public String getTileSetId() {
        return tileSetId;
    }

    public void setTileSetId(String tileSetId) {
        this.tileSetId = tileSetId;
    }

    public BigInteger getBytes() {
        return bytes;
    }

    public void setBytes(BigInteger bytes) {
        this.bytes = bytes;
    }

    @JsonSetter("value")
    public void setBytes(long bytes) {
        setBytes(BigInteger.valueOf(bytes));
    }

    @JsonCreator
    public GWCQuota(@JsonProperty("value") double value, @JsonProperty("units")GWCStorageUnit units) {
        this(BigDecimal.valueOf(value), units);
    }

    public GWCQuota(BigDecimal value, GWCStorageUnit units) {
        this.bytes = units.toBytes(value);
        //Setting unit as byte after the transformation
        this.units = GWCStorageUnit.B;
    }

    /**
     * Supports initialization of instance variables during XStream deserialization
     * 
     * @return
     */
    private Object readResolve() {
        if (this.bytes == null) {
            this.bytes = BigInteger.ZERO;
        }

        return this;
    }

    @Override
    public String toString() {
        GWCStorageUnit bestFit = GWCStorageUnit.bestFit(bytes);
        BigDecimal value = GWCStorageUnit.B.convertTo(new BigDecimal(bytes), bestFit);
        return new StringBuilder(NICE_FORMATTER.format(value)).append(bestFit.toString())
                .toString();
    }

    /**
     * Adds {@code bytes} bytes to this quota
     * 
     * @param bytes
     */
    public void add(BigInteger bytes) {
        this.bytes = this.bytes.add(bytes);
    }

    /**
     * Shorthand for {@link #add(BigInteger) add(BigInteger.valueOf(bytes))}
     */
    public void addBytes(long bytes) {
        this.bytes = this.bytes.add(BigInteger.valueOf(bytes));
    }

    /**
     * Shorthand for {@link #add(BigInteger) add(units.toBytes(amount))}
     */
    public void add(double amount, GWCStorageUnit units) {
        this.bytes = this.bytes.add(units.toBytes(amount));
    }

    /**
     * Shorthand for {@link #add(BigInteger) add(quota.getBytes())}
     */
    public void add(final GWCQuota quota) {
        this.bytes = this.bytes.add(quota.getBytes());
    }

    /**
     * Subtracts {@code bytes} bytes from this quota
     * 
     * @param bytes
     */
    public void subtract(final BigInteger bytes) {
        this.bytes = this.bytes.subtract(bytes);
    }

    /**
     * Shorthand for {@link #subtract(BigInteger) subtract(quota.getBytes())}
     */
    public void subtract(final GWCQuota quota) {
        subtract(quota.getBytes());
    }

    /**
     * Shorthand for {@link #subtract(BigInteger) subtract(units.toBytes(amount))}
     */
    public void subtract(final double amount, final GWCStorageUnit units) {
        subtract(units.toBytes(amount));
    }

    /**
     * Returns the difference between this quota and the argument one, in this quota's units
     * 
     * @param quota
     * @return
     */
    public GWCQuota difference(GWCQuota quota) {
        BigInteger difference = this.bytes.subtract(quota.getBytes());
        return new GWCQuota(difference);
    }

    /**
     * Returns a more user friendly string representation of this quota, like in 1.1GB, 0.75MB, etc.
     * 
     * @return
     */
    public String toNiceString() {
        GWCStorageUnit bestFit = GWCStorageUnit.bestFit(bytes);
        BigDecimal value = GWCStorageUnit.B.convertTo(new BigDecimal(bytes), bestFit);
        return new StringBuilder(NICE_FORMATTER.format(value)).append(' ')
                .append(bestFit.toNiceString()).toString();
    }

    /**
     * @param quota
     *            quota to be compared against this one
     * @return {@code this} or {@code quota}, the one that represents a lower amount
     */
    public GWCQuota min(GWCQuota quota) {
        BigInteger min = this.bytes.min(quota.getBytes());
        return this.bytes.equals(min) ? this : quota;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(GWCQuota o) {
        if (o == null) {
            throw new NullPointerException("Can't compare against null");
        }
        return bytes.compareTo(o.getBytes());
    }

    /**
     * Shorthand for {@code setBytes(unit.convertTo(value, StorageUnit.B).toBigInteger())}
     * 
     * @param value
     * @param unit
     */
    public void setValue(double value, GWCStorageUnit unit) {
        setBytes(unit.convertTo(value, GWCStorageUnit.B).toBigInteger());
    }

    @Override
    public GWCQuota clone() {
        return new GWCQuota(this);
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("units")
    public GWCStorageUnit getUnits() {
        return this.units;
    }

    public void setUnits(GWCStorageUnit units) {
        this.units = units;
    }
}
