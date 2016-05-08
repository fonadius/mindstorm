package cz.muni.ia158.Motors;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.geometry.Point2D;

public class MotorDriver implements Runnable{
	private double currPosX;
	
	private static final float WHEEL_CIRCUMFERENCE = 5; // in meters
	
	private final RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.D);
	private final RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
	
	public MotorDriver() {
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		currPosX = 0;
	}
	
	public boolean canBeReached(double goalX, long time) {
		long timeToImpact = time - System.currentTimeMillis(); //time to ball crossing line
		float maxSpeedInDegrees = leftMotor.getMaxSpeed();
		float maxSpeedInMeters = (maxSpeedInDegrees / 360) * WHEEL_CIRCUMFERENCE;
		double dist = Math.abs(goalX - currPosX);
		double timeToReach = (double) (dist / maxSpeedInMeters);
		//TODO: calculate with acceleration on the beginning
		return timeToReach <= timeToImpact;
	}
	
	public void setCurrPos(double x) {
		currPosX = x;
	}
	
	public boolean isMoving() {
		return leftMotor.isMoving();
	}
	
	public void goTo(double goalX, long timeToImpact) {
		double distInMeters = goalX - currPosX;
		double distInDegrees = (distInMeters / WHEEL_CIRCUMFERENCE) * 360;
		LCD.drawString("Rotating by: " + distInDegrees, 0, 3);
		leftMotor.startSynchronization();
		
		leftMotor.rotate((int) (distInDegrees)); 
		
		leftMotor.endSynchronization();
	}

	@Override
	public void run() {
	
//		while (true) {
//			ml.start
//		}
	}
}
