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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Enumerates the various storage units according to the power of two based units (instead of
 * the more common commercial prefixes, often used but not entirely correct)
 */
public enum GWCStorageUnit {
    /**
     * Byte
     */
    B(BigDecimal.ONE), //
    /**
     * Kibibyte (2<sup>10</sup> Bytes)
     */
    KiB(B.bytes.multiply(BigDecimal.valueOf(1024))), //
    /**
     * Mebibyte (2<sup>20</sup> Bytes)
     */
    MiB(KiB.bytes.multiply(BigDecimal.valueOf(1024))), //
    /**
     * Gibibyte (2<sup>30</sup> Bytes)
     */
    GiB(MiB.bytes.multiply(BigDecimal.valueOf(1024))), //
    /**
     * Tebibyte (2<sup>40</sup> Bytes)
     */
    TiB(GiB.bytes.multiply(BigDecimal.valueOf(1024))), //
    /**
     * Pebibyte (2<sup>50</sup> Bytes)
     */
    PiB(TiB.bytes.multiply(BigDecimal.valueOf(1024))), //
    /**
     * Exibyte (2<sup>60</sup> Bytes)
     */
    EiB(PiB.bytes.multiply(BigDecimal.valueOf(1024))), //
    /**
     * Zebibyte (2<sup>70</sup> Bytes)
     */
    ZiB(EiB.bytes.multiply(BigDecimal.valueOf(1024))),
    /**
     * Yobibyte (2<sup>80</sup> Bytes)
     */
    YiB(ZiB.bytes.multiply(BigDecimal.valueOf(1024)));

    private final BigDecimal bytes;

    private GWCStorageUnit(final BigDecimal bytes) {
        this.bytes = bytes;
    }

    public final BigInteger toBytes(double value) {
        return toBytes(BigDecimal.valueOf(value));
    }

    public final BigInteger toBytes(BigDecimal value) {
        BigDecimal toBytes = bytes.multiply(value);
        return toBytes.toBigInteger();
    }

    public final BigDecimal fromBytes(BigInteger value) {
        return new BigDecimal(value).divide(bytes);
    }

    public BigDecimal convertTo(double value, GWCStorageUnit target) {
        return convertTo(BigDecimal.valueOf(value), target);
    }

    public final BigDecimal convertTo(BigDecimal value, GWCStorageUnit target) {
        return target.fromBytes(toBytes(value));
    }

    /**
     * Returns the most appropriate storage unit to represent the given amount
     * 
     * @param value
     * @param units
     * @return
     */
    public static GWCStorageUnit bestFit(double value, GWCStorageUnit units) {
        return bestFit(BigDecimal.valueOf(value), units);
    }

    /**
     * Returns the most appropriate storage unit to represent the given amount
     * 
     * @param value
     * @param units
     * @return
     */
    public static GWCStorageUnit bestFit(BigDecimal value, GWCStorageUnit units) {
        BigDecimal bytes = new BigDecimal(units.toBytes(value));
        // use compareTo because BigDecimal.equals does not consider 1.0 and 1.00 to be equal, so
        // can't do, for example, bytes.min(TiB.bytes).equals(YiB.bytes)
        if (bytes.compareTo(YiB.bytes) >= 0) {
            return YiB;
        }
        if (bytes.compareTo(ZiB.bytes) >= 0) {
            return ZiB;
        }
        if (bytes.compareTo(EiB.bytes) >= 0) {
            return EiB;
        }
        if (bytes.compareTo(PiB.bytes) >= 0) {
            return PiB;
        }
        if (bytes.compareTo(TiB.bytes) >= 0) {
            return TiB;
        }
        if (bytes.compareTo(GiB.bytes) >= 0) {
            return GiB;
        }
        if (bytes.compareTo(MiB.bytes) >= 0) {
            return MiB;
        }
        if (bytes.compareTo(KiB.bytes) >= 0) {
            return KiB;
        }

        return B;
    }

    public static GWCStorageUnit bestFit(BigInteger bytes) {
        return bestFit(new BigDecimal(bytes), B);
    }

    /**
     * Returns {@code MB} instead of {@code MiB}, {@code GB} instead of {@code GiB}, etc.
     */
    public String toNiceString() {
        String s = toString();
        if (B == this) {
            return s;
        }

        return new StringBuilder().append(s.charAt(0)).append(s.charAt(2)).toString();
    }
}
