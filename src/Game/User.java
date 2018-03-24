package Game;

import GameEngine.Location;

public class User {
	private final String image;
	private int row;
	private int col;
	private int lives;
	
	public User(int r, int c, int lives, String img) {
		row = r;
		col = c;
		this.lives = lives;
		image = img;
	}
	
	public String toString() {
		return image;
	}
	
	public String getImage() {
		return image;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setCol(int c) {
		col = c;
	}
	
	public void move(int a) {
		col += a;
	}
	
	public Location getLoc() {
		return new Location(row, col);
	}
	
	public int getLives() {
		return lives;
	}
	
	public void addLives(int x) {
		lives += x;
	}
}
