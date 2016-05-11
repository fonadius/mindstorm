package cz.muni.ia158.Motors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import cz.muni.ia158.PongRobot.settings.Settings;
import cz.muni.ia158.PongRobot.tcp.BallInformationMessage;

public class RobotDriver implements Runnable{
	private final BlockingQueue<BallInformationMessage> queue;
	private final MotorController mc;
	private final SensorController sc;

	public RobotDriver(BlockingQueue<BallInformationMessage> queue) {
		this.queue = queue;
		sc = new SensorController();
		mc = new MotorController(sc);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				BallInformationMessage msg = queue.poll(Settings.QueueWaitingTime, TimeUnit.MILLISECONDS);
				
				mc.goTo(msg.getXcoord());
				sc.waitForImpact();
				mc.goToCenter();
				
			} catch (InterruptedException ex) {
				System.out.println("Waiting for queue poll was interupted: " + ex);
				ex.printStackTrace();
			}
		}
		mc.close();
		sc.close();
	}
}
