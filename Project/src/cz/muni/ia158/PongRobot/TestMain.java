package cz.muni.ia158.PongRobot;

//import static cz.muni.ia158.PongRobot.settings.Settings.runtimeSettings;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.muni.ia158.PongRobot.tcp.BallInformationMessage;
import cz.muni.ia158.PongRobot.tcp.RobotCommunicator;
import cz.muni.ia158.PongRobot.tcp.TCPConnection;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class TestMain {

	public static TCPConnection transmitter;
	volatile private boolean run = true;

	public TestMain() {
		System.out.println("Robot22");
		try {
		//	transmitter = new TCPConnection(runtimeSettings.getServerUrl(), runtimeSettings.getControlUnitPort());
			
			transmitter = new TCPConnection("192.168.88.54", 8081);
			
		} catch (IOException ex) {
			Logger.getLogger(RobotCommunicator.class.getName()).log(Level.SEVERE, null, ex);
		}

		new Thread() {
			@Override
			public void run() {
				while (run) {

					String message;
					try {
						message = transmitter.readLineBlocking();
						BallInformationMessage ballInfo = BallInformationMessage.parseString(message);
						
				    	LCD.drawString("Robot recieved: " + ballInfo, 0, 4);
						Delay.msDelay(2000);
						System.out.println("Robot recieved: " + ballInfo);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				try {
					transmitter.close();
				} catch (IOException ex) {
					Logger.getLogger(RobotCommunicator.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}.start();
	}
	
	 //should not be needed, but those tcp connections are bidirectional.
    private void sendMessage(String str) {
        System.out.println("SENDING: " + str);
        try {
            transmitter.write(str + "\n");
        } catch (IOException ex) {
            Logger.getLogger(RobotCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isRunning() {
        return run;
    }

    public void setRunning(boolean run) {
        this.run = run;
    }
    
    public static void main(String[] args) {
    	LCD.clear();
    	LCD.drawString("Plugin Test", 0, 4);
		Delay.msDelay(5000);
	
    	TestMain communicator = new TestMain();
 	}
    
	

}
