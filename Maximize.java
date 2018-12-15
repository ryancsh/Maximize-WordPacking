import java.io.*;
import java.util.*;

public class Maximize{
    public static void main(String[] args){
        Maximize max = new Maximize();
    }

    class Stopwatch{
        long startTime;
        long lapTime;
        long endTime;

        public void start(){
            startTime = System.currentTimeMillis();
            endTime = startTime;
        }

        public String lap(){
            lapTime = endTime;
            endTime = System.currentTimeMillis();
            return(""+ (endTime - lapTime) / 1000 + " s");
        }

        public String all(){
            endTime = System.currentTimeMillis();
            return(""+ (endTime - startTime) / 1000 + " s");
        }
    }

    public static final String filename = "sgb-words.txt";  //make sure file exists and correct file name
    
    public static final int VERBOSE = 2;
    public static final int WORDSIZE = 5;

    public static final int SECONDS = 0;
    public static final int MINUTES = 0;
    public static final int HOURS = 0;
    public static final int TIMETOLERANCE = 1;    // 100 => 1/100 => 1%
    public static Stopwatch watch;

    public static final int NUM_GENS = 5;

    Vector<String> allWords;
    boolean[][] compatible;
    final long TIME;

    public Maximize(){
        watch = new Stopwatch();
        TIME = 1000 * (SECONDS + MINUTES * 60 + HOURS * 3600);
        allWords = new Vector<String>();
        watch.start();
        readAllWords();
        if(VERBOSE > 1)System.out.println("readallwords(): " + watch.lap());
        compatible = new boolean[allWords.size()][allWords.size()];
        computeCollisions();
        if(VERBOSE > 1)System.out.println("computeCollisions(): " + watch.lap());
        getMax();
        if(VERBOSE > 1)System.out.println("getMax(): " + watch.lap());

        if(VERBOSE > 1)System.out.println("full run time: " + watch.lap());
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

        myComparator.allWordsStats = allWordsStats;
        myComparator.allWords = allWords;

        Collections.sort(allWords, new myComparator(0));
        Collections.sort(allWords, new myComparator(1));
        Collections.sort(allWords, new myComparator(2));
        Collections.sort(allWords, new myComparator(3));
        Collections.sort(allWords, new myComparator(4));
        Collections.sort(allWords, new myComparator(-1));
    }

    static class myComparator implements Comparator<String>{
        static int[][] allWordsStats;
        static List<String> allWords;

        int position;

        public myComparator(int position){
            this.position = position;
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

    void computeCollisions(){
        int numTrue = 0;
        int numFalse = 0;
        for(int i = 0; i < allWords.size(); i++){
            next:
            for(int j = 0; j < allWords.size(); j++){
                for(int k = 0; k < WORDSIZE; k++){
                    if(allWords.get(i).charAt(k) == allWords.get(j).charAt(k)){
                        numFalse++;
                        continue next;
                    }
                }
                compatible[i][j] = true;
                numTrue++;
            }
        }
        if(VERBOSE > 1) System.out.println("Compatible [true:" + numTrue + " false:" + numFalse + "]");
    }

    static class Bin{
        static List<String> allWords;
        static boolean[][] compatible;

        int[] words = new int[26];
        int size = 0;

        public Bin(){
        }
        
        public Bin(Bin other){
            size = other.size;
            words = new int[26];
            for(int i = 0; i < size; i++){
                words[i] = other.words[i];
            }
        }

        public static void init(List<String> allWords, boolean[][] compatible){
            Bin.allWords = allWords;
            Bin.compatible = compatible;
        }

        public void forceAddWord(int word){
            for(int i = 0; i < size; i++){
                if(!compatible[words[i]][word]){
                    for(int j = i; j < size - 1; j++){
                        words[j] = words[j + 1];
                    }
                    size--;
                    i--;
                }
            }

            words[size] = word;
            size++;
        }

        public boolean addWord(int word){
            for(int i = 0; i < size; i++){
                if(!compatible[word][words[i]]) return false;
            }
            words[size] = word;
            size++;
            return true;
        }

        public int size(){
            return size;
        }

        public String toString(){
            StringBuilder s = new StringBuilder();
            s.append("[ ");
            for(int i = 0; i < size; i++){
                s.append(allWords.get(i));
                s.append(" ");
            }
            s.append("]");
            return s.toString();
        }
    }

    public void getMax(){
        Bin.init(allWords, compatible);
        Bin[] thisGen = new Bin[NUM_GENS];
        Bin[] nextGen = new Bin[NUM_GENS];

        //generate firstGen
        thisGen[0] = new Bin();
        for(int i = 0; i < allWords.size(); i++){
            thisGen[0].addWord(i);
        }
    }
}