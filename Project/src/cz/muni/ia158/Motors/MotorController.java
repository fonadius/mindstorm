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
	
	private volatile int areaLength; //in wheels rotations degrees
	
	private boolean lastDirectionForward = false; //was the last movement forward movement?
	
	private volatile double currPositions = 0;
	
	private Thread movementThread;
//	private Object lock = new Object();
	
	public MotorController(SensorController sc) {
		this.sc = sc;
		
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		areaLength = meassureDefendedArea();
	}
	
	/**
	 * robot calibrates itself for playground
	 * @return size of playground in degrees
	 */
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
			leftMotor.stop(true);
			rightMotor.stop();
			leftMotor.endSynchronization();
			//meassurement is not perfect => average
			int distance = (leftMotor.getTachoCount() + rightMotor.getTachoCount()) / 2;
			
			//now we robots center (not robots light sensor) on position 1
			goByDegrees(Settings.COLOR_TO_ORIGIN_TRANSFORM, true);
			lastDirectionForward = true;
			currPositions = 1;
			return distance;
	}
	
	/**
	 * Waits until current movement is finished
	 */
	public void finishMovement() {
		if (movementThread != null && movementThread.isAlive()) {
			try {
				movementThread.join();
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	public boolean isMoving() {
		return movementThread != null && movementThread.isAlive();
	}
	
	/**
	 * move robots for specified amount of degrees
	 * @param distanceToTravel in degrees negative is backward, positive is forward
	 * @param waitToFinish true - is blocking, false - is non blocking
	 */
	private void goByDegrees(double distanceToTravel, boolean waitToFinish) {
		movementThread = new Thread() {			
			public void run() {	
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
					
					
					//moves until the calculated distance is reached
					while (!Thread.currentThread().isInterrupted() && Math.abs(leftMotor.getTachoCount()) < Math.abs(distanceToTravel)) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException ex) {
							break;
						}
					}
					leftMotor.startSynchronization();
					leftMotor.flt(true); //it takes too much time to restore movement after stop
					rightMotor.flt(false);
					leftMotor.endSynchronization();
					
					if (areaLength != 0) {
						currPositions += ((leftMotor.getTachoCount() + rightMotor.getTachoCount()) / 2.0) / areaLength;
					}
			}
		};
		movementThread.start();
		if (waitToFinish) {
//			while (movementThread.isAlive()) {
				try {
					movementThread.join();
				} catch (InterruptedException e) {
//					continue;
				}
//			}
		}
	}
	
	/**
	 * goes to position
	 * @param position 0 - start, 1 - end, everything between parts of the playground
	 * @param waiToFinish is blocking?
	 */
	public void goTo(double position, boolean waiToFinish) {
		if (movementThread != null && movementThread.isAlive()) {
			movementThread.interrupt();
			finishMovement();
		}
		int distanceToTravel = (int) (areaLength * (position - currPositions));		
		goByDegrees(distanceToTravel, waiToFinish);
		lastDirectionForward = distanceToTravel > 0;
		LCD.clear();
		LCD.drawString("" + currPositions, 0, 0);
	}
	
	public void goTo(double position) {
		goTo(position, false);
	}
	
	public void goToCenter() {
		goTo(0.5, true);
	}
	
	/**
	 * Goes backward until green area is reached
	 */
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
			
			leftMotor.startSynchronization();
			leftMotor.rotate(Settings.COLOR_TO_ORIGIN_TRANSFORM);
			rightMotor.rotate(Settings.COLOR_TO_ORIGIN_TRANSFORM);
			leftMotor.endSynchronization();
			
			lastDirectionForward = false;
			currPositions = 0;
	}

	public void close() {
		leftMotor.close();
		rightMotor.close();
	}
	
}
