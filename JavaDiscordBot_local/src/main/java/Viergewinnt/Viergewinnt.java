package Viergewinnt;

public class Viergewinnt {
	protected String [][] grid;
	protected String char1;
	protected String char2;
	protected String filler;
	protected String cur;
	protected boolean hasWinner;
	protected boolean isTie;
	protected String winner;
	
	
	public Viergewinnt(String char1,String char2,String filler){
		grid= new String[6][7];
		for(int i=0;i<6;i++) {
			for(int j=0;j<7;j++) {
				grid[i][j]=filler;
			}
		}
		this.char1=char1;
		this.char2=char2;
		this.filler=filler;
		cur=char1;
	}
	
	public void insert(int column) throws IllegalMoveException{
		if(hasWinner) {
			throw new IllegalMoveException("There is already a winner");
		}
		else if(isTie) {
			throw new IllegalMoveException("The game is tied");
		}
		else if(column<1||7<column) {
			throw new IllegalMoveException("The column must be between 1 and 7");
		}
		column--;
		for(int i=5;i>=-1;i--) {
			if(i==-1)throw new IllegalMoveException("The column "+(column+1)+" is full");
			if(grid[i][column]==filler) {
				grid[i][column]=cur;
				break;
			}
		}
		if(checkForWinner(cur)) {
			hasWinner=true;
			winner=cur;
		}
		else if(checkForFullBoard()) {
			isTie=true;
		}
		
		cur=notCur();
	}
	
	protected boolean checkForFullBoard() {
		for(int i=0;i<6;i++) {
			for(int j=0;j<7;j++) {
				if(grid[i][j]==filler) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean hasWinner() {
		return hasWinner;
	}
	
	
	protected boolean checkForWinner(String checkfor) {
		if(checkRow(checkfor)) {
			return true;
		}
		else if(checkColumn(checkfor)) {
			return true;
		}
		else if(checkDiagonalLeft(checkfor)) {
			return true;
		}
		else if(checkDiagonalRigth(checkfor)) {
			return true;
		}
		return false;
	}
	
	
	/* 	checks for 4 in a row
	   	e.g.
		
		.......
		.......
		.......
		.......
		.xxxx..
		....... 	*/
	 
	protected boolean checkRow(String checkfor) {
		for(int i=0;i<6;i++) {
			int anzahl=0;
			for(int j=0;j<7;j++) {
				if(grid[i][j]==checkfor) {
					if(++anzahl==4)return true;
				}
				else {
					anzahl=0;
				}
			}
		}
		
		return false;
	}
	
	/* 	checks for 4 in a collum
		e.g
		
		.......
		.......
		x......
		x......
		x......
		x......
		
	 	*/
	
	protected boolean checkColumn(String checkfor) {
		for(int j=0;j<7;j++) {
			int anzahl=0;
			for(int i=0;i<6;i++) {
				if(grid[i][j]==checkfor) {
					if(++anzahl==4)return true;
				}
				else {
					anzahl=0;
				}
			}
		}
		return false;
	}
	
	/* 	checks for 4 in a left Diagonal row
	  	e.g
	  
	  	.......
		.......
		..x....
		...x...
		....x..
		.....x. 
	 */
	protected boolean checkDiagonalLeft(String checkfor) {
		//checks for diagonals which start on the left side
		for(int i=0;i<6;i++) {
			int anzahl=0;
			for(int j=0;i+j<6;j++) {
				if(grid[i+j][j]==checkfor) {
					if(++anzahl==4)return true;
				}
				else {
					anzahl=0;
				}
			}
		}
		//checks for diagonals which start on the top
		for(int j=0;j<7;j++) {
			int anzahl=0;
			for(int i=0;i<6&&j+i<7;i++) {
				if(grid[i][j+i]==checkfor) {
					if(++anzahl==4)return true;
				}
				else {
					anzahl=0;
				}
				
			}
		}
		
		return false;
	}
	/* 	checks for 4 in a right Diagonal row
  		e.g
  
  		.......
		.......
		.....x.
		....x..
		...x...
		..x.... 
	 */
	protected boolean checkDiagonalRigth(String checkfor) {
		//checks for diagonals which start on the left side
				for(int i=0;i<6;i++) {
					int anzahl=0;
					for(int j=0;j<=i;j++) {
						if(grid[i-j][j]==checkfor) {
							if(++anzahl==4)return true;
						}
						else {
							anzahl=0;
						}
					}
				}
		//checks for diagonals which start on the bottom
				for(int j=0;j<7;j++) {
					int anzahl=0;
					for(int i=5;i>=0&&j-i+5<7&&j-i+5>=0;i--) {
						if(grid[i][j-i+5]==checkfor) {
							if(++anzahl==4)return true;
						}
						else {
							anzahl=0;
						}
					}
				}		
		
		return false;
	}
	
	protected String notCur() {
		return cur==char1?char2:char1;
	}
	
	public String toString() {
		String ans="";
		if(hasWinner) {
			ans+=winner+" is the winner \n \n";
		}
		else if(isTie) {
			ans+="The Game is tied \n \n";
		}
		for(int i=0;i<6;i++) {
			for(int j=0;j<7;j++) {
				ans+=grid[i][j];
			}
			ans+="\n";
		}
		
		return ans;
	}
	
	public String toStringDiscord() {
		String ans=toString();
		ans+=":one::two::three::four::five::six::seven:";
		return ans;
	}
	

}
