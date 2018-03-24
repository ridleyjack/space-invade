/*
 * James Riley Jackson 
 * 300200062
 * 
 * Controls: 
 * 	movement - Left/Right Arrows
 * 	laser - space bar
 *  mute/unmute laser - m
 *  Debugging cheat - p
 * 
 * Bonuses (ie gets): 
 * 	- improve laser rate of fire
 * 	- 50% chance to spawn every 4 seconds
 * 
 * Aliens:
 * 	- Spawn in waves, each wave has 10 additional enemies
 *	- Alien spawn numbers reset every 8 waves; their speed increased, and the laser reset
 *
 * Score: 
 *	- +1 per Alien Destroyed
 *	- +10 * waveNumber for wave reached
 *  - +5 per bonus collected
 * 
 */

package Game;
import GameEngine.Grid;
import GameEngine.Location;

public class Game
{
  private Grid grid;
  private User user;
  private Laser laser;
  private int msElapsed;
  
  private boolean scrollRight; // whether the aliens are shifting right or left
  private int enemyCount; //stores how many aliens left on screen

  private int waveNumber;
  private int score;
  private int enemySpeed; // lower = faster
  
  private final String bonusImg;
  private final String alienImg;
  
  public Game()
  {
    grid = new Grid(10, 14);
    msElapsed = 0;
    updateTitle();
    
    user = new User(grid.getNumRows() - 1, grid.getNumCols()/2, 5, "user.gif");
    grid.setImage(user.getLoc(), user.getImage());    
    laser = new Laser (8, "laser.gif", "laser.wav"); //default 8
    
    alienImg = "alien.gif";
    bonusImg = "bonus.gif";
    
    updateMsgBar();

    enemyCount = 0;
    scrollRight = true;
    score = 0;
    enemySpeed = 500;
    		
    waveNumber = 0;
    nextWave();
  }
  
  public void play()
  {
    while (!isGameOver())
    {
      grid.pause(100);
      spawnRandomObjects();  
      scrollGame();
      handleKeyPress();
      updateMsgBar();
      updateTitle();
      msElapsed += 100;
    }
  }
  
  public void handleKeyPress()
  {
	  int key = grid.checkLastKeyPressed();
	  
	  if (key == 37 && user.getCol() > 0) {
		  grid.setImage(user.getLoc(), null);
		  user.move(-1);
		  handleCollision(user.getLoc());
		  grid.setImage(user.getLoc(), user.getImage());		  
	  }	  
	  if (key == 39 && user.getCol() < grid.getNumCols() - 1) { 
		  grid.setImage(user.getLoc(), null);
		  user.move(1);
		  handleCollision(user.getLoc());
		  grid.setImage(user.getLoc(), user.getImage());
	  }
	  if (laser.isOverheated()) laser.decHeatLevel();
	  else if (grid.checkSpacebar()) {
			  Location dest = new Location(user.getRow() - 1, user.getCol());
			  if (!laser.isMute()) grid.playSound(laser.getSound());
			  if (grid.getImage(dest) == alienImg) { //if there is an alien where the laser spawns
				  handleLaserCollison(dest);
			  }
			  else {
			  grid.setImage(dest, laser.getImage());
			  laser.Overheat();
			  }
	  }
	  //CheatMode
	  if (key == 80) {
		  laser.setROF(0);
		  killAliens();		  
	  }
	  //toggle laser sound
	  if (key == 77){
		  laser.toggleMute();
	  }

  }
  
  public void spawnRandomObjects()
  {
	  if (msElapsed % 4000 == 0 && (int)(Math.random()*2) == 1) {
		  int col = 0;
		  
		  do {
			  col = (int)(Math.random()*grid.getNumCols());
		  } while(col == user.getCol());
		  if (grid.getImage(new Location(user.getRow(), col)) == null);
		  grid.setImage(new Location(user.getRow(), col), bonusImg);
	  }
	 
	  
  }
  
  public void scrollGame()
  {
	  if (msElapsed  % enemySpeed == 0) scrollEnemies();
		 
	  if (msElapsed % 100 == 0) scrollLaser();
  }
  
  private void scrollEnemies() {
	  //Move Enemies
	  if (enemyAtEdge()) { // Scroll Down
		  scrollRight = !scrollRight;		  
		  for (int row = grid.getNumRows() - 1; row >= 0; row--) {
			  for (int col = 0; col < grid.getNumCols(); col++) {
				  Location loc = new Location(row, col);
				  String image = grid.getImage(loc);
				  if (image == alienImg) {
					  Location dest = new Location(loc.getRow() + 1, loc.getCol());
					  if (dest.getRow() >= grid.getNumRows()){ //off map
						  user.addLives(-1);
						  enemyCount--;
					  }
					  else if(grid.getImage(dest) == laser.getImage()) { //hit laser
						  handleLaserCollison(dest);
					  }
					  else if(dest.getRow() == user.getRow() && dest.getCol() == user.getCol()) { //hit player
						  	handleCollision(loc);						  
					  }
					  else { //move to empty destination
						  grid.setImage(dest, image);				  
					  }
					  grid.setImage(loc, null);
				  }
			  }
		  }
	  }
	  else { //Scroll horizontally
		  if (scrollRight) { // Scroll Right
			  for (int row = grid.getNumRows() - 1; row >= 0; row--) {
				  for (int col = grid.getNumCols() - 1; col >= 0; col--) {
					  Location loc = new Location(row, col);
					  String image = grid.getImage(loc);
					  if (image ==alienImg) {
						  Location dest = new Location(loc.getRow(), loc.getCol() + 1);
						  if (dest.getRow() == user.getRow() && dest.getCol() == user.getCol()) { //hit player
							  handleCollision(loc);	
						  }
						  else if(grid.getImage(dest) == laser.getImage()) { //hit laser
							  handleLaserCollison(dest);
						  }
						  else {//move to empty destination
							  grid.setImage(dest, image);
						  }
						  grid.setImage(loc, null);	
					  }
				  }
			  }	
		  } 
		  else { // Scroll Left
			  for (int row = grid.getNumRows() - 1; row >= 0; row--) {
				  for (int col = 0; col < grid.getNumCols(); col++) {
					  Location loc = new Location(row, col);
					  String image = grid.getImage(loc);
					  if (image == alienImg) {
						  Location dest = new Location(loc.getRow(), loc.getCol() - 1);						  
						  if (dest.getRow() == user.getRow() && dest.getCol() == user.getCol()) {//hit player
							  handleCollision(loc);
						  }
						  else if(grid.getImage(dest) == laser.getImage()) { //hit laser
							  handleLaserCollison(dest);
						  }
						  else {//move to empty destination
							  grid.setImage(dest, image);
						  }
						  grid.setImage(loc, null);	
					  }
				  }			  	
			  }   
		  }
	  }
  }
  
  private void scrollLaser() {
	  //Move laser
	  for (int col = 0; col < grid.getNumCols(); col++)
		  if (grid.getImage(new Location(0,col)) == laser.getImage()) grid.setImage(new Location(0,col), null);
	  
	  for (int row = 1; row < grid.getNumRows(); row++) {
		  for (int col = 0; col < grid.getNumCols(); col++){
			  Location loc = new Location(row, col);
			  if (grid.getImage(loc) == laser.getImage()) { 
				  Location dest = new Location(row - 1, col);				  
				  if (grid.getImage(dest) == alienImg ) { // hit enemy
					  handleLaserCollison(dest);
				  }
				  else grid.setImage(dest, laser.getImage()); // move to empty space
				  grid.setImage(loc, null);
			  }
		  }
	  }
  }
  
  public void handleCollision(Location loc)
  {
	  String image = grid.getImage(loc);
	  
	  if (image == alienImg) {
		  user.addLives(-1);
		  enemyCount--;
		  updateMsgBar();
	  }
	  if (image == bonusImg && laser.GetROF() > 0) {
		 laser.decROF();
		 grid.playSound("bonus.wav");
		 score += 5;
	  }
  }
  
  public int getScore()
  {
    return score;
  }
  
  public void updateTitle()
  {
    grid.setTitle("Game Score:  " + getScore());
  }
  
  public boolean isGameOver()
  {
	  if (user.getLives() <= 0) {
		  updateMsgBar("Game Over, You Lose");
		  return true;
	  }
	  if (enemyCount <= 0) {
		  nextWave();
	  }
    return false;
  }
  
  public static void test()
  {
    Game game = new Game();
    game.play();
  }
  
  public static void main(String[] args)
  {
    test();
  }
  
  private boolean enemyAtEdge() {
	  int col = (scrollRight ? grid.getNumCols() - 1 : 0);
	   
	  for (int row = 0; row < grid.getNumRows(); row++) 
		  if(grid.getImage(new Location(row, col)) == alienImg) return true;
	  
	  return false;
  }
  
  private void spawnEnemies(int amount) {
	  //clear left over lasers
	  for (int row = 0; row < grid.getNumRows() - 1; row++) {
		  for (int col = 0; col < grid.getNumCols(); col++) {	  
			 Location loc = new Location(row,col);
			 if (grid.getImage(loc) !=null) grid.setImage(loc, null);
		  }
	  }
	  //spawn the enemies
	  int remaining = amount;
	  enemyCount = amount;
	  for (int row = 0; row <= amount/(grid.getNumCols()-4); row++ ){
		  int spawnNum = Math.min(remaining, grid.getNumCols() - 4 );
		  for(int col = 0; col < spawnNum; col++) {
			  grid.setImage(new Location(row, col +2), alienImg);			  
		  }
		  remaining -= spawnNum;
	  }
	  
  }
  
  private void updateMsgBar() {
	  grid.setMessage("Wave: " + waveNumber + "  Lives: " + user.getLives() + "  EnemyLeft: " + enemyCount);
  }
  
  private void updateMsgBar(String msg) {
	  grid.setMessage("Wave: " + waveNumber + "  Lives: " + user.getLives() + "  EnemyLeft: " + enemyCount + "  " + msg);
  }  
  
  private void nextWave() {	  
	  waveNumber++;
	  score += (waveNumber - 1) * 10;
	  
	  if ((waveNumber - 1) % 8 == 0 && waveNumber != 1) {
		  enemySpeed -= 100;
		  laser.resetROF();
		  updateMsgBar("Well Done! Enemy Speed Increased and Laser Reset");
		  grid.pause(4000);
	  }
	  updateMsgBar("Enemy Inbound!");
	  grid.playSound("round.wav");
	  grid.pause(1000);	
	  
	  int spawnCount = 10 * (waveNumber - ((waveNumber-1) / 8)*8);
	 
	  spawnEnemies(spawnCount);
  }
  
  private void killAliens() {
	  for (int row = 0; row < grid.getNumRows(); row++) {
		  for (int col = 0; col < grid.getNumCols(); col++) {	  
			 Location loc = new Location(row, col);
			 if (grid.getImage(loc) == alienImg) grid.setImage(loc, null);
		  }
	  }
	  enemyCount = 0;
  }
  
  private void handleLaserCollison(Location dest) {
		 grid.setImage(dest, null);	
		 enemyCount--;
		 score++;
  }
}