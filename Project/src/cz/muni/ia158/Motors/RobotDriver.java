package cz.muni.ia158.Motors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import cz.muni.ia158.PongRobot.settings.Settings;
import cz.muni.ia158.PongRobot.tcp.BallInformationMessage;
import lejos.hardware.Sound;

public class RobotDriver implements Runnable {
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
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				BallInformationMessage msg = queue.poll(Settings.QueueWaitingTime, TimeUnit.MILLISECONDS);

				if (msg != null) {
					//non null message is waiting for processing
					mc.goTo(msg.getXcoord());
				} else {
					mc.goToCenter();
				}

			} catch (InterruptedException ex) {
				System.out.println("Waiting for queue poll was interupted: " + ex);
				ex.printStackTrace();
			}
		}
		mc.close();
		sc.close();
	}
}
