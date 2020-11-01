package edu.wm.cs.cs301.ShuhongWang.gui;

import edu.wm.cs.cs301.ShuhongWang.generation.CardinalDirection;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
import edu.wm.cs.cs301.ShuhongWang.gui.Robot.Direction;

/**
 * This class provides a reliable sensor for the robot.
 * 
 * Responsibility: detect the distance to obstacle in the given direction.
 * 
 * Collaborators: Maze, ReliableRobot, UnreliableRobot
 * 
 * @author Shuhong Wang
 *
 */
public class ReliableSensor implements DistanceSensor {

	private float energyForSensing = 1;
	private Maze maze;
	private Direction direction;
	private boolean working = true;
	
	public ReliableSensor() {
		energyForSensing = 1;
	}
	/**
	 * Tells the distance to an obstacle (a wallboard) that the sensor
	 * measures. The sensor is assumed to be mounted in a particular
	 * direction relative to the forward direction of the robot.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if the current cell has a wallboard in this direction, 
	 * 1 if it is one step in this direction before directly facing a wallboard,
	 * Integer.MaxValue if one looks through the exit into eternity.
	 * 
	 * This method requires that the sensor has been given a reference
	 * to the current maze and a mountedDirection by calling 
	 * the corresponding set methods with a parameterized constructor.
	 * 
	 * @param currentPosition is the current location as (x,y) coordinates
	 * @param currentDirection specifies the direction of the robot
	 * @param powersupply is an array of length 1, whose content is modified 
	 * to account for the power consumption for sensing
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise.
	 * @throws Exception with message 
	 * SensorFailure if the sensor is currently not operational
	 * PowerFailure if the power supply is insufficient for the operation
	 * @throws IllegalArgumentException if any parameter is null
	 * or if currentPosition is outside of legal range
	 * ({@code currentPosition[0] < 0 || currentPosition[0] >= width})
	 * ({@code currentPosition[1] < 0 || currentPosition[1] >= height}) 
	 * @throws IndexOutOfBoundsException if the powersupply is out of range
	 * ({@code powersupply[0] < 0}) 
	 */
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply)
			throws Exception {
		// check if the sensor is working
		if (!working) {
			throw new Exception("SensorFailure");
		}
		// check if the parameters are null
		if (currentPosition == null || currentDirection == null || powersupply == null) {
			throw new IllegalArgumentException("Illegal Argument!");
		}
		// Check if the battery is enough to detect, if not, throw exception
		if (powersupply[0] < 0) {
			throw new IndexOutOfBoundsException("Powersupply is negative!");
		}
		// check if the energy is enough for sensing
		if (powersupply[0] < energyForSensing) {
			throw new Exception("PowerFailure");
		}
		// check if the current position is inside maze.
		if (currentPosition[0] < 0 || currentPosition[0]>= maze.getWidth()
				|| currentPosition[1] < 0 || currentPosition[1] >= maze.getHeight()) {
			throw new IllegalArgumentException("Illegal position!");
		}
		int x = currentPosition[0];
		int y = currentPosition[1];

		// Determine detect direction
		CardinalDirection detectDirection;
		switch(direction) {
		case RIGHT:
			detectDirection = currentDirection.rotateClockwise().oppositeDirection();
			break;
		case LEFT:
			detectDirection = currentDirection.rotateClockwise();
			break;
		case FORWARD:
			detectDirection = currentDirection;
			break;
		case BACKWARD:
			detectDirection = currentDirection.oppositeDirection();
			break;
		default:
			detectDirection = currentDirection;
		}
		int dx = detectDirection.getDirection()[0];
		int dy = detectDirection.getDirection()[1];

		// Go on this direction until there is a wall in the front.
		int count = 0;
		while (!maze.hasWall(x, y, detectDirection)) {
			count++;
			x += dx;
			y += dy;
			// If go outside of the maze, return MAX_VALUE
			if (x < 0 || x >= maze.getWidth() || y < 0 || y >= maze.getHeight()) {
				return Integer.MAX_VALUE;
			}
		}
		// Subtract the energy used
		powersupply[0] -= energyForSensing;

		return count;
	}

	/**
	 * Provides the maze information that is necessary to make
	 * a DistanceSensor able to calculate distances.
	 * @param maze the maze for this game
	 * @throws IllegalArgumentException if parameter is null
	 * or if it does not contain a floor plan
	 */
	@Override
	public void setMaze(Maze maze) {
		if (maze == null) {
			throw new IllegalArgumentException("Input maze is null!");
		}
		this.maze = maze;
	}

	/**
	 * Provides the angle, the relative direction at which this 
	 * sensor is mounted on the robot.
	 * If the direction is left, then the sensor is pointing
	 * towards the left hand side of the robot at a 90 degree
	 * angle from the forward direction. 
	 * @param mountedDirection is the sensor's relative direction
	 * @throws IllegalArgumentException if parameter is null
	 */
	@Override
	public void setSensorDirection(Direction mountedDirection) {
		if (mountedDirection == null) {
			throw new IllegalArgumentException("Input direction is null!");
		}
		direction = mountedDirection;
	}

	/**
	 * Returns the amount of energy this sensor uses for 
	 * calculating the distance to an obstacle exactly once.
	 * This amount is a fixed constant for a sensor.
	 * @return the amount of energy used for using the sensor once
	 */
	@Override
	public float getEnergyConsumptionForSensing() {
		return energyForSensing;
	}

	/**
	 * Not available.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Method not available!");
	}

	/**
	 * Not available.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Method not available!");
	}

}
