package TicTacToe;

import java.util.Scanner;

public class Main {

	public static void main(String[] args)throws IllegalMoveException {
		// TODO Auto-generated method stub
		Scanner sc= new Scanner(System.in);
		TicTacToeAi t= new TicTacToeAi("x","o"," ");
		while(true) {
			int cur=sc.nextInt();
			if(cur==-1)break;
			else if(cur==0)t.aiMove();
			else if(cur==10)t=new TicTacToeAi("x","o"," ");
			else t.makeMovePlayer(cur);
			System.out.println(t);
		}

	}

}
