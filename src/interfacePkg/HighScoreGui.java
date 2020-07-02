package interfacePkg;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// display high score
public class HighScoreGui extends JFrame{
	private static final long serialVersionUID = 1L;

	private HighScore hs = new HighScore();
	private BattleShip gui;
	private JLabel scoreLabel;
	private JLabel shotLabel;
	private JLabel scoreTitleLabel;
	private JLabel shotTitleLabel;
	
	public HighScoreGui(BattleShip myGui)
	{
		this.gui = myGui;	
		initializeComponents();		
	}
	
	public HighScore getHighScoreComponent() {
		return hs;
	}
	
	private void initializeComponents(){		
		this.setSize(360, 500);
	    this.setVisible(false);
	    this.setResizable(false);
	    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    this.setTitle("High Score");
	    
	    this.scoreTitleLabel = new JLabel("Score");
		this.scoreTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.scoreTitleLabel.setFont(new Font("Arial", Font.PLAIN, 22));
		this.scoreTitleLabel.setBounds(40, 35, 100, 33);
		getContentPane().add(this.scoreTitleLabel);
		
		this.shotTitleLabel = new JLabel("Shot");
		this.shotTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.shotTitleLabel.setFont(new Font("Arial", Font.PLAIN, 22));
		this.shotTitleLabel.setBounds(180, 35, 100, 33);
		getContentPane().add(this.shotTitleLabel);
	}
	
	public void removeLabel() {
		getContentPane().removeAll();
		getContentPane().repaint();
	}
	
	public void ShowHighScore()
	{
		hs.readFromFile();
		for (int i = 0; i < hs.getScoreSave().size(); ++i) {
			String score = String.valueOf(hs.getScoreSave().get(i).getTotalScore());
			this.scoreLabel = new JLabel(score);
			this.scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
			this.scoreLabel.setBounds(40, 65 + 27*i, 100, 33);
			getContentPane().add(this.scoreLabel);
			
			String shot = String.valueOf(hs.getScoreSave().get(i).getTotalShot());
			this.shotLabel = new JLabel(shot);
			this.shotLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.shotLabel.setFont(new Font("Arial", Font.PLAIN, 20));
			this.shotLabel.setBounds(180, 65 + 27*i, 100, 33);
			getContentPane().add(this.shotLabel);
		}
		
		JPanel panel = new JPanel();
		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("Arial", Font.PLAIN, 18));
		btnClose.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				dispose();
			}
		});
		 btnClose.setBounds(100, 360, 160, 50);
		 panel.setLayout(null);
		 panel.add(btnClose);
		 getContentPane().add(panel);
		
		setLocationRelativeTo(this.gui);
		this.setVisible(true);
	}
}
