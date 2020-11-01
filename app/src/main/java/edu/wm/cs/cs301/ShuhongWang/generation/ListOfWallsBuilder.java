package edu.wm.cs.cs301.ShuhongWang.generation;

import java.util.ArrayList;
import java.util.Iterator;

import edu.wm.cs.cs301.ShuhongWang.gui.Constants;
import edu.wm.cs.cs301.ShuhongWang.gui.MazePanel;

/**
 * This class creates a list of walls for a floorplan.
 * The BSP tree operates on walls (polygons) while the floorplan
 * merely works with wallboards. The code in this class
 * helps bridging this gap for the BSPBuilder.
 * 
 * History: class resulted from refactoring the BSPBuilder class. 
 * The generation of walls is a substantial amount of code
 * that is not part of the core responsibility of the BSPBuilder.
 *  
 * @author peterkemper
 *
 */
public class ListOfWallsBuilder {
	private final int width ; 				// width of maze
	private final int height ; 				// height of maze
	private final Distance dists ; 			// distance matrix
	private final Floorplan floorplan ;		// floorplan with maze layout
	private int colchange;

	private MazePanel mazePanel;
	
	/**
	 * Constructor
	 * @param width
	 * @param height
	 * @param floorplan
	 * @param dists
	 * @param colchange
	 * @param mazePanel 
	 */
	public ListOfWallsBuilder(int width, int height, Floorplan floorplan, Distance dists, int colchange, MazePanel mazePanel) {
		this.floorplan = floorplan;
		this.width = width;
		this.height = height;
		this.dists = dists;
		this.colchange = colchange;
		this.mazePanel = mazePanel;
	}
	/**
	 * Identifies continuous sequences of wallboards on the maze and fills the wall list 
	 * @return vector of walls
	 */
	public ArrayList<Wall> generateWalls() {
		ArrayList<Wall> sl = new ArrayList<Wall>();
		// this is left over from changes made in a refactoring step
		// it is an example to show how one can operate the old and the new
		// version of the code in parallel and test if they deliver same results.
		// by flipping the condition one can run the deprecated old version
		// instead of the new variant that uses the iterator for walls
		// TODO: clean up code, remove unnecessary deprecated methods
		if (false) {
		generateWallsForHorizontalWallboards(sl); 

		generateWallsForVerticalWallboards(sl);
		}
		else {
		    generateWallsForHorizontalWallboardsNew(sl); 

	        generateWallsForVerticalWallboardsNew(sl);
		}
		// starting positions for walls seem to be chosen such that walls represent top or left wallboards
		return sl ;
	}
	/////////////////////////////////////////////////////////////////////////////////////
    // new code with iterator
    // status: complete
	/**
     * Identify continuous sequences of wallboards in a vertical direction
     * @param sl
     */
	   private void generateWallsForVerticalWallboardsNew(ArrayList<Wall> sl) {
	        int x;
	        int y;
	        Iterator<int[]> it;
	        int[] cur;
	        // we search for vertical wallboards, so for each row
	        for (x = 0; x < width; x++) {
	            it = floorplan.iterator(x, 0, CardinalDirection.West);
	            while(it.hasNext()) {
	                cur = it.next();
	                int starty = cur[0];
	                y = cur[1];
	                // create wall with (x,starty) being the actual start position of the wall, 
                    // y-starty being the positive length
                    sl.add(new Wall(x*Constants.MAP_UNIT, starty*Constants.MAP_UNIT,
                            0, (y-starty)*Constants.MAP_UNIT, dists.getDistanceValue(x, starty), colchange, mazePanel));
	            }
	            
	            it = floorplan.iterator(x, 0, CardinalDirection.East);
                while(it.hasNext()) {
                    cur = it.next();
                    int starty = cur[0];
                    y = cur[1];
                    // create wall with (x+1,y) being being one off in both directions from the last cell in this wall, starty-y being the negative length
                    // since we are looking at right wallboards, one off in the right direction (x+1) are then cells that have this wall on its left hand side
                    // for some reason the end position is used as a starting position and therefore the length & direction is inverse 
                    sl.add(new Wall((x+1)*Constants.MAP_UNIT, y*Constants.MAP_UNIT,
                            0, (starty-y)*Constants.MAP_UNIT, dists.getDistanceValue(x, starty), colchange, mazePanel));
                }
	        }
	    }
	/**
     * Identify continuous sequences of wallboards in a horizontal direction
     * @param sl
     */
    private void generateWallsForHorizontalWallboardsNew(ArrayList<Wall> sl) {
        int x;
        int y;
        Iterator<int[]> it;
        int[] cur;
        // we search for horizontal wallboards, so for each column
        for (y = 0; y < height; y++) {
            // first round through rows
            it = floorplan.iterator(0,y, CardinalDirection.North);
            while(it.hasNext()) {
                cur = it.next();
                int startx = cur[0];
                x = cur[1];
                // create wall with (x,y) being the end positions, startx-x being the negative length
                // note the (x,y) is not part of the wall
                sl.add(new Wall(x*Constants.MAP_UNIT, y*Constants.MAP_UNIT,
                        (startx-x)*Constants.MAP_UNIT, 0, dists.getDistanceValue(startx, y), colchange, mazePanel));
            }
            // second round through rows, same for bottom wallboards
            it = floorplan.iterator(0,y, CardinalDirection.South);
            while(it.hasNext()) {
                cur = it.next();
                int startx = cur[0];
                x = cur[1];
                // create wall with (startx,y+1) being one below the start position, x-startx being the positive length
                // so this may represent a wallboard at the bottom of the wall as the top wallboard one below
                sl.add(new Wall(startx*Constants.MAP_UNIT, (y+1)*Constants.MAP_UNIT,
                        (x-startx)*Constants.MAP_UNIT, 0, dists.getDistanceValue(startx, y), colchange, mazePanel));
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////
    // deprecated code without
    // status: complete, still working
	/**
	 * Identify continuous sequences of wallboards in a vertical direction
	 * @param sl
	 * @deprecated
	 */
	private void generateWallsForVerticalWallboards(ArrayList<Wall> sl) {
		int x;
		int y;
		// we search for vertical wallboards, so for each row
		for (x = 0; x != width; x++) {
			y = 0;
			// TODO: change this into an iterator for Cells that gives walls
			while (y < height) {
				// find the beginning of a wall
				if (floorplan.hasNoWall(x,y, CardinalDirection.West)) {
					y++;
					continue;
				} 
				int starty = y;
				// find the end of a wall
				y = findEndOfVerticalWall(x, y, CardinalDirection.West);
				// create wall with (x,starty) being being the actual start position of the wall, y-starty being the positive length
				sl.add(new Wall(x*Constants.MAP_UNIT, starty*Constants.MAP_UNIT,
						0, (y-starty)*Constants.MAP_UNIT, dists.getDistanceValue(x, starty), colchange, mazePanel));
			}
			y = 0;
			while (y < height) {
				// find the beginning of a wall
				if (floorplan.hasNoWall(x,y, CardinalDirection.East)) {
					y++;
					continue;
				} 
				int starty = y;
				// find the end of a wall
				y = findEndOfVerticalWall(x, y, CardinalDirection.East);
				// create wall with (x+1,y) being being one off in both directions from the last cell in this wall, starty-y being the negative length
				// since we are looking at right wallboards, one off in the right direction (x+1) are then cells that have this wall on its left hand side
				// for some reason the end position is used as a starting position and therefore the length & direction is inverse 
				sl.add(new Wall((x+1)*Constants.MAP_UNIT, y*Constants.MAP_UNIT,
						0, (starty-y)*Constants.MAP_UNIT, dists.getDistanceValue(x, starty), colchange, mazePanel));
			}
		}
	}

	/**
	 * @deprecated
	 */
	private int findEndOfVerticalWall(int x, int y, CardinalDirection wallOnThisSide) {
		while (floorplan.hasWall(x, y, wallOnThisSide)) {
			y++;
			if (y == height)
				break;
			if (floorplan.hasWall(x, y, CardinalDirection.North))
				break;
		}
		return y;
	}
	/**
     * @deprecated
     */
	private int findEndOfHorizontalWall(int x, int y, CardinalDirection wallOnThisSide) {
		while (floorplan.hasWall(x,y, wallOnThisSide)) {
			x++;
			if (x == width)
				break;
			if (floorplan.hasWall(x,y, CardinalDirection.West))
				break;
		}
		return x;
	}
	/**
	 * Identify continuous sequences of wallboards in a horizontal direction
	 * @param sl
	 * @deprecated
	 */
	private void generateWallsForHorizontalWallboards(ArrayList<Wall> sl) {
		int x;
		int y;
		// we search for horizontal wallboards, so for each column
		for (y = 0; y != height; y++) {
			// first round through rows
			x = 0;
			while (x < width) {
				// find the beginning of a wall
				if (floorplan.hasNoWall(x,y, CardinalDirection.North)) {
					x++;
					continue;
				} 
				// found one
				int startx = x;
				// find the end of a wall
				// follow wall with wallboard on top till
				// x is the first index of a cell that has no wallboard on top
				// stop at outer bound or when hitting a wallboard (cell has wallboard on left)
				// such that length of the wall is startx-x, which is a negative value btw
				x = findEndOfHorizontalWall(x, y, CardinalDirection.North);
				// create wall with (x,y) being the end positions, startx-x being the negative length
				// note the (x,y) is not part of the wall
				sl.add(new Wall(x*Constants.MAP_UNIT, y*Constants.MAP_UNIT,
						(startx-x)*Constants.MAP_UNIT, 0, dists.getDistanceValue(startx, y), colchange, mazePanel));
			}
			// second round through rows, same for bottom wallboards
			x = 0;
			while (x < width) {
				// find the beginning of a wall
				if (floorplan.hasNoWall(x,y, CardinalDirection.South)) {
					x++;
					continue;
				} 
				int startx = x;
				// find the end of a wall
				x = findEndOfHorizontalWall(x, y, CardinalDirection.South);
				// create wall with (startx,y+1) being one below the start position, x-startx being the positive length
				// so this may represent a wallboard at the bottom of the wall as the top wallboard one below
				sl.add(new Wall(startx*Constants.MAP_UNIT, (y+1)*Constants.MAP_UNIT,
						(x-startx)*Constants.MAP_UNIT, 0, dists.getDistanceValue(startx, y), colchange, mazePanel));
			}
		}
	}
}
