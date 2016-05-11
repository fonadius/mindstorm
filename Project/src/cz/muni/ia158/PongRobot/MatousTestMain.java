package cz.muni.ia158.PongRobot;

import cz.muni.ia158.Motors.MotorController;
import cz.muni.ia158.Motors.SensorController;
import lejos.hardware.lcd.LCD;

public class MatousTestMain {
	public static void main(String[] args) throws InterruptedException {
		SensorController sc = new SensorController();
		MotorController mc = new MotorController(sc);
		
//		mc.goTo(0.5);
		sc.waitForImpact();
		mc.goTo(0.2);
		
//		Thread.sleep(4000);
		
//		mc.goToStart();
//		mc.goTo(0.7);
//		mc.goTo(0.5);
//		mc.goTo(0.7);
//		mc.goTo(0.1);;
//		mc.goTo(1);
//		mc.goTo(0);
//		
		mc.close();
		sc.close();
	}
}

