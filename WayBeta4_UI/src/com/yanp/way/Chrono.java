package com.yanp.way;

/**
 * 
 * @author YPierru
 * Simple chronometer
 */

public class Chrono {
	private long begin, end;
	private boolean start=false;
	
	/**
	 * Start the chronometer
	 */
    public void start(){
        begin = System.currentTimeMillis();
        start=true;
    }
 
    /**
     * Stop the chronometer
     */
    public void stop(){
        end = System.currentTimeMillis();
        start=false;
    }
    
    /**
     * 
     * @return true if chronometer is running, false if it's not
     */
    public boolean isStart(){
    	return start;
    }
 
    /**
     * 
     * @return time past in milliseconds
     */
    public long getMilliseconds() {
        return end-begin;
    }
    
    /**
     * 
     * @return time past in seconds
     */
    public double getSeconds() {
        return (end - begin) / 1000;
    }

    /**
     * 
     * @return time past in minutes
     */
    public double getMinutes() {
        return (end - begin) / 60000.0;
    }

    /**
     * 
     * @return time past in hours
     */
    public double getHours() {
        return (end - begin) / 3600000.0;
    }
}
