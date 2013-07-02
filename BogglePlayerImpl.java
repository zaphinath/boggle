
package cs235.boggle;


import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.Arrays;
import java.util.ListIterator;


public class BogglePlayerImpl implements BogglePlayer {
	
	private List<String> dictionaryWords = null;
	private String word = null;
	private	String[][] letters = null;
	private String[] arrayedWords = null;
	private int boardSize;
	private boolean[][] traversed = null;
  private SortedSet found = null ;
	private int min;
	private List orderNumbers = null;
	private int wordElements;
	private String wordToCheck = null;
  private boolean locatedWord;
  private int minimumWordLength;

	/**
	* Loads the dictionary into a data structure for later use. 
	* 
	* @param fileName A string containing the dictionary to be opened.
	* @throws IllegalArgumentException if fileName is null
	* @throws IllegalArgumentException if fileName cannot be opened.
	*/
	public void loadDictionary(String fileName) {
		if (fileName == null ) {
			throw new IllegalArgumentException();
		}
		dictionaryWords = new LinkedList<String>();
		try {
			File foo = new File(fileName);
			if (!foo.canRead())
				throw new IllegalArgumentException();
			Scanner in = new Scanner(new File(fileName));
			while (in.hasNextLine()) {
				String word = in.nextLine();
				dictionaryWords.add(word);
			}
			in.close();
			arrayedWords = new String[dictionaryWords.size()];
			int j = 0;
			for (ListIterator i = dictionaryWords.listIterator(); i.hasNext();
				arrayedWords[j++] = (String)i.next()) {
				System.out.print("");	
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void setBoard(String[] letterArray) {
		if (letterArray == null)
			throw new IllegalArgumentException();
		double length = Math.sqrt(letterArray.length);
		//regression here
		//if (Math.pow(Math.round(letterArray.length), 2) != letterArray.length)
		//	throw new IllegalArgumentException();
		boardSize = (int)length;
		letters = new String[boardSize][boardSize];
		int i;
		int j;
		int listSize;
		for (i=0, listSize = 0; i < boardSize; i++) {
			for (j=0; j < boardSize; j++) {
			  letters[i][j] = letterArray[listSize++].toLowerCase();
			}
		}
	}
	
	public SortedSet getAllValidWords(int minimumWordLength) {
	//	int x;
	//	int y;
		min = 1;
		word = new String("");
    this.minimumWordLength = minimumWordLength;
		found = new TreeSet();
    traversed = new boolean[this.boardSize][this.boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j= 0; j < boardSize; j++) {
				traversed[i][j] = false;
			}
		}
		if (minimumWordLength < min)
			throw new IllegalArgumentException();
		for (int i = 0; i < boardSize; i++) {
			for (int j= 0; j < boardSize; j++) {
				findWords(i,j, word);
			}
		}
		return found;
	}
	
	/**
	* Determines if the given word is in the dictionary.
	* 
	* @param wordToCheck The word to validate
	* @return true if wordToCheck appears in dictionary, false otherwise.
	* @throws IllegalArgumentException if wordToCheck is null.
  * @throws IllegalStateException if loadDictionary has not been called.
	*/
	public boolean isValidWord(String wordToCheck) {
		if (wordToCheck == null)
			throw new IllegalArgumentException();
		if (Arrays.binarySearch(arrayedWords, wordToCheck) >= 0 
				|| dictionaryWords.contains(wordToCheck))
			return true;
	  return false;
	}
	
	/**
	* Determines if there is at least one word in the dictionary with the 
	* given prefix.
	* 
	* @param prefixToCheck The prefix to validate
	* @return true if prefixToCheck appears in dictionary, false otherwise.
	* @throws IllegalArgumentException if prefixToCheck is null.
	* @throws IllegalStateException if loadDictionary has not been called.
  */
	public boolean isValidPrefix(String prefixToCheck) {
		if (prefixToCheck == null)
			throw new IllegalArgumentException();
		if (dictionaryWords == null)
			throw new IllegalStateException();
		if (Arrays.binarySearch(arrayedWords, prefixToCheck) >= 0)
				return true;
		if (Arrays.binarySearch(arrayedWords, prefixToCheck) <= 0) {
			int insertionPoint;
			insertionPoint = Arrays.binarySearch(arrayedWords, prefixToCheck);
			insertionPoint = -(insertionPoint + 1);
			if (arrayedWords[insertionPoint].startsWith(prefixToCheck))
				return true;
		}	
		return false;
	}
	
	public List isOnBoard(String wordToCheck) {
		if (wordToCheck == null)
			throw new IllegalArgumentException();
		this.wordToCheck = wordToCheck;
		orderNumbers = new LinkedList<Integer>();
		String foundWord = new String("");
		traversed = new boolean[this.boardSize][this.boardSize];
		locatedWord = false;
		for (int i = 0; i < boardSize; i++) {
			for (int j= 0; j < boardSize; j++) {
				traversed[i][j] = false;
			}
		}
		for (int i = 0; i < boardSize; i++) {
			for (int j= 0; j < boardSize; j++) {
				checkWords(i, j, foundWord);
			}
		}
		int a = orderNumbers.size();
		int b = wordToCheck.length();
		if (orderNumbers.size() > 0)
			return orderNumbers;
		else
			return null;
	}
	
	/**
	* An optional method that gives a user-defined boggle board to the GUI.
	* 
	* @return a String array in the same form as the input to setBoard().
	*/
	public String[] getCustomBoard() {
		return null;
	}
	
	public void findWords(int i, int j, String word) {
		if (!isValidPrefix(word) 
			  || i < 0 || j < 0 
			  || i >= boardSize || j >= boardSize
			)	return;
			if (this.traversed[i][j]) return;
			traversed[i][j] = true;
      word += letters[i][j];
			if (isValidWord(word) && word.length() >= minimumWordLength 
          && Arrays.binarySearch(arrayedWords, word) >= 0)
				found.add(word);
			findWords(i+1,j,word);
			findWords(i-1,j,word);		
			findWords(i,j+1,word);		
			findWords(i,j-1,word);		
			findWords(i+1,j+1,word);		
			findWords(i+1,j-1,word);		
			findWords(i-1,j+1,word);		
			findWords(i-1,j-1,word);
			traversed[i][j] = false;
	}
	
	public void checkWords(int i, int j, String foundWord) {
		if (i < 0 || j < 0 
		  	|| i >= boardSize || j >= boardSize
        || locatedWord
			) return;
		if (this.traversed[i][j]) return;
			foundWord += letters[i][j];
		if (!wordToCheck.startsWith(foundWord)) return;
			int number = (boardSize * i) + j;
			orderNumbers.add(number);
    if (foundWord.equals(wordToCheck) /*&& orderNumbers.size() == wordToCheck.length()*/)
				locatedWord = true;
			traversed[i][j] = true;
			checkWords(i+1,j,foundWord);
			checkWords(i-1,j,foundWord);		
			checkWords(i,j+1,foundWord);		
			checkWords(i,j-1,foundWord);		
			checkWords(i+1,j+1,foundWord);		
			checkWords(i+1,j-1,foundWord);		
			checkWords(i-1,j+1,foundWord);		
			checkWords(i-1,j-1,foundWord);
			traversed[i][j] = false;
	    if (!locatedWord) {
        int size = orderNumbers.size() -1;
        orderNumbers.remove(size);
      }
  }
}
