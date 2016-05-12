package cz.muni.ia158.PongRobot;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import cz.muni.ia158.Motors.RobotDriver;
import cz.muni.ia158.PongRobot.tcp.BallInformationMessage;
import cz.muni.ia158.PongRobot.tcp.RobotCommunicator;

public class Main {	
	public static void main(String[] args) {
		BlockingQueue<BallInformationMessage> queue = new ArrayBlockingQueue<>(20);
		RobotCommunicator comm = new RobotCommunicator(queue);
		RobotDriver control = new RobotDriver(queue);
		new Thread(control).start();
		new Thread(comm).start();
		System.out.println("Communication and controlls started");
	}

}
