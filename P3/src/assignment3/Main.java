/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * <Student2 Name>
 * <Student2 EID>
 * <Student2 5-digit Unique No.>
 * Slip days used: <0>
 * Git URL:
 * Fall 2020
 */


package assignment3;
import javax.xml.soap.Node;
import java.util.*;
import java.io.*;

public class Main {

    // static variables and constants only here.
    public static Set<String> dictionary;       //all words of provided dictionary
    public static ArrayList<String> wordList;   //user inputs
    public static String startingWord;          //starting word of word ladder
    public static String endingWord;            //ending word of world ladder
    public static Scanner kb;                   //input scanner
    public static Hashtable<String, List<String>> comboDict;

    public static void main(String[] args) throws Exception {

        //Scanner kb;	// input Scanner for commands
        PrintStream ps;	// output file, for student testing and grading only
        // If arguments are specified, read/write from/to files instead of Std IO.
        if (args.length != 0) {
            kb = new Scanner(new File(args[0]));
            ps = new PrintStream(new File(args[1]));
            System.setOut(ps);			// redirect output to ps
        } else {
            kb = new Scanner(System.in);// default input from Stdin
            ps = System.out;			// default output to Stdout
        }
        initialize();

        // TODO methods to read in words, output ladder
        ArrayList<String> wordLadder = getWordLadderBFS(startingWord,endingWord);
        printLadder(wordLadder);
    }

    public static void initialize() {
        // initialize your static variables or constants here.
        // We will call this method before running our JUNIT tests.  So call it
        // only once at the start of main.
        dictionary = makeDictionary();
        wordList = parse(kb);
        if (wordList.get(0) == "/quit"){
            return;
        }
        else {
            startingWord = wordList.get(0).toString();
            endingWord = wordList.get(1).toString();
        }

        comboDict = createComboDict(dictionary);
    }

    /**
     * @param keyboard Scanner connected to System.in
     * @return ArrayList of Strings containing start word and end word.
     * If command is /quit, return empty ArrayList.
     */
    public static ArrayList<String> parse(Scanner keyboard) {
        // obtain user input for start and end word
        String userInput = keyboard.nextLine();

        // check if /quit was entered
        if (userInput.equals("/quit")) {
            return new ArrayList<String>(Arrays.asList("/quit","/quit"));
        }

        ArrayList<String> wordList = new ArrayList<String>(Arrays.asList(userInput.split(" ")));


        return wordList;
    }

    public static ArrayList<String> getWordLadderDFS(String start, String end) {

        // Returned list should be ordered start to end.  Include start and end.
        // If ladder is empty, return list with just start and end.
        // TODO some code

        return null; // replace this line later with real return
    }

    public static class WordNode {
        public String word; public int depth; public String prev;

        public WordNode(String word, int depth, String prev) {
            this.word = word; this.depth = depth; this.prev = prev;
        }
    }

    public static ArrayList<String> getWordLadderBFS(String start, String end) {

        ArrayList<String> ladder = new ArrayList<String>();
        ArrayList<String> discoveredStrings = new ArrayList<String>();
        ArrayList<WordNode> discoveredNodes = new ArrayList<>();

        Queue<WordNode> Q = new LinkedList<>();

        int level = 0;
        WordNode startNode = new WordNode(start.toUpperCase(), level, null);
        Q.add(startNode);
        discoveredStrings.add(startNode.word);
        discoveredNodes.add(new WordNode(startNode.word, startNode.depth, startNode.prev));

        while(!Q.isEmpty()) {
            WordNode word = Q.remove();

            for (int i = 0; i < word.word.length(); i++) {
                // replace single character with "*"
                String starWord = word.word.substring(0, i) + "*" + word.word.substring(i + 1);

                // get value of dictionary for given key
                List<String> adjacentWords = comboDict.get(starWord);
                adjacentWords = new ArrayList<>(adjacentWords);

                // if ending word is found
                if (adjacentWords.contains(end.toUpperCase())) {
                    discoveredStrings.add(end.toUpperCase());
                    discoveredNodes.add(new WordNode(end.toUpperCase(), level+1, word.word));
                    Q.clear();
                    break;
                }

                // add all adjacent words that havent been discovered to queue
                for (String adjWord : adjacentWords) {
                    if (!discoveredStrings.contains(adjWord)) {
                        Q.add(new WordNode(adjWord, level+1,word.word));
                        discoveredStrings.add(adjWord);
                        discoveredNodes.add(new WordNode(adjWord, word.depth, word.word));
                    }
                }
            }
            level++;
        }

        if (discoveredStrings.contains(end.toUpperCase())) {
            ladder = backtrackBFS(discoveredNodes);
            return ladder; // replace this line later with
        }
        else {
            return ladder;
        }
    }

    public static ArrayList<String> backtrackBFS(ArrayList<WordNode> discoveredNodes) {
        ArrayList<String> ladder = new ArrayList<>();

        int index = discoveredNodes.size() - 1;
        String prevWord = "****";

        while(prevWord != null) {
            ladder.add(discoveredNodes.get(index).word);
            prevWord = discoveredNodes.get(index).prev;
            for (int i = 0; i < discoveredNodes.size(); i++) {
                if (discoveredNodes.get(i).word == prevWord) {
                    index = i;
                }
            }
        }
        return ladder;
    }

    public static int calcDistance(String adjWord, String endWord) {
        int distance = 0;
        for (int i = 0; i < adjWord.length(); i++) {
            distance += endWord.toUpperCase().charAt(i) - adjWord.charAt(i);
        }

        return distance;
    }

    public static void printLadder(ArrayList<String> ladder) {
        if (ladder.size() > 0) {
            Collections.reverse(ladder);
            System.out.println("a " + (ladder.size() - 2) + "-rung word ladder exists between " + startingWord + " and " + endingWord);
            for (String word : ladder) {
                System.out.println(word.toLowerCase());
            }
        }
        else {
            System.out.println("no word ladder was found between " + startingWord + " and " + endingWord);
        }
    }

    // TODO
    // Other private static methods here


    /* Do not modify makeDictionary */
    public static Set<String>  makeDictionary () {
        Set<String> words = new HashSet<String>();
        Scanner infile = null;
        try {
            infile = new Scanner (new File("five_letter_words.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary File not Found!");
            e.printStackTrace();
            System.exit(1);
        }
        while (infile.hasNext()) {
            words.add(infile.next().toUpperCase());
        }
        return words;
    }

    // create dictionary that holds words and their adjacent words in a list
    public static Hashtable<String, List<String>> createComboDict(Set<String> wordList) {
        Hashtable<String, List<String>> comboDict = new Hashtable<String, List<String>>();

        // iterate over every word in wordList
        for (String word : wordList) {
            // iterate over each character in word
            for (int i = 0; i < word.length(); i++){
                // replace single character with "*"
                String starWord = word.substring(0,i) + "*" + word.substring(i+1);

                // if dictionary is empty or key does not exist add key,value
                if (comboDict.isEmpty() == true || comboDict.get(starWord) == null) {
                    List<String> temp = Arrays.asList(new String[]{word});
                    comboDict.put(starWord, temp);
                }
                else {
                    // get value of dictionary for given key
                    List<String> adjacentWords = comboDict.get(starWord);
                    adjacentWords = new ArrayList<>(adjacentWords);


                    // add word to list if it doesn't already exist
                    if (!adjacentWords.contains(word)) {
                        adjacentWords.add(word);
                        comboDict.put(starWord,adjacentWords);

                    }
                }
            }
        }

        return comboDict;
    }
}
