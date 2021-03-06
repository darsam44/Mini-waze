package gui;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONObject;

import MYdataStructure.DGraph;
import MYdataStructure.edge_data;
import MYdataStructure.graph;
import MYdataStructure.node_data;
import Server.Game_Server;
import Server.game_service;
import gameClient.Fruit;
import gameClient.Robot;
import gameClient.*;
import utils.Point3D;
import utils.StdDrawGame;

/**
 * This class represents the gui graph that we initalize , every MyGameGui have :
    public  graph graph; - graph object
	Hashtable<Point3D, Fruit> fruits; - hashtable for the fruits
	Hashtable<Integer, Robot> robots; - hashtable for the robots
	LinkedList<Point3D> points = new LinkedList<Point3D>(); - linked list of points
	double X_min = Integer.MAX_VALUE; - the x min value in the frame
	double X_max = Integer.MIN_VALUE; - the x max value in the frame
	double Y_min = Integer.MAX_VALUE; - the y min value in the frame
	double Y_max = Integer.MIN_VALUE; - the y max value in the frame
 * 
 * @author USER
 *
 */
public class MyGameGUI  {
	public static final String jdbcUrl="jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser="student";
	public static final String jdbcUserPassword="OOP2020student";
	game_service game;
	public  graph graph;
	public Hashtable<Point3D, Fruit> fruits;
	public Hashtable<Integer, Robot> robots;
	private static final long serialVersionUID = 6128157318970002904L;
	public LinkedList<Point3D> points = new LinkedList<Point3D>();
	public double X_min = Integer.MAX_VALUE;
	public double X_max = Integer.MIN_VALUE;
	public double Y_min = Integer.MAX_VALUE;
	public double Y_max = Integer.MIN_VALUE;
	public double x;
	public double y;
	public boolean isRobot = false;
	public KML_Logger KML;

	/**
	 * This function is a default constructor
	 */
	public MyGameGUI(){
		this.graph =null;
		initGUI();

	}

	/**
	 * This function is constructor that get graph g and init it to gui
	 * @param g
	 */
	public MyGameGUI(graph g)
	{
		this.graph=g;
		initGUI();
	}

	/**
	 * This function is constructor that gets graph g, fruit f, roobot r 
	 * @param g
	 * @param f
	 * @param r
	 */
	public MyGameGUI(graph g , Fruit f , Robot r)
	{
		this.graph=g;
		initGUI();
	}


	/**
	 * This function is meant to show the results of the id that the user entered, 
	 * in this function we connect the server and using sql get the info that we need 
	 * we print the user the level he is in it, the best score in all levels, numbers of game
	 * he play in the sever and his place according to the teame play in the server.
	 */
	public void Results () {
		StdDrawGame.clear();
		String r="";
		Hashtable<Integer, Integer> id_best = new Hashtable<Integer, Integer>();
		ArrayList<Integer> score = new ArrayList<Integer>();
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int [] need_moves = {290,580,0,580,0,500,0,0,0,580,0,580,0,580,0,0,290,0,0,580,290,0,0,1140};
		int [] need_grade = {145,450,0,720,0,570,0,0,0,510,0,1050,0,310,0,0,235,0,0,250,200,0,0,1000};
		int [] place = new int [24]; 
		try {
			for (int i = 0; i < 24; i++) {
				score.add(i, 0);
				moves.add(i, 0);
			}
			int count =0;
			int moves_level=0 , score_level = 0,  level=0;
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String s ="";
			s = JOptionPane.showInputDialog(
					null, "Enter ID:");
			int id_my = Integer.parseInt(s);
			String allCustomersQuery = "SELECT * FROM oop.Logs where userID = "+id_my+";";
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			while(resultSet.next())
			{
				count++;
				int id = resultSet.getInt("levelID");
				score_level = resultSet.getInt("score");
				moves_level = resultSet.getInt("moves");
				if (score_level >= need_grade[id]) {
					if (score.get(id) < score_level && moves_level <= need_moves[id] ) {
						score.remove(id);
						score.add(id, score_level);
						moves.remove(id);
						moves.add(id, moves_level);
					}
				}
			}

			for (int i = 0; i < 24; i++) {
				String allCustomersQuery2 = "SELECT * FROM oop.Logs where levelID = "+i+" and score > "+score.get(i)+" and moves <= "+need_moves[i]+";";
				ResultSet resultSet2 = statement.executeQuery(allCustomersQuery2);
				while (resultSet2.next()) {
					id_best.put(resultSet2.getInt("userID"), resultSet2.getInt("score"));
				}
				place[i] = id_best.size()+1;
				id_best.clear();
			}
			double y = 0;

			Font font = new Font("Calibri", Font.BOLD, 16);
			StdDrawGame.setFont(font);
			StdDrawGame.setPenColor(Color.BLACK);

			StdDrawGame.textLeft(X_min, Y_max-y, "The best games of id: " + id_my );


			for (int i = 0; i < 24; i++) {
				if ( score.get(i) != 0 ) {
					y+= 35;
					StdDrawGame.setPenColor(Color.DARK_GRAY);
					r= "Level:"+i;
					StdDrawGame.textLeft(X_min, Y_max-y, r);
					StdDrawGame.setPenColor(Color.blue);
					r= " Score: " + score.get(i);
					StdDrawGame.textLeft(X_min+80, Y_max-y, r);
					StdDrawGame.setPenColor(Color.RED);
					r= " Moves:" + moves.get(i)  ;
					StdDrawGame.textLeft(X_min+210, Y_max-y, r);
					StdDrawGame.setPenColor(Color.MAGENTA);
					r=" Your place at this level is: " + place[i]+ "\n";
					StdDrawGame.textLeft(X_min+330, Y_max-y, r);
					level=i;
				}
			}
			if ( level != 23) {
				level++;
				while (need_moves[level] ==0) {
					level++;
				}
			}
			StdDrawGame.setPenColor(Color.BLACK);
			y+=35;
			StdDrawGame.textLeft(X_min, Y_max-y, "Numbers of games you play in the server : "+count);
			y+= 35;
			StdDrawGame.textLeft(X_min, Y_max-y, "You are in level: "+ level);
			StdDrawGame.show();

			resultSet.close();
			statement.close();		
			connection.close();		
		}
		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	This function is playing the automatic game by open a object of Automatic
 	(this class include all the algoritms for automatic game) and set her this (my game gui).
	All the algorithm that we used is detailed in the Automatic section	 */
	
	public void Playautomatic() {

		JFrame input = new JFrame();
		String s ="";
		s = JOptionPane.showInputDialog(
				null, "Please enter a Scenario number between 0-23");

		int sen=Integer.parseInt(s);
		if(sen<0 || sen>23) {
			JOptionPane.showMessageDialog(input, "The number that you entered isn't a Scenario number " );
		}
		else {
			this.game = Game_Server.getServer(sen); // you have [0,23] games
			Automatic Auto= new Automatic (this);//call for autogame for this graph and string 
			Auto.Playautomatic(game, sen);
		}

	}

	/**
	This function get the point that the user 
	clicked on it the frame from the StdDraw lib that we made (g_StdDraw ).	
	 * @param x
	 * @param y
	 */
	public void setXY(double x , double y) {
		this.x=x;
		this.y=y;
	}

	/**
	 * This function is playing the manual game by open a object of Manual 
	 * (this class include all the algoritms for manual game) and set her this (my game gui).
	 * All the algorithms that we used is detailed in the Manual section.
	 * @param game
	 */

	public void PlayManual() {
		try {
			JFrame input = new JFrame();
			String s ="";
			s = JOptionPane.showInputDialog(
					null, "Please enter a Scenario number between 0-23");
			int sen=Integer.parseInt(s);
			if(sen<0 || sen>23) {
				JOptionPane.showMessageDialog(input, "The number that you entered isn't a Scenario number " );
			}
			else {
				this.game = Game_Server.getServer(sen); // you have [0,23] games
				String g = game.getGraph();
				DGraph gg = new DGraph();
				gg.init(g);
				graph =gg;
				String info = game.toString();

				Manual playMan=new Manual(this);
				playMan.PlayManual(game, sen);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * this function will paint the graph 
	 */
	public void paint() {
		StdDrawGame.clear();
		if(this.graph !=null) {
			Collection <node_data> Nodes = this.graph.getV();
			for (node_data node_data : Nodes) {
				Point3D p = node_data.getLocation();
				StdDrawGame.setPenColor(Color.ORANGE);
				StdDrawGame.filledCircle(p.x(), p.y(), 0.0001); //nodes in orange
				StdDrawGame.setPenColor(Color.BLACK);

				StdDrawGame.text(p.x(), p.y()+(p.y()*0.000004) , (Integer.toString(node_data.getKey())));
				Collection<edge_data> Edge = this.graph.getE(node_data.getKey());
				for (edge_data edge_data : Edge) {
					if (edge_data.getTag() ==100) {
						edge_data.setTag(0);
						StdDrawGame.setPenColor(Color.RED); 
					}
					else {
						StdDrawGame.setPenColor(Color.BLUE);
					}
					node_data dest = graph.getNode(edge_data.getDest());
					Point3D p2 = dest.getLocation();
					if (p2 != null) {
						StdDrawGame.line(p.x(), p.y(), p2.x(), p2.y());
						StdDrawGame.setPenColor(Color.MAGENTA);
						double x_place =((((((p.x()+p2.x())/2)+p2.x())/2)+p2.x())/2);
						double y_place = ((((((p.y()+p2.y())/2)+p2.y())/2)+p2.y())/2);
						StdDrawGame.filledCircle(x_place, y_place, 0.0001);
						StdDrawGame.setPenColor(Color.BLUE);
						//cut the number to only 1 digit after the point
						String toShort=Double.toString(edge_data.getWeight());
						int i=0;
						while(i<toShort.length()) {
							if(toShort.charAt(i)=='.') {
								toShort=toShort.substring(0, i+2);
							}
							i++;
						}
						StdDrawGame.text(x_place, y_place-(y_place*0.000004),toShort );
					}	
				}
			}
			painStatus();
			paintFruit();
			paintRobot();
			StdDrawGame.show();
		}
	}

	/**
	 * This function will paint the fruit on the screen by pass over the 
	 * fruit location .
	 */
	public void paintFruit() {
		if ( this.fruits != null ) {
			Set <Point3D> set = fruits.keySet();
			for (Point3D p3 : set) {
				Fruit fru = fruits.get(p3);
				if (fru.getType() == -1) {
					StdDrawGame.setPenColor(Color.RED);
					StdDrawGame.picture(p3.x(), p3.y(), "data/boy.jpg", 0.0006, 0.0004);
				}
				else {
					StdDrawGame.setPenColor(Color.CYAN);
					StdDrawGame.picture(p3.x(), p3.y(), "data/girl.jpg", 0.0006, 0.0004);
				}
			}
		}
	}

	/**
	 * This function paint the robots by stdDraw
	 */
	public void paintRobot() {
		if(this.robots!=null) {
			Set <Integer> set = robots.keySet();
			for (Integer robot : set) {
				Robot robo = robots.get(robot);
				Point3D p = robo.getPoint3D();
				StdDrawGame.setPenColor(Color.GREEN);
				StdDrawGame.picture(p.x(), p.y(), "data/car2.jpg", 0.0008, 0.0004);
			}
		}
	}

	/**
	 * this function print the time left, score and moves 
	 */
	public void painStatus() {
		try {
			StdDrawGame.setPenColor(Color.BLACK);
			String info = game.toString();
			JSONObject obj = new JSONObject(info);
			JSONObject GAME =obj.getJSONObject("GameServer");
			int grade = GAME.getInt("grade");
			StdDrawGame.text(X_max, Y_max,"grade: " +""+grade);
			StdDrawGame.text(X_max, Y_max-(Y_max*0.000009),"time: " +""+game.timeToEnd()/1000);
			StdDrawGame.text(X_max, Y_max-(Y_max*0.000018),"move: " +""+GAME.getInt("moves"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function is init the gui 
	 */
	public void initGUI() {
		StdDrawGame.setCanvasSize(800, 600);
		StdDrawGame.enableDoubleBuffering();
		if (graph != null) {
			X_min = Integer.MAX_VALUE;
			X_max = Integer.MIN_VALUE;
			Y_min = Integer.MAX_VALUE;
			Y_max = Integer.MIN_VALUE;
		}
		else {
			X_min =0;
			Y_min = 0;
			X_max = 800;
			Y_max = 600;
		}

		// rescale the coordinate system
		if (graph != null) {
			KML = new KML_Logger(graph);
			KML.kml_Graph();
			Collection<node_data> nodes=graph.getV();   
			for(node_data node:nodes) {
				if(node.getLocation().x()>X_max) {
					X_max=(node.getLocation().x());
				}
				if(node.getLocation().x()<X_min) {
					X_min=(node.getLocation().x());
				}
				if(node.getLocation().y()>Y_max) {
					Y_max=(node.getLocation().y());
				}
				if(node.getLocation().y()<Y_min) {
					Y_min=(node.getLocation().y());
				}

			}	
		}
		StdDrawGame.setXscale(X_min, X_max);
		StdDrawGame.setYscale(Y_min, Y_max);
		StdDrawGame.setGuiGraph(this);
		paint();
	}







}
