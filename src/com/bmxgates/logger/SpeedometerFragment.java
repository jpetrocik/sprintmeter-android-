package com.bmxgates.logger;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmxgates.logger.R.color;

public class SpeedometerFragment extends Fragment {

	TextView speedView, distanceView, clockView, maxView, mph20View;

	double maxSpeed = 0;

	long time20mph;

	boolean past20mph = false;

	double speedAvg1, speedAvg2, speedAvg3;

	long time;
	
	long distance;
	
	double speed;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_speedometer, container, false);

		speedView = (TextView) rootView.findViewById(R.id.speedo_speed_view);
		distanceView = (TextView) rootView.findViewById(R.id.speedo_distance_view);
		clockView = (TextView) rootView.findViewById(R.id.speedo_time_view);
		maxView = (TextView) rootView.findViewById(R.id.speed_max_view);
		mph20View = (TextView) rootView.findViewById(R.id.speedo_mph_20);

		return rootView;
	}

	public void show20Time(boolean show){
		if (show){
			mph20View.setVisibility(View.VISIBLE);
		} else{
			mph20View.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();

	}

	public void reset() {
		setSpeed(-1);
		setTime(0);
		setDistance(0);
		setMaxSpeed(0);
		set20MphTime(-1);
		setError(false);
	}

	public void set(double speed, long distance, long time) {
		setSpeed(speed);
		setDistance(distance);
		setTime(time);
		if (speed > 8.9) {
			set20MphTime(time);
		}

	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
		
		clockView.setText(Formater.time(time, true));
	}


	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		
		if (distance < 0)
			this.distance = 0;
		else
			this.distance = distance;
		
		distanceView.setText(Formater.distance(this.distance));
	}


	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		
		if (speed < 0) {
			speedView.setText("--.-");
			speedAvg1 = 0;
			speedAvg2 = 0;
			speedAvg3 = 0;
		} else {
			speedAvg3 = speedAvg2;
			speedAvg2 = speedAvg1;
			speedAvg1 = speed;

			double speedAvg = (speedAvg3 + speedAvg2 + speedAvg1) / 3.0;

			speedView.setText(Formater.speed(speedAvg));

			if (speed > maxSpeed) {
				setMaxSpeed(speed);
			}

		}
	}

	public void setMaxSpeed(double speed) {
		maxView.setText(Formater.speed(speed));
		maxSpeed = speed;
	}

	public void set20MphTime(long time) {
		if (time < 0) {
			mph20View.setText("20mph @ -.---");
			past20mph = false;
		} else if (!past20mph) {
			mph20View.setText("20mph @" + Formater.time(time, false));
			past20mph = true;
		}
	}

	public void add(int wheelSize, int split) {
		
		time += split;
		distance += wheelSize;
		speed = wheelSize / (double) split;
		set(speed, distance, time);
		
	}
	
	public void setError(boolean error){
		if (error){
			clockView.setTextColor(Color.RED);
		} else {
			clockView.setTextColor(color.LCD_TEXT);
			
		}
	}
}
