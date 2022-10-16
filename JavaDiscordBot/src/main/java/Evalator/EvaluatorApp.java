package Evalator;

import java.util.Scanner;


public class EvaluatorApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExprEvaluator exprevaluator = new ExprEvaluator();

        while (true) {
            System.out.print(">> ");
            String function = scanner.nextLine();
            if (function.equals("exit"))
                break;
            try {
                
                System.out.println(exprevaluator.evaluate(function));
            } catch (EvaluationException e) {
                System.out.println("Invalid expression! " + e.getMessage());
            }
        }
    }
}
