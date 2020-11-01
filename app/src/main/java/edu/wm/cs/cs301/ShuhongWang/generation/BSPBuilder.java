package edu.wm.cs.cs301.ShuhongWang.generation;

import java.util.ArrayList;

import edu.wm.cs.cs301.ShuhongWang.gui.Constants;
import edu.wm.cs.cs301.ShuhongWang.gui.MazePanel;

/**
 * This class has the responsibility to obtain the tree of BSP nodes for a given maze.
 * BSP stands for binary space partitioning. 
 * See https://en.wikipedia.org/wiki/Binary_space_partitioning for some details.
 * 
 * This code is refactored code from MazeBuilder.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 *
 */
public class BSPBuilder {
	private final int width ; 				// width of maze
	private final int height ; 				// height of maze
	private final Distance dists ; 			// distance matrix
	private final Floorplan floorplan ;		// floorplan with maze layout
	private final int colchange ;			// comes from a random number, purpose unclear, 
	// colchange: reason for randomization unclear, used to determine color of wall 
	private final int expectedPartiters ; 	// comes from Constants partct array, entry chosen according to skill level
	// only usage is in updateProgressBar to estimate progress made in the BSP tree construction
	int partiters = 0 ; // relocated from MazeBuilder attribute partiters here. 
	private final Order order ; 		// current order
	
	private MazePanel mazePanel;
	/**
	 * Constructor
	 * @param order
	 * @param dists
	 * @param floorplan
	 * @param width
	 * @param height
	 * @param colchange
	 * @param expectedPartiters
	 * @param mazePanel 
	 */
	public BSPBuilder(Order order, Distance dists, Floorplan floorplan, int width, int height, int colchange, int expectedPartiters, MazePanel mazePanel) {
		this.order = order ;
		this.dists = dists ;
		this.floorplan = floorplan ;
		this.width = width ;
		this.height = height ;
		this.colchange = colchange ;
		this.expectedPartiters = expectedPartiters ;
		this.mazePanel = mazePanel ;

		partiters = 0 ; // counter for keeping track of progress made in BSP calculation, starts at 0
	}

	/**
	 * Create tree of BSP nodes for a given list of walls.
	 * The binary space partitioning algorithm is recursive.
	 * General idea at each level of recursion: 
	 * pick one wall, split all other walls into two lists. 
	 * One list contains all walls that are in front of selected wall,
	 * the other list contains all walls that are behind the selected wall.
	 * Walls that intersect are split into two walls accordingly; the parts
	 * are added to the corresponding lists. 
	 * In theory, polygons can lie in the plane of the selected wall such
	 * that a BSP node would hold on to a list of polygons. This is not 
	 * possible in this maze application.
	 * The code names lists rather left and right to match the terminology of trees
	 * rather than front and back which would resemble terminology for drawing.
	 * The code selects the wall that has the minimum grade value as the one 
	 * for partitioning.
	 * If all the walls in one node are partitioned, it will stop to split.
	 * @param walls the list of walls (polygons) to partition
	 * @return root node for BSP tree
	 * @throws InterruptedException 
	 */
	private BSPNode genNodes(ArrayList<Wall> walls) throws InterruptedException {
		// Recursion anchor:
		// if there is no wall with a partition bit set to false, 
		// there is nothing else to do and we are at a leaf node
		if (countNonPartitions(walls) == 0)
			return new BSPLeaf(walls);
		// Step: pick the wall that is used to partition all others into left and right
		// Criterion: from the ones that have a partition bit set to false, 
		// pick a candidate with a low grade
		// Note: the splitter remains an element of the walls list but is marked as partitioned
		Wall splitter = findPartitionCandidate(walls);
		splitter.setPartition(true);
		
		// Step: split all walls into two lists
		final ArrayList<Wall> left = new ArrayList<Wall>();
		final ArrayList<Wall> right = new ArrayList<Wall>();
		splitWalls(walls, splitter, left, right);
		
		// Recursion
		// Case: one sided recursion, tree has only 1 branch.
		// Note: the splitter is in one of the 2 lists, so if one list is empty,
		// we can omit the current node and just work with the non-empty list.
		if (left.size() == 0)
			return new BSPLeaf(right);
		if (right.size() == 0)
			return new BSPLeaf(left);
		// Case: two sided recursion, need to create a node
		// and recursively calculate subtrees for both sides.
		return new BSPBranch(splitter.getStartPositionX(), splitter.getStartPositionY(), 
				splitter.getExtensionX(), splitter.getExtensionY(), 
				genNodes(left), genNodes(right)); 
	}
	/**
	 * Partitions the given list of walls for the given wall into left and right walls
	 * @param walls the list of walls to split
	 * @param splitter the candidate wall used for partitioning
	 * @param left the resulting list of walls for the left side of the subtree
	 * @param right the resulting list of walls for the right side of the subtree
	 */
	private void splitWalls(ArrayList<Wall> walls, Wall splitter, final ArrayList<Wall> left, final ArrayList<Wall> right) {
		final int x  = splitter.getStartPositionX();
		final int y  = splitter.getStartPositionY();
		final int dx = splitter.getExtensionX();
		final int dy = splitter.getExtensionY();
		
		for (int i = 0; i != walls.size(); i++) {
			// MEMO: code very similar to Walls.calculateGrade method
			// Note: pe is visited in this loop as well
			Wall se = (Wall) walls.get(i);
			int df1x = se.getStartPositionX() - x;
			int df1y = se.getStartPositionY() - y;
			int df2x = se.getEndPositionX() - x; 
			int df2y = se.getEndPositionY() - y; 
			int dot1 = df1x * dy + df1y * -dx;
			int dot2 = df2x * dy + df2y * -dx;
			// Case 1: current wall se intersects with wall pe
			// if this is the case, split se into two walls
			// add one each to the left and right list
			if (getSign(dot1) != getSign(dot2)) {
				if (dot1 == 0)
					dot1 = dot2;
				else if (dot2 != 0) {
					// we need to split this
					Wall[] sps = se.calculatePartitioning(splitter, colchange);
					if (dot1 > 0) {
						right.add(sps[0]);
						left.add(sps[1]);
					} else {
						right.add(sps[1]);
						left.add(sps[0]);
					}
					continue;
				}
			}
			// Case 2 and 3: add the wall to the right or left list
			// decide where to add current wall
			if (dot1 > 0 || (dot1 == 0 && se.hasSameDirection(splitter))) {
				right.add(se);
				if (dot1 == 0)
					se.setPartition(true);
			} else if (dot1 < 0 || (dot1 == 0 && se.hasOppositeDirection(splitter))) { 
				left.add(se);
				if (dot1 == 0)
					se.setPartition(true);
			} else {
				dbg("error xx 1 "+dot1);
			}
		}
	}

	

    /**
	 * Counts how many elements in the wall vector have their partition bit set to false
	 * @param sl all walls
	 * @return number of walls where the partition flag is not set
	 */
	private static int countNonPartitions(ArrayList<Wall> sl) {
		int result = 0 ;
		for (int i = 0; i != sl.size(); i++)
		{
			if (!(sl.get(i)).isPartition())
				result++;
		}
		return result;
	}

	/**
	 * It finds the wall which has the minimum grade value.
	 * @param sl list of walls, remains unchanged
	 * @return wall that is best candidate according to grade partition (smallest grade)
	 * @throws InterruptedException 
	 */
	private Wall findPartitionCandidate(ArrayList<Wall> sl) throws InterruptedException {
		Wall result = null ;
		int bestgrade = 5000; // used to compute the minimum of all observed grade values, set to some high initial value
		final int maxtries = 50; // constant, only used to determine skip
		// consider a subset of walls proportional to the number of tries, here 50, seems to randomize the access a bit
		int skip = (sl.size() / maxtries);
		if (skip == 0)
			skip = 1;
		assert (0 < skip) : "Increment for loop must be positive";
		for (int i = 0; i < sl.size(); i += skip) {
			Wall element = sl.get(i);
			// skip walls where the partition flag was set
			if (element.isPartition())
				continue;
			// provide feedback for progress bar every 32 iterations
			partiters++;
			if ((partiters & 31) == 0) {
				updateProgressBar(partiters); // side effect: update progress bar
			}
			// check grade and keep track of minimum
			int grade = grade_partition(sl, element);
			if (grade < bestgrade) {
				bestgrade = grade;
				result = element; // determine wall with smallest grade
			}
		}
		return result;
	}

	/**
	 * Push information on progress into maze such that UI can update progress bar
	 * @param partiters
	 */
	private void updateProgressBar(int partiters) throws InterruptedException {
		// During maze generation, the most time consuming part needs to occasionally update the current screen
		// 
		int percentage = partiters*100/expectedPartiters ;
		if (null != order) {
			order.updateProgress(percentage) ;
			if (percentage < 100) {
				// give main thread a chance to process keyboard events
				Thread.currentThread().sleep(10);
			}
		}
	}

	/**
	 * Set the partition bit to true for walls on the border and where the direction is 0
	 * @param sl
	 */
	private void setPartitionBitForCertainWalls(ArrayList<Wall> sl) {
	    // TODO: check if seg just works with width and height or needs map_unit adjustment
	    //System.out.println("set Partition bit in BSP builder with scaled width and height values");
		for (Wall se : sl) {
			//se.updatePartitionIfBorderCase(width, height);
		    se.updatePartitionIfBorderCase(width*Constants.MAP_UNIT, height*Constants.MAP_UNIT);
		}
	}




	/**
	 * Method called in genNodes to determine the minimum of all such grades. 
	 * The method is static, i.e. it does not update internal attributes and just calculates the returned value.
	 * @param sl vector of walls
	 * @param pe particular wall
	 * @return undocumented
	 */
	private int grade_partition(ArrayList<Wall> sl, Wall pe) {
	    // code relocated to Seg.java
	    ///* original code
		// copy attributes of parameter pe
		final int x  = pe.getStartPositionX();
		final int y  = pe.getStartPositionY();
		final int dx = pe.getExtensionX();
		final int dy = pe.getExtensionY();
		final int inc = (sl.size() >= 100) ? sl.size() / 50 : 1 ; // increment for iteration below
		// define some local counter
		int lcount = 0, rcount = 0, splits = 0;
		// check all walls, loop calculates lcount, rcount and splits
		for (int i = 0; i < sl.size(); i += inc) {
			Wall se = (Wall) sl.get(i);
			// extract information from wall
			int df1x = se.getStartPositionX() - x; // difference between beginning of wall and x
			int df1y = se.getStartPositionY() - y; // difference between beginning of wall and y
			int df2x = se.getEndPositionX() - x; // difference between end of wall and x
			int df2y = se.getEndPositionY() - y; // difference between end of wall and y
			int nx = dy;
			int ny = -dx;
			int dot1 = df1x * nx + df1y * ny;
			int dot2 = df2x * nx + df2y * ny;
			// update splits if necessary
			if (getSign(dot1) != getSign(dot2)) {
				if (dot1 == 0)
					dot1 = dot2;
				else if (dot2 != 0) {
					splits++;
					continue;
				}
			}
			// update lcount, rcount values
			if (dot1 > 0 ||
					(dot1 == 0 && se.hasSameDirection(pe))) {
				rcount++;
			} else if (dot1 < 0 ||
					(dot1 == 0 && se.hasOppositeDirection(pe))) {
				lcount++;
			} else {
				dbg("grade_partition problem: dot1 = "+dot1+", dot2 = "+dot2);
			}
		}
		int result_old = Math.abs(lcount-rcount) + splits * 3;
		//*/
	    // new code
	    int result_new = pe.calculateGrade(sl);
	    assert (result_old == result_new) : "BSPBuilder grade calculation fails";
	    return result_new;
	}
	/**
	 * Generate tree of BSP nodes for a given maze.
	 * We use the binary space partitioning algorithm to compute a BSP tree.
	 * The method is recursive and operates on a list of polygons.
	 * Here each wall, i.e. a continuous sequence of wallboards, forms
	 * such a polygon.  
	 * @return the root node for the BSP tree
	 * @throws InterruptedException 
	 */
	public BSPNode generateBSPNodes() throws InterruptedException {
		// Binary space partitioning operates on polygons (here: walls)
		// the floorplan only lists wallboards.
		// We need to determine walls, i.e. wallboards over multiple cells in
		// a vertical or horizontal direction.
		ListOfWallsBuilder builder = new ListOfWallsBuilder(width, height, floorplan, dists, colchange, mazePanel);
		ArrayList<Wall> walls = builder.generateWalls(); 

		// The size and balance of the resulting BSP tree depends on 
		// which polygons are selected for the partitioning.
		// Hypothesis: the partition bit is used for this decision 
		// Observation: partition bit true means that that polygon 
		// is not considered any further for node generation
		setPartitionBitForCertainWalls(walls); 

		// TODO: check why this is done. 
		// It creates a top wallboard on position (0,0). 
		// This may even corrupt a maze and block its exit!
		floorplan.addWallboard(new Wallboard(0, 0, CardinalDirection.North), false);
		
		// Start the recursive BSP calculation for the list of polygons
		// and return the root node of the tree
		return genNodes(walls); 
	}
	/**
	 * Provides the sign of a given integer number
	 * @param num
	 * @return -1 if num < 0, 0 if num == 0, 1 if num > 0
	 */
	static int getSign(int num) {
		return (num < 0) ? -1 : (num > 0) ? 1 : 0;
	}
	/**
	 * Produce output for debugging purposes
	 * @param str
	 */
	static void dbg(String str) {
		System.out.println("BSPBuilder: "+str);
	}
}
