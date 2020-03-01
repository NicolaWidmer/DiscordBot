package Evalator;
import java.util.*;

public class ExprEvaluator {
	static HashMap<String,Double> variables=new HashMap<String,Double>();
	static Set<String> mathvariables= new HashSet<String>();
	
	public ExprEvaluator(){
		variables.put("Pi", Math.PI);
		variables.put("e", Math.E);
		variables.put("pi", Math.PI);
		mathvariables=new HashSet<String>();
		for(String variable: variables.keySet()) {
			mathvariables.add(variable);
		}
		
	}
	
	
	public double evaluate(String expr) throws EvaluationException {
        // TODO!
		
		
    	Tokenizer tz= new Tokenizer(expr);
    	
    	double ans;
    	
    	if(tz.hasNewVariable()) {
    		String s=tz.nextNewVariable();
    		if(mathvariables.contains(s)) {
    			throw new EvaluationException("The variable "+s+" can't be overwritten");
    		}
    		ans=evaluateExpr(tz);
    		variables.put(s,ans);
    	}
    	
    	else {
    		ans=evaluateExpr(tz);
    		
    		if(tz.hasNext()) {
    			throw new EvaluationException("Syntax error: unexpected token ’"+tz.next()+"’, expected ’)’");
    		}
    		
    		return ans;
    	}
    	return ans;
    	
    }
    
	double evaluateAtom(Tokenizer tz) throws EvaluationException {
		
    	if(tz.hasNextNum()) {
    		return tz.nextNum();
    	}
    	
    	else if(tz.hasNextVar()) {
    		String s=tz.nextVar();
    		if(variables.keySet().contains(s)) {
    			return variables.get(s);
    		}
    		else {
    			throw new EvaluationException("Evaluations error: the variable "+s+" is undifined");
    		}
    		
    	}
    	
    	else {
    		
    		if(tz.hasNext()) {
    			throw new EvaluationException("Syntax error: unexpected token ’"+tz.next()+"’, expected ’nummber or variable’");
			}
			else {
				throw new EvaluationException("Syntax error: expected ’nummber or variable’ but was nothing");
			}
    	}
		
	}
    
    double evaluateTerm(Tokenizer tz) throws EvaluationException  {
	    	double ans;
    	
    	if(tz.hasNextOpen()) {
    		tz.nextOpen();											
    		ans=evaluateExpr(tz);
    		
    		if(!tz.hasNextClose()) {
    			if(tz.hasNext()) {
    				throw new EvaluationException("Syntax error: unexpected token ’"+tz.next()+"’, expected ’)’");
    			}
    			else {
    				throw new EvaluationException("Syntax error: expected ’)’ but was nothing");
    			}
    		}
    		
    		tz.nextClose();
    		
    	}
    	
    	else if (tz.hasNextFunc()){
    		String funktion=tz.nextFunc();
    		ans=funktion(funktion,evaluateExpr(tz));
    		if(!tz.hasNextClose()) {
    			if(tz.hasNext()) {
    				throw new EvaluationException("Syntax error: unexpected token ’"+tz.next()+"’, expected ’)’");
    			}
    			else {
    				throw new EvaluationException("Syntax error: expected ’)’ but was nothing");
    			}
    			
    		}
    		
    		tz.nextClose();
    	
    	}
    	else if(tz.hasNextOp()) {
    		String op=tz.nextOp();
    		if(op.equals("-")) {
    			return -evaluateExpr(tz);
    		}
    		else {
    			throw new EvaluationException("Syntax error: unexpected token ’"+tz.next()+"’, expected ’-’");
    		}
    		
    	}
    	else {
    		ans=evaluateAtom(tz);
    	}
    	return ans;
		
	}
    
    double evaluateExpr(Tokenizer tz) throws EvaluationException {
    	
    	double ans=evaluateTerm(tz);
    	
    	if(tz.hasNextOp()) {
    		
    		ans=operation(tz.nextOp(),evaluateTerm(tz),ans);
    		
    	}
    	
    	if(tz.hasNext()) {
    		if(!tz.hasNextClose()) {
    			throw new EvaluationException("Syntax error: unexpected token ’"+tz.next()+"’, expected ’Op’");
    		}
    	}
    	
		return ans;
	}
    
    double operation(String op, double term, double ans) {
    	if(op.equals("+")) {
    		ans+=term;
    	}
    	else if(op.equals("-")) {
    		ans-=term;
    		
    	}
    	else if(op.equals("*")) {
    		ans*=term;
    	}
    	else if(op.equals("/")) {
    		ans/=term;
    	}
    	else if(op.equals("^")) {
    		ans=Math.pow(ans, term);
    	}
    	return ans;
    }
    
    double funktion(String funktion,double term) throws EvaluationException {
    	if(funktion.equals("sin(")||funktion.equals("Sin(")) {
    		return Math.sin(term);
    	}
    	else if(funktion.equals("cos(")||funktion.equals("Cos(")) {
    		return Math.cos(term);
    	}
    	else if(funktion.equals("tan(")||funktion.equals("Tan(")) {
    		return Math.tan(term);
    	}
    	else if(funktion.equals("log(")) {
    		return Math.log(term);
    	}
    	else if(funktion.equals("sqrt(")) {
    		return Math.sqrt(term);
    	}
    	else if(funktion.equals("sinh(")||funktion.equals("Sinh(")) {
    		return Math.sinh(term);
    	}
    	else if(funktion.equals("cosh(")||funktion.equals("Cosh(")) {
    		return Math.cosh(term);
    	}
    	else if(funktion.equals("tanh(")||funktion.equals("Tanh(")) {
    		return Math.tanh(term);
    	}
    	else if(funktion.equals("abs(")||funktion.equals("Abs(")) {
    		return Math.abs(term);
    	}
    	else {
    		throw new EvaluationException(funktion+" isnt a real funktion");
    	}
    	
    }

}
