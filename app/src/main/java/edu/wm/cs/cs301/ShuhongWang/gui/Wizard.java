package edu.wm.cs.cs301.ShuhongWang.gui;

import edu.wm.cs.cs301.ShuhongWang.generation.CardinalDirection;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
import edu.wm.cs.cs301.ShuhongWang.gui.Robot.Direction;
import edu.wm.cs.cs301.ShuhongWang.gui.Robot.Turn;

/**
 * This class uses information of the Maze object to get out the maze.
 * 
 * Responsibility: drive the robot to exit using the information of the Maze object. Provide information of
 * success or not to the controller.
 * 
 * Collaborators: ReliableRobot, UnreliableRobot, Maze
 * 
 * @author Shuhong Wang
 *
 */
public class Wizard implements RobotDriver {

	private Robot robot;
	private Maze maze;
	private float initialEnergyLevel;
	
	public Wizard() {
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
		// while the robot is not at the exit
		while (!robot.isAtExit()) {
			// drive the robot 1 step to exit
			// if catch any error, throw exception.
			boolean success = drive1Step2Exit();
			// if  drive1step2exit return false, return false
			if (!success) {
				return false;
			}
		}
		// if the robot is at the exit
//		return true;
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
		
		if (robot.getBatteryLevel() < robot.getEnergyForStepForward()) {
			throw new Exception("Lack of energy for step forward!");
		}
		// if the robot is not at the exit
		if (!robot.isAtExit()) {
			// get the current position
			int x = robot.getCurrentPosition()[0];
			int y = robot.getCurrentPosition()[1];
			CardinalDirection currentDirection = robot.getCurrentDirection();
			
			///////////////////////// P4 BONUS WIZARD JUMP PART STARTS //////////////////////////////
			// get the position of the neighbor/cell that is closest to the exit
			int[] neighbor = getNextPosition(x,y);
			///////////////////////// P4 BONUS WIZARD JUMP PART ENDS //////////////////////////////
			CardinalDirection neighborDirection = CardinalDirection.getDirection(neighbor[0]-x, neighbor[1]-y);

			rotateToFaceTargetDirection(currentDirection, neighborDirection);
			
			///////////////////////// P4 BONUS WIZARD JUMP PART STARTS //////////////////////////////
			// Decide to move or to jump.
			if (maze.hasWall(x, y, neighborDirection)) {
				robot.jump();
			}
			else {
				robot.move(1);
			}
			///////////////////////// P4 BONUS WIZARD JUMP PART ENDS //////////////////////////////
			
			if (robot.hasStopped()) {
				return false;
			}
			return true;
		}
		// if the robot is at the exit position
		else {
			// rotate the robot to face the exit
			if (robot.canSeeThroughTheExitIntoEternity(Direction.LEFT)) {
				robot.rotate(Turn.LEFT);
//				robot.move(1);
			}
			else if (robot.canSeeThroughTheExitIntoEternity(Direction.RIGHT)) {
				robot.rotate(Turn.RIGHT);
//				robot.move(1);
			}
			if (robot.hasStopped()) {
				return false;
			}
			return true;
		}
		
	}

	/**
	 * Rotate the robot to face the target position's direction.
	 * 
	 * @param currentDirection current direction of the robot
	 * @param neighborDirection the neighbor's direction 
	 * @throws Exception if direction is not valid
	 */
	private void rotateToFaceTargetDirection(CardinalDirection currentDirection, CardinalDirection neighborDirection)
			throws Exception {
		// move to the neighbor's position
		// rotate the robot
		// Initialize a list of directions
		CardinalDirection[] list = new CardinalDirection[4];
		list[0] = CardinalDirection.North;
		list[1] = CardinalDirection.East;
		list[2] = CardinalDirection.South;
		list[3] = CardinalDirection.West;
		
		int curDir = 100, nbrDir = 100;
		for (int i = 0; i < 4; i++) {
			if (currentDirection == list[i])
				curDir = i;
			if (neighborDirection == list[i])
				nbrDir = i;
		}
		
		if (curDir > 3 || nbrDir > 3) {
			throw new Exception("direction error");
		}
		
		// Determine the turn type according to the rotation value
		switch (curDir - nbrDir) {
		case 0:
			break;
		case 1:
		case -3:
			robot.rotate(Turn.RIGHT);
			break;
		case -1:
		case 3:
			robot.rotate(Turn.LEFT);
			break;
		case 2:
		case -2:
			robot.rotate(Turn.AROUND);
			break;
		}
	}
	
	///////////////////////// P4 BONUS WIZARD JUMP PART STARTS //////////////////////////////
	/**
	 * Helps the wizard to decide next place to go. Check the position is worth jumping or not.
	 * 
	 * @param x the x-coordinate of current position
	 * @param y the y-coordinate of current position
	 * @return neighbor the next position that the wizard is going to.
	 */
	private int[] getNextPosition(int x, int y) {
		// find best candidate
		int dnow= maze.getDistanceToExit(x, y) ;
		int[] result = new int[2] ;
		int[] dir;
		// Create a hash table to store the candidates.
		int[] distances = new int[4];

		CardinalDirection[] cds = CardinalDirection.values();
		for (int i = 0; i < cds.length; i++) {
			// check the distance
			dir = cds[i].getDirection();
			// if the neighbor in this direction is not inside maze, give it MAX_VALUE;
			if (!maze.isValidPosition(x+dir[0], y+dir[1])) {
				distances[i] = Integer.MAX_VALUE;
			}
			else {
				int dn = maze.getDistanceToExit(x+dir[0], y+dir[1]);
				if (dn < dnow) {
					// update neighbor position with less distance
					distances[i] = dn;
				}	
			}
		}
		
		// Find the minimum distance and its corresponding index. 
		int minDistance = Integer.MAX_VALUE;
		int index = 100;
		for (int i = 0; i < 4; i++) {
			if (distances[i] < minDistance) {
				minDistance = distances[i];
				index = i;
			}
		}
		// Check if the position is worth jumping or not.
		dir = cds[index].getDirection();
		result[0] = x+dir[0];
		result[1] = y+dir[1];
		int dnext = maze.getDistanceToExit(result[0], result[1]);
		
		// if worth, return the position
		if (40 < (dnow-dnext)*6) {
			return result;
		}
		// if not, return the ordinary result.
		else {
			return maze.getNeighborCloserToExit(x, y);
		}
	}
	///////////////////////// P4 BONUS WIZARD JUMP PART ENDS //////////////////////////////
	
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
