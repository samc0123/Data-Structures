package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {

	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage
	 * format of the polynomial is:
	 * 
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * 
	 * with the guarantee that degrees will be in descending order. For example:
	 * 
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * 
	 * which represents the polynomial:
	 * 
	 * <pre>
	 * 4 * x ^ 5 - 2 * x ^ 3 + 2 * x + 3
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients
	 *         and degrees read from scanner
	 */
	public static Node read(Scanner sc) throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}

	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input
	 * polynomials. The returned polynomial MUST have all new nodes. In other words,
	 * none of the nodes of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the
	 *         returned node is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		Node result = null;
		Node temp = null;
		Node rear = null;
		if (poly1 == null || poly2 == null) {
			while (poly1 == null && poly2 != null) {
				temp = new Node(poly2.term.coeff, poly2.term.degree, null);
				if (result == null) {
					result = temp;
					rear = temp;
				} else {
					rear.next = temp;
					rear = rear.next;
				}
				poly2 = poly2.next;
			}
			while (poly2 == null && poly1 != null) {
				temp = new Node(poly1.term.coeff, poly1.term.degree, null);
				if (result == null) {
					result = temp;
					rear = temp;
				} else {
					rear.next = temp;
					rear = rear.next;
				}
				poly1 = poly1.next;
			}
		} // handles the poly1+null or vice versa cases
		while (poly1 != null && poly2 != null) {
			if (poly1.term.degree > poly2.term.degree) {
				temp = new Node(poly2.term.coeff, poly2.term.degree, null);
				if (result == null) {
					result = temp;
					rear = temp;
				} else {
					rear.next = temp;
					rear = rear.next;
				}
				poly2 = poly2.next;
			} else if (poly1.term.degree < poly2.term.degree) {
				temp = new Node(poly1.term.coeff, poly1.term.degree, null);
				if (result == null) {
					result = temp;
					rear = temp;
				} else {
					rear.next = temp;
					rear = rear.next;
				}
				poly1 = poly1.next;
			}

			else if (poly1.term.degree == poly2.term.degree) {
				if (poly1.term.coeff + poly2.term.coeff != 0) {
					temp = new Node((poly1.term.coeff + poly2.term.coeff), poly2.term.degree, null);
					if (result == null) {
						result = temp;
						rear = temp;
					} else {
						rear.next = temp;
						rear = rear.next;
					}
				}
				poly2 = poly2.next;
				poly1 = poly1.next;
			}
			while (poly1 == null || poly2 == null) {
				if (poly1 == null && poly2 == null) {
					break;
				}
				if (poly2 == null) {
					temp = new Node(poly1.term.coeff, poly1.term.degree, null);
					if (result == null) {
						result = temp;
						rear = temp;
					} else {
						rear.next = temp;
						rear = rear.next;
					}
					poly1 = poly1.next;

				} else if (poly1 == null) {
					temp = new Node(poly2.term.coeff, poly2.term.degree, null);
					if (result == null) {
						result = temp;
						rear = temp;
					} else {
						rear.next = temp;
						rear = rear.next;
					}
					poly2 = poly2.next;

				}
			}

		}
		if (result == null) {
			temp = new Node(0, 0, null);
			result = temp;
		}

		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		return result;
	}

	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input
	 * polynomials. The returned polynomial MUST have all new nodes. In other words,
	 * none of the nodes of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the
	 *         returned node is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node temp = null;
		Node multResult = new Node(0, 0, null);
		Node rPtr = null;
		Node holder = null;
		Node moveTwo = poly2;
		while (poly1 != null) {
			while (poly2 != null) {
				temp = new Node(poly1.term.coeff * poly2.term.coeff, poly1.term.degree + poly2.term.degree, null);
				if (holder == null) {
					holder = temp;
					rPtr = temp;
				} else {
					rPtr.next = temp;
					rPtr = rPtr.next;
				}
				poly2 = poly2.next;
			}
			poly2 = moveTwo; // resets pointer in poly 2
			multResult = add(holder, multResult);
			holder = null;
			poly1 = poly1.next;
		}

		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		return multResult;
	}

	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x    Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float answer = 0;
		float temp = 0;
		while (poly != null) {
			temp = (float) (poly.term.coeff * Math.pow(x, poly.term.degree));
			answer += temp;
			poly = poly.next;
		}

		return answer;
	}

	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		}

		String retval = poly.term.toString();
		for (Node current = poly.next; current != null; current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}
}
