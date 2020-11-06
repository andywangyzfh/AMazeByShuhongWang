package edu.wm.cs.cs301.ShuhongWang.gui;

import java.util.Random;

import edu.wm.cs.cs301.ShuhongWang.generation.CardinalDirection;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
import edu.wm.cs.cs301.ShuhongWang.generation.Order;
import edu.wm.cs.cs301.ShuhongWang.generation.Order.Builder;
import edu.wm.cs.cs301.ShuhongWang.gui.Constants.UserInput;

/**
 * Class handles the user interaction. 
 * It implements an automaton with states for the different stages of the game.
 * It has state-dependent behavior that controls the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt with a key listener (SimpleKeyListener)
 * and then handed over to a Controller object by way of the keyDown method.
 *
 * The class is part of a state pattern. It has a state object to implement
 * state-dependent behavior.
 * The automaton currently has 4 states.
 * StateTitle: the starting state where the user can pick the skill-level
 * StateGenerating: the state in which the factory computes the maze to play
 * and the screen shows a progress bar.
 * StatePlaying: the state in which the user plays the game and
 * the screen shows the first person view and the map view.
 * StateWinning: the finish screen that shows the winning message.
 * The class provides a specific method for each possible state transition,
 * for example switchFromTitleToGenerating contains code to start the maze
 * generation.
 *
 * This code is refactored code from Maze.java by Paul Falstad, 
 * www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 * 
 * @author Peter Kemper
 */
public class Controller {
	/**
	 * The game has a reservoir of 4 states: 
	 * 1: show the title screen, wait for user input for skill level
	 * 2: show the generating screen with the progress bar during 
	 * maze generation
	 * 3: show the playing screen, have the user or robot driver
	 * play the game
	 * 4: show the finish screen with the winning/loosing message
	 * The array entries are set in the constructor. 
	 * There is no mutator method.
	 */
    State[] states;
    /**
     * The current state of the controller and the game.
     * All state objects share the same interface and can be
     * operated in the same way, although the behavior is 
     * vastly different.
     * currentState is never null and only updated by 
     * switchFrom .. To .. methods.
     */
    State currentState;
    /**
     * The panel is used to draw on the screen for the UI.
     * It can be set to null for dry-running the controller
     * for testing purposes but otherwise panel is never null.
     */
    MazePanel panel;
    /**
     * The filename is optional, may be null, and tells
     * if a maze is loaded from this file and not generated.
     */
    String fileName;
    /**
     * The builder algorithm to use for generating a maze.
     */
    Order.Builder builder;
    /**
     * Specifies if the maze is perfect, i.e., it has
     * no loops, which is guaranteed by the absence of 
     * rooms and the way the generation algorithms work.
     */
    boolean perfect;
    /**
     * The seed to be used for the random number generator
     * during maze generation.
     */
    int seed;
    /**
     * In the deterministic setting (true), 
     * the same random maze will be generated over 
     * and over again for the same settings of skill level, 
     * builder, and perfect.
     * If false, a different maze will be generated each
     * and every time, even if settings of skill level, 
     * builder, and perfect remain the same.
     */
    boolean deterministic;
    
    public Controller() {
    	states = new State[5];
//        states[0] = new StateTitle();
        states[1] = new StateGenerating();
        states[2] = new StatePlaying();
//        states[3] = new StateWinning();
        states[4] = new StateLosing();
        currentState = states[0];
//        panel = new MazePanel();
        fileName = null;
        builder = Order.Builder.DFS; // default
        perfect = false; // default
        seed = 13; // default
        deterministic = true; // default
    }

    public Controller(MazePanel mazePanel) {
        states = new State[5];
//        states[0] = new StateTitle();
        states[1] = new StateGenerating();
        states[2] = new StatePlaying();
//        states[3] = new StateWinning();
        states[4] = new StateLosing();
        currentState = states[0];
        panel = mazePanel;
        fileName = null;
        builder = Order.Builder.DFS; // default
        perfect = false; // default
        seed = 13; // default
        deterministic = true; // default
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setBuilder(Builder builder) {
        this.builder = builder; 
    }
    public void setPerfect(boolean isPerfect) {
        this.perfect = isPerfect; 
    }
    public void setSeed(int seed) {
        this.seed = seed;  
    }
    public void setDeterministic(boolean deterministic) {
    	this.deterministic = deterministic;
    }
    
    
    public MazePanel getPanel() {
        return panel;
    }
    /**
     * Starts the controller and begins the game 
     * with the title screen.
     */
    public void start() { 
        currentState = states[0]; // initial state is the title state
        currentState.setFileName(fileName); // can be null
        currentState.start(this, panel);
        fileName = null; // reset after use
    }
    /**
     * Switches the controller to the generating screen.
     * Assumes that builder and perfect fields are already set
     * with set methods if default settings are not ok.
     * A maze is generated.
     * @param skillLevel, 0 <= skillLevel, size of maze to be generated
     */
    public void switchFromTitleToGenerating(int skillLevel) {
        currentState = states[1];
        currentState.setSkillLevel(skillLevel);
        currentState.setBuilder(builder); 
        currentState.setPerfect(perfect);
        if (!deterministic) {
        	// System.out.println("Assignment: implement code such that a repeated generation creates different mazes! Program stops!");
			// System.exit(0) ;
        	// TODO: implement code that makes sure we generate different random mazes
        	// HINT: check http://download.oracle.com/javase/6/docs/api/java/util/Random.html

        	Random rand = new Random();
        	seed = rand.nextInt();
        }
        currentState.setSeed(seed);
        currentState.start(this, panel);
    }
    /**
     * Switches the controller to the generating screen and
     * loads maze from file.
     * @param filename gives file to load maze from
     */
    public void switchFromTitleToGenerating(String filename) {
        currentState = states[1];
        currentState.setFileName(filename);
        currentState.start(this, panel);
    }
    /**
     * Switches the controller to the playing screen.
     * This is where the user or a robot can navigate through
     * the maze and play the game.
     * @param config contains a maze to play
     */
    public void switchFromGeneratingToPlaying(Maze config) {
        currentState = states[2];
        currentState.setMazeConfiguration(config);
        if (robot != null) {
//        	robot.setController(this);
        	if (this.getRobot() instanceof UnreliableRobot) {
    			((UnreliableRobot) this.getRobot()).startRobot();
    		}
        }
        if (driver != null) {
        	driver.setMaze(config);
        	driver.setRobot(robot);
        }
        currentState.start(this, panel);
    }
    /**
     * Switches the controller to the final screen
     * @param pathLength gives the length of the path
     */
    public void switchFromPlayingToWinning(int pathLength) {
        currentState = states[3];
        currentState.setPathLength(pathLength);
        currentState.start(this, panel);
    }
    public void switchFromPlayingToWinning(int pathLength, float energy) {
        currentState = states[3];
        currentState.setPathLength(pathLength);
//    	((StateWinning)currentState).setEnergyConsumed(energy);
        currentState.start(this, panel);
    }
    /**
     * Switches the controller to the initial screen.
     */
    public void switchToTitle() {
        currentState = states[0];
        currentState.start(this, panel);
    }
    
    public void switchFromPlayingToLosing(int PathLength, float energy) {
    	currentState = states[4];
    	currentState.setPathLength(PathLength);
    	((StateLosing)currentState).setEnergyConsumed(energy);
    	currentState.start(this, panel);
    }
    
    /**
     * Method incorporates all reactions to keyboard input in original code. 
     * The simple key listener calls this method to communicate input.
     */
    public boolean keyDown(UserInput key, int value) {
        // delegated to state object
        return currentState.keyDown(key, value);
    }
    /**
     * Turns of graphics to dry-run controller for testing purposes.
     * This is irreversible. 
     */
    public void turnOffGraphics() {
    	panel = null;
    }
    
    //// Extension in preparation for Project 3: robot and robot driver //////
    /**
     * The robot that interacts with the controller starting from P3
     */
    Robot robot;
    /**
     * The driver that interacts with the robot starting from P3
     */
    RobotDriver driver;
    
    /**
     * Sets the robot and robot driver
     * @param robot
     * @param robotdriver
     */
    public void setRobotAndDriver(Robot robot, RobotDriver robotdriver) {
        this.robot = robot;
        driver = robotdriver;
        
    }
    /**
     * @return the robot, may be null
     */
    public Robot getRobot() {
        return robot;
    }
    /**
     * @return the driver, may be null
     */
    public RobotDriver getDriver() {
        return driver;
    }
    /**
     * Provides access to the maze configuration. 
     * This is needed for a robot to be able to recognize walls
     * for the distance to walls calculation, to see if it 
     * is in a room or at the exit. 
     * Note that the current position is stored by the 
     * controller. The maze itself is not changed during
     * the game.
     * This method should only be called in the playing state.
     * @return the MazeConfiguration
     */
    public Maze getMazeConfiguration() {
        return ((StatePlaying)states[2]).getMazeConfiguration();
    }
    /**
     * Provides access to the current position.
     * The controller keeps track of the current position
     * while the maze holds information about walls.
     * This method should only be called in the playing state.
     * @return the current position as [x,y] coordinates, 
     * 0 <= x < width, 0 <= y < height
     */
    public int[] getCurrentPosition() {
        return ((StatePlaying)states[2]).getCurrentPosition();
    }
    /**
     * Provides access to the current direction.
     * The controller keeps track of the current position
     * and direction while the maze holds information about walls.
     * This method should only be called in the playing state.
     * @return the current direction
     */
    public CardinalDirection getCurrentDirection() {
        return ((StatePlaying)states[2]).getCurrentDirection();
    }

}
