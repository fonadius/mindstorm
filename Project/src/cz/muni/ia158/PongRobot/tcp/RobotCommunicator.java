package cz.muni.ia158.PongRobot.tcp;

import static cz.muni.ia158.PongRobot.settings.Settings.runtimeSettings;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.muni.ia158.Motors.MotorController;
import cz.muni.ia158.PongRobot.settings.Settings;
import cz.muni.ia158.PongRobot.tests.tcp.BallInformationMessageTest;

public class RobotCommunicator implements Runnable{
	private TCPConnection transmitter;
	private final BlockingQueue<BallInformationMessage> queue;

	public RobotCommunicator(BlockingQueue<BallInformationMessage> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		System.out.println("Starting robot communication");
		try {
			transmitter = new TCPConnection(runtimeSettings.getServerUrl(), runtimeSettings.getControlUnitPort());
		} catch (IOException ex) {
			System.out.println("Cannot create transmitter: " + ex);
			ex.printStackTrace();
		}
	
		System.out.println("Waiting for server to Do something");
		while (!Thread.currentThread().isInterrupted()) {
			String message;
			try {
				message = transmitter.readLineBlocking();
				BallInformationMessage ballInfo = BallInformationMessage.parseString(message);
				System.out.println("Robot recieved msg: " + ballInfo);
				if(ballInfo.getTime() == 6666){
					break; //ugly, hardcoded termination message... had some trouble with detecting closed socket..
				}
				boolean added = queue.offer(ballInfo, Settings.QueueWaitingTime, TimeUnit.MILLISECONDS);
				if (!added) {
					System.out.println("Waiting for queue timmed out. Msg is dropped.");
				}
				
			} catch (IOException ex) {
				System.out.println("Error during communication: " + ex);
				ex.printStackTrace();
			} catch (InterruptedException ex) {
				System.out.println("Waiting for queue offer was interupted: " + ex);
				ex.printStackTrace();
			}
			if(transmitter.closed()){
				break;
			}
		}
		try {
			transmitter.close();
		} catch (IOException ex) {
			System.out.println("Transmitter cannot be closed: " + ex);
			ex.printStackTrace();
		}
	}
    public static void main(String[] args) {
        
    	new Thread(new RobotCommunicator(new ArrayBlockingQueue<>(20))).start();
    	
 	}
}
