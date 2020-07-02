package interfacePkg;

public class Effects {
	// path to the sound files
	  private final String SOUND_PATH = "../sounds/";

	  public void splash()
	  {
	    play("water_splash.mp3");
	  }

	  public void strike()
	  {
	    play("strike.mp3");
	  }

	  public void sinking()
	  {
	    play("sinking.mp3");
	  }
	  
	  public void summersong() {
		  play("summersong.mp3");
	  }
	  
	  public void closeSummersong() {
		  close("summersong.mp3");
	  }
	  
	  public void closeSplash()
	  {
		 close("water_splash.mp3");
	  }

	  public void closeStrike()
	  {
		 close("strike.mp3");
	  }
	  
	  private void play(String filename)
	  {
	    Sound sound = new Sound(SOUND_PATH + filename);
	    sound.playSound();
	  }
	  
	  private void close(String filename) {
		  Sound sound = new Sound(SOUND_PATH + filename);
		  sound.close();
	  }
}
