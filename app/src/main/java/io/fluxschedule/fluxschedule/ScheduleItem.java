package io.fluxschedule.fluxschedule;
import java.util.*;
import java.text.*;
import java.time.*;
/**
 * Created by albertwu on 3/3/18.
 */

public class ScheduleItem {
    String name;
    int startTime;
    int duration;
    boolean locked;
    Calendar c = Calendar.getInstance();
    public ScheduleItem(String name, int startTime, int duration) {
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
    }
    public ScheduleItem(String name, int duration) {
        this.name = name;
        this.duration = duration;
        this.startTime = 7 * 60;
    }

    public void setDuration(String sDuration) {
        Scanner s = new Scanner(sDuration).useDelimiter(":");
        duration = s.nextInt() * 60 + s.nextInt();
    }
    public void setStartTime(String sStartTime) {

        Scanner s = new Scanner(sStartTime).useDelimiter(":");
        int hour = s.nextInt();
        int minute = s.nextInt();
        String ampm = s.next();
        if(ampm == "PM") {
            hour += 12;
        }
        startTime = hour * 60 + minute;
    }
    public int getStartHours(){
        return startTime / 60;
    }
    public int getStartMinutes() {
        return startTime % 60;
    }
    public int getDurHours(){
        return duration / 60;
    }
    public int getDurMinutes(){
        return duration % 60;
    }

    public String getDurationDate() {
        int hour = duration / 60;
        int minute = duration % 60;
        String minString = Integer.toString(minute);
        if(minute < 10) {
            minString = "0"+minString;
        }
        String hourString = Integer.toString(hour);
        if(hour < 10) {
            hourString = "0"+ hourString;
        }

        return hourString + ":" + minString;
    }
    public String getStartTimeDate() {
        String ampm = "AM";
        int hour = startTime / 60;
        int minute = startTime % 60;
        String minString = Integer.toString(minute);
        if(minute < 10) {
            minString = "0"+minString;
        }


        if(hour > 12) {
            hour = hour - 12;
            ampm = "PM";
        }

        String hourString = Integer.toString(hour);
        if(hour < 10) {
            hourString = " "+ hourString;
        }
        return hourString + ":" + minString + " " + ampm;

    }


}
