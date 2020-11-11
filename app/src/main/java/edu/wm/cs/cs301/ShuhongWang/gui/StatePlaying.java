package edu.wm.cs.cs301.ShuhongWang.gui;

import edu.wm.cs.cs301.ShuhongWang.generation.CardinalDirection;
import edu.wm.cs.cs301.ShuhongWang.generation.Floorplan;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;


/**
 * Class handles the user interaction
 * while the game is in the third stage
 * where the user plays the game.
 * This class is part of a state pattern for the
 * Controller class.
 * 
 * It implements a state-dependent behavior that controls
 * the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt
 * with a key listener (SimpleKeyListener)
 * and then handed over to a Controller object
 * by way of the keyDown method.
 * 
 * Responsibilities:
 * Show the first person view and the map view,
 * Accept input for manual operation (left, right, up, down etc),  
 * Update the graphics, recognize termination.
 *
 * This code is refactored code from Maze.java by Paul Falstad, 
 * www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class StatePlaying extends DefaultState {
	FirstPersonView firstPersonView;
	Map mapView;
    MazePanel panel;
//    Controller control;
    PlayManuallyActivity playManuallyActivity;
    PlayAnimationActivity playAnimationActivity;

    Maze mazeConfig ; 
    
    private boolean showMaze;           // toggle switch to show overall maze on screen
    private boolean showSolution;       // toggle switch to show solution in overall maze on screen
    private boolean mapMode; // true: display map of maze, false: do not display map of maze
    // mapMode is toggled by user keyboard input, causes a call to drawMap during play mode

    // current position and direction with regard to MazeConfiguration
    int px, py ; // current position on maze grid (x,y)
    int dx, dy;  // current direction
    
    int angle; // current viewing angle, east == 0 degrees
    int walkStep; // counter for intermediate steps within a single step forward or backward
    Floorplan seenCells; // a matrix with cells to memorize which cells are visible from the current point of view
    // the FirstPersonView obtains this information and the Map uses it for highlighting currently visible walls on the map
    private CompassRose cr; // compass rose to show current direction
    
    // debug stuff
    //private boolean deepdebug = false;
    //private boolean allVisible = false;
    //private boolean newGame = false;

    boolean started;

    private Robot robot;
    private RobotDriver driver;

    private boolean manual;

    public StatePlaying() {
        started = false;
    }
    @Override
    public void setMazeConfiguration(Maze config) {
        mazeConfig = config;
    }

    /**
     * set the robot and driver for play animation activity.
     * @param robot the robot
     * @param driver the robot driver
     */
    public void setRobotAndDriver(Robot robot, RobotDriver driver){
        this.robot = robot;
        this.driver = driver;
    }

    public void setPlayManuallyActivity(PlayManuallyActivity playManuallyActivity) {
        this.playManuallyActivity = playManuallyActivity;
        manual = true;
    }

    public void setPlayAnimationActivity(PlayAnimationActivity playAnimationActivity) {
        this.playAnimationActivity = playAnimationActivity;
        manual = false;
    }
    /**
     * Start the actual game play by showing the playing screen.
     * If the panel is null, all drawing operations are skipped.
     * This mode of operation is useful for testing purposes, 
     * i.e., a dryrun of the game without the graphics part.
//     * @param controller provides access to the controller this state resides in
     * @param panel is part of the UI and visible on the screen, needed for drawing
     */
    public void start(MazePanel panel) {
        started = true;
        // keep the reference to the controller to be able to call method to switch the state
//        control = controller;
        // keep the reference to the panel for drawing
        this.panel = panel;
        //
        // adjust internal state of maze model
        // visibility settings
        showMaze = false ;
        showSolution = false ;
        mapMode = false;
        // init data structure for visible walls
        seenCells = new Floorplan(mazeConfig.getWidth()+1,mazeConfig.getHeight()+1) ;
        // set the current position and direction consistently with the viewing direction
        setPositionDirectionViewingDirection();
        walkStep = 0; // counts incremental steps during move/rotate operation
        
        // configure compass rose
        cr = new CompassRose(panel);
        cr.setPositionAndSize(Constants.VIEW_WIDTH/2,
        		(int)(0.1*Constants.VIEW_HEIGHT),35*3);
        
        // if (control.getRobot() != null && control.getDriver() != null) {
		// 	control.getRobot().setController(control);
		// 	control.getDriver().setRobot(control.getRobot());
		// 	control.getDriver().setMaze(control.getMazeConfiguration());
		// 	try {
		// 		control.getDriver().drive2Exit();
		// 	} catch (Exception e) {
		// 		e.printStackTrace();
		// 	}
        // }

        if (panel != null) {
        	startDrawer();
        }
        else {
        	// else: dry-run without graphics, most likely for testing purposes
        	printWarning();
        }

//        if (control.getDriver() instanceof Wizard || control.getDriver() instanceof WallFollower) {
//        	try {
////        		control.getRobot().setController(controller);
//        		control.getRobot().setBatteryLevel(3500);
////        		if (control.getRobot() instanceof UnreliableRobot) {
////        			((UnreliableRobot) control.getRobot()).startRobot();
////        		}
//        		if (!control.getDriver().drive2Exit()) {
//					control.switchFromPlayingToLosing(control.getDriver().getPathLength(), control.getDriver().getEnergyConsumption());
//        		}
//        		else {
//        			control.switchFromPlayingToWinning(control.getDriver().getPathLength(), control.getDriver().getEnergyConsumption());
//        		}
//			} catch (Exception e) {
//				e.printStackTrace();
//				control.switchFromPlayingToLosing(control.getRobot().getOdometerReading(), control.getDriver().getEnergyConsumption());
//			}
//        }
        
    }
    /**
     * Initializes the drawer for the first person view
     * and the map view and then draws the initial screen
     * for this state.
     */
	protected void startDrawer() {
		firstPersonView = new FirstPersonView(Constants.VIEW_WIDTH,
				Constants.VIEW_HEIGHT, Constants.MAP_UNIT,
				Constants.STEP_SIZE, seenCells, mazeConfig.getRootnode()) ;
		mapView = new Map(seenCells, 15*3, mazeConfig) ;
		// draw the initial screen for this state
		draw();
	}
    /**
     * Internal method to set the current position, the direction
     * and the viewing direction to values consistent with the 
     * given maze.
     */
	private void setPositionDirectionViewingDirection() {
		// obtain starting position
        int[] start = mazeConfig.getStartingPosition() ;
        setCurrentPosition(start[0],start[1]) ;
        // set current view direction and angle
        angle = 0; // angle matches with east direction, 
        // hidden consistency constraint!
        setDirectionToMatchCurrentAngle();
        // initial direction is east, check this for sanity:
        assert(dx == 1);
        assert(dy == 0);
	}
 

    /**
     * Method incorporates all reactions to keyboard input in original code, 
     * The simple key listener calls this method to communicate input.
     * Method requires {@link #start( MazePanel) start} to be
     * called before.
     * @param key provides the feature the user selected
     * @return false if not started yet otherwise true
     */
    public boolean keyDown(Constants.UserInput key) {
        if (!started)
            return false;

        // react to input for directions and interrupt signal (ESCAPE key)  
        // react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
        // react to input to display solution (on/off toggle switch)
        // react to input to increase/reduce map scale
        switch (key) {
        case Start: // misplaced, do nothing
            break;
        case Up: // move forward
            walk(1);
            // check termination, did we leave the maze?
            if (isOutside(px,py)) {
//                control.switchFromPlayingToWinning(0);
                playManuallyActivity.startWinningActivity();
            }
            break;
        case Left: // turn left
            rotate(1);
            break;
        case Right: // turn right
            rotate(-1);
            break;
        case Down: // move backward
            walk(-1);
            // check termination, did we leave the maze?
            if (isOutside(px,py)) {
//                control.switchFromPlayingToWinning(0);
                playManuallyActivity.startWinningActivity();
            }
            break;
        case ReturnToTitle: // escape to title screen
//            control.switchToTitle();
            break;
        case Jump: // make a step forward even through a wall
            // go to position if within maze
            if (mazeConfig.isValidPosition(px + dx, py + dy)) {
                setCurrentPosition(px + dx, py + dy) ;
                draw() ;
            }
            break;
        case ToggleLocalMap: // show local information: current position and visible walls
            // precondition for showMaze and showSolution to be effective
            // acts as a toggle switch
            mapMode = !mapMode;         
            draw() ; 
            break;
        case ToggleFullMap: // show the whole maze
            // acts as a toggle switch
            showMaze = !showMaze;       
            draw() ; 
            break;
        case ToggleSolution: // show the solution as a yellow line towards the exit
            // acts as a toggle switch
            showSolution = !showSolution;       
            draw() ;
            break;
        case ZoomIn: // zoom into map
        	mapView.incrementMapScale(); 
            draw() ;
            break ;
        case ZoomOut: // zoom out of map
        	mapView.decrementMapScale(); 
            draw() ; 
            break ;
        } // end of internal switch statement for playing state
        return true;
    }
    /**
     * Draws the current content on panel to show it on screen.
     */
    protected void draw() {
    	if (panel == null) {
    		printWarning();
    		return;
    	}
    	panel.clearCanvas();
    	// draw the first person view and the map view if wanted
    	firstPersonView.draw(panel, px, py, walkStep, angle, 
    			getPercentageForDistanceToExit()) ;

        if (isInMapMode()) {
            System.out.println("SP: map on");
			mapView.draw(panel, px, py, angle, walkStep,
					isInShowMazeMode(),isInShowSolutionMode()) ;
		}
		// update the screen with the buffer graphics
        panel.update() ;
//        panel.commit();
    }
    /**
     * Calculates a distance to exit as a percentage. 
     * 1.0 is for the starting position as this is the maximal
     * distance possible. 
     * @return a value between 0.0 and 1.0, the smaller the closer
     */
    float getPercentageForDistanceToExit() {
    	return mazeConfig.getDistanceToExit(px, py) / 
    			((float) mazeConfig.getMazedists().getMaxDistance());
    }
    /**
     * Prints the warning about a missing panel only once
     */
    boolean printedWarning = false;
    protected void printWarning() {
    	if (printedWarning)
    		return;
    	System.out.println("StatePlaying.start: warning: no panel, dry-run game without graphics!");
    	printedWarning = true;
    }
    ////////////////////////////// set methods ///////////////////////////////////////////////////////////////
    ////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
    protected void setCurrentPosition(int x, int y) {
        px = x ;
        py = y ;
    }
    private void setCurrentDirection(int x, int y) {
        dx = x ;
        dy = y ;
    }
    /**
     * Sets fields dx and dy to be consistent with
     * current setting of field angle.
     */
    private void setDirectionToMatchCurrentAngle() {
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
	}

    ////////////////////////////// get methods ///////////////////////////////////////////////////////////////
    protected int[] getCurrentPosition() {
        int[] result = new int[2];
        result[0] = px;
        result[1] = py;
        return result;
    }
    protected CardinalDirection getCurrentDirection() {
        return CardinalDirection.getDirection(dx, dy);
    }
    boolean isInMapMode() { 
        return mapMode ; 
    } 
    boolean isInShowMazeMode() { 
        return showMaze ; 
    } 
    boolean isInShowSolutionMode() { 
        return showSolution ; 
    } 
    public Maze getMazeConfiguration() {
        return mazeConfig ;
    }
    //////////////////////// Methods for move and rotate operations ///////////////
    final double radify(int x) {
        return x*Math.PI/180;
    }
    /**
     * Helper method for walk()
     * @param dir
     * @return true if there is no wall in this direction
     */
    protected boolean checkMove(int dir) {
        CardinalDirection cd = null;
        switch (dir) {
        case 1: // forward
            cd = getCurrentDirection();
            break;
        case -1: // backward
            cd = getCurrentDirection().oppositeDirection();
            break;
        default:
            throw new RuntimeException("Unexpected direction value: " + dir);
        }
        return !mazeConfig.hasWall(px, py, cd);
    }
    /**
     * Draws and waits. Used to obtain a smooth appearance for rotate and move operations
     */
    private void slowedDownRedraw() {
        draw() ;
        try {
            Thread.sleep(25);
        } catch (Exception e) { 
        	// may happen if thread is interrupted
        	// no reason to do anything about it, ignore exception
        }
    }
 	
    /**
     * Performs a rotation with 4 intermediate views, 
     * updates the screen and the internal direction
     * @param dir for current direction, values are either 1 or -1
     */
    synchronized private void rotate(int dir) {
        final int originalAngle = angle;
        final int steps = 4;

        for (int i = 0; i != steps; i++) {
            // add 1/4 of 90 degrees per step 
            // if dir is -1 then subtract instead of addition
            angle = originalAngle + dir*(90*(i+1))/steps; 
            angle = (angle+1800) % 360;
            // draw method is called and uses angle field for direction
            // information.
			slowedDownRedraw();
        }
        // update maze direction only after intermediate steps are done
        // because choice of direction values are more limited.
        setDirectionToMatchCurrentAngle();
        //logPosition(); // debugging
        drawHintIfNecessary(); 
    }
	
    /**
     * Moves in the given direction with 4 intermediate steps,
     * updates the screen and the internal position
     * @param dir, only possible values are 1 (forward) and -1 (backward)
     */
    synchronized private void walk(int dir) {
    	// check if there is a wall in the way
        if (!checkMove(dir))
            return;
        // walkStep is a parameter of FirstPersonDrawer.draw()
        // it is used there for scaling steps
        // so walkStep is implicitly used in slowedDownRedraw
        // which triggers the draw operation in 
        // FirstPersonDrawer and MapDrawer
        for (int step = 0; step != 4; step++) {
            walkStep += dir;
            slowedDownRedraw();
        }
        setCurrentPosition(px + dir*dx, py + dir*dy) ;
        walkStep = 0; // reset counter for next time
        //logPosition(); // debugging
        drawHintIfNecessary();  
    }

    /**
     * Checks if the given position is outside the maze
     * @param x coordinate of position
     * @param y coordinate of position
     * @return true if position is outside, false otherwise
     */
    private boolean isOutside(int x, int y) {
        return !mazeConfig.isValidPosition(x, y) ;
    }
    /**
     * Draw a visual cue to help the user unless the 
     * map is on display anyway. 
     * This is the map if current position faces a dead end
     * otherwise it is a compass rose.
     */
    private void drawHintIfNecessary() {
    	if (isInMapMode())
    		return; // no need for help
    	// in testing environments, there is sometimes no panel to draw on
    	// check this and quietly move on if drawing is impossible
    	if (panel == null) {
    		printWarning();
    		return;
    	}
    	// if current position faces a dead end, show map with solution
    	// for guidance
    	if (isFacingDeadEnd()) {
        	//System.out.println("Facing deadend, help by showing solution");
        	mapView.draw(panel, px, py, angle, walkStep, true, true) ;
        }
    	else {
    		// draw compass rose
    		cr.setCurrentDirection(getCurrentDirection());
//    		cr.paintComponent(panel.getBufferGraphics());
    		cr.paintComponent(panel);
    	}
//    	panel.update();
    	panel.commit();
    }
    /**
     * Checks if the current position and direction
     * faces a dead end
     * @return true if at the current position there is
     * a wall to the left, right and front, false otherwise
     */
    private boolean isFacingDeadEnd() {
    	return (!isOutside(px,py) &&
    			mazeConfig.hasWall(px, py, getCurrentDirection()) &&
    			mazeConfig.hasWall(px, py, getCurrentDirection().oppositeDirection().rotateClockwise()) &&
    			mazeConfig.hasWall(px, py, getCurrentDirection().rotateClockwise()));
    }

    /////////////////////// Methods for debugging ////////////////////////////////
    /*
    private void dbg(String str) {
        //System.out.println(str);
    }
    
    private void logPosition() {
        if (!deepdebug)
            return;
        dbg("x="+viewx/Constants.MAP_UNIT+" ("+
                viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
                angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
    }
    */
}



