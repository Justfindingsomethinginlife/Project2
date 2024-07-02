target=
main_class=

test:
	javac -d classes/ src/main/java/$(target)/*.java
	java -cp classes/ $(target).$(main_class)
project_compile: 
	javac -d classes/ src/main/java/$(target)/*.java
run:
	java -cp classes/ $(target).$(main_class)
clean: *.class
	rm -f classes/*.class
