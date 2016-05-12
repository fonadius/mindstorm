package cz.muni.ia158.PongRobot.tcp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CentralUnitCommunicator {

	private static final int PORT = 8081;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("CUC");
//use different thread to listen to events.
		try {

			TCPServer listener = new TCPServer(PORT);
			TCPConnection connection = listener.waitForConnection();
            //should get from server...
			Random random = new Random();
		
			while (true) {
				
				try {
					Thread.sleep(random.nextInt(2000));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BallInformationMessage message = new BallInformationMessage(random.nextDouble()*100, random.nextLong()*100);
				System.out.println("Server writing: " + message.toString());
				connection.write(message.toString() + "\n");
				
				
				/*
				 * robot will notprobably  send messages, so this will not be necessary
				 */
				if (connection.readerReady()) {
					String str = connection.readLine();
					System.out.println("Server recieved: " + str);
				}
				if (connection.closed()){
					break;
				}
			}
			connection.close();
			System.out.println("Server Out ");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
