package TicTacToe;

import Game.Game;
import Game.IllegalMoveException;

public class TicTacToeAi extends TicTacToe implements Game{
	/*
	 * This is a TicTacToe AI based on the minimax algorithm
	 */

	public TicTacToeAi(String char1, String char2, String filler) {
		super(char1, char2, filler);
	}
	public void aiMove() throws IllegalMoveException{
		//search free spot
		if(winner!=filler)throw new IllegalMoveException("The Game is over");
		int besti=0,bestj=0;
		double bestScore=-1000;
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				if(grid[i][j]==filler) {
					grid[i][j]=cur;
					double score=minimax(false);
					if(score>bestScore) {
						bestScore=score;
						besti=i;
						bestj=j;
					}
					grid[i][j]=filler;
				}
			}
		}
		grid[besti][bestj]=cur;
		winner=checkForWinner();
		cur=cur==char1?char2:char1;
	}
	
	private double minimax(boolean isAIsTurn) {
		/* returns 
		 * 1-2 if its a winning move for the AI
		 * 0-1 if its a move for tie 
		 * -1-0 if its a losing move for the AI
		 */
		String winner=checkForWinner();
		if(winner!=filler) {
			if(winner=="tie")return Math.random();
			else if(winner==cur)return 1+Math.random();
			else return Math.random()-1;
		}
		if(!isAIsTurn) {
			double bestScore=1000;
			for(int i=0;i<3;i++) {
				for(int j=0;j<3;j++) {
					if(grid[i][j]==filler) {
						grid[i][j]=cur==char1?char2:char1;
						double score=minimax(true);
						if(score<bestScore) {
							bestScore=score;
						}
						grid[i][j]=filler;
					}
				}
			}
			return bestScore;
		}
		else {
			double bestScore=-1000;
			for(int i=0;i<3;i++) {
				for(int j=0;j<3;j++) {
					if(grid[i][j]==filler) {
						grid[i][j]=cur;
						double score=minimax(false);
						if(score>bestScore) {
							bestScore=score;
						}
						grid[i][j]=filler;
					}
				}
			}
			return bestScore;
			
		}
	}
	

}
