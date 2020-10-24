package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode,
 * with fields for tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root = null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed in to the
	 * constructor and stored in the sc field of this object.
	 * 
	 * The root of the tree that is built is referenced by the root field of this
	 * object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		Stack<TagNode> tempStack = new Stack<TagNode>();
		root = new TagNode("html", null, null);
		sc.nextLine();
		String checkTag = sc.nextLine();
		tempStack.push(root);
		while (sc.hasNextLine() == true) {
			if (checkTag.charAt(0) == '<' && checkTag.charAt(1) != '/') {
				checkTag = checkTag.substring(1, checkTag.length() - 1);
				TagNode storeTempNode = new TagNode(checkTag, null, null);
				tempStack.push(storeTempNode);
			} else if (checkTag.length() > 1 && checkTag.charAt(1) == '/') {
				String popper = checkTag.substring(2, checkTag.length() - 1);
				while (!(tempStack.peek().tag.equalsIgnoreCase(popper))) {
					TagNode popTempNode = new TagNode(null, null, null);
					popTempNode = tempStack.pop();// stores the temp value from the stack
					if (tempStack.peek().tag.equalsIgnoreCase(popper)) { // first location of opening tag assigned as
																			// parent
						tempStack.peek().firstChild = popTempNode;
						break;
					}
					tempStack.peek().sibling = popTempNode; // concurrent tags are siblings
				}
			} else {
				TagNode storeTempNodeText = new TagNode(checkTag, null, null);
				tempStack.push(storeTempNodeText);
			}
			checkTag = sc.nextLine();
		}
		root.firstChild = tempStack.pop();
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		replaceHelper(oldTag, newTag, root);
	}

	private void replaceHelper(String oldTag, String newTag, TagNode replacer) {
		if (replacer == null) {
			return;
		} else if (replacer.tag.equalsIgnoreCase(oldTag)) {
			replacer.tag = newTag;
		}
		replaceHelper(oldTag, newTag, replacer.sibling);
		replaceHelper(oldTag, newTag, replacer.firstChild);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The
	 * boldface (b) tag appears directly under the td tag of every column of this
	 * row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		boldRowHelper(row, root);
	}

	private void boldRowHelper(int row, TagNode start) {
		if (start == null) {
			return;
		} else {
			if (start.tag.equalsIgnoreCase("table")) {
				TagNode rowChecker = start.firstChild;
				int counter = 0;
				while (counter != row) {
					if (rowChecker.tag.equalsIgnoreCase("tr")) {
						counter++;
					}
					if (row != counter) {
						rowChecker = rowChecker.sibling;
					}

				}
				TagNode tdChecker = rowChecker.firstChild;
				while (tdChecker != null) {
					TagNode bold = new TagNode("b", tdChecker.firstChild, null);
					tdChecker.firstChild = bold;
					tdChecker = tdChecker.sibling;
				}
			}
		}
		boldRowHelper(row, start.sibling);
		boldRowHelper(row, start.firstChild);
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b,
	 * all occurrences of the tag are removed. If the tag is ol or ul, then All
	 * occurrences of such a tag are removed from the tree, and, in addition, all
	 * the li tags immediately under the removed tag are converted to p tags.
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		removeTagHelper(tag, root);
	}

	private void removeTagHelper(String tag, TagNode start) {
		if (start == null) {
			return;

		}
		if (start.sibling != null) {
			if (start.sibling.tag.equalsIgnoreCase(tag)) { // looks for case if sibling is what needs to be removed
				if (tag.equalsIgnoreCase("p") || tag.equalsIgnoreCase("b") || tag.equalsIgnoreCase("em")) { // has issue
																											// with null
																											// pointer
																											// exceptions
																											// of
					if (start.sibling.firstChild != null) { // caller function
						TagNode endSibling = siblingFinder(start.sibling.firstChild);
						endSibling.sibling = start.sibling.sibling;
						start.sibling = start.sibling.firstChild;
					} else {
						start.sibling = start.sibling.sibling;
					}
				} else if (tag.equalsIgnoreCase("ol") || tag.equalsIgnoreCase("ul")) {
					TagNode ptr = start.sibling.firstChild;
					TagNode tempPtr = start.sibling.firstChild;
					TagNode continueTree = start.sibling.sibling;
					while (tempPtr.sibling != null) {
						tempPtr.tag = "p";
						tempPtr = tempPtr.sibling;
					}
					start.sibling = ptr;
					tempPtr.tag = "p";
					start = tempPtr;
					tempPtr.sibling = continueTree;
				}
			}
		}

		if (start.firstChild != null) {
			if (start.firstChild.tag.equalsIgnoreCase(tag)) { // case when the child of the current tag is the tag
				if (tag.equalsIgnoreCase("p") || tag.equalsIgnoreCase("b") || tag.equalsIgnoreCase("em")) {
					if (start.firstChild.firstChild != null) {
						TagNode endSibling = siblingFinder(start.firstChild.firstChild);
						endSibling.sibling = start.firstChild.sibling;
						start.firstChild = start.firstChild.firstChild;
					} else {
						start.firstChild = start.firstChild.sibling;
					}
				} else if (tag.equalsIgnoreCase("ol") || tag.equalsIgnoreCase("ul")) {
					TagNode ptr = start.firstChild.firstChild;
					TagNode tempPtr = start.firstChild.firstChild;
					TagNode continueTree = start.firstChild.sibling;
					while (tempPtr.sibling != null) {
						tempPtr.tag = "p";
						tempPtr = tempPtr.sibling;
					}
					tempPtr.tag = "p";
					start.firstChild = ptr;
					tempPtr.sibling = continueTree;
				}
			}
		}

		removeTagHelper(tag, start.sibling);
		removeTagHelper(tag, start.firstChild);

	}

	private TagNode siblingFinder(TagNode start) {
		while (start.sibling != null) {
			start = start.sibling;
		}
		return start;
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag  Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		recursiveAddHelper(word, tag, root);
	}

	private void recursiveAddHelper(String word, String tag, TagNode start) {
		if (start == null) {
			return;
			// corner cases(tagged word is first or last)

		}
		if (start.firstChild != null) {
			recursiveAddHelper(word, tag, start.firstChild);
		}
		if (start.sibling != null) {
			recursiveAddHelper(word, tag, start.sibling);
			if (start.sibling.tag.length() >= word.length()) {
				if (start.sibling.tag.indexOf(word) != -1) {
					TagNode ptr = new TagNode(null, null, null);
					ptr = start.sibling;
					TagNode addedNode = new TagNode(null, null, null);
					addedNode = addHelper(start.sibling.tag, tag, word);
					start.sibling = addedNode;
					start = ptr;
				}
			}
		}
		if (start.firstChild != null) {
			if (start.firstChild.tag.length() >= word.length()) {
				String tempTag = start.firstChild.tag.toLowerCase();
				if (tempTag.indexOf(word) != -1) {
					TagNode keepStart = start;
					TagNode siblingPtr = new TagNode(null, null, null);
					siblingPtr = start.firstChild.sibling;
					TagNode addedNode = new TagNode(null, null, null);
					addedNode = addHelper(start.firstChild.tag, tag, word);
					start.firstChild = addedNode;
					TagNode lastSibling = siblingFinder(addedNode);
					lastSibling.sibling = siblingPtr;
					start = lastSibling;
				}
			}
		}
	
	}

	private TagNode addHelper(String sentence, String tag, String word) {
		String stringHolder[] = new String[sentence.length()];
		stringHolder = sentence.split(" ");
		String beforeTransform = "";
		boolean canWeTag = false;
		TagNode treeBuild = new TagNode(null, null, null);
		TagNode move = treeBuild;
		for (int i = 0; i < stringHolder.length; i++) { // can't have it disregard the end
			canWeTag = isTaggable(word, stringHolder[i]);
			if (canWeTag == false) {
				if (beforeTransform.equalsIgnoreCase("")) {
					beforeTransform += stringHolder[i];
				} else {
					beforeTransform += " " + stringHolder[i];
				}
			} else {
				if (beforeTransform.equalsIgnoreCase("")) {
					move.tag = tag;
					move.firstChild = new TagNode(stringHolder[i], null, null);
					beforeTransform = "";
					
				} else if(move==treeBuild) {
					move.tag = beforeTransform;
					move.sibling = new TagNode(tag, null, null);
					move.sibling.firstChild = new TagNode(stringHolder[i], null, null);
					beforeTransform = "";
					move = move.sibling;
				}
				else {
					TagNode tempNode=new TagNode(stringHolder[i],null,null);
					move.sibling=new TagNode(beforeTransform,tempNode,null);
					move=move.sibling;
					beforeTransform="";
				}
			}
		}
		if (beforeTransform != "") {
			move.sibling = new TagNode(beforeTransform, null, null);
		}
		return treeBuild;
	}

	private boolean isTaggable(String word, String partOfSentence) {
		if (partOfSentence.length() >= word.length()) {
			int lengthWord = word.length();
			if (partOfSentence.length() > lengthWord) {
				if (partOfSentence.charAt(lengthWord) == '.' || partOfSentence.charAt(lengthWord) == ','
						|| partOfSentence.charAt(lengthWord) == '?' || partOfSentence.charAt(lengthWord) == '!'
						|| partOfSentence.charAt(lengthWord) == ';' || partOfSentence.charAt(lengthWord) == ':') {
						return true;
				}
			} else {
				if (partOfSentence.equalsIgnoreCase(word)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes new
	 * lines, so that when it is printed, it will be identical to the input file
	 * from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines.
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

	/**
	 * Prints the DOM tree.
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < level - 1; i++) {
				System.out.print("      ");
			}
			;
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level + 1);
			}
		}
	}
}
