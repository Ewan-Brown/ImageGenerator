package main;

import java.awt.Color;

public class Particle {

	public Particle(double x, double y, double tX, double tY, int RGB){
		this.x = x;
		this.y = y;
		this.targetX = tX;
		this.targetY = tY;
		this.RGB = new Color(RGB);
	}
	double x = 0;
	double y = 0;
	double targetX = 0;
	double targetY = 0;
	double vX = 0;
	double vY = 0;
	Color RGB = Color.GREEN;
	
}
