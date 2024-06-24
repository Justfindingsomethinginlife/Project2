test:
	javac src/main/java/*.java
	java -d Main
	clean
project_compile: src/main/java/*.java
	javac -d classes/ src/main/java/*.java
run:
	java -cp classes/ Main	
clean:
	rm -f *.class
