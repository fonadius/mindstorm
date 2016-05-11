package cz.muni.ia158.Motors;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.Color;
import lejos.robotics.ColorAdapter;
import lejos.robotics.TouchAdapter;

public class SensorController {
	private final ColorAdapter colorAdapter;
	private final TouchAdapter touchAdapter;

	public SensorController() {
		Port p1 = LocalEV3.get().getPort("S1");
		Port p2 = LocalEV3.get().getPort("S2");
		colorAdapter = new ColorAdapter(new EV3ColorSensor(p1));
		touchAdapter = new TouchAdapter(new EV3TouchSensor(p2));
	}

	public void waitForImpact() {
		setLightBusy();
		boolean wasTrue = false;
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LCD.drawString("" + wasTrue, 0, 0);
			if (touchAdapter.isPressed()) {
				wasTrue = true;
			}
		}
//		Sound.beepSequence();
//		setLightOff();
	}
	
	public boolean isGroudGreen() {
		Color c = colorAdapter.getColor();
		double r = c.getRed() / ((double) c.getGreen());
		double b = c.getBlue() / ((double) c.getGreen());
		return (r < 0.5 && b < 0.5);
	}
	
	public void setLightBusy() {
		Button.LEDPattern(5);
	}
	
	public void setLightReady() {
		Button.LEDPattern(1);
	}
	
	public void setLightOff() {
		Button.LEDPattern(0);
	}
	
	public void close() {
		setLightOff();
	}
}
