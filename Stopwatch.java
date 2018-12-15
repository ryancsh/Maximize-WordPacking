class Stopwatch{
  long startTime;
  long lapTime;
  long endTime;

  public void start(){
    startTime = System.currentTimeMillis();
    endTime = startTime;
  }

  public long lap(){
    lapTime = endTime;
    endTime = System.currentTimeMillis();
    return(endTime - lapTime);
  }

  public long all(){
    endTime = System.currentTimeMillis();
    return(endTime - startTime);
  }

  public void lap(String s){
    Util.print(format(s, all()));
  }

  public void all(String s){
    Util.print(format(s, all()));
  }

  String format(String s, long time){
    StringBuilder result = new StringBuilder(s);
    result.append(": ");
    result.append(time);
    result.append(" ms");
    return result.toString();
  }
}