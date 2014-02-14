
####################### Compilation & Running ########################

To compile the program simply stay in the main directory and type make

Upon compilation the .so and .o files will be placed in the object directory, the java
classes will be places in the classes directory

Running can be accomplished by typing make run in the main directory or navigating to the 
src directory and running the command java -cp ../classes Main_IDE.

Please note that in order to run, you need to set up the LD_LIBRARY_PATH to the ./obj 
directory using the command export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:./obj



######################### Use #############################

To use the program simply load any properly formatted .config file (one is provided as an 
example in the src directory.) and press compile to create corresponding .java files.

In order to run the created java files simply navigate to the corresponding folder and 
type javac along with the .java file. Please note that including the action listeners that 
correspond with your buttons must be implemented by you.

######################### Known Errors #############################
I couldn't get yacc to parse a file from a different directory all it states is a default error message of "input in flex scanner failed", to get around this I move the file to the src directory and move stuff from there.


