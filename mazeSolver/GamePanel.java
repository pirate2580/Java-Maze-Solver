package mazeSolver;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Queue;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.*;

//grid coloring:
// 0 -> untouched is white
// 1 -> wall is black
// 2 -> start is green
// 3 -> end is red

public class GamePanel extends JPanel implements ActionListener{

	class Node{
		int x;
		int y;
		double distance = Integer.MAX_VALUE;
		
		//parent in particular is the path you take to get to the current node
		Node parent = null;
		boolean visited;
		boolean blocked;
		
		public Node(int x,int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public ArrayList<Node> distance(int startI, int startJ,int endI,int endJ) {
		

		Node start = new Node(startI, startJ);
		Node end = new Node(endI, endJ);
		// The grid that is used to store nodes
		//gridArea = new Node[size][size];

		// Creating nodes and finding blocked cells in matrix and mapping accordingly to our grid

		// setting start distance to 0. 
		// All other nodes will have infinity distance at the beginning
		start.distance =0;

		// a comparator object to deal with Priority Queue
		Comparator<Node> adjacencyComparator = (left, right) -> {
			if (left.distance > (right.distance)) {
				return 1;
			}
			else {
				return -1;
			}
		};
		 
		// Queue to store visiting nodes
		Queue<Node> queueB = new PriorityQueue(((SCREEN_HEIGHT-50)/UNIT_SIZE)*(SCREEN_WIDTH/UNIT_SIZE), adjacencyComparator);

		queueB.add(start);

		while (queueB.size() > 0) {
			Node current = queueB.remove();
			Node t;

			// Top
			if (current.x - 1 >= 0) {
				t = grid[current.x - 1][current.y];
				if (!t.visited && !t.blocked && t.distance > current.distance + hVDistance) {
					t.distance = current.distance + hVDistance;
					t.parent = current;
					queueB.add(t);
				}
			}
	
			// Left
			if (current.y - 1 > 0) {
				t = grid[current.x][current.y - 1];
				if (!t.visited && !t.blocked && t.distance > current.distance + hVDistance) {
					t.distance = current.distance + hVDistance;
					t.parent = current;
					queueB.add(t);
				}
			}
	
			 // Right
			if (current.y + 1 < SCREEN_WIDTH/UNIT_SIZE) {
				t = grid[current.x][current.y + 1];
				if (!t.visited && !t.blocked && t.distance > current.distance + hVDistance) {
					t.distance = current.distance + hVDistance;
					t.parent = current;
					queueB.add(t);
				}
			}
			
			// Down
			if (current.x + 1 < (SCREEN_HEIGHT-50)/UNIT_SIZE) {
				t = grid[current.x + 1][current.y];
				if (!t.visited && !t.blocked && t.distance > current.distance + hVDistance) {
					t.distance = current.distance + hVDistance;
					t.parent = current;
					queueB.add(t);
				}
			}
			 
			current.visited = true;
		}

		ArrayList<Node> path = new ArrayList<>();

		// Checking if a path exists
		if (!(grid[end.x][end.y].distance == Integer.MAX_VALUE)) {
			//Trace back the path
			Node current = grid[end.x][end.y];
	
			while (current.parent != null) {
				path.add(current.parent);
				current = current.parent;
			}
		} 
		else {
			System.out.println("No possible path");
		}

		return path;
	}

	
	
	//setting the game panel's dimensions
	static final int SCREEN_HEIGHT = 600;
	static final int SCREEN_WIDTH  = 600;
	static final int UNIT_SIZE     =  10;
	static final int DELAY = 75;
	
	static final double hVDistance = 1;
	// the 2-d grid
	final Node[][]grid = new Node[(SCREEN_HEIGHT-50)/UNIT_SIZE][SCREEN_WIDTH/UNIT_SIZE];
	
	public void setUp() {
		for (int i=0;i<(SCREEN_HEIGHT-50)/UNIT_SIZE;i++) {
			for (int j=0;j<(SCREEN_WIDTH/UNIT_SIZE);j++) {
				grid[i][j]=new Node(i,j);
				if (i==0 || j==0 || i==((SCREEN_HEIGHT-50)/UNIT_SIZE)-1 ||j==((SCREEN_WIDTH-50)/UNIT_SIZE)-1) {
					grid[i][j].blocked = true;
				}
			}
		}
	}
	
	//flag for the steps of the process, setting a startpoint, endpoint, and when you're done making walls
	boolean flagStart = false;
	boolean flagpaintStart =false;
	boolean flagpaintEnd = false;
	boolean flagEnd = false;
	boolean flagFindPath = false;
	
	
	//we set start and end pos to -1 so it doesn't make a change until user input
	int startX=-1;
	int startY=-1;
	int endX=-1;
	int endY=-1;
	
	//this is to capture frame changes
	Timer timer;
	
	//Thread t1= new Thread(new MyThread());
	
	GamePanel(){
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		this.addMouseListener(new MyMouseAdapter());
		this.addMouseMotionListener(new MyMouseMotionAdapter());
		setUp();
		
		timer = new Timer(DELAY,this);
		timer.start();
	}
	
	//paintComponent method makes visual changes to the GUI
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw (Graphics g) {
		
		//this for loop draws the grid
		//we leave the bottom of the gui empty because that is where we will display text
		for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
			g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT-50);
			if (i*UNIT_SIZE<=SCREEN_HEIGHT-50) {
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
			}
		}
		//System.out.print(startX);System.out.print(' ');System.out.println(startY);
		//System.out.print(endX);System.out.print(' ');System.out.println(endY);
		
		if (flagStart) {
			g.setColor(Color.green);
			g.fillRect(startX*UNIT_SIZE+2, startY*UNIT_SIZE+2, UNIT_SIZE-3, UNIT_SIZE-3);
		}
		if (flagEnd) {
			g.setColor(Color.red);
			g.fillRect(endX*UNIT_SIZE+2, endY*UNIT_SIZE+2, UNIT_SIZE-3, UNIT_SIZE-3);
		}
		//this for loop is to visualize walls in the grid
		
		for (int i=0;i<SCREEN_HEIGHT-50;i+=UNIT_SIZE) {
			for (int j=0;j<SCREEN_WIDTH;j+=UNIT_SIZE) {
				//System.out.println('a');
				//if the square is a wall
				if (grid[i/UNIT_SIZE][j/UNIT_SIZE].blocked==true) {
					g.setColor(Color.black);
					g.fillRect(i+2, j+2, UNIT_SIZE-3, UNIT_SIZE-3);
				}
			}
		}
		
		if (!flagStart) {
			g.setColor(Color.black);
			g.setFont(new Font("Arial",Font.BOLD,15));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Welcome to the pathfinding visualizer, select a startNode", 10, 570);
		}
		else if (!flagEnd){
			g.setColor(Color.black);
			g.setFont(new Font("Arial",Font.BOLD,15));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Now select an endNode", 10, 570);
		}
		else if (flagStart&&flagEnd&&!flagFindPath) {
			g.setColor(Color.black);
			g.setFont(new Font("Arial",Font.BOLD,15));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Now make some walls, I'll see if I can find a path!", 10, 570);
			g.drawString("When you want me to find the path, click s to start", 10, 585);
		}
		else {
			Node start = new Node(startX, startY);
			Node end = new Node(endX, endY);
			
			ArrayList<Node> ans = distance(startX,startY,endX,endY);
			
			for (int i=0;i<ans.size();i++) {
				g.setColor(Color.yellow);
				g.fillRect((ans.get(i)).x*UNIT_SIZE+1, (ans.get(i)).y*UNIT_SIZE+1, UNIT_SIZE-3, UNIT_SIZE-3);
			}
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {//this function is the equivalent of our run function
		//paint start point
		if (startX!=-1&&startY!=-1&&!flagpaintStart){
			//System.out.println('a');
			repaint();
			flagpaintStart=true;
		}
		//paint end point
		if (endX!=-1&&endY!=-1&&!flagpaintEnd) {
			//System.out.println('b');
			repaint();
			flagpaintEnd = true;
		}
		
		//paint walls
		if (!flagFindPath) {
			//System.out.println('c');
			repaint();
		}
		repaint();
		/*
		if (!Djikstraflag) {
			System.out.println('d');
			repaint();
		}*/
		
		
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			char ch = e.getKeyChar();
			
			//if you want to start pathfinding
			if (ch=='s') {
				flagFindPath = true;
			}
			//if you want to run DFS algorithm
		}
	}
	//the mouse clicks are to determine the starting and ending point
	public class MyMouseAdapter extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			
			//if startpoint not chosen
			if (!flagStart) {
				startX = (int)e.getX()/UNIT_SIZE;
				startY = (int)e.getY()/UNIT_SIZE;
				flagStart = true;
			}
			
			//if endpoint not chosen
			else if (!flagEnd){
				endX = (int)e.getX()/UNIT_SIZE;
				endY = (int)e.getY()/UNIT_SIZE;
				flagEnd = true;
			}
		}
	}
	//mouse drag to create walls
	public class MyMouseMotionAdapter extends MouseMotionAdapter{
		@Override
		public void mouseDragged(MouseEvent e) {
			if (flagStart&&flagEnd&&!flagFindPath) {
				grid[(int)e.getX()/UNIT_SIZE][(int)e.getY()/UNIT_SIZE].blocked =true;
			}
		}
	}
}