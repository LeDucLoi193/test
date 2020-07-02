package interfacePkg;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import singlePlayerPkg.SinglePlayerHandler;

import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JSeparator;
import javax.swing.JTextPane;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JButton;

public class BattleShip extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	// high score 
	private HighScoreGui hsgui;
    
    // computer level
	public static boolean easy = true;
	
	// effect
	public static boolean mute = false;
	
	//board
	private JList myEnemyList;
	private JList myBoardList;
	
	//board renderer
	private MyListRenderer myRenderer;
	private MyListRenderer myEnemyRenderer;
	
	//mouse move
	private int mHoveredJListIndex = -1;
	private ArrayList<Integer> mHoveredJListIndexList = new ArrayList<Integer>();
	private int orientation = MyDefines.ORIENTATION_HORIZONTAL;

	//board update
	private MapUpdateHandler myMapUpdate;
	private MapUpdateHandler myEnemyMapUpdate;
	
	//output panel
	private JTextPane outputTextField;
	private HTMLEditorKit outputKit;
	private HTMLDocument outputDoc;
	    
	//allocation panel
	private JButton btnRestartAllocation;
	
	// high score button
	private JButton btnHighScore;
	private JButton btnResetHighScore;
	
    public JButton btnConnect;
	public JButton btnStopServer;	

	private boolean PlayAgainsPC = true;
	
	//single player
	SinglePlayerHandler mySinglePlayerObj;
	
	//main frame
	private JFrame myFrame;
	
	//game over interface
	private GameOverGui myGOScreen;
	
	Sound sound = new Sound("../sounds/summersong.mp3");
	
	public BattleShip()
	{
		// play summer song
		sound.playSound();
		
		//start handlers
		startMyHandlers();
		
		//my board creation
		initializeMyBoard();
		
		//my enemy's board creation
		initializeMyEnemyBoard();
		
		//output panel creation
		initializeOutputPanel();

		//allocation panel creation
		initialiazeAllocationPanel();
		
		//connection panel creation
		initializeConnectionPanel();
		
		//rest of components
		initializeComponents();
	
		setVisible(true);
	}
	
//	private void backgroundMusic() {	
//		this.effects.summersong();
//	}

	// start 
	private void startMyHandlers() 
	{	
		this.hsgui = new HighScoreGui(this);
		
		//take application path
		File currDir = new File("");
				
		//main frame for all JOptionPane
		this.myFrame = this;
				
		//game over screen
		this.myGOScreen = new GameOverGui(currDir.getAbsolutePath()+"\\img\\", this);
				
		//board update handler
		this.myMapUpdate = new MapUpdateHandler(currDir.getAbsolutePath()+"\\img\\");
		this.myEnemyMapUpdate = new MapUpdateHandler(currDir.getAbsolutePath()+"\\img\\");

		//board renderer
		this.myRenderer = new MyListRenderer(this.myMapUpdate.getImageMap(), this.myMapUpdate.getImageMapHelp(), this.myMapUpdate.getApplicationPath(), this.myMapUpdate.getMatrixMap());
		this.myEnemyRenderer = new MyListRenderer(this.myEnemyMapUpdate.getImageMap(), this.myMapUpdate.getImageMapHelp(), this.myEnemyMapUpdate.getApplicationPath(), this.myEnemyMapUpdate.getMatrixMap());
	}
	
	// check position
	private boolean checkPosition(int index, ArrayList<Integer> test) {
		// boat is allocated
		if (test.get(index) != -1) {
			return false;
		}
		// horizontal
		if (orientation==MyDefines.ORIENTATION_HORIZONTAL) {
			
			if (myMapUpdate.getBoatType()==MyDefines.PATROL_BOAT && test.get(index+1) != -1) {
				return false;
			}
			else if (myMapUpdate.getBoatType()==MyDefines.DESTROYER && test.get(index+2) != -1) {
				return false;
			}
			else if (myMapUpdate.getBoatType()==MyDefines.SUBMARINE && test.get(index+2) != -1) {
				return false;
			}
			else if (myMapUpdate.getBoatType()==MyDefines.BATTLESHIP && test.get(index+3) != -1) {				
				return false;
			}
			else if (myMapUpdate.getBoatType()==MyDefines.AIRCRAFT_CARRIER && test.get(index+4) != -1) {
				return false;
			}
		}
		// vertical
		else {
			if (myMapUpdate.getBoatType()==MyDefines.PATROL_BOAT && test.get(index-11) != -1) {
				return false;
			}
			else if (myMapUpdate.getBoatType()==MyDefines.DESTROYER && (test.get(index-11) != -1 || test.get(index-22) != -1)) {
				return false;
			}
			else if (myMapUpdate.getBoatType()==MyDefines.SUBMARINE && (test.get(index-11) != -1 || test.get(index-22) != -1)) {
				return false;
			}
			else if (myMapUpdate.getBoatType()==MyDefines.BATTLESHIP && (test.get(index-11) != -1 || test.get(index-22) != -1 || test.get(index-33) != -1)) {				
				return false;
			}
			else if (myMapUpdate.getBoatType()==MyDefines.AIRCRAFT_CARRIER && (test.get(index-11) != -1 || test.get(index-22) != -1 || test.get(index-33) != -1 || test.get(index-44) != -1)) {
				return false;
			}	
		}
		
		return true;
	}
	
	private boolean checkPos(int index, ArrayList<Integer> test) {
		if (myMapUpdate.getReservedList().contains(index)) {
			JOptionPane.showMessageDialog(myFrame, "Ban khong duoc dat thuyen ra ngoai.");
			return false;
		}
		
		if (orientation==MyDefines.ORIENTATION_HORIZONTAL) {
			if (myMapUpdate.getReservedList().contains(index+1) || 
				((myMapUpdate.getReservedList().contains(index+2) && myMapUpdate.getBoatType()==MyDefines.DESTROYER)) ||
				((myMapUpdate.getReservedList().contains(index+2) && myMapUpdate.getBoatType()==MyDefines.SUBMARINE)) ||
				((myMapUpdate.getReservedList().contains(index+2) && myMapUpdate.getBoatType()==MyDefines.BATTLESHIP) || (myMapUpdate.getReservedList().contains(index+3) && myMapUpdate.getBoatType()==MyDefines.BATTLESHIP)) ||
				((myMapUpdate.getReservedList().contains(index+2) && myMapUpdate.getBoatType()==MyDefines.AIRCRAFT_CARRIER) || (myMapUpdate.getReservedList().contains(index+3) && myMapUpdate.getBoatType()==MyDefines.AIRCRAFT_CARRIER) || (myMapUpdate.getReservedList().contains(index+4) && myMapUpdate.getBoatType()==MyDefines.AIRCRAFT_CARRIER))) {
				 JOptionPane.showMessageDialog(myFrame, "Ban khong duoc dat thuyen ra ngoai.");
				 return false;
			}
			else if (!checkPosition(index, test)) {
				JOptionPane.showMessageDialog(myFrame, "Thuyen da duoc dat o day. Ban khong duoc dat thuyen.");
				return false;
			}
		}
		// vertical
		else if (orientation==MyDefines.ORIENTATION_VERTICAL){
			if (myMapUpdate.getReservedList().contains(index-11) || 
				((myMapUpdate.getReservedList().contains(index-22) && myMapUpdate.getBoatType()==MyDefines.DESTROYER)) ||
				((myMapUpdate.getReservedList().contains(index-22) && myMapUpdate.getBoatType()==MyDefines.SUBMARINE)) ||
				((myMapUpdate.getReservedList().contains(index-22) && myMapUpdate.getBoatType()==MyDefines.BATTLESHIP) || (myMapUpdate.getReservedList().contains(index-33) && myMapUpdate.getBoatType()==MyDefines.BATTLESHIP)) ||
				((myMapUpdate.getReservedList().contains(index-22) && myMapUpdate.getBoatType()==MyDefines.AIRCRAFT_CARRIER) || (myMapUpdate.getReservedList().contains(index-33) && myMapUpdate.getBoatType()==MyDefines.AIRCRAFT_CARRIER) || (myMapUpdate.getReservedList().contains(index-44) && myMapUpdate.getBoatType()==MyDefines.AIRCRAFT_CARRIER))) {
				 JOptionPane.showMessageDialog(myFrame, "Ban khong duoc dat thuyen ra ngoai.");	
				 return false;
			}
			else if (!checkPosition(index, test)) {
				JOptionPane.showMessageDialog(myFrame, "Thuyen da duoc dat o day. Ban khong duoc dat thuyen.");
				return false;
			}
		}
		return true;
	}
	
	// player's board
	private void initializeMyBoard() 
	{
		//my boar panel
		JPanel myShipsPanel = new JPanel();
		myShipsPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Your ships:", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		myShipsPanel.setBounds(10, 11, 349, 369);
		getContentPane().add(myShipsPanel);
		
		//my boar list
		this.myBoardList = new JList(MyDefines.NAME_LIST);
		myShipsPanel.add(this.myBoardList);
		this.myBoardList.setCellRenderer(this.myRenderer);
		this.myBoardList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.myBoardList.setVisibleRowCount(11);
		this.myBoardList.setFixedCellHeight(30);
		this.myBoardList.setFixedCellWidth(29);
		
		//allocate a ship or change orientation listener
		this.myBoardList.addMouseListener(new MouseListener() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				//if allocation is done, the listener returns because there is no ship to be allocated
				if (myMapUpdate.isSelectionDone())
				{
					return;
				}
				
				//left mouse button place the ship
				
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					// get mouse position
					Point p = new Point(e.getX(),e.getY());
					
					//get matrix index according to mouse position
					// check position is right or wrong
					int index = myBoardList.locationToIndex(p);
					ArrayList<Integer> test = myMapUpdate.getMatrixMap();
					
					//tried to update the map with selected position
					if (checkPos(index, test) && myMapUpdate.updateMap(orientation,mHoveredJListIndexList))
					{
						//repaint board
						myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
						myBoardList.repaint();
						
						//tells user which boat was allocated
						
						if (myMapUpdate.isSelectionDone())
						{
							writeOutputMessage(" - Ban da dat thuyen Aircraft Carrier!");
							writeOutputMessage(" - Choi thoi.");
						}
						else if ((myMapUpdate.getBoatType()-1) == MyDefines.PATROL_BOAT)
						{
							writeOutputMessage(" - Ban da dat thuyen Patrol Boat!");
						}
						else if ((myMapUpdate.getBoatType()-1) == MyDefines.DESTROYER)
						{
							writeOutputMessage(" - Ban da dat thuyen Submarine!");
						}
						else if ((myMapUpdate.getBoatType()-1) == MyDefines.SUBMARINE)
						{
							writeOutputMessage(" - Ban da dat thuyen Destroyer!");
						}
						else
						{
							writeOutputMessage(" - Ban da dat thuyen Battleship!");
						}
					}
					else
					{
						// nothing to do
					}
				}
				
				//right mouse button changes ship orientation
				else if (e.getButton()==MouseEvent.BUTTON3)
				{
					//horizontal to vertical
					if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
					{
						orientation = MyDefines.ORIENTATION_VERTICAL;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(mHoveredJListIndex);
						mHoveredJListIndexList.add(mHoveredJListIndex-11);
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					}
					//vertical to horizontal
					else
					{
						orientation = MyDefines.ORIENTATION_HORIZONTAL;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(mHoveredJListIndex);
						mHoveredJListIndexList.add(mHoveredJListIndex+1);
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					}
				}
				else
				{
					//nothing to do
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				// do nothing
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				// do nothing
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				// do nothing
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				// do nothing
			}
		});
		
		//highlight ship position listener after set ship
		this.myBoardList.addMouseMotionListener(new MouseAdapter() 
		{
			public void mouseMoved(MouseEvent me) 
			{
				//get mouse position
				Point p = new Point(me.getX(),me.getY());
				
				//get matrix index according to mouse position
				int index = myBoardList.locationToIndex(p);
				
				//check is allocation is done (in case of yes, the board does not need to be highlighted)
				if (myMapUpdate.isSelectionDone())
				{
					mHoveredJListIndex = index;
					mHoveredJListIndexList.clear();
					myBoardList.repaint();
				}
				
				//only perform something if current index is different than the last index
				if (index != mHoveredJListIndex) 
				{
					//if ship type is Patrol Boat, only two cells need to be highlighted
					if (myMapUpdate.getBoatType()==MyDefines.PATROL_BOAT)
					{
						mHoveredJListIndex = index;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(index);
						if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
						{
							mHoveredJListIndexList.add(index+1);
						}
						else
						{
							mHoveredJListIndexList.add(index-11);
						}
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					}
					//if ship type is Destroyer or Submarine, three cells need to be highlighted
					else if (myMapUpdate.getBoatType()==MyDefines.DESTROYER || myMapUpdate.getBoatType()==MyDefines.SUBMARINE)
					{
						mHoveredJListIndex = index;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(index);
						if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
						{
							mHoveredJListIndexList.add(index+1);
							mHoveredJListIndexList.add(index+2);
						}
						else
						{
							mHoveredJListIndexList.add(index-11);
							mHoveredJListIndexList.add(index-22);
						}
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					}
					//if ship type is Battleship, four cells need to be highlighted
					else if (myMapUpdate.getBoatType()==MyDefines.BATTLESHIP)
					{
						mHoveredJListIndex = index;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(index);
						if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
						{
							mHoveredJListIndexList.add(index+1);
							mHoveredJListIndexList.add(index+2);
							mHoveredJListIndexList.add(index+3);
						}
						else
						{
							mHoveredJListIndexList.add(index-11);
							mHoveredJListIndexList.add(index-22);
							mHoveredJListIndexList.add(index-33);
						}
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					}
					//if ship type is Aircraft carrier, five cells need to be highlighted
					else
					{
						mHoveredJListIndex = index;
						mHoveredJListIndexList.clear();
						mHoveredJListIndexList.add(index);
						if (orientation==MyDefines.ORIENTATION_HORIZONTAL)
						{
							mHoveredJListIndexList.add(index+1);
							mHoveredJListIndexList.add(index+2);
							mHoveredJListIndexList.add(index+3);
							mHoveredJListIndexList.add(index+4);
						}
						else
						{
							mHoveredJListIndexList.add(index-11);
							mHoveredJListIndexList.add(index-22);
							mHoveredJListIndexList.add(index-33);
							mHoveredJListIndexList.add(index-44);
						}
						myRenderer.setIndexHover(mHoveredJListIndexList);
						myBoardList.repaint();
					}
				}
			}
		});
	}

	// Enemy's board same to player's board
	private void initializeMyEnemyBoard() 
	{
		JPanel myEnemyPanel = new JPanel();
		myEnemyPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Your enemy:", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		myEnemyPanel.setBounds(369, 11, 349, 369);
		getContentPane().add(myEnemyPanel);
		
		//my enemy's board
		this.myEnemyList = new JList(MyDefines.NAME_LIST);
		myEnemyPanel.add(this.myEnemyList);
		this.myEnemyList.setCellRenderer(this.myEnemyRenderer);
		this.myEnemyList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.myEnemyList.setVisibleRowCount(11);
		this.myEnemyList.setFixedCellHeight(30);
		this.myEnemyList.setFixedCellWidth(29);
		
		//play listener
		this.myEnemyList.addMouseListener(new MouseListener() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				//left mouse button clicked
				if (e.getButton()==MouseEvent.BUTTON1)
				{
					//get mouse position
					Point p = new Point(e.getX(),e.getY());
					
					//convert mouse position in matrix index 
					int index = myBoardList.locationToIndex(p);
					
					//check if connection is on (if not connected cannot play)
					if (!btnConnect.isEnabled())
					{
						if (PlayAgainsPC)
						{
							if (myMapUpdate.getMyTurn())
							{
								//check if this position is legal
								if (myEnemyMapUpdate.isPositionLegal(index))
								{
									//check if the position was already played
									if (myMapUpdate.positionPlayed(index))
									{
										JOptionPane.showMessageDialog(myFrame, "Vi tri nay da duoc ban. Hay ban vao cho khac.");
									}
									else
									{
										//inform
										writeOutputMessage(" - Vi tri bom cua ban: "+myEnemyMapUpdate.getLine(index)+myEnemyMapUpdate.getColumn(index));
										
										//legal position and not yet played, it informs the computer where it was played
										mySinglePlayerObj.indexPlayed(index);
									
										//add to played list
										myMapUpdate.addPlayedPosition(index);
									
										//set my turn false
										myMapUpdate.setMyTurn(false);
									}
								}
								//not a legal position
								else
								{
									JOptionPane.showMessageDialog(myFrame, "Ban vao trong ay ban oi, ai lai ban ra ngoai ban do the.");
								}
							}
						}
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				// do nothing
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				// do nothing
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				// do nothing
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				// do nothing
			}
		});
		
	}
	
	// Output messages about fire position
	private void initializeOutputPanel()
	{
		this.outputKit = new HTMLEditorKit();
		this.outputDoc = new HTMLDocument();
		
		this.outputTextField = new JTextPane();
		this.outputTextField.setContentType("text/html");
		this.outputTextField.setEditorKit(this.outputKit);
		this.outputTextField.setDocument(this.outputDoc);
		
		JScrollPane outputScrollPane = new JScrollPane(this.outputTextField);
		
		this.outputTextField.setFont(this.outputTextField.getFont().deriveFont(this.outputTextField.getFont().getStyle() | Font.BOLD));
		this.outputTextField.setEditable(false);
		this.outputTextField.setBounds(10, 21, 688, 141);
		writeOutputMessage("<b> --- Game cac ban eiii </b>");
		writeOutputMessage("--- Dat thuyen vao vi tri ban thich di ban eiii...");
		
		JPanel outputPanel = new JPanel();
		outputPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Output:", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		outputPanel.setBounds(10, 404, 969, 207);
		outputPanel.setLayout(new BorderLayout());
		outputPanel.add(outputScrollPane,BorderLayout.CENTER);
		getContentPane().add(outputPanel);
	}
	
	
	// Clear allocation if wrong click or player want to allocate ship again
	private void initialiazeAllocationPanel()
	{
		JPanel shipsAllocationPanel = new JPanel();
		shipsAllocationPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Ships allocation:", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		shipsAllocationPanel.setBounds(740, 11, 244, 107);
		getContentPane().add(shipsAllocationPanel);
		shipsAllocationPanel.setLayout(null);
	    
	    this.btnRestartAllocation = new JButton("Clear allocation");
	    this.btnRestartAllocation.setBounds(50, 45, 150, 23);
	    shipsAllocationPanel.add(this.btnRestartAllocation);
	    
	    //restart map (sets all to water and clear positions)
	    //clear own map and enemy's map
	    this.btnRestartAllocation.addActionListener(new ActionListener() 
	    {
	    	public void actionPerformed(ActionEvent arg0) 
	    	{
	    		writeOutputMessage(" - Khoi tao lai dat thuyen.");
	    		myMapUpdate.restartAllocation();
	    		myRenderer.updateMatrix(myMapUpdate.getMatrixMap());
				myBoardList.repaint();
				
				myEnemyMapUpdate.restartAllocation();
	    		myEnemyRenderer.updateMatrix(myEnemyMapUpdate.getMatrixMap());
				myEnemyList.repaint();
	    	}
	    });
	}
	
	// Menu: mute, unmute, high score, level play
	private void initializeConnectionPanel()
	{
		JPanel connectionPanel = new JPanel();
	    connectionPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Style:", TitledBorder.LEFT, TitledBorder.TOP, null, null));
	    connectionPanel.setBounds(740, 129, 244, 178);
	    getContentPane().add(connectionPanel);
	    connectionPanel.setLayout(null);

	    JRadioButton rdbtnPlayAgainstComputer = new JRadioButton("Play against PC");
	    rdbtnPlayAgainstComputer.setSelected(true);
	    rdbtnPlayAgainstComputer.setBounds(6, 17, 150, 23);
	    connectionPanel.add(rdbtnPlayAgainstComputer);
	    
	    JRadioButton rdbtnEasyLevel = new JRadioButton("Easy level");
	    rdbtnEasyLevel.setSelected(true);
	    rdbtnEasyLevel.setBounds(6, 37, 100, 23);
	    connectionPanel.add(rdbtnEasyLevel);
	    
	    JRadioButton rdbtnHardLevel = new JRadioButton("Hard level");
	    rdbtnHardLevel.setSelected(false);
	    rdbtnHardLevel.setBounds(6, 57, 100, 23);
	    connectionPanel.add(rdbtnHardLevel);
	    
	    JRadioButton rdbtnUnmute = new JRadioButton("Unmute");
	    rdbtnUnmute.setSelected(true);
	    rdbtnUnmute.setBounds(6, 77, 100, 23);
	    connectionPanel.add(rdbtnUnmute);
	    
	    JRadioButton rdbtnMute = new JRadioButton("Mute");
	    rdbtnMute.setSelected(false);
	    rdbtnMute.setBounds(6, 97, 100, 23);
	    connectionPanel.add(rdbtnMute);
	    
	    ButtonGroup group1 = new ButtonGroup();
	    group1.add(rdbtnPlayAgainstComputer);
	    
	    final ButtonGroup group2 = new ButtonGroup();
	    group2.add(rdbtnEasyLevel);
	    group2.add(rdbtnHardLevel);
//	    final Enumeration<AbstractButton> enumeration = group2.getElements();
	    
	    ButtonGroup group3 = new ButtonGroup();
	    group3.add(rdbtnMute);
	    group3.add(rdbtnUnmute);
	    
	    this.btnHighScore = new JButton("HighScore");
	    this.btnHighScore.setBounds(120, 47, 109, 23);
	    connectionPanel.add(this.btnHighScore);
	    
	    this.btnResetHighScore = new JButton("Reset HighScore");
	    this.btnResetHighScore.setBounds(120, 77, 109, 23);
	    connectionPanel.add(this.btnResetHighScore);
	    
	    btnHighScore.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				hsgui.ShowHighScore();
			}    	
	    });
	    
	    btnResetHighScore.addActionListener(new ActionListener() {
	    	@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
	    		hsgui.removeLabel();
	    		hsgui.getHighScoreComponent().readFromFile();
				for (int i = hsgui.getHighScoreComponent().getScoreSave().size(); i > 0; --i) {
					hsgui.getHighScoreComponent().getScoreSave().remove(i-1);
				}
				try {
					hsgui.getHighScoreComponent().writeToFile();
					hsgui.ShowHighScore();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    });
	    
	    rdbtnHardLevel.addActionListener(new ActionListener() {
	    	@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				easy = false;
			}
	    });
	    
	    rdbtnEasyLevel.addActionListener(new ActionListener() {
	    	@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				easy = true;
			}
	    });
	    
	    rdbtnMute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sound.close();
				mute = true;
			}  	 
	    });
	    
	    rdbtnUnmute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				mute = false;
				sound.run();
			}  	
	    });
	    
	    rdbtnPlayAgainstComputer.addActionListener(this);
	    
	    this.btnConnect = new JButton("Start Game");
	    
	    //start game handler
	    this.btnConnect.addActionListener(new ActionListener() 
	    {
	    	public void actionPerformed(ActionEvent arg0) 
	    	{
	    		//check if is playing against computer
	    		if (PlayAgainsPC)
	    		{
	    			if (myMapUpdate.isSelectionDone())
    				{
	    				//create my server thread
	    				mySinglePlayerObj = new SinglePlayerHandler(getMyClassObject());
    				
	    				//allocate computer ships
	    				mySinglePlayerObj.AllocateShips();
	    				
    					//start thread
	    				mySinglePlayerObj.start();
    				
    					//disable buttons
    					btnConnect.setEnabled(false);
    					btnStopServer.setEnabled(true);
    					btnRestartAllocation.setEnabled(false);
    					
    					final Enumeration<AbstractButton> enumeration = group2.getElements();
    					
    					while (enumeration.hasMoreElements()) {
    					    enumeration.nextElement().setEnabled(false);
    					}
    					
    					myMapUpdate.setMyTurn(true);
    				}
    				else
    				{
    					JOptionPane.showMessageDialog(myFrame, "Ban phai dat het thuyen len ban do.");
    				}
	    		}
	    	}
	    });
	    
	    this.btnConnect.setBounds(6, 142, 109, 23);
	    connectionPanel.add(btnConnect);

	    this.btnStopServer = new JButton("Stop Game");
	    this.btnStopServer.setEnabled(false);
	    
	    //stop game
	    this.btnStopServer.addActionListener(new ActionListener() 
	    {
	    	public void actionPerformed(ActionEvent arg0) 
	    	{
	    		//check if is playing against computer
	    		if (PlayAgainsPC)
	    		{
	    			StopSingleGame();
	    			final Enumeration<AbstractButton> enumeration = group2.getElements();
					
					while (enumeration.hasMoreElements()) {
					    enumeration.nextElement().setEnabled(true);
					}
	    		}
	    		
	    		myMapUpdate.setMyTurn(false);
	    	}
	    });
	    this.btnStopServer.setBounds(123, 142, 109, 23);
	    connectionPanel.add(this.btnStopServer);
	}
	
	private void initializeComponents()
	{
		JSeparator separatorH = new JSeparator();
		separatorH.setBounds(10, 391, 719, 2);
		getContentPane().add(separatorH);
		
		JSeparator separatorV = new JSeparator();
		separatorV.setOrientation(SwingConstants.VERTICAL);
		separatorV.setBounds(728, 11, 2, 600);
		getContentPane().add(separatorV);
	    
		getContentPane().setLayout(null);
		
		setTitle("BattleShip");
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.myMapUpdate.getApplicationPath()+"\\icon.png"));
		setResizable(false);
		setSize(1000, 650);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//radio buttons listener
	public void actionPerformed(ActionEvent e) 
	{
		this.btnConnect.setText("Start Game");
		this.btnStopServer.setText("Stop Game");
		this.PlayAgainsPC = true;
	}
	
	//write messages to the output panel
	public void writeOutputMessage(String msg)
	{
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		
		try 
		{
			this.outputKit.insertHTML(this.outputDoc, this.outputDoc.getLength(), dateFormat.format(date)+msg, 0, 0, null);
		}
		catch (BadLocationException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.outputTextField.setCaretPosition(this.outputDoc.getLength());
	}
	
	//repaint my board
	public void repaintMyBoard()
	{
		this.myBoardList.repaint();
	}
	
	//repaint enemy's board
	public void repaintMyEnemyBoard()
	{
		this.myEnemyList.repaint();
	}
	
	//restart allocations
	public void restartAllocations()
	{
		this.myMapUpdate.restartAllocation();
		this.myRenderer.updateMatrix(this.myMapUpdate.getMatrixMap());
		this.myBoardList.repaint();
		
		this.myEnemyMapUpdate.restartAllocation();
		this.myEnemyRenderer.updateMatrix(this.myEnemyMapUpdate.getMatrixMap());
		this.myEnemyList.repaint();
	}
	
	//stop single player game
	public void StopSingleGame()
	{
		this.mySinglePlayerObj.SetMyTurn(false);
		this.mySinglePlayerObj.StopGame();
		
		this.restartAllocations();
		
		this.btnConnect.setEnabled(true);
		this.btnStopServer.setEnabled(false);
		this.myMapUpdate.setMyTurn(false);
		btnRestartAllocation.setEnabled(true);
	}
	
	//return BattleShip class object
	public BattleShip getMyClassObject()
	{
		return this;
	}
	
	//return my MapUpdateHandler object
	public MapUpdateHandler getMyMapUpdate()
	{
		return this.myMapUpdate;
	}
	
	//return enemy's MapUpdateHandler object
	public MapUpdateHandler getMyEnemyMapUpdate()
	{
		return this.myEnemyMapUpdate;
	}
	
	//return if is playing against PC
	public boolean isSinglePlayer()
	{
		return this.PlayAgainsPC;
	}
	
	//return game over interface
	public GameOverGui getGameOverGui()
	{
		return this.myGOScreen;
	}
	
	// start new game
	public static void main(String[] args) 
	{
		new BattleShip();
	}
}
