package Viergewinnt;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IllegalMoveException{
		// TODO Auto-generated method stub
		ViergewinntAi v= new ViergewinntAi("x","o"," ",6);
		Scanner sc= new Scanner(System.in);
		while(true) {
			int i=sc.nextInt();
			if(i==-1)break;
			else if(i==0) {
				v.aiMove();
			}
			else if(i==10) {
				v= new ViergewinntAi("x","o"," ",6);
			}
			else {
				v.insert(i);
			}
			System.out.println(v);
		}
	
	}
}
		
	


