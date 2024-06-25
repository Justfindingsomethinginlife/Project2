test:
	javac -d classes/ src/main/java/*.java
	java -cp classes/ Main
project_compile: src/main/java/*.java
	javac -d classes/ src/main/java/*.java
run:
	java -cp classes/ Main	
clean: *.class
	rm -f classes/*.class
