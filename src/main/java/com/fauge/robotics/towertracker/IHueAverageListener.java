package com.fauge.robotics.towertracker;

import org.opencv.core.Scalar;

public interface IHueAverageListener {
	public void averageColorChanged(Scalar avg);
}
