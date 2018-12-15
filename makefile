default:
	javac Maximize.java Bin.java Stopwatch.java Util.java
	java Maximize

test:
	javac *.java
	java Test