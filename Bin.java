import java.util.*;

class Bin implements Comparable<Bin>{
  static String[] allWords;
  static boolean[][] notCompatible;
  final static Random rng = new Random(123456789);

  public static void init(String[] allWords){
    final int WORDSIZE = allWords[0].length();
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
  boolean[] conflict = new boolean[allWords.length];

  public void copy(Bin other){
    this.size = other.size;
    for(int i = 0; i < size; i++){
      this.words[i] = other.words[i];
    }
    for(int i = 0; i < conflict.length; i++){
      this.conflict[i] = other.conflict[i];
    }
  }

  public void nextGeneration(){
    //remove randomly
    int toRemove = rng.nextInt(size);
    removeIndex(toRemove);
    //add everything that can be added
    int start = rng.nextInt(conflict.length);
    for(int i = start + 1; i != start; i++){
      if(i == conflict.length) i = 0;
      if(i == toRemove) continue;
      addWord(i);
    }
    addWord(toRemove);
  }

  public boolean compatible(int word){
    return !conflict[word];
  }

  void removeIndex(int index){
    int toRemove = words[index];

    for(int i = index + 1; i < size; i++){
      words[i - 1] = words[i];
    }
    size--;

    for(int i = 0; i < conflict.length; i++){
      conflict[i] = false;
    }

    for(int i = 0; i < size; i++){
      for(int j = 0; j < conflict.length; j++){
        if(notCompatible[words[i]][j]) conflict[j] = true;
      }
    }
  }
  /*
  void removeIndex(int index){
    int toRemove = words[index];

    for(int i = index + 1; i < size; i++){
      words[i - 1] = words[i];
    }
    size--;

    for(int i = 0; i < conflict.length; i++){
      if(!notCompatible[toRemove][i]) conflict[i] = true;
    }
  }
  */

  public boolean addWord(int word){
    if (conflict[word]) return false;

    words[size] = word;
    size++;
    for(int i = 0; i < conflict.length; i++){
      if(notCompatible[word][i]){
        conflict[i] = true;
      }
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
    //potential optimization
    for(int i = 0; i < conflict.length; i++){
      if(conflict[i] != other.conflict[i]) return false;
    }
    return true;
  }

  public void clear(){
    size = 0;
    for(int i = 0; i < conflict.length; i++){
      conflict[i] = false;
    }
  }
}