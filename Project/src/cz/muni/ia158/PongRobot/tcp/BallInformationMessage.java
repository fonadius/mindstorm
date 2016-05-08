package cz.muni.ia158.PongRobot.tcp;

public class BallInformationMessage {
	private double xcoord;
	private double time;
	private boolean isRed;
	
	public double getXcoord() {
		return xcoord;
	}
	public double getTime() {
		return time;
	}
	public boolean isRed() {
		return isRed;
	}
	
	/*
	 * creates object that tell robot where (TODO decide in what coordinate system)
	 * and when the ball will cross the "finishing line".
	 * 
	 */
	public BallInformationMessage(double xcoord, double time){
		this(xcoord, time, false);
	}
	
	/*
	 * This constructor is for bonus assignment. If there is red ball in the field, 
	 * then robot should care about red one, not white one.
	 */
	public BallInformationMessage(double xcoord, double time, boolean isRed){
		if(xcoord < 0){
			throw new IllegalArgumentException("xcoord can't be negative. (or if we are using coord system in which it can be negative, than remove this line from source code of this class");
		}
		if(time < 0 ){
			throw new IllegalArgumentException("Time cannot be negative.");
		}
		this.xcoord =  xcoord;
		this.time = time;
		this.isRed = isRed;
	}
	
	/*
	 * expected string format:
	 * xcoord;time;[isRed;]  //probably does'nt matter if optional or not, for all practical purposes
	 */
	public static BallInformationMessage parseString(String p) {
		String params[] = p.split(";");
		
		double xcoord = Double.parseDouble(params[0]);
		double time = Double.parseDouble(params[1]);

		boolean isRed = false;
		if (params.length == 3){ //isRed is also there
			isRed = Boolean.parseBoolean(params[2]);
		} 
		
		return new BallInformationMessage(xcoord,time,isRed);
	}

	/*
	 * Format might change... 
	 */
	@Override
	public String toString() {
		return xcoord + ";" + time + ";" + isRed + ";";
	}

	
}
