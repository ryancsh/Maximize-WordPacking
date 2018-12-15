class Util{
  static String[] allWords;

  public static void init(String[] allWords){
    Util.allWords = allWords;
  }
  public static void printBin(Bin b){
    printBin("", b);
  }

  public static void printBin(String someText, Bin b){
    StringBuilder s = new StringBuilder();
    if(someText.length() > 0){
      s.append(someText);
      s.append(": ");
    }
    s.append(b.size());
    s.append(' ');
    s.append(b);
    System.out.println(s);
  }
  public static void printArr(Object[] arr){
    StringBuilder res = new StringBuilder();
    for(int i = 0; i < arr.length; i++){
      res.append(arr[i]);
      res.append(' ');
    }
    res.append('\n');
    print(res);
  }

  public static void printArr(boolean[] arr){
    StringBuilder res = new StringBuilder();
    for(int i = 0; i < arr.length; i++){
      if(arr[i]) res.append('T');
      else res.append('F');
      res.append(' ');
    }
    res.append('\n');
    print(res);
  }

  public static void print(Object o){
    System.out.println(o.toString());
  }
  public static void print(int i){
    System.out.println(i);
  }

  public static void findWord(String word){
    for(int i = 0; i < allWords.length; i++){
      if(allWords[i].equals(word)){
        print(i);
        return;
      }
    }
    print(-1);
  }
}