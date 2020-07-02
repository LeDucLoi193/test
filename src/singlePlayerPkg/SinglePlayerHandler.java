package singlePlayerPkg;

import java.io.IOException;

import javax.swing.JOptionPane;

import interfacePkg.BattleShip;
import interfacePkg.HighScore;
import interfacePkg.MapUpdateHandler;
import interfacePkg.MyDefines;

public class SinglePlayerHandler extends Thread
{
	private BattleShip gui;
	private boolean myTurn;
	private boolean myBreak;
	private PCGameHandler myGameHandler;
	private MapUpdateHandler pcMap;
	public static HighScore userScore = new HighScore();
	
	public SinglePlayerHandler(BattleShip gui)
	{
		this.gui = gui;
		this.myBreak = false;
		this.myTurn = false;
		this.pcMap = new MapUpdateHandler(this.gui.getMyMapUpdate().getApplicationPath());
		this.myGameHandler = new PCGameHandler(this.gui, this.pcMap);
	}

	//allocate computer ships
	public void AllocateShips()
	{
		int timeOutCounter = 0;
		
		this.pcMap.restartAllocation();
		
		//it tries to allocate until find a valid position for all boats or the timeout happens
		while (!this.pcMap.randomAllocation())
		{
			try 
			{
				//wait 100 ms to perform a better random position (random is using as seed System.currentTimeMillis())
				//and has a counter up to 5 s for timeout
				Thread.sleep(MyDefines.DELAY_100MS);
			}
			catch ( java.lang.InterruptedException ie) 
			{
				JOptionPane.showMessageDialog(this.gui, "Loi. Nhan lai nut \"Start Game\" ");
				this.pcMap.restartAllocation();
				return;
			}
			timeOutCounter += 100;
			//timeout of 5 seconds to stop trying
			if (timeOutCounter>=MyDefines.DELAY_5S)
			{
				JOptionPane.showMessageDialog(this.gui, "Qua thoi gian. Nhan nut \"Start Game\" ");
				this.pcMap.restartAllocation();
				return;
			}
			this.pcMap.restartAllocation();
		}
		
		// Computer is ready
//		this.gui.writeOutputMessage(" - Computer: Minh dat thuyen xong roi ban eii.");
		this.gui.writeOutputMessage(" - Computer: Turn cua ban do. Let's go.");
	}
	
	//thread that waits computer turn to play
	public void run()
	{
		// Enter the main loop
		while(!this.myBreak)
		{
			//wait until my turn
			if (this.myTurn)
			{
				if (!BattleShip.easy) {
					this.myGameHandler.playHard();
					SetMyTurn(false);
				}
				else {
					this.myGameHandler.playEasy();
					SetMyTurn(false);
				}
			}
			
			// Sleep
			try
			{
				Thread.sleep(MyDefines.DELAY_200MS);
			}
			catch(InterruptedException e)
			{
				this.gui.writeOutputMessage(" - Thread error. Server was interrupted!");
			}
		}
	}
	
	//stop game
	public void StopGame()
	{
		this.myBreak = true;
		this.gui.writeOutputMessage(" - Computer: Minh se doi ban.");
	}

	//set computer turn
	public void SetMyTurn(boolean value)
	{
		this.myTurn = value;
	}
	
	//handles user shot, game over and set turn to the computer
	public void indexPlayed(int index)
	{
		int shot = userScore.getTotalShot();
		userScore.setTotalShot(++shot);
		
		if (this.pcMap.hitSomething(index))
		{
			int	score = userScore.getTotalScore();
			userScore.setTotalScore(score += MyDefines.HIT_SCORE);
			
			this.gui.getMyEnemyMapUpdate().setEnemyHit(index, true);
			this.gui.writeOutputMessage(" - Ban trung roi, co len.");
		}
		else
		{
			this.gui.getMyEnemyMapUpdate().setEnemyHit(index, false);
			this.gui.writeOutputMessage(" - Truot roi.");
		}
		
		this.pcMap.updatePosition(index);
		if (this.pcMap.isGameOver())
		{
			int	score = userScore.getTotalScore();
			userScore.setTotalScore(score += 170);
			try {
				userScore.readFromFile();
				userScore.getScoreSave().add(userScore);
				userScore.writeToFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userScore.setTotalScore(0);
			userScore.setTotalShot(0);
			userScore.removeScoreSave();
			
			SetMyTurn(false);
			this.gui.getGameOverGui().ShowGameOver(1);
			this.gui.writeOutputMessage(" - Chuc mung. Ban da gianh chien thang.");
		}
		
		SetMyTurn(true);
	}
}
