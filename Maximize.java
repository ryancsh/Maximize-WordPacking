import java.io.*;
import java.util.*;

public class Maximize{
  public static void main(String[] args){
    final String[] allWords = readAllWords();
    Stopwatch watch = new Stopwatch();

    Maximize max = new Maximize();
    watch.start();
    max.getMax(allWords);
    watch.all("Maximize");
  }
  
  public static final String filename = "sgb-words.txt";  //make sure file exists and correct file name
  
  public static final int VERBOSE = 2;
  public static final int WORDSIZE = 5;

  public static final int OLD_GEN = 10;
  public static final int NEW_GEN = 2;
  final int IMPROVEMENT_CYCLES = 100000;

  public static String[] readAllWords() {
    //read all words from file
    Vector<String> allWords = new Vector<String>();
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
    /*
    Collections.sort(allWords, new myComparator(0));
    Collections.sort(allWords, new myComparator(1));
    Collections.sort(allWords, new myComparator(2));
    Collections.sort(allWords, new myComparator(3));
    Collections.sort(allWords, new myComparator(4));
    Collections.sort(allWords, new myComparator(-1));
    */

    String[] resultAllWords = new String[allWords.size()];
    for(int i = 0; i < allWords.size(); i++){
      resultAllWords[i] = allWords.get(i);
    }
    return resultAllWords;
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

  public void getMax(String[] allWords){
    Bin.init(allWords);
    final int NUM_GENS = OLD_GEN * NEW_GEN;
    
    //empty bins
    Bin[] bins = new Bin[NUM_GENS];
    for(int i = 0; i < NUM_GENS; i++){
        bins[i] = new Bin();
    }
    //generate first bin
    for(int i = 0; i < allWords.length; i++){
        bins[0].addWord(i);
    }
    //only bin we have so far is best bin
    Bin bestBin = new Bin();
    bestBin.copy(bins[0]);
    if(VERBOSE > 0) Util.printBin(bestBin);

    for(int count = 0; count < IMPROVEMENT_CYCLES; count++){
      //generate next generation
      for(int i = 0; i < OLD_GEN; i++){
        if(bins[i].size() == 0) break;

        for(int j = OLD_GEN + i; j < bins.length; j += OLD_GEN){
          bins[j].copy(bins[i]);
          bins[j].nextGeneration();
        }
      }
      //remove copies
      for(int i = 1; i < bins.length; i++){
        for(int j = 0; j < i; j++){
          if(bins[i].equals(bins[j])){
            bins[i].clear();
            break;
          }
        }
      }
      //sort
      Arrays.sort(bins);
      //check best bin
      if(bins[0].size() > bestBin.size()){
        count = 0;
        bestBin.copy(bins[0]);
        if(VERBOSE > 0) Util.printBin(bestBin);
        for(int i = 0; i < bins.length; i++){
          if(bins[i].size() < bestBin.size()){
            if(bins[i].size() == 0) break;
            bins[i].clear();
          }
        }
      }
    }
  }
}