package edu.wm.cs.cs301.ShuhongWang.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This class has the responsibility to create a maze of given dimensions (width, height) 
 * together with a solution based on a distance matrix.
 * The MazeBuilder implements Runnable such that it can be run a separate thread.
 * The MazeFactory has a MazeBuilder and handles the thread management.   
 * 
 * The maze is built with Eller's algorithm.
 * The Eller's algorithm initialize the cells of the first row to each exist in their own set.
 * Then, randomly join adjacent cells and merge the cells in both sets into the same set.
 * For each set, randomly create at least one vertical connections downward to the next row, 
 * and assign the rest of the cells to new sets.
 * Repeat this process until reached the last row. After joining every disjoint cells in the last
 * row, we have a maze.
 * Borders are used to keep the outside surrounding of the maze enclosed and 
 * to make sure that rooms retain outside walls and do not end up as open stalls. 
 *   
 * @author Shuhong Wang
 */
public class MazeBuilderEller extends MazeBuilder implements Runnable {
	
	/**
	 * The 2-dimensional array that stores the information about which set a cell belongs to 
	 */
	private int[][] cells; 	

	/**
	 * This is the constructor of the class. 
	 * Call the constructor of MazeBuilder super class to initiate.
	 */
	public MazeBuilderEller() {
		// call the constructor of MazeBuilder Class and print the message.
		super();
		System.out.println("MazeBuilderEller uses Eller's algorithm to generate maze.");
	}
	
	/**
	 * This is the main method generates pathways into the maze by using Eller's algorithm.
	 * The method calls different method in this class to join cells in a row and expand to the next row.
	 */
	@Override
	protected void generatePathways() {
		// Initialize the floorplan where all wallboards are up. 
		// Initialize the cells array according to the maze's size
		cells = new int[width][height];
		
		// Assign each cell in the first row to different sets. The sets are represented in integers starting with 1
		for (int x = 0; x < width; x++)
			cells[x][0] = x+1;
		
		merge(0);
		
		// For each rows except the first row and last row,
		for (int y = 1; y < height; y++) {
			// Get randomly generated vertical connections to the previous row. Assign the rest of the cells to new sets.
			extend(y);
			// randomly merge the cells in the current row
			merge(y);
		}
		
		// When it comes to the last row, first get the randomly generated vertical connections to the previous row
		extend(height-1);
		// Then, join all adjacent cells that do not share the same set
		mergeLastRow();
	}
	
	/**
	 * Randomly join adjacent cells in the given row y. Call breakWall method to break the wall and update the sets.
	 * @param y The row number
	 */
	private void merge(int y) {
		// For each cell except the last one in a row
		for (int x = 0; x < width-1; x++) {
			Wallboard wallEast = new Wallboard(x, y, CardinalDirection.East);
			Wallboard wallSouth = new Wallboard(x, y, CardinalDirection.South);
			
			// if the wall on its right can be tore down and the current cell is in different set to the one on its right 
			if (floorplan.canTearDown(wallEast) && cells[x][y] != cells[x+1][y]) {
				Random rand = new Random();
				boolean bool = rand.nextBoolean();
				// If the cell on its right is in a room, tear down the wall anyway
				// Randomly decide to tear down the wall or not
				// If decide to tear down, update the sets
				if(floorplan.isInRoom(x+1, y) || bool) {
					breakWall(x, y, 1);
				}
			}
			
			// if the south wall is a border, tear down both the left and right wall
			if (x != 0 && !floorplan.canTearDown(wallSouth)) {
				breakWall(x, y, 1);
				breakWall(x, y, -1);
			}
		}
	}
	
	/**
	 * Take the coordinates and direction as input. Break the wall and update the sets. 
	 * @param x The x-coordinate of the cell
	 * @param y The y-coordinate of the cell
	 * @param direction The direction that the wall is located
	 */
	private void breakWall(int x, int y, int direction) {
		// If the two cells are already equivalent, do nothing and return.
		if (cells[x][y] == cells[x+direction][y])
			return;
		
		// compare the sets that the two cells belong to. Choose the smaller one as the new one and the other the old one.
		int oldSet, newSet;
		if (cells[x][y] < cells[x+direction][y]) {
			newSet = cells[x][y];
			oldSet = cells[x+direction][y];
		}
		else {
			oldSet = cells[x][y];
			newSet = cells[x+direction][y];
		}
		
		// iterate through every cell in this row, update all cells with old sets to new sets.
		for (int i = 0; i < width; i++) {
			if (cells[i][y] == oldSet) {
				cells[i][y] = newSet;
			}
		}
		
		// break the walls according to the coordinates and direction
		if (direction == 1) {
			Wallboard wall = new Wallboard(x, y, CardinalDirection.East);
			floorplan.deleteWallboard(wall);
		}
		else if (direction == -1) {
			Wallboard wall = new Wallboard(x, y, CardinalDirection.West);
			floorplan.deleteWallboard(wall);
		}
	}
	
	/**
	 * Vertically extend the previous row. Take the row number of the current row y as input.
	 * @param y The row number
	 */
	private void extend(int y) {
		// store the previous row in a local array
		int[] prevRow = new int[width];
		for (int x = 0; x < width; x++) {
			prevRow[x] = cells[x][y-1];
		}
		
		// store the sets of previous row and number of cells of the set in previous row in a hashmap
		HashMap<Integer, List<Integer>> sets = new HashMap<Integer, List<Integer>>();
		for (int x = 0; x < width; x++) {
			if (sets.get(prevRow[x]) == null) {
				sets.put(prevRow[x], new ArrayList<Integer>());
			}
			// if the south wall can be broken, add the cell to the arraylist
			Wallboard wall = new Wallboard(x, y-1, CardinalDirection.South);
			if (floorplan.canTearDown(wall)) {
				sets.get(prevRow[x]).add(x);
			}
		}
		
		// Decide which walls to break.
		// Initialize the arraylist which store the positions that is going to be connected
		List<Integer> toBreak = new ArrayList<Integer>();
		Random rand = new Random();
		// For each set in the hashmap
		for (Integer key: sets.keySet()) {
			List<Integer> positions = sets.get(key);
			// randomly decide how many cells to connect and which cells to connect.
			Collections.shuffle(positions);
			int num = rand.nextInt(positions.size())+1;
			// store the candidates that are going to connect in an arraylist.
			toBreak.addAll(positions.subList(0, num));
		}
		// if the cell in the current row is in a room, add the position to the arraylist anyway.
		for (int x = 0; x < width; x++) {
			if (floorplan.isInRoom(x, y) && !toBreak.contains(x)) {
				toBreak.add(x);
			}
		}
		
		// Break the walls.
		// For each position in the arraylist
		for (int i = 0; i < toBreak.size(); i++) {
			// delete the walls and add the cell in the current row to the set.
			Wallboard wall = new Wallboard(toBreak.get(i), y-1, CardinalDirection.South);
			floorplan.deleteWallboard(wall);
			cells[toBreak.get(i)][y] = cells[toBreak.get(i)][y-1];
		}
			
		// Find the largest set number
		int maxSet = 0;
		for (int x = 0; x < width; x++) {
			if (prevRow[x] > maxSet) {
				maxSet = prevRow[x];
			}
		}
		// Assign the rest of the cells to new sets.
		for (int x = 0; x < width; x++) {
			if (cells[x][y] == 0) {
				cells[x][y] = ++maxSet;
			}
		}
	}
	
	/**
	 * Merge last row. Merge every two cells that are disjoint.
	 */
	private void mergeLastRow() {
		int y = height-1;
		// for every cell in the last row except the rightmost one
		for (int x = 0; x < width-1; x++) {
			// if the cell is disjoint to the cell to its right, and the walls between them can be tore down
			Wallboard wall = new Wallboard(x,y,CardinalDirection.East);
			if (cells[x][y] != cells[x+1][y] && floorplan.canTearDown(wall)) {
				// tear down the wall and update the set
				floorplan.deleteWallboard(wall);
				cells[x+1][y] = cells[x][y];
			}
		}
	}
}
