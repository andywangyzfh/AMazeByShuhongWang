/**
 * 
 */
package edu.wm.cs.cs301.ShuhongWang.gui;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import edu.wm.cs.cs301.ShuhongWang.generation.BSPBranch;
import edu.wm.cs.cs301.ShuhongWang.generation.BSPLeaf;
import edu.wm.cs.cs301.ShuhongWang.generation.BSPNode;
import edu.wm.cs.cs301.ShuhongWang.generation.Distance;
import edu.wm.cs.cs301.ShuhongWang.generation.Floorplan;
import edu.wm.cs.cs301.ShuhongWang.generation.Maze;
import edu.wm.cs.cs301.ShuhongWang.generation.MazeContainer;
import edu.wm.cs.cs301.ShuhongWang.generation.Wall;

//import java.awt.Color;

/**
 * This class provides functionality to read the content of a Maze object from a file. 
 * File format is XML and produced with MazeFileWriter.
 * The class is a simple wrapper to make all fields of a Maze object accessible 
 * such that with the help of this class it is straightforward
 * to instantiate a MazeConfiguration object.
 * 
 *
 */
public class MazeFileReader {

	// fields of maze configuration object
	private int width ;
	private int height ;
	private int rooms ;
	private int[][] dists ;
	private int expected_partiters ;
	private Floorplan cells ;
	private int startx ;
	private int starty ;
	private BSPNode root ;
	
	private static MazePanel mazePanel;

	/**
	 * Constructor reads maze data from given file. The file format is an XML format produced by the MazeFileWriter class.
	 *  
	 * @param filename with data of a Maze object
	 * @param panel 
	 */
	public MazeFileReader(String filename, MazePanel panel) {
		load(filename) ;
		mazePanel = panel;
	}

	/**
	 * Provides the data loaded from file wrapped in a MazeConfiguration.
	 * @return maze configuration loaded from file
	 */
	Maze getMazeConfiguration() {
		Maze mazeConfig = new MazeContainer() ;
		mazeConfig.setHeight(getHeight());
		mazeConfig.setWidth(getWidth());
		mazeConfig.setFloorplan(getCells());
		Distance dists = new Distance(getDistances()) ;
		mazeConfig.setMazedists(dists);
		mazeConfig.setRootnode(getRootNode());
		mazeConfig.setStartingPosition(getStartX(), getStartY());
		return mazeConfig;
	}
	/////////////////// set of straightforward get methods //////////////
	int getWidth() {
		return width ;
	}
	int getHeight() {
		return height ;
	}
	int getRooms() {
		return rooms ;
	}
	int[][] getDistances() {
		return dists ;
	}
	int getExpectedPartiters() {
		return expected_partiters ;
	}
	Floorplan getCells() {
		return cells ;
	}
	int getStartX() {
		return startx ;
	}
	int getStartY() {
		return starty ;
	}
	BSPNode getRootNode() {
		return root ;
	}
	
	/**
	 * Method provides main functionality to read all attributes of maze object from the given file
	 * @param filename gives the input file
	 */
	private void load(String filename)
	{
		try{

			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Maze");

			for (int temp = 0 ; temp < nList.getLength() ; temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					width = getElementIntValue("sizeX", eElement);
					height = getElementIntValue("sizeY", eElement);
					rooms =  getElementIntValue("roomNum", eElement);
					dists = new int[width][height];
					expected_partiters = getElementIntValue("partiters", eElement);
					cells = readCells(eElement);
					// read array of distance values
					readDistances(eElement);
					// read start position
					startx = getElementIntValue("startX", eElement);
					starty = getElementIntValue("startY", eElement);
					// read tree of BSPNodes
					number = 0 ; // field used as an index, that is shared an updated across recursive readBSPNode calls
					root = readBSPNode(eElement);
				}
			}
		}
		catch (Exception e) { // TODO: implement serious error handling
			e.printStackTrace();
		}

	}

	// shared index number for nodes in the tree of BSPNodes, used as an additional return value for recursive calls
	// the sequence of values is increasing, when switching from a left branch to a right branch in a preorder treetraversal 
	// we need to keep track of the node number 
	int number ;
	/**
	 * Reads data for a BSPNode from file for the given element
	 * 
	 * The method recursively explores the left and right branches and builds a complete tree.
	 * @param eElement element to read data from
	 * @return a new BSPNode, fully initialized with all necessary data
	 */
	private BSPNode readBSPNode(Element eElement) {
		// read fields of BSBNode class
		/* unused, as these values are recalculated in the BSPnode constructor
		int xlLoad = getElementIntValue("xlBSPNode_"+number, eElement);
		int ylLoad = getElementIntValue("ylBSPNode_"+number, eElement);
		int xuLoad = getElementIntValue("xuBSPNode_"+number, eElement);
		int yuLoad = getElementIntValue("yuBSPNode_"+number, eElement);
		 */
		int mynumber = number ; // keep track of own node number, as that the shared attribute number gets manipulated in recursive method calls
		boolean isleafLoad= getElementBooleanValue("isleafBSPNode_"+mynumber, eElement);
		// laod data for leaf nodes and bsp branch nodes
		if (isleafLoad)
		{
			// BSBLeaf, load walls
			// note xl, yl, xu and yu are computed from the walls within the leaf constructor
			// so there is no need to store those
			ArrayList<Wall> slist = new ArrayList<Wall>() ;
			int n = getElementIntValue("numSeg_" + mynumber, eElement); // get the total number of walls to load
			//System.out.println("Trace: read Leaf " + mynumber + ", walls: " + n) ;
			for (int i = 0 ; i < n ; i++)
			{
				slist.add(readWall(eElement, number, i)) ;
			}
			return new BSPLeaf(slist) ;
		}
		else
		{
			//BSPBranch, load fields and left and right branches
			int x = getElementIntValue("xBSPNode_"+mynumber, eElement);
			int y = getElementIntValue("yBSPNode_"+mynumber, eElement);
			int dx = getElementIntValue("dxBSPNode_"+mynumber, eElement); 
			int dy = getElementIntValue("dyBSPNode_"+mynumber, eElement);
			// read left branch before right branch, increment index number for next node to visit
			number++ ;
			BSPNode l = readBSPNode(eElement) ; // recursion updates index number for each element of the subtree
			number++ ; // increment index number for next node to visit
			BSPNode r = readBSPNode(eElement) ;
			// other fields of BSBNode class need not be set, computed in constructor from branches
			return new BSPBranch(x,y,dx,dy,l,r) ;
		}
	}
	/**
	 * Read a single wall from file
	 * @param eElement element to read from 
	 * @param number suffix with index number of BSPNode
	 * @param i suffix with index of wall
	 * @return new wall, fully initialized with all necessary data
	 */
	private static Wall readWall(Element eElement, int number, int i) {
		// load wall attributes		
		int dist = getElementIntValue("distSeg_" + number+ "_" + i, eElement);
		int dx = getElementIntValue("dxSeg_" + number+ "_" + i, eElement);
		int dy = getElementIntValue("dySeg_" + number+ "_" + i, eElement);
		int x = getElementIntValue("xSeg_" + number+ "_" + i, eElement);
		int y = getElementIntValue("ySeg_" + number+ "_" + i, eElement);	
		int cc = 0 ; // use this as a dummy for the constructor, the correct color is explicitly set below
		Wall result = new Wall(x,y,dx,dy,dist,cc, mazePanel) ;
		// get a few more attributes and set those explicitly
		int col = getElementIntValue("colSeg_" + number+ "_" + i, eElement);
		result.setColor(col); 
		result.setSeen(getElementBooleanValue("seenSeg_" + number+ "_" + i, eElement));
		result.setPartition(getElementBooleanValue("partitionSeg_" + number+ "_" + i, eElement));
		return result;
	}

	/**
	 * Reads data for a two-dimensional array of distance values. 
	 * Requires that fields width and height have been set. 
	 * @param eElement to read data from
	 */
	private void readDistances(Element eElement) {
		int number = 0 ;
		for ( int x = 0; x != width; x++) {
			for ( int y = 0; y != height; y++) {
				dists[x][y] = getElementIntValue("dists"+ "_" + Integer.toString(number), eElement);
				number++;
			}
		}
	}

	/**
	 * Reads data for a cells object that contains values for walls. 
	 * Requires that fields width and height have been set. 
	 * @param eElement to read data from
	 */
	private Floorplan readCells(Element eElement) {
		int [][]cellValue = new int[width][height] ;
		int number = 0 ;

		for ( int x = 0; x != width; x++) {
			for ( int y = 0; y != height; y++) {
				cellValue[x][y] = getElementIntValue("cell"+ "_" + Integer.toString(number), eElement);
				number++;
			}
		}
		return new Floorplan(cellValue);
	}
	
	/**
	 * Obtains an integer value from the given element for the given string
	 * @param string identifier
	 * @param parent element that holds data
	 * @return
	 */
	public static int getElementIntValue(  String string, Element parent) {
		return Integer
				.parseInt(getElementStringValue( string, parent));
	}

	/**
	 * Obtains an integer value from the given element for the given string
	 * @param string identifier
	 * @param parent element that holds data
	 * @return
	 */
	public static boolean getElementBooleanValue(  String string, Element parent ) {
		return Boolean
				.valueOf(getElementStringValue(string, parent))
				.booleanValue();
	}

	/**
	 * Obtains an integer value from the given element for the given string
	 * @param string identifier
	 * @param parent element that holds data
	 * @return
	 */
	public static String getElementStringValue( String string, Element parent) {
		NodeList nl = parent.getElementsByTagName(string);
		if (nl.getLength() == 0) {
			return "";
		}

		Node n = nl.item(0).getFirstChild();
		if (n == null) {
			return "";
		}

		return n.getNodeValue();
	}

	/////////////////////////////////// internal methods used in testing /////////////////////////////////////////
	// TODO: change these into equals and compare methods for the corresponding Maze and BSPNode classes
	// TODO: create junit test class that is a subclass of MazeFileReader that takes this code and performs unit tests
	/**
	 * compares given data with maze data read from file
	 * @param mazew
	 * @param mazeh
	 * @param rooms2
	 * @param expected_partiters2
	 * @param root2
	 * @param mazecells
	 * @param mazedists
	 * @param px
	 * @param py
	 */
	public void compare(int mazew, int mazeh, int rooms2,
			int expected_partiters2, BSPNode root2, Floorplan mazecells,
			int[][] mazedists, int px, int py) {
		if (mazew != this.width)
			System.out.println("MazeFileReader.compare: width mismatch");
		if (mazeh != this.height)
			System.out.println("MazeFileReader.compare: height mismatch");
		if (rooms2 != this.rooms)
			System.out.println("MazeFileReader.compare: rooms mismatch");
		if (expected_partiters2 != this.expected_partiters)
			System.out.println("MazeFileReader.compare: expected partiters mismatch");
		if (px != this.startx)
			System.out.println("MazeFileReader.compare: start x mismatch");
		if (py != this.starty)
			System.out.println("MazeFileReader.compare: start y mismatch");
		compareCells(mazecells) ;
		compareDistances(mazedists) ;
		System.out.println("Start comparing BSP nodes") ;
		compareBSPNodes(root, root2) ;
		
	}

	private static void compareBSPNodes(BSPNode root, BSPNode root2) {
		
		// compare BSPNode fields
		if (root.isIsleaf() != root2.isIsleaf()) 
			System.out.println("MazeFileReader.compareBSPNodes:isleaf mismatch");
		if (root.getLowerBoundX() != root2.getLowerBoundX()) 
			System.out.println("MazeFileReader.compareBSPNodes:xl mismatch");
		if (root.getUpperBoundX() != root2.getUpperBoundX()) 
			System.out.println("MazeFileReader.compareBSPNodes:xu mismatch");
		if (root.getLowerBoundY() != root2.getLowerBoundY()) 
			System.out.println("MazeFileReader.compareBSPNodes:yl mismatch");
		if (root.getUpperBoundY() != root2.getUpperBoundY()) 
			System.out.println("MazeFileReader.compareBSPNodes:yu mismatch");
		// if Leaf nodes compare seqment lists
		//System.out.println("Start recursion for comparing BSP nodes") ;
		if (BSPLeaf.class == root.getClass())
		{
			if (BSPLeaf.class != root2.getClass()) 
				System.out.println("MazeFileReader.compareBSPNodes: type of nodes mismatch, root node has leaf, other node as branch");
			compareWalls(((BSPLeaf)root).getSlist(),((BSPLeaf)root2).getSlist()) ;
		}
		// if Branch nodes compare attributes and branches
		if (BSPBranch.class == root.getClass())
		{
			if (BSPBranch.class != root2.getClass()) 
				System.out.println("MazeFileReader.compare: mismatch");

			BSPBranch b = (BSPBranch)root ;
			BSPBranch b2 = (BSPBranch)root2 ;
			
			if(b.getX() != b2.getX()) 
				System.out.println("MazeFileReader.compare: mismatch");
			if(b.getY() != b2.getY()) 
				System.out.println("MazeFileReader.compare: mismatch");
			if(b.getDx() != b2.getDx()) 
				System.out.println("MazeFileReader.compare: mismatch");
			if(b.getDy() != b2.getDy()) 
				System.out.println("MazeFileReader.compare: mismatch");
			compareBSPNodes(b.getLeftBranch(), b2.getLeftBranch()) ;
			compareBSPNodes(b.getRightBranch(), b2.getRightBranch()) ;
		}

	}

	private static void compareWalls(ArrayList<Wall> slist, ArrayList<Wall> slist2) {
		int n = slist.size() ;
		if (n != slist2.size()) 
			System.out.println("MazeFileReader.compare walls: length mismatch, " + n + " vs " + slist2.size());
		Wall s ;
		//Seg s2 ;
		for (int i = 0 ; i < n ; i++)
		{
			s = slist.get(i) ;
			if (!s.equals(slist2.get(i))) {
				assert false : "MazeFileReader.compare walls do not mismatch" ;
				// if assert not enabled during execution, at least print a waring
				System.out.println("MazeFileReader.compare walls do not match"); 
			}
		}

	}
	private void compareDistances(int[][] mazedists) {
		int[][] dists2 = mazedists ;
		for (int i = 0 ; i < width ; i++)
		{
			for (int j = 0 ; j < height ; j++)
			{
				if (dists[i][j] != dists2[i][j]) 
					System.out.println("MazeFileReader.compare distances: mismatch");
			}
		}
		
	}

	private void compareCells(Floorplan mazecells) {
		if (!cells.equals(mazecells))
			System.out.println("MazeFileReader.compare cells: mismatch");
	}

}
