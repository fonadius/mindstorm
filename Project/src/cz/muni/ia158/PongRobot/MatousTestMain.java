package cz.muni.ia158.PongRobot;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class MatousTestMain {
	public static void main(String[] args) {
		RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.D);
		RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		leftMotor.synchronizeWith(new RegulatedMotor[] {rightMotor});
		leftMotor.startSynchronization();
		leftMotor.rotate(1080);
		leftMotor.endSynchronization();
	}
}
