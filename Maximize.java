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
        //represents current bin we are working on
        int[] index = new int[27];  
                                    
        //index[curIndex]
        int curIndex = 0;

        //tracks possible words that can go into each slot in the current bin
        //we take each list at the position we are working on, remove anything that won't be possible to fit in
        //due to what is in the bin, and save that as the list for the next position
        //position 0 is initialised to have everything that is in allWords
        List<List<String>> workingList = new Vector<List<String>>(27);
        workingList.add(new Vector(allWords));

        //list of largest bins
        //the number of bins we keep is determined by numHitsToRemember
        List<List<String>> biggestList = new Vector<List<String>>(numHitsToRemember);

        //while the position of the first word in the current bin is less than the numTriesPerSlot, loop
        //this allows us to only look at the few words at the top of the whole list
        //this won't guarantee a perfect result but makes the run time much shorter
        //since we sorted the words based on their rarity, this should give a decently good result
        while(index[0] < (workingList.get(0).size() > numTriesPerSlot ? numTriesPerSlot : workingList.get(0).size())){
            // if index[curIndex] is less than numTriesPerSlot and size of workingList at the current position,
            // we keep adding words to the bin
            // look at the list of possible words for current position in workingList,
            // pick one word, take the list, make a copy and remove everything
            // that would conflict with our chosen word with clean()
            // then we save that list to workingList for the next index
            // increment curIndex so we work on the next slot in the current bin
            if(index[curIndex] < (workingList.get(curIndex).size() > numTriesPerSlot ? numTriesPerSlot: workingList.get(curIndex).size())){
                workingList.add(clean(workingList.get(curIndex), index[curIndex]));
                curIndex++;
            }
            // if index[curIndex] is not less than size of workingList, we went out of bounds
            // if index[curIndex] is not less than numTries, we want to skip the rest of the list for time saving
            // in both cases, we check what is in the current bin and its size and decide if we keep or trash it
            // once done, we clean up the list at curIndex
            // step back one slot with curIndex--
            // since we just finished checking index[curIndex] we do index[curIndex]++ to go to the next potential word
            else{
                //save current bin which is an array of positions index[] to List<String> temp
                List<String> temp = new Vector<String>(27);
                for(int i = 0; i < curIndex; i++){
                    temp.add(workingList.get(i).get(index[i]));
                }

                //if we don't have a largest bin yet, any bin is a good bin
                if(biggestList.size() == 0){
                    biggestList.add(temp);
                    if(verbose > 0 && incremental) System.out.printf("%s %d\n", temp, temp.size());
                }
                //if temp is larger than the largest bin so far
                //wipe the biggestList which contains the list of largest bins we found
                //save temp to the list of largest bins
                else if(temp.size() > biggestList.get(0).size()){
                    biggestList.clear();
                    biggestList.add(temp);
                    if(verbose > 0 && incremental) System.out.printf("-----\n%s %d\n", temp, temp.size());
                }
                //this section only executes if we want to find every single bin 
                //AND if temp is same length as the largest bin
                else if(findAll && temp.size() == biggestList.get(0).size()){
                    //compare each bin in biggestList to temp
                    //if we find a bin that contains the same strings as temp, don't add anything and break
                    //if we don't have the following check, we will have the same bins but in different order
                    //instead, we want bins that are unique
                    boolean notFound = true;
                    outer:
                    for(List<String> l: biggestList){
                        for(String s: temp){
                            if(!l.contains(s)){
                                continue outer;
                            }
                        }
                        notFound = false;
                        break;
                    }
                    if (notFound){
                        if(biggestList.size() < numHitsToRemember) biggestList.add(temp);
                        if(verbose > 1 && incremental) System.out.printf("%s %d\n", temp, temp.size());
                    }
                }
            
                //once we are done deciding whether to save or trash temp
                //remove the subList of possible values for current slot
                //reset index[curIndex] to 0 for future iterations
                //step back one slot with curIndex--
                //since we checked index[curIndex] already, increment it so we don't get stuck in infinite loop
                workingList.remove(curIndex);
                index[curIndex] = 0;
                curIndex--;
                index[curIndex]++;
            }    
        }

        //print out the list of bins we found and the length
        if(verbose > 1){
            System.out.println("-----");
            for(List<String> list: biggestList){
                System.out.println(list);
            }
        }
        System.out.printf("Max length: %d\n", biggestList.get(0).size());
    }

    //take list, and return new list that doesn't contain any conflicts with word
    static Vector<String> clean(List<String> list, int word){
        char[] chars = list.get(word).toCharArray();
        Vector<String> result = new Vector<String>();

        outer:
        for(int i = 0; i < list.size(); i++){
            char[] temp = list.get(i).toCharArray();
            for(int j = 0; j < temp.length; j++){
                if(temp[j] == chars[j]) continue outer;
            }
            result.add(list.get(i));
        }
        return result;
    }
}