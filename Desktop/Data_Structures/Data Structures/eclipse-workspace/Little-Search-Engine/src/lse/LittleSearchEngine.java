package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		if (docFile == null) {
			throw new FileNotFoundException();
		}
		Scanner sc = new Scanner(new File(docFile));
		HashMap<String, Occurrence> storage = new HashMap<String, Occurrence>();
		while (sc.hasNext()) {
			String tempString = getKeyword(sc.next());
			if (storage.containsKey(tempString) && tempString != null) {
				storage.get(tempString).frequency++;
			} else if (tempString != null) {
				Occurrence item = new Occurrence(docFile, 1);
				storage.put(tempString, item);
			}
		}
		sc.close();
		System.out.println(storage.keySet());
		System.out.println(storage.size());
		return storage;
	} // ERROR WITH RETURNING FIRST SPACE IN WOW.TXT

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		Set<String> newSet = new HashSet<String>();
		newSet = kws.keySet();
		Iterator<String> newIterator = newSet.iterator();
		while (newIterator.hasNext()) {
			String temp = newIterator.next();
			if (keywordsIndex.containsKey(temp)) {
				Occurrence oc = kws.get(temp);
				keywordsIndex.get(temp).add(oc);
				insertLastOccurrence(keywordsIndex.get(temp));
			} else {
				ArrayList<Occurrence> tempList = new ArrayList<Occurrence>();
				tempList.add(kws.get(temp));
				keywordsIndex.put(temp, tempList);
			}
		}
		System.out.println(keywordsIndex.size());
	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!' NO
	 * OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be
	 * stripped So "word!!" will become "word", and "word?!?!" will also become
	 * "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		word = word.toLowerCase();
		String temp = "";
		boolean foundPunct = false;
		boolean otherPunct = false;
		if (word.charAt(0) == '.' || word.charAt(0) == ',' || word.charAt(0) == ';' || word.charAt(0) == ':'
				|| word.charAt(0) == '?' || word.charAt(0) == '!' || word.equals("") || word.equals(" ")
				|| word.charAt(0) == '-' || word.charAt(0) == '_') {
			return null;
		}
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			boolean checkLetter = Character.isLetter(c);
			if (c == '.' || c == ',' || c == '?' || c == ':' || c == ';' || c == '!') {
				if (foundPunct == true && !(noiseWords.contains(temp))) {
					return temp;
				}
				if (foundPunct == true) {
					return null;
				}

				foundPunct = true;
			}
			if (foundPunct == true && checkLetter == true) {
				return null;
			}
			if (checkLetter == false && !(c == '.' || c == ',' || c == '?' || c == ':' || c == ';' || c == '!')) {
				otherPunct = true;
			}
			if (checkLetter == true) {
				temp += c;
			}
		}

		if (noiseWords.contains(temp) || otherPunct == true) {
			return null;
		}
		if (word.equals(null)) {
			return null;
		}
		if (temp.equals("") || temp.equals(" ")) {
			temp = null;
		}

		return temp;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/ // 1059 words
		if (occs.size() <= 1) {
			return null;
		}
		ArrayList<Integer> frequencies = new ArrayList<Integer>();
		ArrayList<Integer> middle = new ArrayList<Integer>();
		for (int i = 0; i < occs.size() - 1; i++) {
			frequencies.add(occs.get(i).frequency);
		}
		int target = occs.get(occs.size() - 1).frequency;
		int l = frequencies.size() - 1;
		int r = 0;
		int m = 0;
		while (r <= l) {
			m = (l + r) / 2; // indicates where the placement of the new element will be
			middle.add(m);
			if (target == occs.get(m).frequency) {
				break;
			}
			if (target < occs.get(m).frequency) {
				r = m + 1;
			} else {
				l = m - 1;
			}
		}

		if (occs.get(m).frequency >= target) {
			occs.add(m + 1, occs.remove(occs.size() - 1));
		} else {
			occs.add(m, occs.remove(occs.size() - 1));
		}

		return middle;
	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile       Name of file that has a list of all the document file
	 *                       names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise
	 *                       word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input
	 *                               files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies.
	 * 
	 * Note that a matching document will only appear once in the result.
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. That is,
	 * if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same
	 * frequency f1, then doc1 will take precedence over doc2 in the result.
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all,
	 * result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/

		String string1 = kw1;
		String string2 = kw2;
		ArrayList<String> top5Search = new ArrayList<String>();
		// checks if objects exist at all
		ArrayList<Occurrence> string1Location = new ArrayList<Occurrence>();
		ArrayList<Occurrence> string2Location = new ArrayList<Occurrence>();
		string1Location = null;
		string2Location = null;
		if (keywordsIndex.containsKey(string1)) {
			string1Location = keywordsIndex.get(string1);
		}
		if (keywordsIndex.containsKey(string2)) {
			string2Location = keywordsIndex.get(string2);
		}
		if (string1Location == null && string2Location == null) {
			return null;
		}
		if (string1Location == null || string2Location == null) {
			if (string1Location == null) {
				for (int j = 0; j < string2Location.size(); j++) {
					if (top5Search.size() <= 4) {
						String temp2 = string2Location.get(j).document;
						if (top5Search.contains(temp2) == false) {
							top5Search.add(temp2);
							
						}
					}
				}
				return top5Search;
			}
			else if(string2Location == null)
				for (int i = 0; i < string1Location.size(); i++) {
					if (top5Search.size() <= 4) {
						String temp1 = string1Location.get(i).document;
						if (top5Search.contains(temp1) == false) {
							top5Search.add(temp1);
							
						}
					}
				}
			return top5Search;
		}
		for (int i = 0; i < string1Location.size(); i++) {
			boolean f2Insert = false;
			if (top5Search.size() <= 4) {
				int f1 = string1Location.get(i).frequency;
				String temp1 = string1Location.get(i).document;
				for (int j = 0; j < string2Location.size(); j++) {
					int f2 = string2Location.get(j).frequency;
					String temp2 = string2Location.get(j).document;
					if (f2 <= f1 && top5Search.contains(temp1) == false && top5Search.size()<5) {
						top5Search.add(temp1);
					} else if (f2 > f1 && top5Search.contains(temp2) == false && top5Search.size()<5) {
						top5Search.add(temp2);

					}

				}
			}

		}
		if(top5Search.size()<5) {
			int size1=string1Location.size();
			int size2=string2Location.size();
			if(size1>size2) {
				for (int i = 0; i < string1Location.size(); i++) {
					if (top5Search.size() <= 4) {
						String temp1 = string1Location.get(i).document;
						if (top5Search.contains(temp1) == false) {
							top5Search.add(temp1);
							
						}
					}
			}
		}
			else {
				for (int j = 0; j < string2Location.size(); j++) {
					if (top5Search.size() <= 4) {
						String temp2 = string2Location.get(j).document;
						if (top5Search.contains(temp2) == false) {
							top5Search.add(temp2);
							
						}
					}
				}
			}
		}
		return top5Search;
	}
}
