package com.bmxgates.logger;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formater {

	
	private final static double MPH_CONVERSION = 2.23691410468716;

	private final static double FOOT_CONVERSION = 0.00328084;

	public final static double SPEED_CONVERSION = MPH_CONVERSION;

	public final static double DISTANCE_CONVERSION = FOOT_CONVERSION;

	private final static DecimalFormat speedFormat = new DecimalFormat("00.0");
	
	private final static DecimalFormat distanceFormat = new DecimalFormat("0000.0");

	private final static DateFormat dateFormat = DateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
	

	public static String speed(double value){
		return speedFormat.format(value * SPEED_CONVERSION);
	}

	public static String distance(long value){
		return distanceFormat.format(value * DISTANCE_CONVERSION);
	}

	public static CharSequence time(long time, boolean fixed) {
		long sec = time/1000;
		long min = sec/60;
		long milis = time%1000;

		sec = sec - min*60;
		
		if (fixed)
			return String.format("%02d:", min) + String.format("%02d.", sec) + String.format("%03d", milis);
		else 
			return (min>0?String.format("%02d:", min):"") + (sec>0?String.format("%02d.", sec):"0.") + String.format("%03d", milis);
			
	}
	
	public static String date(Date date){
		return dateFormat.format(date);
	}
}
