/**
 * 
 */
package edu.wm.cs.cs301.ShuhongWang.gui;

import edu.wm.cs.cs301.ShuhongWang.gui.Constants.UserInput;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
import edu.wm.cs.cs301.ShuhongWang.generation.Order.Builder;

/**
 * The state interface is used for the controller 
 * such that all states of the UI conform and can 
 * be treated the same way.
 * 
 * @author Peter Kemper
 *
 */
public interface State {
	/**
	 * Starts the main operation of to be performed in this state.
	 * Semantics depends on the particular state implementing it.
	 * This is polymorphism in action.
	 * 
	 * @param controller is a reference to the current controller
	 * @param panel is the panel to draw graphics on
	 */
    void start(MazePanel panel);
    /**
     * Sets the filename that is used to load a maze from
     * @param filename for a maze
     */
    void setFileName(String filename);
    /**
     * Sets the skill level to determine the size of maze
     * that should be generated.
     * @param skillLevel for the size of the maze
     */
    void setSkillLevel(int skillLevel);
	/**
	 * Sets the seed to be used for the random number generator
	 * during the maze generation.
	 */
	void setSeed(int seed);
    /**
     * Sets the maze to play, the maze configuration is a
     * container that holds all necessary information about
     * a particular maze that can be played.
     * It is the outcome of the maze generation process
     * performed in the maze factory.
     * @param config gives a maze to play
     */
    void setMazeConfiguration(Maze config);
    /**
     * Sets the length of the path that one has passed
     * through the maze, typically of interest for 
     * the final screen.
     * @param pathLength walked through the maze
     */
    void setPathLength(int pathLength);
    /**
     * Provides input the user has given with the keyboard.
     * The SimpleKeyListener maps input into UserInput
     * and filters out invalid input.
     * @param key input from Enum set of legal commands
     * @param value carries a value, typically the skill level, optional
     * @return
     */
    boolean keyDown(UserInput key, int value);
    /**
     * Sets the builder algorithm that should be used
     * to generate a maze
     * @param dfs specifies the algorithm, e.g. depth-first-search
     */
    void setBuilder(Builder dfs);
    /**
     * Specifies if the maze that should be generated
     * must be perfect, i.e., it does not have cycles, 
     * which is achieved by not(!) allowing for rooms.
     * A maze that is not perfect can contain cycles.
     * Cycles are often a byproduct of the presence
     * of rooms.
     * @param isPerfect is true if maze must be perfect, false otherwise
     */
    void setPerfect(boolean isPerfect);

}
