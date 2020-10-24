package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is
	 * created and stored, even if it appears more than once in the expression. At
	 * this time, values for all variables and all array items are set to zero -
	 * they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr   The expression
	 * @param vars   The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		/**
		 * DO NOT create new vars and arrays - they are already created before being
		 * sent in to this method - you just need to fill them in.
		 **/
		String delims2 = " \t*+-/()]";
		StringTokenizer strToken = new StringTokenizer(expr, delims2);
		while (strToken.hasMoreTokens()) {
			String currentToken = strToken.nextToken();
			Variable checkVar = new Variable(currentToken);
			boolean checker = removeConstants(currentToken);
			if (checker == false) {
				if (currentToken.contains("[")) { // for the arrays
					String delimsTemp = "[";
					StringTokenizer tempTokens = new StringTokenizer(currentToken, delimsTemp);
					int counter = 0;
					int max = tempTokens.countTokens();
					if (max == 1) {
						String currentToken3 = tempTokens.nextToken();
						Array checkArr1 = new Array(currentToken3);
						checker = removeConstants(currentToken3);
						if (!arrays.contains(checkArr1) && checker == false) {
							arrays.add(checkArr1);
						}
					}
					while (tempTokens.hasMoreTokens()) {
						String currentToken2 = tempTokens.nextToken();
						Variable check = new Variable(currentToken2);
						Array checkArr = new Array(currentToken2);
						counter++;
						if (counter == max) {
							checker = removeConstants(currentToken2);
							if (checker == false && !vars.contains(check)) {
								Variable temp = new Variable(currentToken2);
								vars.add(temp);
							}
						} else if (!arrays.contains(checkArr)) {
							arrays.add(checkArr);
						}

					}

				} else if (!(currentToken.contains("[")) && !vars.contains(checkVar)) { // for the variables
					Variable temp = new Variable(currentToken);
					vars.add(temp);
				}
			}
		}

	}

	private static boolean removeConstants(String expr) {
		boolean checker = true;

		try {
			Integer.parseInt(expr);
		} catch (NumberFormatException e) {
			checker = false;
		}

		return checker;
	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input
	 * @param vars   The variables array list, previously populated by
	 *               makeVariableLists
	 * @param arrays The arrays array list - previously populated by
	 *               makeVariableLists
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars   The variables array list, with values for all variables in the
	 *               expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
		/** COMPLETE THIS METHOD **/
		// following line just a placeholder for compilation
		try {
			expr=expr.replaceAll(" ","");
		}
		catch(PatternSyntaxException e) {
			expr=expr;
		}
		Stack<String> stringStack = new Stack<String>();
		Stack<Float> numberStack = new Stack<Float>();
		float result = 0f;
		float tempResult = 0f;
		float secondExpr = 0f;
		float firstExpr = 0f;
		int parenCounter = 0;
		// initial data types
		for (int i = 0; i < expr.length(); i++) {
			char c = expr.charAt(i);
			String s = Character.toString(c);
			boolean checker = removeConstants(s);
			int doubVdg=i+1;
			if(checker==true) {
				char d=expr.charAt(doubVdg);// placeholding checker for double digit cases
				String dCheck=Character.toString(d);
			while(removeConstants(dCheck)==true) {
				s=expr.substring(i,doubVdg+1);
				doubVdg++;
				try{
					d=expr.charAt(doubVdg);// placeholding checker for double digit cases
				}catch(StringIndexOutOfBoundsException e) {
					break;
				}
				dCheck=Character.toString(d);
				
				}
			
			}
			if(doubVdg!=i+1) {
				i=doubVdg-1;
			}// code up to doubVdg accounts for the double digit number cases 
			// code below until next marker attempts to create the double variable case 
			if (s.equalsIgnoreCase("*") || s.equalsIgnoreCase("/") || s.equalsIgnoreCase("-")|| s.equalsIgnoreCase("+")) {
				if (s.equalsIgnoreCase("*") || s.equalsIgnoreCase("/")) {
					boolean precedenceCheck=precedence(stringStack,s);
					while(stringStack.isEmpty() == false && precedenceCheck==true) { 
							secondExpr = numberStack.pop(); // pops out the first number
							firstExpr = numberStack.pop(); // guarnteed 2nd number after operator
							String operator = stringStack.pop();
							tempResult = doMath(firstExpr, secondExpr, operator);
							numberStack.push(tempResult);
							precedenceCheck=precedence(stringStack,s);
						}
					stringStack.push(s);
				}
				 else if (stringStack.isEmpty() == true) {
					stringStack.push(s);
				} else if ((s.equalsIgnoreCase("-") || s.equalsIgnoreCase("+")) && stringStack.isEmpty() == false) {
					boolean precedenceCheck=precedence(stringStack,s);
					if (parenCounter != 0) {
						 while (precedenceCheck == true && !stringStack.peek().equalsIgnoreCase("(")) {
								secondExpr = numberStack.pop(); // pops out the first number
								firstExpr = numberStack.pop(); // guarnteed 2nd number after operator
								String operator = stringStack.pop();
								tempResult = doMath(firstExpr, secondExpr, operator);
								numberStack.push(tempResult);
								precedenceCheck=precedence(stringStack,s);
							}
						
						} while (precedenceCheck == true && !stringStack.isEmpty()) {
							secondExpr = numberStack.pop(); // pops out the first number
							firstExpr = numberStack.pop(); // guarnteed 2nd number after operator
							String operator = stringStack.pop();
							tempResult = doMath(firstExpr, secondExpr, operator);
							numberStack.push(tempResult);
							precedenceCheck=precedence(stringStack,s);
						}
						stringStack.push(s); 
					// this code is for evaluating for inside parentheses, tho i dont know how it helps
				}
					
					 else {
						secondExpr = numberStack.pop(); // pops out the first number
						firstExpr = numberStack.pop(); // guarnteed 2nd number after operator
						String operator = stringStack.pop();
						tempResult = doMath(firstExpr, secondExpr, operator);
						numberStack.push(tempResult);
						if (!stringStack.isEmpty()) {
							stringStack.push(s);
						}
					}
				}
			 else if (s.equalsIgnoreCase(")") || s.equalsIgnoreCase("(")) {
				if (s.equalsIgnoreCase(")")) {
					while (!stringStack.peek().equalsIgnoreCase("(")) {
						secondExpr = numberStack.pop(); // pops out the first number
						firstExpr = numberStack.pop(); // guarnteed 2nd number after operator
						String operator = stringStack.pop();
						tempResult = doMath(firstExpr, secondExpr, operator);
						numberStack.push(tempResult);
					}
					stringStack.pop();
					parenCounter--;
				} else {
					stringStack.push(s);
					parenCounter++;
				}
			} else if (checker == true) { // checks if the number is an integer, means this is an integer
				float p = Float.parseFloat(s);
				numberStack.push(p);
			} else if (checker == false) {
				Variable temp = new Variable(s);
				int index = vars.indexOf(temp);
				float tempNum = vars.get(index).value;
				numberStack.push(tempNum);
			}

		}

		if (!stringStack.isEmpty()) {
			result = terminator(numberStack, stringStack);
		} else {
			secondExpr = numberStack.pop(); // the 2nd thing in the expression because (3-2) 2 would pop first
			firstExpr = 0f; // the 1st thing in the expression because (3-2) 3 would pop second
			try {
				firstExpr = numberStack.pop();
			} catch (NoSuchElementException e) {
				result = secondExpr;
				return result;
			}
			String operator = stringStack.pop();
			result = doMath(firstExpr, secondExpr, operator);
		}
		return result;
	}

	private static float doMath(float firstExpr, float secondExpr, String operand) {
		float mathResult = 0f;
		if (operand.equalsIgnoreCase("*")) {
			mathResult = firstExpr * secondExpr;
		} else if (operand.equalsIgnoreCase("/")) {
			mathResult = firstExpr / secondExpr;
		} else if (operand.equalsIgnoreCase("+")) {
			mathResult = firstExpr + secondExpr;
		} else if (operand.equalsIgnoreCase("-")) {
			mathResult = firstExpr - secondExpr;
		}
		return mathResult;

	}

	private static float terminator(Stack<Float> numberStack, Stack<String> stringStack) {
		float endRes = 0f;
		if (stringStack.isEmpty() == true) {
			return numberStack.pop();
		}
		float secondExpr = numberStack.pop();
		float firstExpr = numberStack.pop();
		String operand = stringStack.pop();
		endRes += doMath(firstExpr, secondExpr, operand);
		numberStack.push(endRes);
		return terminator(numberStack, stringStack);
	}

	private static boolean precedence(Stack<String> stringStack, String operand) {
		String firstSign = operand;
		boolean precedence = false;
		String secondSign=null;
		try {
			secondSign=stringStack.peek();
		}
		catch(NoSuchElementException e) {
			return precedence;
		}
		if ((firstSign.equalsIgnoreCase("+") && secondSign.equalsIgnoreCase("-"))
				|| (firstSign.equalsIgnoreCase("-")) && secondSign.equalsIgnoreCase("+")) {
			precedence = true;
		} else if (firstSign.equalsIgnoreCase(secondSign)) { // works for (-)(-) and (/)(/)
			return true;
		} else if ((firstSign.equalsIgnoreCase("*") && secondSign.equalsIgnoreCase("/"))
				|| (firstSign.equalsIgnoreCase("/")) && secondSign.equalsIgnoreCase("*")) {
			return true;
		}
		else if ((firstSign.equalsIgnoreCase("+") && secondSign.equalsIgnoreCase("/"))
				|| (firstSign.equalsIgnoreCase("+")) && secondSign.equalsIgnoreCase("*")) {
			return true;
		}
		else if ((firstSign.equalsIgnoreCase("-") && secondSign.equalsIgnoreCase("/"))
				|| (firstSign.equalsIgnoreCase("-")) && secondSign.equalsIgnoreCase("*")) {
			return true;
		}
		// fill in the cases of other precedence and make sure implementatio is correct
		// problem is when -*+ bc need to do times then minus then plus but problems
		// occur with precedence implementation debug that
		return precedence;
	}

}
