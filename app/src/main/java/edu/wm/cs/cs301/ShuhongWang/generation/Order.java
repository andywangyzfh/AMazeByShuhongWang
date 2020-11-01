package edu.wm.cs.cs301.ShuhongWang.generation;


/**
 * An order describes functionality needed to order a maze from
 * the maze factory. It allows for asynchronous production 
 * with a mechanism to deliver a MazeConfiguration.
 * 
 * @author Peter Kemper
 *
 */
public interface Order {
	/**
	 * Gives the required skill level, range of values 0,1,2,...,15
	 */
	int getSkillLevel() ;
	/** 
	 * Gives the requested builder algorithm, possible values 
	 * are listed in the Builder enum type.
	 */
	Builder getBuilder() ;
	/**
	 * Lists all maze generation algorithms that are supported
	 * by the maze factory (Eller needs to be implemented for P2)
	 *
	 */
	enum Builder { DFS, Prim, Kruskal, Eller } ;
	/**
	 * Describes if the ordered maze should be perfect, i.e. there are 
	 * no loops and no isolated areas, which also implies that 
	 * there are no rooms as rooms can imply loops
	 */
	boolean isPerfect() ;
	/**
	 * Gives the seed that is used for the random number generator
	 * used during the maze generation.
	 */
	int getSeed();
	/**
	 * Delivers the produced maze. 
	 * This method is called by the factory to provide the 
	 * resulting maze as a MazeConfiguration.
	 * @param the maze
	 */
	void deliver(Maze mazeConfig) ;
	/**
	 * Provides an update on the progress being made on 
	 * the maze production. This method is called occasionally
	 * during production, there is no guarantee on particular values.
	 * Percentage will be delivered in monotonously increasing order,
	 * the last call is with a value of 100 after delivery of product.
	 * @param current percentage of job completion
	 */
	void updateProgress(int percentage) ;
}
