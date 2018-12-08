import java.io.*;
import java.util.*;

public class Maximize{
    public static final String filename = "sgb-words.txt";  //make sure file exists and correct file name
    
    static Vector<String> allWords = new Vector<String>();

    public static int verbose = 1;              //print extra details 0=final result only; 1=reasonable; 2=all
    public static boolean incremental = true;   //print results as we go
    public static boolean findAll = false;       //find all matches
    public static int numTriesPerSlot = 3;      //number of words to try max per slot per iteration
    public static int numHitsToRemember = 5;    //number of matching bins to remember

    public static void main(String[] args){
        long readAllWordsTime = System.currentTimeMillis();
        readAllWords();
        readAllWordsTime = System.currentTimeMillis() - readAllWordsTime;

        long getMaxTime = System.currentTimeMillis();
        getMax();
        getMaxTime = System.currentTimeMillis() - getMaxTime;

        System.out.printf("-----\nReadAllWords() : %f s\n", (double)readAllWordsTime/1000);
        System.out.printf("getMax() : %f s\n", (double)getMaxTime/1000);
    }

    private static void readAllWords() {
        //read all words from file
        try {
            Scanner s = new Scanner(new File(filename));
            while(s.hasNextLine()) {
                allWords.add(s.nextLine());
            }
            s.close();
            if(verbose > 0) { System.out.println("Read " + allWords.size() + " words from <" + filename + ">."); }
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
        //print out the stats of the words
        if(verbose > 1){
            System.out.println("----- Stats for all words");
            for(int i = 0; i < 26; i++){
                StringBuilder s = new StringBuilder();
                for(int j = 0; j < allWordsStats.length; j++){
                    if(j == 0){
                        s.append((char)(i + (int)'a'));
                        s.append('\t');
                    }
                    s.append(allWordsStats[j][i]);
                    s.append('\t');
                }
                System.out.println(s);
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

    private static void getMax(){
    }
}