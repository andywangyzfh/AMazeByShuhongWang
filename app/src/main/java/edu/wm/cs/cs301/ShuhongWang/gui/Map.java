/**
 * 
 */
package edu.wm.cs.cs301.ShuhongWang.gui;

import edu.wm.cs.cs301.ShuhongWang.generation.CardinalDirection;
import edu.wm.cs.cs301.ShuhongWang.generation.Floorplan;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
//import java.awt.Color;
//import java.awt.Graphics;

/**
 * This class encapsulates all functionality to draw a map of the overall maze,
 * the set of visible walls, the solution.
 * The map is drawn on the screen in such a way that the current position
 * remains at the center of the screen.
 * The current position is visualized as a red dot with an attached arc
 * for its current direction.
 * The solution is visualized as a yellow line from the current position
 * towards the exit of the map.
 * Walls that have been visible in the first person view are drawn white,
 * all other walls that were never shown before are drawn in grey.
 * It is possible to zoom in and out of the map by increasing or decreasing
 * the map scale.
 * 
 * This code is refactored code from Maze.java by Paul Falstad,
 * www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 *
 */
public class Map {

	// keep local copies of values determined for UI appearance
	final int viewWidth;  // set to Constants.VIEW_WIDTH, 
	final int viewHeight; // set to Constants.VIEW_HEIGHT
	final int mapUnit;    // set to Constants.MAP_UNIT
	final int stepSize;  // set to Constants.STEP_SIZE, typical value: map_unit/4
	
	/**
	 * The user can increment or decrement the scale of the map.
	 * map_scale is used to keep track of the current setting.
	 * Minimum value is 1.
	 */
	int mapScale;
	
	/**
	 * SeenWalls contains information on walls that are seen from the current point of view.
	 * The field is set by the constructor. The referenced object is shared with
	 * the FirstPersonDrawer that writes content into it. The MapDrawer only
	 * reads content to decide which lines to draw and in which color.
	 */
	final Floorplan seenWalls ; 

	/**
	 * Contains all necessary information about current maze, i.e.
	 * cells: location of wallboards
	 * dists: distance to exit
	 * width and height of the maze
	 */
	final Maze maze ;

	/**
	 * Constructor 
	 * @param width of display
	 * @param height of display
	 * @param mapUnit
	 * @param stepSize
	 * @param seenWalls
	 * @param mapScale
	 * @param maze
	 */
	public Map(int width, int height, int mapUnit, int stepSize, Floorplan seenWalls, int mapScale, Maze maze){
		//System.out.println("MapDrawer: constructor called") ;
		viewWidth = width ;
		viewHeight = height ;
		this.mapUnit = mapUnit ;
		this.stepSize = stepSize ;
		this.seenWalls = seenWalls ;
		this.mapScale = mapScale >= 1 ? mapScale: 1 ; // 1 <= map_scale
		this.maze = maze ;
		// correctness considerations
		assert maze != null : "MapDrawer: maze configuration can't be null at instantiation!" ;
		assert seenWalls != null : "MapDrawer: seencells can't be null at instantiation!" ;
	}
	
	/**
	 * Constructor with default settings
	 * from Constants.java for width, height, mapUnit and stepSize.
	 * @param seenCells
	 * @param mapScale
	 * @param maze
	 */
	public Map(Floorplan seenCells, int mapScale, Maze maze){
		this(Constants.VIEW_WIDTH,Constants.VIEW_HEIGHT,Constants.MAP_UNIT,
    			Constants.STEP_SIZE, seenCells, mapScale, maze);
		}
	
	public void incrementMapScale() {
		mapScale += 1 ;
	}
	
	public void decrementMapScale() {
		mapScale -= 1 ;
		if (1 > mapScale)
			mapScale = 1 ;
	}

	/**
	 * Draws the current map on top of the first person view.
	 * Method assumes that we are in the playing state and that
	 * the map mode is switched on.
	 * @param panel
	 * @param x
	 * @param y
	 * @param angle
	 * @param walkStep is a counter between 0, 1, 2, ..., 3
	 * for in between stages for a walk operation, needed to obtain
	 * exact location in map
	 * @param showMaze if true, highlights already seen walls in white
	 * @param showSolution if true shows a path to the exit as a yellow line,
	 * otherwise path is not shown.
	 */
	public void draw(MazePanel panel, int x, int y, int angle, int walkStep,
			boolean showMaze, boolean showSolution) {
//		Graphics g = panel.getBufferGraphics() ;
        // viewers draw on the buffer graphics
        if (null == panel) {
            System.out.println("MapDrawer.draw: can't get graphics object to draw on, skipping draw operation") ;
            return;
        }
        final int viewDX = getViewDX(angle); 
        final int viewDY = getViewDY(angle);
        drawMap(panel, x, y, walkStep, viewDX, viewDY, showMaze, showSolution) ;
        drawCurrentLocation(panel, viewDX, viewDY) ;
	}
	//////////////////////////////// private, internal methods //////////////////////////////
	private int getViewDX(int angle) {
		return (int) (Math.cos(radify(angle))*(1<<16));
	}
	private int getViewDY(int angle) {
		return (int) (Math.sin(radify(angle))*(1<<16));
	}
	final double radify(int x) {
        return x*Math.PI/180;
    }
	/**
	 * Helper method for draw, called if map_mode is true, i.e. the users wants to see the overall map.
	 * The map is drawn only on a small rectangle inside the maze area such that only a part of the map is actually shown.
	 * Of course a part covering the current location needs to be displayed.
	 * The current cell is (px,py). There is a viewing direction (view_dx, view_dy).
	 * @param panel graphics handler to manipulate screen
	 * @param px current position, x index
	 * @param py current position, y index 
	 */
	private void drawMap(MazePanel panel, int px, int py, int walkStep, 
			int viewDX, int viewDY, boolean showMaze, boolean showSolution) {
		// dimensions of the maze in terms of cell ids
		final int mazeWidth = maze.getWidth() ;
		final int mazeHeight = maze.getHeight() ;
		
		panel.setColor(MazePanel.WHITE);
		
		// note: 1/2 of width and height is the center of the screen
		// the whole map is centered at the current position
		final int offsetX = getOffset(px, walkStep, viewDX, viewWidth);
		final int offsetY = getOffset(py, walkStep, viewDY, viewHeight);
		
		// We need to calculate bounds for cell indices to consider
		// for drawing. Since not the whole maze may be visible
		// for the given screen size and the current position (px,py)
		// is fixed to the center of the drawing area, we need
		// to find the min and max indices for cells to consider.
		// compute minimum for x,y
		final int minX = getMinimum(offsetX);
		final int minY = getMinimum(offsetY);
		// compute maximum for x,y
		final int maxX = getMaximum(offsetX, viewWidth, mazeWidth);
		final int maxY = getMaximum(offsetY, viewHeight, mazeHeight);
		
		// iterate over integer grid between min and max of x,y indices for cells
		for (int y = minY; y <= maxY; y++)
			for (int x = minX; x <= maxX; x++) {
				// starting point of line
				int startX = mapToCoordinateX(x, offsetX);
				int startY = mapToCoordinateY(y, offsetY);
				
				// draw horizontal line
				boolean theCondition = (x >= mazeWidth) ? false : ((y < mazeHeight) ?
						maze.hasWall(x,y, CardinalDirection.North) :
							maze.hasWall(x,y-1, CardinalDirection.South));

				panel.setColor(seenWalls.hasWall(x,y, CardinalDirection.North) ? MazePanel.WHITE : MazePanel.GRAY);
				if ((seenWalls.hasWall(x,y, CardinalDirection.North) || showMaze) && theCondition)
					panel.addLine(startX, startY, startX + mapScale, startY); // y coordinate same
				
				// draw vertical line
				theCondition = (y >= mazeHeight) ? false : ((x < mazeWidth) ?
						maze.hasWall(x,y, CardinalDirection.West) :
							maze.hasWall((x-1),y, CardinalDirection.East));

				panel.setColor(seenWalls.hasWall(x,y, CardinalDirection.West) ? MazePanel.WHITE : MazePanel.GRAY);
				if ((seenWalls.hasWall(x,y, CardinalDirection.West) || showMaze) && theCondition)
					panel.addLine(startX, startY, startX, startY - mapScale); // x coordinate same
			}
		
		if (showSolution) {
			drawSolution(panel, offsetX, offsetY, px, py) ;
		}
	}
	/**
	 * Obtains the maximum for a given offset
	 * @param offset either in x or y direction
	 * @param viewLength is either viewWidth or viewHeight
	 * @param mazeLength is either mazeWidth or mazeHeight
	 * @return maximum that is bounded by mazeLength
	 */
	private int getMaximum(int offset, int viewLength, int mazeLength) {
		int result = (viewLength-offset)/mapScale+1;
		if (result >= mazeLength)  
			result = mazeLength;
		return result;
	}

	/**
	 * Obtains the minimum for a given offset
	 * @param offset either in x or y direction
	 * @return minimum that is greater or equal 0
	 */
	private int getMinimum(final int offset) {
		final int result = -offset/mapScale;
		return (result < 0) ? 0 : result;
	}

	/**
	 * Calculates the offset in either x or y direction
	 * @param coordinate is either x or y coordinate of current position
	 * @param walkStep
	 * @param viewDirection is either viewDX or viewDY
	 * @param viewLength is either viewWidth or viewHeight
	 * @return the offset
	 */
	private int getOffset(int coordinate, int walkStep, int viewDirection, int viewLength) {
		final int tmp = coordinate*mapUnit + mapUnit/2 + mapToOffset((stepSize*walkStep),viewDirection);
		return -tmp*mapScale/mapUnit + viewLength/2;
	}
	
	/**
	 * Maps the y index for some cell (x,y) to a y coordinate
	 * for drawing.
	 * @param cellY, {@code 0 <= cellY < height}
	 * @param offsetY
	 * @return y coordinate for drawing
	 */
	private int mapToCoordinateY(int cellY, int offsetY) {
		// TODO: bug suspect: inversion with height is suspect for upside down effect on directions
		// note: (cellY*map_scale + offsetY) same as for mapToCoordinateX
		return viewHeight-1-(cellY*mapScale + offsetY);
	}

	/**
	 * Maps the x index for some cell (x,y) to an x coordinate
	 * for drawing. 
	 * @param cellX is the index of some cell, {@code 0 <= cellX < width}
	 * @param offsetX
	 * @return x coordinate for drawing
	 */
	private int mapToCoordinateX(int cellX, int offsetX) {
		return cellX*mapScale + offsetX;
	}
	
	/**
	 * Maps a given length and direction into an offset for drawing coordinates.
	 * @param length
	 * @param direction
	 * @return offset
	 */
	private int mapToOffset(final int length, final int direction) {
		// Signed bit shift to the right performs a division by 2^16
		// preserves the sign
		// discards the remainder as the result is int
		return (length * direction) >> 16;
	}
	/**
	 * Unscale value
	 * @param x
	 * @return
	 */
	final int unscaleViewD(int x) {
		// >> is the signed right shift operator
		// shifts input x in its binary representation
		// 16 times to the right
		// same as divide by 2^16 and discard remainder
		// preserves sign
		// essentially used here for the following mapping
		// (based on debug output observations)
		// dbg("right shift: " + x + " gives " + (x >> 16));
		// -2097152 gives -32
		// -4194304 gives -64
		// -6291456 gives -96
		// -8388608 gives -128
		// 2097152 gives 32
		// 4194304 gives 64
		// 6291456 gives 96
		// 8388608 gives 128
		return x >> 16;
	}
	/**
	 * Draws a red circle at the center of the screen and
	 * an arrow for the current direction.
	 * It always reside on the center of the screen. 
	 * The map drawing moves if the user changes location.
	 * The size of the overall visualization is limited by
	 * the size of a single cell to avoid that the circle
	 * or arrow visually collide with an adjacent wallboard on the
	 * map visualization. 
	 * @param panel to draw on
	 */
	private void drawCurrentLocation(MazePanel panel, int viewDX, int viewDY) {
		panel.setColor(MazePanel.RED);
		// draw oval of appropriate size at the center of the screen
		int centerX = viewWidth/2; // center x
		int centerY = viewHeight/2; // center y
		int diameter = mapScale/2; // circle size
		// we need the top left corner of a bounding box the circle is in
		// and its width and height to draw the circle
		// top left corner is (centerX-radius, centerY-radius)
		// width and height is simply the diameter
		panel.addFilledOval(centerX-diameter/2, centerY-diameter/2, diameter, diameter);
		// draw a red arrow with the oval to show current direction
		drawArrow(panel, viewDX, viewDY, centerX, centerY);
	}

	/**
	 * Draws an arrow either in horizontal or vertical direction.
	 * @param panel to draw on
	 * @param viewDX is the current viewing direction, x coordinate
	 * @param viewDY is the current viewing direction, y coordinate
	 * @param startX is the x coordinate of the starting point
	 * @param startY is the y coordinate of the starting point
	 */
	private void drawArrow(MazePanel panel, int viewDX, int viewDY, 
			final int startX, final int startY) {
		// calculate length and coordinates for main line
		final int arrowLength = mapScale*7/16; // arrow length, about 1/2 map_scale
		final int tipX = startX + mapToOffset(arrowLength, viewDX);
		final int tipY = startY - mapToOffset(arrowLength, viewDY);
		// draw main line, goes from starting (x,y) to end (tipX,tipY)
		panel.addLine(startX, startY, tipX, tipY);
		// calculate length and positions for 2 lines pointing towards (tipX,tipY)
		// find intermediate point (tmpX,tmpY) on main line
		final int length = mapScale/4;
		final int tmpX = startX + mapToOffset(length, viewDX);
		final int tmpY = startY - mapToOffset(length, viewDY);
		// find offsets at intermediate point for 2 points orthogonal to main line
		// negative sign used for opposite direction
		// note the flip between x and y for view_dx and view_dy
		/*
		final int offsetX = -(length * view_dy) >> 16;
		final int offsetY = -(length * view_dx) >> 16;
		*/
		final int offsetX = mapToOffset(length, -viewDY);
		final int offsetY = mapToOffset(length, -viewDX);
		// draw two lines, starting at tip of arrow
		panel.addLine(tipX, tipY, tmpX + offsetX, tmpY + offsetY);
		panel.addLine(tipX, tipY, tmpX - offsetX, tmpY - offsetY);
	}


	
	/**
	 * Draws a yellow line to show the solution on the overall map. 
	 * Method is only called if in state playing and map_mode 
	 * and showSolution are true.
	 * Since the current position is fixed at the center of the screen, 
	 * all lines on the map are drawn with some offset.
	 * @param panel to draw lines on
	 * @param offsetX is the offset for x coordinates
	 * @param offsetY is the offset for y coordinates
	 * @param px is the current position, an index x for a cell
	 * @param py is the current position, an index y for a cell
	 */
	private void drawSolution(MazePanel panel, int offsetX, int offsetY, int px, int py) {

		if (!maze.isValidPosition(px, py)) {
			dbg(" Parameter error: position out of bounds: (" + px + "," + 
					py + ") for maze of size " + maze.getWidth() + "," + 
					maze.getHeight()) ;
			return ;
		}
		// current position on the solution path (sx,sy)
		int sx = px;
		int sy = py;
		int distance = maze.getDistanceToExit(sx, sy);
		
		panel.setColor(MazePanel.YELLOW);
		
		// while we are more than 1 step away from the final position
		while (distance > 1) {
			// find neighbor closer to exit (with no wallboard in between)
			int[] neighbor = maze.getNeighborCloserToExit(sx, sy) ;
			if (null == neighbor)
				return ; // error
			// scale coordinates, original calculation:
			// x-coordinates
			// nx1     == sx*map_scale + offx + map_scale/2;
			// nx1+ndx == sx*map_scale + offx + map_scale/2 + dx*map_scale == (sx+dx)*map_scale + offx + map_scale/2;
			// y-coordinates
			// ny1     == view_height-1-(sy*map_scale + offy) - map_scale/2;
			// ny1+ndy == view_height-1-(sy*map_scale + offy) - map_scale/2 + -dy * map_scale == view_height-1 -((sy+dy)*map_scale + offy) - map_scale/2
			// current position coordinates
			//int nx1 = sx*map_scale + offx + map_scale/2;
			//int ny1 = view_height-1-(sy*map_scale + offy) - map_scale/2;
			//
			// we need to translate the cell indices x and y into
			// coordinates for drawing, the yellow lines is centered
			// so 1/2 of the size of the cell needs to be added to the
			// top left corner of a cell which is + or - map_scale/2.
			int nx1 = mapToCoordinateX(sx,offsetX) + mapScale/2;
			int ny1 = mapToCoordinateY(sy,offsetY) - mapScale/2;
			// neighbor position coordinates
			//int nx2 = neighbor[0]*map_scale + offx + map_scale/2;
			//int ny2 = view_height-1-(neighbor[1]*map_scale + offy) - map_scale/2;
			int nx2 = mapToCoordinateX(neighbor[0],offsetX) + mapScale/2;
			int ny2 = mapToCoordinateY(neighbor[1],offsetY) - mapScale/2;
			panel.addLine(nx1, ny1, nx2, ny2);
			
			// update loop variables for current position (sx,sy)
			// and distance d for next iteration
			sx = neighbor[0];
			sy = neighbor[1];
			distance = maze.getDistanceToExit(sx, sy) ;
		}
	}
	

	/**
	 * Debug output
	 * @param str
	 */
	private void dbg(String str) {
		// TODO: change this to a logger
		System.out.println("MapDrawer:"+ str);
	}
}
