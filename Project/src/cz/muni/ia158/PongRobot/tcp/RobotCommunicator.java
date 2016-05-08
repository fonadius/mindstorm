package cz.muni.ia158.PongRobot.tcp;

import static cz.muni.ia158.PongRobot.settings.Settings.runtimeSettings;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RobotCommunicator {
	public static TCPConnection transmitter;
	volatile private boolean run = true;

	public RobotCommunicator() {
		System.out.println("Robot");
		try {
			transmitter = new TCPConnection(runtimeSettings.getServerUrl(), runtimeSettings.getControlUnitPort());
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
    
    	RobotCommunicator communicator = new RobotCommunicator();
 	}
}
