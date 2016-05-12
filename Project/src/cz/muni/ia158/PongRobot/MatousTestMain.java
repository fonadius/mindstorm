package cz.muni.ia158.PongRobot;

import cz.muni.ia158.Motors.MotorController;
import cz.muni.ia158.Motors.SensorController;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class MatousTestMain {
	public static void main(String[] args) throws InterruptedException {
		SensorController sc = new SensorController();
		MotorController mc = new MotorController(sc);
		
		
		mc.goTo(0.5, false);
		Thread.sleep(1000);
		mc.goTo(2, true);
//		mc.finishMovement();
		mc.goTo(0.1, true);
		Thread.sleep(5000);
				
		mc.close();
		sc.close();
	}
}

