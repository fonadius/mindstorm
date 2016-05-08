package cz.muni.ia158.PongRobot.tests.tcp;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.muni.ia158.PongRobot.tcp.BallInformationMessage;

public class BallInformationMessageTest {

	@Test
	public void testParseString() {
		BallInformationMessage expected1 = new BallInformationMessage(12.0,0.5, false);
		BallInformationMessage actual1 = BallInformationMessage.parseString("12.0;0.5;false;");

		assertEquals(expected1.getTime(), actual1.getTime(), 0.05);
		assertEquals(expected1.getXcoord(), actual1.getXcoord(), 0.05);
		assertEquals(expected1.isRed(), actual1.isRed());
		
		BallInformationMessage expected2 = new BallInformationMessage(1234.5678,9876.5432, true);
		BallInformationMessage actual2 = BallInformationMessage.parseString("1234.5678;9876.5432;true;");

		assertEquals(expected2.getTime(), actual2.getTime(), 0.05);
		assertEquals(expected2.getXcoord(), actual2.getXcoord(), 0.05);
		assertEquals(expected2.isRed(), actual2.isRed());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testINegativeXcoord() {
		BallInformationMessage.parseString("-12.0;0.5;false;");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testINegativeTime() {
		 BallInformationMessage.parseString("12.0;-0.5;false;");
	}
	@Test
	public void testToString() {
		assertEquals("12.0;0.5;false;", new BallInformationMessage(12.0,0.5, false).toString());
	}

}
