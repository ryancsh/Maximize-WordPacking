import java.io.*;
import java.util.*;

public class Maximize{
    public static void main(String[] args){
        Maximize max = new Maximize();
    }

    public static final String filename = "sgb-words.txt";  //make sure file exists and correct file name
    
    public static final int VERBOSE = 1;
    public static final int WORDSIZE = 5;

    public static final int SECONDS = 0;
    public static final int MINUTES = 0;
    public static final int HOURS = 0;

    Vector<String> allWords;
    boolean[][] compatible;

    public Maximize(){
        allWords = new Vector<String>();
        readAllWords();
        compatible = new boolean[allWords.size()][allWords.size()];
        computeCollisions();
    }

    void readAllWords() {
        //read all words from file
        try {
            Scanner s = new Scanner(new File(filename));
            while(s.hasNextLine()) {
                allWords.add(s.nextLine());
            }
            s.close();
            if(VERBOSE > 0) { System.out.println("Read " + allWords.size() + " words from <" + filename + ">."); }
        }
        catch(Exception e) {
            System.out.println("Unable to read file: " + e);
        }
        
        //look at frequency for each letter at each position
        int[][] allWordsStats = new int[5][26];
        for(String s: allWords){
            char[] chars = s.toCharArray();
            for(int i = 0; i < allWordsStats.length; i++){
                allWordsStats[i][(int)chars[i] - (int)'a']++;
            }
        } 

        //sort allWords based on allWordsStats
        //less commonly seen words go first
        //we can sort by a single position only
        //negative position = sort using all letters
        class myComparator implements Comparator<String>{
            int position;
            int[][] allWordsStats;

            public myComparator(int position, int[][] allWordsStats){
                this.position = position;
                this.allWordsStats = allWordsStats;
            }

            @Override
            public int compare(String s1, String s2){
                int s1val = 0;
                int s2val = 0;

                if(position < 0){
                    for(int i = 0; i<s1.length(); i++){
                        s1val += allWordsStats[i][(int)(s1.charAt(i)) - (int)'a'];
                        s2val += allWordsStats[i][(int)(s2.charAt(i)) - (int)'a'];
                    }
                }
                else{
                    s1val = allWordsStats[position][(int)(s2.charAt(position)) - (int)'a'];
                    s1val = allWordsStats[position][(int)(s2.charAt(position)) - (int)'a'];
                }

                if(s1val < s2val) return -1;
                else if(s1val > s2val) return 1;
                else return 0;
            }
        }

        //do the actual sorting
        Collections.sort(allWords, new myComparator(0, allWordsStats));
        Collections.sort(allWords, new myComparator(1, allWordsStats));
        Collections.sort(allWords, new myComparator(2, allWordsStats));
        Collections.sort(allWords, new myComparator(3, allWordsStats));
        Collections.sort(allWords, new myComparator(4, allWordsStats));
        Collections.sort(allWords, new myComparator(-1, allWordsStats));
    }

    void computeCollisions(){
        for(int i = 0; i < allWords.size(); i++){
            next:
            for(int j = i + 1; j < allWords.size(); j++){
                for(int k = 0; k < WORDSIZE; k++){
                    if(allWords.get(i).charAt(k) == allWords.get(j).charAt(k)){
                        continue next;
                    }
                }
                compatible[i][j] = true;
            }
        }
    }

    static class Node{
        static Node root;
        boolean[][] compatible;

        int value;
        int current = 0;
        int depth = 0;
        boolean done = false;

        Vector<Node> next = null;
        Node previous;

        Node(int value, Node previous){
            if(value < 0){
                root = this;
            }

            this.value = value;
            this.previous = previous;
        }

        public Node(int value, Node previous, boolean[][] compatible){
            this(value, previous);
            this.compatible = compatible;
        }

        public Node getNext(int depth){
            if(next == null){
                next = new Vector<Node>();
                for(int i = value + 1; i < compatible.length; i++){
                    if(compatible[value][i]) next.add(new Node(i, this));
                }
            }
            while(!done){
                if(depth > next.size()){
                    next = null;
                    done = true;
                    return null;
                }
                if(this.depth < depth){
                    current = 0;
                    this.depth = depth;
                }
                if(current > depth){
                    return null;
                }
                Node result = next.get(current);
                current++;
                if(! result.done){
                    return result;
                }
            }
            return null;
        }
    }

    void getMax(){
    }
}