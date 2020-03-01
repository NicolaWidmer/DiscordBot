package TicTacToe;

public class TicTacToe {
	protected String[][] grid;
	/*
	 * 1 2 3
	 * 4 5 6
	 * 7 8 9
	 */
	protected String char1;
	protected String char2;
	protected String filler;
	protected String cur;
	protected String winner;
	/*
	 * winner is eiter
	 * filler: if there is no winner
	 * "tie": if it is tied
	 * char1: if char1 won
	 * char2: if char2 won
	 */
	
	
	public TicTacToe(String char1,String char2,String filler){
		grid= new String[3][3];
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				grid[i][j]=filler;
			}
		}
		this.char1=char1;
		this.char2=char2;
		this.filler=filler;
		cur=char1;
		winner=filler;
	}
	
	public void makeMovePlayer(int place) throws IllegalMoveException{
		if(winner!=filler)throw new IllegalMoveException("The Game is over");
		else if(place<1||place>9)throw new IllegalMoveException("The fields have numbers between 1 and 9");
		else if(getField(place)!=filler)throw new IllegalMoveException("The fields is occupied");
		setField(place);
		winner=checkForWinner();
		cur=cur==char1?char2:char1;
	}
	
	protected String getField(int n) {
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++,n--) {
				if(n==1)return grid[i][j];
			}
		}
		return filler;
	}
	
	protected void setField(int n) {
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++,n--) {
				if(n==1)grid[i][j]=cur;
			}
		}
	}
	
	public String checkForWinner(){
		//horizontal
		for(int i=0;i<3;i++) {
			if(grid[i][0]==grid[i][1]&&grid[i][1]==grid[i][2]&&grid[i][0]!=filler)return grid[i][0];
		}
		//vertical
		for(int j=0;j<3;j++) {
			if(grid[0][j]==grid[1][j]&&grid[1][j]==grid[2][j]&&grid[0][j]!=filler)return grid[0][j];
		}
		//diagonal
		if(grid[0][0]==grid[1][1]&&grid[1][1]==grid[2][2]&&grid[1][1]!=filler)return grid[1][1];
		else if(grid[0][2]==grid[1][1]&&grid[1][1]==grid[2][0]&&grid[1][1]!=filler)return grid[1][1];
		
		if(winner==filler) {
			for(int i=0;i<3;i++) {
				for(int j=0;j<3;j++) {
					if(grid[i][j]==filler)return filler;
				}
			}
		}
		return "tie";
		
	}
	
	public String toString() {
		String ans="";
		if(winner!=filler) {
			if(winner=="tie") {
				ans+="The game is tied";
			}
			else {
				ans+="The winner is "+winner;
			}
			ans+="\n \n";
		}
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				ans+=grid[i][j];
			}
			ans+="\n";
		}
		return ans;
	}
	
	public String toStringDiscord() {
		String ans="";
		if(winner!=filler) {
			if(winner=="tie") {
				ans+="The game is tied";
			}
			else {
				ans+="The winner is "+winner;
			}
			ans+="\n \n";
		}
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				ans+=grid[i][j];
			}
			ans+="\n";
		}
		ans+="\n";
		ans+=":one::two::three:\n";
		ans+=":four::five::six:\n";
		ans+=":seven::eight::nine:\n";
		return ans;
	}
}
