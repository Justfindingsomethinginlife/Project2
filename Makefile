test:
	javac -d classes/ src/main/java/Project2_6681012/*.java
	java -cp classes/ Project2_6681012.Main
project_compile: src/main/java/*.java
	javac -d classes/ src/main/java/Project2_6681012/*.java
run:
	java -cp classes/ Main	
clean: *.class
	rm -f classes/*.class
