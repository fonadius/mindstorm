package cz.muni.ia158.PongRobot.tcp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
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
			System.out.println("Waiting for connection...");
			TCPConnection connection = listener.waitForConnection();
		
			while (true) {
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Scanner reader = new Scanner(System.in);
				System.out.print("Write your input: ");
				double pose = reader.nextDouble();
				reader.close();
				BallInformationMessage message = new BallInformationMessage(pose, 0);
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
