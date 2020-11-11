/**
 * 
 */
package edu.wm.cs.cs301.ShuhongWang.generation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * A leaf node for a tree of BSPNodes. It carries a list of walls. 
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class BSPLeaf extends BSPNode {

	private ArrayList<Wall> walls; // list of walls

	/**
	 * Constructor
	 * stores reference to given list of walls and updates bounds
	 * @param listOfWalls is a list of walls, can not be empty
	 */
	public BSPLeaf(ArrayList<Wall> listOfWalls) {
		walls = listOfWalls;
		// list should not be empty as this is the only way to provide content
		assert (!listOfWalls.isEmpty()) : "BSPLeaf needs walls, list is empty!" ;
		// update the bounds that are kept in the super class
		updateBounds(listOfWalls);
	}

	/**
	 * Update bounds based on min and max values seen in start and end positions
	 * of a given list of walls
	 * @param listofWalls
	 */
	private void updateBounds(ArrayList<Wall> listofWalls) {
	    /*
		setLowerBoundX(1000000); // TODO: poor programming, supposed to be largest possible integer
		setUpperBoundX(-1000000); // TODO: poor programming, supposed to be smallest possible integer
		setLowerBoundY(1000000); // TODO: poor programming, supposed to be largest possible integer
		setUpperBoundY(-1000000); // TODO: poor programming, supposed to be smallest possible integer
		for (int i = 0; i != sl.size(); i++) {
			Seg se = (Seg)slist.get(i);
			fix_bounds(se.getStartPositionX(), se.getStartPositionY());
			fix_bounds(se.getEndPositionX(), se.getEndPositionY());
		}
		*/
	    setLowerBoundX(Integer.MAX_VALUE); 
        setUpperBoundX(Integer.MIN_VALUE);
        setLowerBoundY(Integer.MAX_VALUE); 
        setUpperBoundY(Integer.MIN_VALUE); 
        for (Wall se: listofWalls) {
            updateBounds(se.getStartPositionX(), se.getStartPositionY());
            updateBounds(se.getEndPositionX(), se.getEndPositionY());
        }
	}
	/**
	 * @return tells if object is a leaf node
	 */
	@Override
	public boolean isIsleaf() {
		return true ;
	}
	/**
	 * Store the content of a leaf node, in particular its list of walls.
	 * All entries carry the number of the node as an index and each wall has an additional second index for the wall number.
	 * @param doc document to add data to
	 * @param mazeXML element to add data to
	 * @param number is an index number for this node in the XML format
	 * @return the highest used index number, in this case the given number
	 */
	public int store(Document doc, Element mazeXML, int number) {
		super.store(doc, mazeXML, number) ; //leaves number unchanged
		if (isIsleaf() == false)
			System.out.println("WARNING: isleaf flag and class are inconsistent!");
		// store list of walls, store total number of elements first
//		MazeFileWriter.appendChild(doc, mazeXML, "numSeg_" + number, walls.size()) ;
		int i = 0 ;
		for (Wall s : walls)
		{
			s.storeWall(doc, mazeXML, number, i);
			i++ ;
		}
		return number ;
	}

	/**
	 * @return the list of walls 
	 */
	public ArrayList<Wall> getSlist() {
		return walls;
	}

}

