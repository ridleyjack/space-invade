package Game;

public class Laser {
	  private int heatLevel; //stores how many counts until laser can be fired again  
	  private int ROF; // how long until laser can be fired after each shot, lower = faster
	  private final int initialROF;
	 
	  private final String image;
	  private final String sound;
	  private boolean muted;
	  
	  public Laser(int fireRate, String img, String s) {
		    heatLevel = 0;
		    ROF = fireRate;
		    initialROF = fireRate;
		    image = img;
		    sound = s;
	  }
	  
	  public boolean isOverheated() {
		  return heatLevel > 0;
	  }
	  
	  public String getImage() {
		  return image;
	  }
	  
	  public int GetROF() {
		  return ROF;
	  }
	  
	  public void Overheat(){
		  heatLevel = ROF;
	  }
	 	  
	  public void decROF () {
		  ROF --;
	  }
	  
	  public void decHeatLevel() {
		  heatLevel--;
	  }
	  
	  public void resetROF() {
		  ROF = initialROF;
	  }
	  
	  public void setROF(int in) {
		  ROF = in;
	  }
	  
	  public String getSound() {
		  return sound;
	  }
	  
	  public void toggleMute() {
		  muted = muted ? false: true;
	  }

	public boolean isMute() {
		return muted;
	}
	  	  
}
