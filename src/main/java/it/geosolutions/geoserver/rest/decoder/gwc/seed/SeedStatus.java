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

package it.geosolutions.geoserver.rest.decoder.gwc.seed;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class SeedStatus extends ArrayList<Long> {
    private static final long serialVersionUID = -5774790742826850888L;
    private final static Logger LOGGER = LoggerFactory.getLogger(SeedStatus.class);

    public enum GWCSeedTaskStatus{
        ABORTED(-1), PENDING(0), RUNNING(1), DONE(2), NOT_FOUND(999);

        private final long statusCode;
        
        private GWCSeedTaskStatus(long statusCode) {
            this.statusCode = statusCode;
        }

        public long getStatusCode() {
            return statusCode;
        }
        
        public static GWCSeedTaskStatus getTaskStatus(int value){
//            LOGGER.debug("Value to search for task: " + value);
            switch(value){
                case -1: return ABORTED;
                case 0: return PENDING;
                case 1: return RUNNING;
                case 2: return DONE;
                default: return NOT_FOUND;
            }
        }
    }
    
    public long getTilesProcessed(){
        return super.get(0);
    }
    
    /**
     * 
     * @return The total number of tiles to process
     */
    public long getTotalNumOfTilesToProcess(){
        return super.get(1);
    }
    
    /**
     * 
     * @return The expected remaining time in seconds
     */
    public long getExpectedRemainingTime(){
        return super.get(2);
    }

    /**
     * 
     * @return The task ID
     */
    public long getTaskID(){
        return super.get(3);
    }

    /**
     * 
     * @return The task status. The meaning of the Task status field is: 
     * -1 = ABORTED, 0 = PENDING, 1 = RUNNING, 2 = DONE
     */
    public GWCSeedTaskStatus getTaskStatus(){
        return GWCSeedTaskStatus.getTaskStatus(
                super.get(4) != null ? super.get(4).intValue() : 999);
    }
    
}
