package Viergewinnt;

import Game.Game;
import Game.IllegalMoveException;

public class ViergewinntAi extends Viergewinnt implements Game {
	/*
	 * This is a Viergewinnt AI based on the minimax algorithm
	 */
	private int maxDepth;//amount of turns the ai considers in his move;
	
	public ViergewinntAi(String char1, String char2, String filler,int maxDepth) {
		super(char1, char2, filler);
		this.maxDepth=maxDepth;
	}
	
	public void aiMove()throws IllegalMoveException{
		//search for free column
		int maxj=0;
		double bestScore=-30000;
		for(int j=1;j<=7;j++) {
			if(grid[0][j-1]==filler) {
				tryColumn(j,cur);
				double score=minimax(false,1);
				remove(j);
				if(score>bestScore) {
					bestScore=score;
					maxj=j;
				}
			}
		}
		makeMove(maxj);
	}
	
	private void tryColumn(int column,String charToUse) {
		
		column--;
		for(int i=5;i>=-1;i--) {
			if(grid[i][column]==filler) {
				grid[i][column]=charToUse;
				break;
			}
		}
		
	}
	
	private double minimax(boolean isAisTurn,int depth) {
		/* returns 
		 * 1-2 if its a winning move for the AI
		 * 0-1 if its a move for tie 
		 * -1-0 if its a losing move for the AI
		 */
		if(checkForWinner(cur)) {
			return Math.random()+1;
		}
		else if(checkForWinner(notCur())) {
			return Math.random()-1;
		}
		else if(checkForFullBoard()) {
			return Math.random();
		}
		else if(depth==maxDepth) {
			return Math.random();
		}
		
		else if(isAisTurn) {
			double bestScore=-30000;
			for(int j=1;j<=7;j++) {
				if(grid[0][j-1]==filler) {
					tryColumn(j,cur);
					double score=minimax(false,depth+1);
					remove(j);
					if(score>bestScore) {
						bestScore=score;
					}
				}
			}
			return bestScore;
		}
		else{
			double bestScore=30000;
			for(int j=1;j<=7;j++) {
				if(grid[0][j-1]==filler) {
					tryColumn(j,notCur());
					double score=minimax(true,depth+1);
					remove(j);
					if(score<bestScore) {
						bestScore=score;
					}
				}
			}
			return bestScore;
		}
	}
	
	private void remove(int column) {
		column--;
		for(int i=0;i<6;i++) {
			if(grid[i][column]!=filler) {
				grid[i][column]=filler;
				break;
			}
		}
	}

}
