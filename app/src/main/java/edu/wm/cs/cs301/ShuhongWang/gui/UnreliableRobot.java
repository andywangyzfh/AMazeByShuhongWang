/**
 * 
 */
package edu.wm.cs.cs301.ShuhongWang.gui;

import java.util.ArrayList;

import edu.wm.cs.cs301.ShuhongWang.generation.CardinalDirection;
import edu.wm.cs.cs301.ShuhongWang.gui.Constants.UserInput;

/**
 * This class provides a robot with unreliable sensors.
 * 
 * Responsibilities: move, rotate, call sensors to detect the distance.
 * 
 * Collaborators: ReliableSensor, UnreliableSensor. 
 * 
 * @author Shuhong Wang
 *
 */
public class UnreliableRobot implements Robot {

	// define private variables here
	private float batteryLevel;
	private int odometer;
	private Controller controller;
	private float energyForFullRotation;
	private float energyForStepForward;
	private float energyForJump;
	private boolean stopped;

	private DistanceSensor leftSensor;
	private DistanceSensor rightSensor;
	private DistanceSensor frontSensor;
	private DistanceSensor backSensor;
	
	private int downTime;
	private int upTime;

	private ArrayList<UnreliableSensor> unreliableSensors;
	
	/**
	 * Constructor of the class. Initialize the robot.
	 */
	public UnreliableRobot() {
		// Initialize the private variables, e.g., battery level and odometer.
		batteryLevel = 3500;
		odometer = 0;
		energyForFullRotation = 12;
		energyForStepForward = 6;
		energyForJump = 40;
		stopped = false;
		downTime = 2;
		upTime = 4;
	}
	
	/**
	 * Constructor that takes the input of whether the sensor is working.
	 * @param list
	 */
	public UnreliableRobot(int[] list) {
		// Initialize the configurations.
		this();
		
		initializeSensors(list);
	}

	private void initializeSensors(int[] list) {
		// create a list of unreliable sensors
		unreliableSensors = new ArrayList<UnreliableSensor>();		
        		
		// Set each sensor according to the input, add unreliable sensors to a list.
		if (list[0] == 0) {
			frontSensor = new UnreliableSensor();
			frontSensor.setSensorDirection(Direction.FORWARD);
			unreliableSensors.add((UnreliableSensor) frontSensor);
		}
		else {
			frontSensor = new ReliableSensor();
			frontSensor.setSensorDirection(Direction.FORWARD);
		}
		if (list[1] == 0) {
			leftSensor = new UnreliableSensor();
			leftSensor.setSensorDirection(Direction.LEFT);
			unreliableSensors.add((UnreliableSensor) leftSensor);
		}
		else {
			leftSensor = new ReliableSensor();
			leftSensor.setSensorDirection(Direction.LEFT);
		}
		if (list[2] == 0) {
			rightSensor = new UnreliableSensor();
			rightSensor.setSensorDirection(Direction.RIGHT);
			unreliableSensors.add((UnreliableSensor) rightSensor);
		}
		else {
			rightSensor = new ReliableSensor();
			rightSensor.setSensorDirection(Direction.RIGHT);
		}
		if (list[3] == 0) {
			backSensor = new UnreliableSensor();
			backSensor.setSensorDirection(Direction.BACKWARD);
			unreliableSensors.add((UnreliableSensor) backSensor);
		}
		else {
			backSensor = new ReliableSensor();
			backSensor.setSensorDirection(Direction.BACKWARD);
		}
	}
	
	public void startRobot() {
		// Start each of the sensor with 1.3s delay
		if (!unreliableSensors.isEmpty()) {
			for (int i = 0; i < (unreliableSensors).size(); i++) {
				unreliableSensors.get(i).startFailureAndRepairProcess(upTime, downTime);
				try {
					Thread.sleep(1300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}	
	}

	/**
	 * Initialize the controller that provides the robot needed information
	 * 
	 * @param controller is the communication partner for robot
	 * @throws IllegalArgumentException if controller is null, 
	 */
	@Override
	public void setController(Controller controller) {
		// Check if the controller is null. If null, throw IllegalArgumentException.
		if (controller == null) {
			throw new IllegalArgumentException("controller is null!");
		}
		
		// Initialize the private value.
		this.controller = controller;
		leftSensor.setMaze(controller.getMazeConfiguration());
		rightSensor.setMaze(controller.getMazeConfiguration());
		frontSensor.setMaze(controller.getMazeConfiguration());
		backSensor.setMaze(controller.getMazeConfiguration());
	}
	
	/**
	 * Provide the information of the controller.
	 * 
	 * @return controller is the communication partner for robot.
	 */
	public Controller getController() {
		return controller;
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
		int[] currentPosition = controller.getCurrentPosition();
		int x = currentPosition[0];
		int y = currentPosition[1];
		int height = controller.getMazeConfiguration().getHeight();
		int width = controller.getMazeConfiguration().getWidth();
		
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
		return controller.getCurrentDirection();
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
				controller.keyDown(UserInput.Left, 1);
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
				controller.keyDown(UserInput.Right, 1);
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
				controller.keyDown(UserInput.Right, 1);
				controller.keyDown(UserInput.Right, 1);
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
			if (controller.getMazeConfiguration().hasWall(currentPosition[0], currentPosition[1], currentDirection)) {
				System.out.println("ROBOT CRASHED.");
				batteryLevel = 0;
				stopped = true;
				break;
			}
			// If the robot can move
			else {
				// move the robot forward
				controller.keyDown(UserInput.Up, 1);
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
			int height = controller.getMazeConfiguration().getHeight();
			int width = controller.getMazeConfiguration().getWidth();
			
			if (x+dx < 0 || x+dx >= width || y+dy < 0 || y+dy >= height) {
				// if landed outside, stop the robot, and don't move the robot.
				batteryLevel = 0;
				stopped = true;
				return;
			}
			// If not, move the robot one cell to the front.
			else {
				controller.keyDown(UserInput.Jump, 1);
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
		
		return controller.getMazeConfiguration().getFloorplan().isExitPosition(currentPosition[0], currentPosition[1]);
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
		return controller.getMazeConfiguration().getFloorplan().isInRoom(currentPosition[0], currentPosition[1]);
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
			throw new UnsupportedOperationException("UnreliableRobot.distanceToObstacle: No sensor in this direction!");
		}
		
//		if (sensor instanceof UnreliableSensor) {
//			if (!((UnreliableSensor) sensor).isWorking()) {
//				throw new UnsupportedOperationException("UnreliableRobot.distanceToObstacle: the sensor is not working.");
//			}
//		}
		
		// Let the sensor detect the distance.
		sensor.setMaze(controller.getMazeConfiguration());
		float[] powersupply = new float[1];
		powersupply[0] = batteryLevel;

		int distance;
		try {
			distance = sensor.distanceToObstacle(getCurrentPosition(), getCurrentDirection(), powersupply);
		} catch (Exception e) {
			e.printStackTrace();
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
			throw new UnsupportedOperationException("UnreliableRobot.canSeeThroughTheExitIntoEternity: No sensor in this direction!");
		}

		// Check if the distance to obstacle in this direction is MAX_VALUE.
		return (distanceToObstacle(direction) == Integer.MAX_VALUE);
	}

	/**
	 * Method starts a concurrent, independent failure and repair
	 * process that makes the sensor fail and repair itself.
	 * This creates alternating time periods of up time and down time.
	 * Up time: The duration of a time period when the sensor is in 
	 * operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeBetweenFailures.
	 * Down time: The duration of a time period when the sensor is in repair
	 * and not operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeToRepair.
	 * 
	 * @param direction the direction the sensor is mounted on the robot
	 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair) {
		// Call the startFailureAndRepairProcess method in the corresponding sensor.
		switch (direction) {
		case LEFT:
			leftSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case RIGHT:
			rightSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case FORWARD:
			frontSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		case BACKWARD:
			backSensor.startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
			break;
		}
	}

	/**
	 * This method stops a failure and repair process and
	 * leaves the sensor in an operational state.
	 * 
	 * It is complementary to starting a 
	 * failure and repair process. 
	 * 
	 * Intended use: If called after starting a process, this method
	 * will stop the process as soon as the sensor is operational.
	 * 
	 * If called with no running failure and repair process, 
	 * the method will return an UnsupportedOperationException.
	 * 
	 * @param direction the direction the sensor is mounted on the robot
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) {
		// Call the stopFailureAndRepairProcess method in the corresponding sensor.
		switch (direction) {
		case LEFT:
			leftSensor.stopFailureAndRepairProcess();
			break;
		case RIGHT:
			rightSensor.stopFailureAndRepairProcess();
			break;
		case FORWARD:
			frontSensor.stopFailureAndRepairProcess();
			break;
		case BACKWARD:
			backSensor.stopFailureAndRepairProcess();
			break;
		}
	}
	
	/**
	 * Check whether the sensor in the given direction is working or not.
	 * 
	 * @param direction The direction that the target sensor is located.
	 * @return	true if the sensor is working, false if not.
	 */
	public boolean isSensorWorking(Direction direction) {
		switch (direction) {
		case LEFT:
			if (leftSensor instanceof ReliableSensor) {
				return true;
			}
			else {
				return((UnreliableSensor) leftSensor).isWorking();
			}
		case RIGHT:
			if (rightSensor instanceof ReliableSensor) {
				return true;
			}
			else {
				return((UnreliableSensor) rightSensor).isWorking();
			}
		case FORWARD:
			if (frontSensor instanceof ReliableSensor) {
				return true;
			}
			else {
				return((UnreliableSensor) frontSensor).isWorking();
			}
		case BACKWARD:
			if (backSensor instanceof ReliableSensor) {
				return true;
			}
			else {
				return((UnreliableSensor) backSensor).isWorking();
			}
		default:
			return true;
		}
	}

	///////////////////////////// METHODS FOR TESTING ///////////////////////////////
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
