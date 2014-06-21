package com.ironrabbit.waybeta4ui;

public class Chrono {
	private long begin, end;
	private boolean start=false;
	
    public void start(){
        begin = System.currentTimeMillis();
        start=true;
    }
 
    public void stop(){
        end = System.currentTimeMillis();
        start=false;
    }
    
    public boolean isStart(){
    	return start;
    }
 
    public long getTime() {
        return end-begin;
    }
 
    public long getMilliseconds() {
        return end-begin;
    }
 
    public double getSeconds() {
        return (end - begin) / 1000;
    }
 
    public double getMinutes() {
        return (end - begin) / 60000.0;
    }
 
    public double getHours() {
        return (end - begin) / 3600000.0;
    }
}
