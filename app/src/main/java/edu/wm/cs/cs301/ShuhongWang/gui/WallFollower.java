package edu.wm.cs.cs301.ShuhongWang.gui;

import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
import edu.wm.cs.cs301.ShuhongWang.gui.Robot.Direction;
import edu.wm.cs.cs301.ShuhongWang.gui.Robot.Turn;

/**
 * This class implemented a wall-follower algorithm to quickly solve the maze
 * 
 * Responsibility: Solve the maze with wall-follower algorithm
 * 
 * Collaborators: UnreliableRobot, ReliableRobot.
 * 
 * @author Shuhong Wang
 *
 */
public class WallFollower implements RobotDriver {

	private Robot robot;
	private Maze maze;
	private float initialEnergyLevel;
	
	/**
	 * Constructor of the class/
	 */
	public WallFollower() {
		robot = null;
		maze = null;
	}
	
	/**
	 * Assigns a robot platform to the driver. 
	 * The driver uses a robot to perform, this method provides it with this necessary information.
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		robot = r;
		initialEnergyLevel = robot.getBatteryLevel();
	}

	/**
	 * Provides the robot driver with the maze information.
	 * @param maze represents the maze, must be non-null and a fully functional maze object.
	 */
	@Override
	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	/**
	  * Drives the robot towards the exit following
	  * its solution strategy and given the exit exists and  
	  * given the robot's energy supply lasts long enough. 
	  * When the robot reached the exit position and its forward
	  * direction points to the exit the search terminates and 
	  * the method returns true.
	  * If the robot failed due to lack of energy or crashed, the method
	  * throws an exception.
	  * If the method determines that it is not capable of finding the
	  * exit it returns false, for instance, if it determines it runs
	  * in a cycle and can't resolve this.
	  * @return true if driver successfully reaches the exit, false otherwise
	  * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	  */
	@Override
	public boolean drive2Exit() throws Exception {
		// check if the robot is in the room and there is no walls on its left.
		if (robot.isInsideRoom() && !this.detectWall(Direction.LEFT)) {
			// if the robot is inside room, move forward until meet a wall
			while (!this.detectWall(Direction.FORWARD)) {
				robot.move(1);
			}
			// when meet a wall in front, turn left to make the wall on its left.
			robot.rotate(Turn.RIGHT);
		}
		////////////////////////// P4 BONUS AVOIDING INNER WALL ////////////////////////
		// If the robot is following an inner wall, it has to go over its starting position over 4 times.
		// The starting position is defined to be the first time the robot touches the wall,
		// i.e., the first time the program enters the while loop.
		// If we store the starting position and count the times that the robot go over it, if the count
		// is greater than 4, then we can tell the robot is following an inner wall.
		// If the robot is following an inner wall, we simply let the robot to rotate right and move
		// forward until reach a wall and then follow that wall.
		// We reset the starting point and then repeat this process to guarantee that the robot will
		// not be following the inner wall finally.
		
		int[] startingPoint = robot.getCurrentPosition();
		int count = 0;
		// while the robot is not at the exit
		while (!robot.isAtExit()) {
			// drive the robot 1 step to exit
			// if catch any error, throw exception.
			boolean success = drive1Step2Exit();
			// if  drive1step2exit return false, return false
			if (!success) {
				return false;
			}
			// check if the robot is going over its starting point
			if (startingPoint.equals(robot.getCurrentPosition())) {
				count++;
				// if the robot go over this point over 4 times, try to stop following the inner wall.
				if (count > 4) {
					robot.rotate(Turn.RIGHT);
					while (!this.detectWall(Direction.FORWARD)) {
						robot.move(1);
					}
					// when meet a wall in front, turn left to make the wall on its left.
					robot.rotate(Turn.RIGHT);
					// reset the values to avoid running into another inner wall.
					startingPoint = robot.getCurrentPosition();
					count = 0;
				}
			}
		}
		// if the robot is at the exit
		return drive1Step2Exit();
	}

	/**
	  * Drives the robot one step towards the exit following
	  * its solution strategy and given the exists and 
	  * given the robot's energy supply lasts long enough.
	  * It returns true if the driver successfully moved
	  * the robot from its current location to an adjacent
	  * location.
	  * At the exit position, it rotates the robot 
	  * such that if faces the exit in its forward direction
	  * and returns false. 
	  * If the robot failed due to lack of energy or crashed, the method
	  * throws an exception. 
	  * @return true if it moved the robot to an adjacent cell, false otherwise
	  * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	  */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		// Handle energy exceptions
		if (robot.getBatteryLevel() < robot.getEnergyForStepForward() + 1) {
			throw new Exception("WallFollower.drive1Step2Exit: Energy not enough!");
		}
		// check if the robot is at exit.
		if (robot.isAtExit()) {
			// check the position of the exit, then rotate the robot to face the exit.
			if (this.detectExit(Direction.LEFT)) {
				robot.rotate(Turn.LEFT);
			}
			else if (this.detectExit(Direction.RIGHT)) {
				robot.rotate(Turn.RIGHT);
			}
			// For testing purpose:
			else if (!this.detectExit(Direction.FORWARD)) {
				System.out.println("WallFollower.drive1Step2Exit: Exit backwards.");
			}
			if (robot.hasStopped()) {
				System.out.println("WallFollower.drive1Step2Exit: stopped at 1.");
				return false;
			}
		}
		// if not at the exit,
		else {
			// check if there is a wall on the left side
			if (!detectWall(Direction.LEFT)) {
				// if not, rotate left and move 1 step forward.
				robot.rotate(Turn.LEFT);
				robot.move(1);
			}
			else {
				// if it has, check if there is a wall forward
				if (!detectWall(Direction.FORWARD)) {
					// if not, move one step forward
					robot.move(1);
				}
				else {
					// if there is, check if there is a wall on its right
					if (detectWall(Direction.RIGHT)) {
						// if there is, turn around and move 1 step forward.
						robot.rotate(Turn.AROUND);
						robot.move(1);
					}
					else {
						// if there isn't, turn right and move 1 step forward.
						robot.rotate(Turn.RIGHT);
						robot.move(1);
					}
				}
			}
			// check if the robot is still running or not
			if (robot.hasStopped()) {
				System.out.println("WallFollower.drive1Step2Exit: stopped at 2.");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * A method that helps the drive1Step2Exit to detect walls in the given direction 
	 * with unreliable sensors.
	 * 
	 * @param direction the direction that we want to detect.
	 */
	private boolean detectWall(Direction direction) {
		if (robot instanceof ReliableRobot) {
			return robot.distanceToObstacle(direction) == 0;
		}
		// check if the sensor of this direction works. If it work, use it to detect.
		if (((UnreliableRobot) robot).isSensorWorking(direction)) {
			return robot.distanceToObstacle(direction) == 0;
		}
		else {
			// If not, check the closest sensor to it is working or not.
			// If there exists a working one, rotate the robot and use that sensor to detect.

			// Use a list to find the closest sensor
			Direction[] list = new Direction[4];
			list[0] = Direction.FORWARD;
			list[1] = Direction.RIGHT;
			list[2] = Direction.BACKWARD;
			list[3] = Direction.LEFT;
			
			int currentSensor = 100;
			for (int i = 0; i < 4; i++) {
				if(list[i] == direction) {
					currentSensor = i;
				}
			}

			// check the one to the sensor's right
			int targetSensor = (currentSensor + 1) % 4;
			robot.rotate(Turn.LEFT);
			if (((UnreliableRobot) robot).isSensorWorking(list[targetSensor])) {
				boolean result = robot.distanceToObstacle(list[targetSensor]) == 0;
				robot.rotate(Turn.RIGHT);
				return result;
			}
			// check the one to the sensor's left
			targetSensor = (currentSensor + 2) % 4;
			robot.rotate(Turn.LEFT);
			if (((UnreliableRobot) robot).isSensorWorking(list[targetSensor])) {
				boolean result = robot.distanceToObstacle(list[targetSensor]) == 0;
				robot.rotate(Turn.AROUND);
				return result;
			}
			// check the one on the opposite direction;
			targetSensor = (currentSensor + 3) % 4;
			robot.rotate(Turn.LEFT);
			if (((UnreliableRobot) robot).isSensorWorking(list[targetSensor])) {
				boolean result = robot.distanceToObstacle(list[targetSensor]) == 0;
				robot.rotate(Turn.LEFT);
				return result;
			}
			// If there doesn't exist a working sensor, wait until the target sensor to wake and detect.
			while (!((UnreliableRobot) robot).isSensorWorking(direction)) {
				// do nothing and wait
				assert true;
			}
			return robot.distanceToObstacle(direction) == 0;
		}

	}
	
	/**
	 * A method that helps the drive1Step2Exit() method to detect exit with unreliable sensors.
	 * 
	 * @param direction the direction to be tested
	 * @return true if there is an exit in this direction, false if not.
	 */
	private boolean detectExit(Direction direction) {
		if (robot instanceof ReliableRobot) {
			return robot.canSeeThroughTheExitIntoEternity(direction);
		}
		// check if the sensor of this direction works. If it work, use it to detect.
		if (((UnreliableRobot) robot).isSensorWorking(direction)) {
			return robot.canSeeThroughTheExitIntoEternity(direction);
		}
		else {
			// If not, check the closest sensor to it is working or not.
			// If there exists a working one, rotate the robot and use that sensor to detect.

			// Use a list to find the closest sensor
			Direction[] list = new Direction[4];
			list[0] = Direction.FORWARD;
			list[1] = Direction.RIGHT;
			list[2] = Direction.BACKWARD;
			list[3] = Direction.LEFT;
			
			int currentSensor = 100;
			for (int i = 0; i < 4; i++) {
				if(list[i] == direction) {
					currentSensor = i;
				}
			}
			// check the one to the sensor's right
			int targetSensor = (currentSensor + 1) % 4;
			if (((UnreliableRobot) robot).isSensorWorking(list[targetSensor])) {
				robot.rotate(Turn.LEFT);
				boolean result = robot.canSeeThroughTheExitIntoEternity(list[targetSensor]);
				robot.rotate(Turn.RIGHT);
				return result;
			}
			// check the one to the sensor's left
			targetSensor = (currentSensor - 1) % 4;
			if (((UnreliableRobot) robot).isSensorWorking(list[targetSensor])) {
				robot.rotate(Turn.RIGHT);
				boolean result = robot.canSeeThroughTheExitIntoEternity(list[targetSensor]);
				robot.rotate(Turn.LEFT);
				return result;
			}
			// check the one on the opposite direction;
			targetSensor = (currentSensor + 2) % 4;
			if (((UnreliableRobot) robot).isSensorWorking(list[targetSensor])) {
				robot.rotate(Turn.AROUND);
				boolean result = robot.canSeeThroughTheExitIntoEternity(list[targetSensor]);
				robot.rotate(Turn.AROUND);
				return result;
			}

			// If there doesn't exist a working sensor, wait until the target sensor to wake and detect.
			while (!((UnreliableRobot) robot).isSensorWorking(direction)) {
				// do nothing and wait
				assert true;
			}
			// when the sensor changed to working status, detect wall.
			return robot.distanceToObstacle(direction) == 0;
		}

	}

	/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position. 
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total energy consumption of the journey
	 */
	@Override
	public float getEnergyConsumption() {
		return 3500 - robot.getBatteryLevel();
	}

	/**
	 * Returns the total length of the journey in number of cells traversed. 
	 * Being at the initial position counts as 0. 
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total length of the journey in number of cells traversed
	 */
	@Override
	public int getPathLength() {
		return robot.getOdometerReading();
	}
}
