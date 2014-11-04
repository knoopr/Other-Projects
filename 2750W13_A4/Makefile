CC          =	gcc
CFLAGS      =	-Wall -ansi -pedantic -g

Dialogc: obj/libDialogc.so src/Dialogc.h obj/JNI_wrapper.o ./yadc 

obj/libDialogc.so : obj/JNI_wrapper.o obj/Linked_list.o obj/ParameterManager.o obj/Queue.o
	gcc -shared -Wl,-soname,libDialogc.so \
	-I. \
	-I/usr/lib/jvm/java-6-sun-1.6.0.26/include/linux \
	-I/usr/lib/jvm/java-6-sun-1.6.0.26/include \
	-o obj/libDialogc.so \
	obj/JNI_wrapper.o obj/Linked_list.o obj/ParameterManager.o obj/Queue.o

src/Dialogc.h: classes/Dialogc.class 
	cd classes;javah -jni Dialogc;cd ..;mv classes/Dialogc.h src/Dialogc.h

classes/Dialogc.class: Dialogc.java
	javac -d ./classes Dialogc.java

obj/JNI_wrapper.o: src/JNI_wrapper.c src/Dialogc.h 
	$(CC) $(CFLAGS) -c  src/JNI_wrapper.c -o  obj/JNI_wrapper.o -fPIC -Isrc \
	-I/usr/lib/jvm/java-6-sun-1.6.0.26/include/linux \
	-I/usr/lib/jvm/java-6-sun-1.6.0.26/include

obj/ParameterManager.o: src/ParameterManager.c src/ParameterManager.h
	$(CC) $(CFLAGS) -c  src/ParameterManager.c -o  obj/ParameterManager.o -Iinclude -fPIC

obj/Queue.o: src/Queue.c src/Queue.h
	$(CC) $(CFLAGS) -c  src/Queue.c -o  obj/Queue.o -Iinclude -fPIC

obj/Linked_list.o: src/Linked_list.c src/Linked_list.h
	$(CC) $(CFLAGS) -c  src/Linked_list.c -o  obj/Linked_list.o -Iinclude -fPIC

./yadc: src/yacc.tab.c src/lex.yy.c
	$(CC) -ansi -pedantic  src/lex.yy.c src/yacc.tab.c -o  ./yadc -Iinclude -fPIC -lfl

src/yacc.tab.c: src/yacc.y
	cd src; bison -d yacc.y

src/lex.yy.c: src/lex.l
	cd src; flex -i lex.l

run: Dialogc
	java -cp ./classes Dialogc

clean:
	@ rm obj/*.o
	@ rm classes/*
	@ rm src/Dialogc.h
	@ rm obj/libDialogc.so
	@ rm src/yacc.tab.*
	@ rm src/lex.yy.c
	@ rm yadc