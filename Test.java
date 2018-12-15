class Test{
  public static String[] allWords;
  public static void main(String[] args){
    allWords = Maximize.readAllWords();
    Bin.init(allWords);
    Util.init(allWords);

    Bin b = new Bin();
    testCompatibilityTable(b);
    for(int i = 0; i < allWords.length; i++){
      b.addWord(i);
      testCompatibilityTable(b);
    }
    for(int i = 0; i < 1000; i++){
      int toRemove = b.words[2];
      b.removeIndex(2);
      testCompatibilityTable(b);
      b.addWord(toRemove);
      testCompatibilityTable(b);
    }
  }

  static void testCompatibilityTable(Bin b){
    boolean[] inBin = new boolean[5 * 26];
    for(int i = 0; i < b.size; i++){
      for(int j = 0; j < 5; j++){
        if(inBin[j * 26 + allWords[b.words[i]].charAt(j) - 'a']) {
          Util.print("" + i + " " + j + " " + allWords[b.words[i]] + " " + allWords[b.words[i]].charAt(j));
          Util.printBin(b);
          Util.printArr(inBin);
          Util.printArr(b.inBin);
          Util.crash( "word already in bin");
        }
        inBin[j * 26 + allWords[b.words[i]].charAt(j) - 'a'] = true;
      }
    }
    for(int i = 0; i < inBin.length; i++){
      if(inBin[i] != b.inBin[i]) Util.crash("inBin and b.inBin not same");
    }
  }
}