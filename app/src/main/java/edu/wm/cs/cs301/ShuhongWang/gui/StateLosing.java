package edu.wm.cs.cs301.ShuhongWang.gui;

import edu.wm.cs.cs301.ShuhongWang.gui.Constants.UserInput;


/**
 * Class handles the user interaction
 * while the game is in the final stage
 * where the user sees the final screen
 * and can restart the game.
 * This class is part of a state pattern for the
 * Controller class. It is a ConcreteState.
 * 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt with a key listener (SimpleKeyListener)
 * and then handed over to a Controller object by way of the keyDown method.
 * 
 * Responsibilities
 * Show the final screen
 * Accept input of any kind to return to title screen  
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class StateLosing extends DefaultState {
    SimpleScreens view;
    MazePanel panel;
    Controller control;
    
    
    boolean started;
    int pathLength;
    float energyConsumed;
    
    public StateLosing() {
        pathLength = 0;
        started = false;
        energyConsumed = 0;
    }
    
    /**
     * Start the game by showing the final screen with a winning message.
     * @param controller needed to be able to switch states, must be not null
     * @param panel is the UI entity to produce the screen on 
     */
    public void start(Controller controller, MazePanel panel) {
        started = true;
        // keep the reference to the controller to be able to call method to switch the state
        control = controller;
        // keep the reference to the panel for drawing
        this.panel = panel;
        // init mazeview, controller not needed for final screen
        view = new SimpleScreens();

        if (panel == null) {
    		System.out.println("Statelosing.start: warning: no panel, dry-run game without graphics!");
    		return;
    	}
        // otherwise show finish screen with winning message
        // draw content on panel
        view.redrawFail(panel, pathLength, energyConsumed);
        // update screen with panel content
//        panel.update();
        panel.commit();

    }
    
    /**
     * Method incorporates all reactions to keyboard input in original code, 
     * The simple key listener calls this method to communicate input.
     * Method requires {@link #start(Controller, MazePanel) start} to be
     * called before.
     * @param key provides the feature the user selected
     * @param value is not used, exists only for consistency across State classes
     */
    public boolean keyDown(UserInput key, int value) {
        if (!started)
            return false;
        // for any keyboard input switch to title screen
        control.switchToTitle();    
        return true;
    }

    @Override
    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }
    
    public void setEnergyConsumed(float energy) {
    	this.energyConsumed = energy;
    }
}



