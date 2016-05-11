package cz.muni.ia158.Motors;

import cz.muni.ia158.PongRobot.settings.Settings;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class MotorController {
	private final SensorController sc;
	
	private final RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
	private final RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);
	
	private int areaLength; //in wheels rotations degrees
	
	private boolean lastDirectionForward = false; //was the last movement forward movement?
	
	private double currPositions = 0;
	
	public MotorController(SensorController sc) {
		this.sc = sc;
		
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		areaLength = meassureDefendedArea();
	}
	
	private int meassureDefendedArea() {
		goToStart();
		leftMotor.startSynchronization();
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.endSynchronization();
		
		boolean goOver = !lastDirectionForward;
		//go until you reach next green area
		while (!sc.isGroudGreen() || goOver) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				System.out.println("Thread sleep error: " + ex);
				ex.printStackTrace();
				break;
			}
			if (!sc.isGroudGreen()) {
				goOver = false;
			}
		}
		leftMotor.startSynchronization();
		leftMotor.flt(true);
		rightMotor.flt();
		leftMotor.endSynchronization();
		int distance = (leftMotor.getTachoCount() + rightMotor.getTachoCount()) / 2;
		goByDegrees(Settings.COLOR_TO_ORIGIN_TRANSFORM);
		lastDirectionForward = true;
		currPositions = 1;
		return distance;
	}
	
	public boolean isMoving() {
		return leftMotor.isMoving() || rightMotor.isMoving();
	}
	
	private void goByDegrees(double distanceToTravel) {
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		leftMotor.startSynchronization();
		if (distanceToTravel > 0) {
			leftMotor.forward();
			rightMotor.forward();
		} else {
			leftMotor.backward();
			rightMotor.backward();
		}
		leftMotor.endSynchronization();
		
		while (Math.abs(leftMotor.getTachoCount()) < Math.abs(distanceToTravel)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				System.out.println("Thread sleep error: " + ex);
				ex.printStackTrace();
				break;
			}
		}
		
		leftMotor.startSynchronization();
		leftMotor.flt(true);
		rightMotor.flt(false);
		leftMotor.endSynchronization();
	}
	
	public void goTo(double position) {
		int distanceToTravel = (int) (areaLength * (position - currPositions));		
		goByDegrees(distanceToTravel);
		lastDirectionForward = distanceToTravel > 0;
		currPositions = position;
	}
	
	public void goToCenter() {
		goTo(0.5);
	}
	
	public void goToStart() {
		leftMotor.startSynchronization();
		leftMotor.backward();
		rightMotor.backward();
		leftMotor.endSynchronization();
		boolean goOver = lastDirectionForward;
		while (!sc.isGroudGreen() || goOver) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {
				System.out.println("Thread sleep error: " + ex);
				ex.printStackTrace();
				break;
			}
			if (!sc.isGroudGreen()) {
				goOver = false;
			}
		}
		leftMotor.startSynchronization();
		leftMotor.flt(true);
		rightMotor.flt();
		leftMotor.endSynchronization();
		
		lastDirectionForward = false;
		currPositions = 0;
		
		goByDegrees(Settings.COLOR_TO_ORIGIN_TRANSFORM);
	}

	public void close() {
		leftMotor.close();
		rightMotor.close();
	}
	
}
