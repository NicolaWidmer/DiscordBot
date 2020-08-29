package Game;


public interface Game {

	public void aiMove()throws IllegalMoveException;
	public String toStringDiscord();
	public String toString();
	public boolean hasWinner();
	public String winner();
	public void makeMove(int field)throws IllegalMoveException;

}
