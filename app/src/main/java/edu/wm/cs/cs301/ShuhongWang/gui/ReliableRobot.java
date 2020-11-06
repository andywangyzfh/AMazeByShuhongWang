package edu.wm.cs.cs301.ShuhongWang.gui;

import edu.wm.cs.cs301.ShuhongWang.generation.CardinalDirection;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
import edu.wm.cs.cs301.ShuhongWang.gui.Constants.UserInput;

/**
 * This class provides a well-functioning robot, whose sensors are all working.
 * 
 * Responsibilities: provide a robot with limited battery; it can move in certain directions and jump over a wall;
 * it can detect the obstacles and exits.
 * 
 * Collaborators: Controller, ReliableSensor, UnreliableSensor.
 * 
 * @author Shuhong Wang
 *
 */
public class ReliableRobot implements Robot {
	
	// define private variables here
	private float batteryLevel;
	private int odometer;
//	private Controller controller;
	private StatePlaying statePlaying;
	private float energyForFullRotation;
	private float energyForStepForward;
	private float energyForJump;
	private boolean stopped;
	private DistanceSensor leftSensor;
	private DistanceSensor rightSensor;
	private DistanceSensor frontSensor;
	private DistanceSensor backSensor;
	
	/**
	 * Constructor of the class. Initialize the robot.
	 */
	public ReliableRobot() {
		// Initialize the private variables, e.g., battery level and odometer.
		batteryLevel = 3500;
		odometer = 0;
		energyForFullRotation = 12;
		energyForStepForward = 6;
		energyForJump = 40;
		stopped = false;

		leftSensor = new ReliableSensor();
		leftSensor.setSensorDirection(Direction.LEFT);
		rightSensor = new ReliableSensor();
		rightSensor.setSensorDirection(Direction.RIGHT);
		frontSensor = new ReliableSensor();
		frontSensor.setSensorDirection(Direction.FORWARD);
		backSensor = new ReliableSensor();
		backSensor.setSensorDirection(Direction.BACKWARD);
	}

	/**
	 * Initialize the controller that provides the robot needed information
	 * 
	 * @param statePlaying is the communication partner for robot
	 * @throws IllegalArgumentException if controller is null, 
	 */
	@Override
	public void setStatePlaying(StatePlaying statePlaying) {
		// Check if the controller is null. If null, throw IllegalArgumentException.
		if (statePlaying == null) {
			throw new IllegalArgumentException("controller is null!");
		}
		
		// Initialize the private value.
		this.statePlaying = statePlaying;
		Maze maze = statePlaying.getMazeConfiguration();
		leftSensor.setMaze(maze);
		rightSensor.setMaze(maze);
		frontSensor.setMaze(maze);
		backSensor.setMaze(maze);
	}
	
	/**
	 * Provide the information of the controller.
	 * 
	 * @return controller is the communication partner for robot.
	 */
	public StatePlaying getStatePlaying() {
		return statePlaying;
	}

	/**
	 * Provides the current position as (x,y) coordinates for 
	 * the maze as an array of length 2 with [x,y].
	 * @return array of length 2, x = array[0], y = array[1]
	 * and ({@code 0 <= x < width, 0 <= y < height}) of the maze
	 * @throws Exception if position is outside of the maze
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		// Get the position from the controller.
		int[] currentPosition = statePlaying.getCurrentPosition();
		int x = currentPosition[0];
		int y = currentPosition[1];
		int height = statePlaying.getMazeConfiguration().getHeight();
		int width = statePlaying.getMazeConfiguration().getWidth();
		
		// If the position is invalid, throw Exception.
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new Exception("Position outside of the maze!");
		}

		return currentPosition;
	}

	/**
	 * Provides the robot's current direction.
	 * @return cardinal direction is the robot's current direction in absolute terms
	 */	
	@Override
	public CardinalDirection getCurrentDirection() {
		// Return the current direction.
		return statePlaying.getCurrentDirection();
	}

	/**
	 * Returns the current battery level.
	 * The robot has a given battery level (energy level) 
	 * that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call 
	 * for sensor distance2Obstacle may use less energy than a move forward operation.
	 * If battery {@code level <= 0} then robot stops to function and hasStopped() is true.
	 * @return current battery level, {@code level > 0} if operational. 
	 */
	@Override
	public float getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * Sets the current battery level.
	 * The robot has a given battery level (energy level) 
	 * that it draws energy from during operations. 
	 * The particular energy consumption is device dependent such that a call 
	 * for distance2Obstacle may use less energy than a move forward operation.
	 * If battery {@code level <= 0} then robot stops to function and hasStopped() is true.
	 * @param level is the current battery level
	 * @throws IllegalArgumentException if level is negative 
	 */
	@Override
	public void setBatteryLevel(float level) {
		// if level < 0, throw IllegalArgumentException.
		if (level < 0) {
			throw new IllegalArgumentException("Invalid input battery level!");
		}
		// Set the battery level.
		batteryLevel = level;
	}

	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption. 
	 * @return energy for a full rotation
	 */
	@Override
	public float getEnergyForFullRotation() {
		return energyForFullRotation;
	}

	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step.
	 * For simplicity, we assume that this equals the energy necessary 
	 * to move 1 step backwards and that scaling by a larger number of moves is 
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	@Override
	public float getEnergyForStepForward() {
		return energyForStepForward;
	}

	/** 
	 * Gets the distance traveled by the robot.
	 * The robot has an odometer that calculates the distance the robot has moved.
	 * Whenever the robot moves forward, the distance 
	 * that it moves is added to the odometer counter.
	 * The odometer reading gives the path length if its setting is 0 at the start of the game.
	 * The counter can be reset to 0 with resetOdomoter().
	 * @return the distance traveled measured in single-cell steps forward
	 */
	@Override
	public int getOdometerReading() {
		return odometer;
	}

	/** 
     * Resets the odometer counter to zero.
     * The robot has an odometer that calculates the distance the robot has moved.
     * Whenever the robot moves forward, the distance 
     * that it moves is added to the odometer counter.
     * The odometer reading gives the path length if its setting is 0 at the start of the game.
     */
	@Override
	public void resetOdometer() {
		odometer = 0;
	}
	
	/**
	 * Turn robot on the spot for amount of degrees. 
	 * If robot runs out of energy, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * @param turn is the direction to turn and relative to current forward direction. 
	 */
	@Override
	public void rotate(Turn turn) {
		// decide the amount of battery cost according to the direction
		float energyCost;
		switch (turn) {
		case LEFT:
			energyCost = this.getEnergyForFullRotation()/4;
			// If the battery level is not enough, do nothing
			if (this.getBatteryLevel() < energyCost || hasStopped()) {
				batteryLevel = 0;
				stopped = true;
			}
			else {
				// set the direction of the robot according to Turn.
				statePlaying.keyDown(UserInput.Left);
				// subtract the battery cost
				setBatteryLevel(getBatteryLevel() - energyCost);
			}
			break;
		case RIGHT:
			energyCost = this.getEnergyForFullRotation()/4;
			if (this.getBatteryLevel() < energyCost || hasStopped()) {
				batteryLevel = 0;
				stopped = true;
			}
			else {
				statePlaying.keyDown(UserInput.Right);
				setBatteryLevel(getBatteryLevel() - energyCost);
			}
			break;
		case AROUND:
			energyCost = this.getEnergyForFullRotation()/2;
			if (this.getBatteryLevel() < energyCost || hasStopped()) {
				batteryLevel = 0;
				stopped = true;
			}
			else {
				statePlaying.keyDown(UserInput.Right);
				statePlaying.keyDown(UserInput.Right);
				setBatteryLevel(getBatteryLevel() - energyCost);
			}
			break;
		}

	}

	/**
	 * Moves robot forward a given number of steps. A step matches a single cell.
	 * If the robot runs out of energy somewhere on its way, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level. 
	 * If the robot hits an obstacle like a wall, it remains at the position in front 
	 * of the obstacle and also hasStopped() == true as this is not supposed to happen.
	 * This is also helpful to recognize if the robot implementation and the actual maze
	 * do not share a consistent view on where walls are and where not.
	 * @param distance is the number of cells to move in the robot's current forward direction 
	 * @throws IllegalArgumentException if distance not positive
	 */
	@Override
	public void move(int distance) {
		// If the distance is not positive, throw exception
		if (distance < 0) {
			throw new IllegalArgumentException("Input distance is not positive!");
		}

		// For each step, 
		while (distance > 0) {
			// get current position and direction.
			int[] currentPosition;
			try {
				currentPosition = getCurrentPosition();
			} catch (Exception e) {
				stopped = true;
				break;
			}
			CardinalDirection currentDirection = this.getCurrentDirection();
	
			// Check if the battery level is enough for the movement. If not, do nothing.
			if (batteryLevel < getEnergyForStepForward() || hasStopped()) {
				batteryLevel = 0;
				stopped = true;
				break;
			}
			// For each step, check if there is a wall in the front. If there is, stop movement,
			if (statePlaying.getMazeConfiguration().hasWall(currentPosition[0], currentPosition[1], currentDirection)) {
				System.out.println("ROBOT CRASHED.");
				batteryLevel = 0;
				stopped = true;
				break;
			}
			// If the robot can move
			else {
				// move the robot forward
				statePlaying.keyDown(UserInput.Up);
				// consume energy
				setBatteryLevel(getBatteryLevel() - getEnergyForStepForward());
				// decrease distance by 1
				distance--;
				// increase odometer by 1
				odometer++;
			}
		}

	}

	/**
	 * Makes robot move in a forward direction even if there is a wall
	 * in front of it. In this sense, the robot jumps over the wall
	 * if necessary. The distance is always 1 step and the direction
	 * is always forward.
	 * If the robot runs out of energy somewhere on its way, it stops, 
	 * which can be checked by hasStopped() == true and by checking the battery level.
	 * If the robot tries to jump over an exterior wall and
	 * would land outside of the maze that way,  
	 * it remains at its current location and direction,
	 * hasStopped() == true as this is not supposed to happen.
	 */
	@Override
	public void jump() {
		// check if the energy is enough. If not, do nothing
		if (batteryLevel < energyForJump || hasStopped()) {
			batteryLevel = 0;
			stopped = true;
			return;
		}
		// If the energy is enough
		else {
			// subtract the energy used
			setBatteryLevel(getBatteryLevel() - energyForJump);

			int[] currentPosition;
			try {
				currentPosition = getCurrentPosition();
			} catch (Exception e) {
				return;
			}
			// check if the robot will landed outside the maze
			int x = currentPosition[0];
			int y = currentPosition[1];
			CardinalDirection direction = getCurrentDirection();
			int[] d = direction.getDirection();
			int dx = d[0];
			int dy = d[1];
			int height = statePlaying.getMazeConfiguration().getHeight();
			int width = statePlaying.getMazeConfiguration().getWidth();
			
			if (x+dx < 0 || x+dx >= width || y+dy < 0 || y+dy >= height) {
				// if landed outside, stop the robot, and don't move the robot.
				batteryLevel = 0;
				stopped = true;
				return;
			}
			// If not, move the robot one cell to the front.
			else {
				statePlaying.keyDown(UserInput.Jump);
			}
			
		}

	}

	/**
	 * Tells if the current position is right at the exit but still inside the maze. 
	 * The exit can be in any direction. It is not guaranteed that 
	 * the robot is facing the exit in a forward direction.
	 * @return true if robot is at the exit, false otherwise
	 */
	@Override
	public boolean isAtExit() {
		// get the current position and check if it is the exit.
		int[] currentPosition;
		try {
			currentPosition = getCurrentPosition();
		} catch (Exception e) {
			return false;
		}
		
		return statePlaying.getMazeConfiguration().getFloorplan().isExitPosition(currentPosition[0], currentPosition[1]);
	}

	/**
	 * Tells if current position is inside a room. 
	 * @return true if robot is inside a room, false otherwise
	 */	
	@Override
	public boolean isInsideRoom() {
		int[] currentPosition;
		try {
			currentPosition = getCurrentPosition();
		} catch (Exception e) {
			return false;
		}
		return statePlaying.getMazeConfiguration().getFloorplan().isInRoom(currentPosition[0], currentPosition[1]);
	}

	/**
	 * Tells if the robot has stopped for reasons like lack of energy, 
	 * hitting an obstacle, etc.
	 * Once a robot is has stopped, it does not rotate or 
	 * move anymore.
	 * @return true if the robot has stopped, false otherwise
	 */
	@Override
	public boolean hasStopped() {
		return stopped;
	}

	/**
	 * Tells the distance to an obstacle (a wall) 
	 * in the given direction.
	 * The direction is relative to the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle, 
	 * e.g. 0 if the current cell has a wallboard in this direction, 
	 * 1 if it is one step forward before directly facing a wallboard,
	 * Integer.MaxValue if one looks through the exit into eternity.
	 * @param direction specifies the direction of the sensor
	 * @return number of steps towards obstacle if obstacle is visible 
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		// Decide which sensor to use
		DistanceSensor sensor;
		switch(direction) {
		case LEFT:
			sensor = leftSensor;
			break;
		case RIGHT:
			sensor = rightSensor;
			break;
		case FORWARD:
			sensor = frontSensor;
			break;
		case BACKWARD:
			sensor = backSensor;
			break;
		default:
			sensor = null;
		}
		
		// Check is there exists this sensor
		if (sensor == null) {
			throw new UnsupportedOperationException("No sensor in this direction!");
		}
		
		// Let the sensor detect the distance.
		sensor.setMaze(statePlaying.getMazeConfiguration());
		float[] powersupply = new float[1];
		powersupply[0] = batteryLevel;

		int distance;
		try {
			distance = sensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), powersupply);
		} catch (Exception e) {
			throw new UnsupportedOperationException();
		}

		return distance;
	}

	/**
	 * Tells if a sensor can identify the exit in the given direction relative to 
	 * the robot's current forward direction from the current position.
	 * @param direction is the direction of the sensor
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational
	 */
	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		// If there is no sensor in this direction, throw exception
		DistanceSensor sensor;
		switch(direction) {
		case LEFT:
			sensor = leftSensor;
			break;
		case RIGHT:
			sensor = rightSensor;
			break;
		case FORWARD:
			sensor = frontSensor;
			break;
		case BACKWARD:
			sensor = backSensor;
			break;
		default:
			sensor = null;
		}
		
		// Check is there exists this sensor
		if (sensor == null) {
			throw new UnsupportedOperationException("No sensor in this direction!");
		}

		// Check if the distance to obstacle in this direction is MAX_VALUE.
		return (distanceToObstacle(direction) == Integer.MAX_VALUE);
	}

	/**
	 * Not useful in this class.
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not useful in this class.
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return the left sensor for testing.
	 * @return leftSensor the left sensor of the robot.
	 */
	public DistanceSensor getLeftSensor() {
		return leftSensor;
	}

	/**
	 * Return the right sensor for testing.
	 * @return rightSensor the right sensor of the robot.
	 */
	public DistanceSensor getRightSensor() {
		return rightSensor;
	}

	/**
	 * Return the front sensor for testing.
	 * @return frontSensor the front sensor of the robot.
	 */
	public DistanceSensor getFrontSensor() {
		return frontSensor;
	}

	/**
	 * Return the back sensor for testing.
	 * @return backSensor the back sensor of the robot.
	 */
	public DistanceSensor getBackSensor() {
		return backSensor;
	}

}
