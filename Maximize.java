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

    public static final int TARGET_INDEX = 0;

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

    static class nodeComparator implements Comparator<Node>{
        static int[] sortingStats;

        @Override
        public int compare(Node n1, Node n2){
            if (n1.value < 0 || n2.value < 0) throw new RuntimeException("wtf");
            return sortingStats[n1.value] - sortingStats[n2.value];
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

    static class Node{
        static Node root;
        static boolean[][] compatible;
        static List<String> allWords;

        public final int value;
        public Node previous;

        boolean notInitialised = true;
        Vector<Node> next;
        boolean[] removed;
        int lastReturned;
        int size;
        
        Node(int value, Node previous){
            this.value = value;
            this.previous = previous;
            next = new Vector<Node>();
        }

        public Node(int value, Node previous, boolean[][] compatible, List<String> allWords){
            this(value, previous);
            this.compatible = compatible;
            this.allWords = allWords;
            root = this;
        }

        void initialise(){
            notInitialised = false;

            if(this == root){
                for(int i = 0; i < compatible.length; i++){
                    next.add(new Node(i, this));
                }
            }
            else{
                for(Node n: this.previous.next){
                    if(n.value > this.value && compatible[value][n.value]) next.add(new Node(n.value, this));
                }
            }
            removed = new boolean[next.size()];
            size = removed.length;
            lastReturned = 0;
        }

        public Node next(){
            if(notInitialised) initialise();
            
            for(int i = lastReturned; i < removed.length; i++){
                if(!removed[i]){
                    removed[i] = true;
                    size--;
                    lastReturned = i;
                    return next.get(i);
                }
            }
            return null;
        }

        public void removeNode(){
            previous.removed[previous.next.indexOf(this)] = true;
            next = null;
            removed = null;
            previous = null;
        }

        public int size(){
            if(notInitialised) initialise();
            return size;
        }
    }

    public void getMax(){
        Node rootNode = new Node(-1, null, compatible, allWords);
        Node currentNode = rootNode;

        int[] largestBin = new int[27];
        int largestSize = TARGET_INDEX;
        int currentSize = 0;

        long timeInaccuracy = TIME * 100 / TIMETOLERANCE;
        long timeLimit = (TIME == 0 ? Integer.MAX_VALUE : timeInaccuracy);
        long totalRounds = 0;
        long startTime = System.currentTimeMillis();
        long endTime = TIME + startTime;

        timeLoop:
        while(TIME == 0 || System.currentTimeMillis() < endTime){
            innerTimeLoop:
            for(long timeCount = 0; timeCount < timeLimit;){
                if(currentNode.size() == 0 || currentNode.size() <= largestSize - currentSize){
                    timeCount++;
                    if(currentNode == rootNode){
                        break timeLoop;
                    }
                    else{
                        if(currentSize > largestSize){
                            largestSize = currentSize;
                            saveBin(largestBin, currentNode);
                            if(VERBOSE > 0){ printBin(largestBin, System.currentTimeMillis() - startTime); }
                        }
                        currentSize--;
                        Node prevNode = currentNode.previous;
                        currentNode.removeNode();
                        currentNode = prevNode;
                        continue innerTimeLoop;
                    }
                }
                currentSize++;
                currentNode = currentNode.next();
            }
            totalRounds += timeLimit;
        }

        if(VERBOSE > 1){
            StringBuilder s = new StringBuilder();
            s.append("Time: ");
            s.append((System.currentTimeMillis() - startTime)/1000);
            s.append("s, Rounds: ");
            s.append(totalRounds);
            System.out.println(s.toString());
        }
    }

    void saveBin(int[] where, Node lastNode){
        Node rootNode = Node.root;
        int i = 0;
        while(lastNode != rootNode){
            where[i] = lastNode.value;
            lastNode = lastNode.previous;
            i++;
        }
        for(i = i; i < where.length; i++){
            where[i] = -1;
        }
    }

    void printBin(int[] bin, long timeMillis){
        StringBuilder s2 = new StringBuilder();
        int i;
        for(i = 0; bin[i] != -1; i++){
            s2.append(allWords.get(bin[i]));
            s2.append(' ');
        }

        StringBuilder s = new StringBuilder();
        s.append(i);
        s.append(" [ ");
        s.append(s2);
        s.append("] ");
        s.append(timeMillis / 1000);
        s.append(" s");
        System.out.println(s.toString());
    }

    void print(CharSequence c){
        System.out.println(c);
    }
}