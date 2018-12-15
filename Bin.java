import java.util.*;

class Bin implements Comparable<Bin>{
  static String[] allWords;
  static boolean[][] notCompatible;
  static int WORDSIZE;

  final static Random rng = new Random(123456789);

  public static void init(String[] allWords){
    WORDSIZE = allWords[0].length();
    Bin.allWords = allWords;
    Bin.notCompatible = new boolean[allWords.length][allWords.length];
    outer:
    for(int i = 0; i < allWords.length; i++){
      inner:
      for(int j = 0; j < allWords.length; j++){
        for(int k = 0; k < WORDSIZE; k++){
          if(allWords[i].charAt(k) == allWords[j].charAt(k)){
            notCompatible[i][j] = true;
            continue inner;
          }
        }
      }
    }
  }

  int[] words = new int[26];
  int size = 0;
  boolean[] inBin = new boolean[WORDSIZE * 26];

  public void copy(Bin other){
    this.size = other.size;
    for(int i = 0; i < size; i++){
      this.words[i] = other.words[i];
    }
    for(int i = 0; i < inBin.length; i++){
      this.inBin[i] = other.inBin[i];
    }
  }

  public void nextGeneration(int gen){
    //remove randomly
    int indexToRemove = rng.nextInt(size);
    int wordToRemove = get(indexToRemove);
    removeIndex(indexToRemove);
    //add everything that can be added
    int start = rng.nextInt(allWords.length);
    int limit = allWords.length - 1;
    for(int i = start + 1; i != start; i++){
      if(i == allWords.length){
        i = -1;
        continue;
      }
      if(i == wordToRemove) continue;
      addWord(i);
    }
    addWord(wordToRemove);
  }

  public boolean compatible(int word){
    return(compatible(allWords[word]));
  }

  public boolean compatible(String toCheck){
    for(int i = 0; i < WORDSIZE; i++){
      if(inBin[i * 26 + toCheck.charAt(i) - 'a']) return false;
    }
    return true;
  }

  void removeIndex(int index){
    String toRemove = allWords[words[index]];

    for(int i = index + 1; i < size; i++){
      words[i - 1] = words[i];
    }
    size--;

    for(int i = 0; i < WORDSIZE; i++){
      inBin[i * 26 + toRemove.charAt(i) - 'a'] = false;
    }
  }

  public int get(int index){
    return words[index];
  }

  public boolean addWord(int word){
    String toAdd = allWords[word];
    if(!compatible(toAdd)) return false;

    words[size] = word;
    size++;
    for(int i = 0; i < WORDSIZE; i++){
      inBin[i * 26 + toAdd.charAt(i) - 'a'] = true;
    }
    return true;
  }

  public int size(){
    return size;
  }

  public String toString(){
    StringBuilder s = new StringBuilder();
    s.append("[ ");
    for(int i = 0; i < size; i++){
      s.append(allWords[words[i]]);
      s.append(" ");
    }
    s.append("]");
    return s.toString();
  }

  public int compareTo(Bin other){
    return other.size - size;
  }

  public boolean equals(Bin other){
    if(size != other.size) return false;
    for(int i = 0; i < inBin.length; i++){
      if(inBin[i] != other.inBin[i]) return false;
    }
    return true;
  }

  public void clear(){
    size = 0;
    for(int i = 0; i < inBin.length; i++){
      inBin[i] = false;
    }
  }
}