#!/usr/bin/python
import curses, time, threading, os, re, glob
from subprocess import PIPE, Popen

#Function to update time in the top corner
def update_time(screen,):
	while 1:
		height,width = screen.getmaxyx()
		screen.addstr(0, 0, time.ctime() + " " * (width - len(str(screen.getmaxyx())) -24)  + str(screen.getmaxyx()))
        	screen.refresh()
        	time.sleep(1)


#Gets the current working directory and displays it down the left side of the screen.
def show_Dir ():
	j = 2
	while j < height:
		screen.hline(j,0, " ", 19)
		j += 1
	curDir = re.split('/',os.getcwd())
	j = 2
	for i in curDir:
		if not i == '':
			screen.addstr(j,0,i[:18] + "/")
			j += 1

def run_Commands(curCommand,output):
	curCommand = curCommand.strip().lower()
	if curCommand != "" :
		command = re.split(' ', curCommand) 
		if command[0] == "cd":
			try:
				os.chdir(command[1])
				show_Dir()
				screen.hline(3,20, " ", width-20)
			except:
				screen.hline(3,20, " ", width-20)
				screen.addstr(3,20,"No such directory exists")
        	elif command[0] == "clear":
			clear_Screen()
			output[:] = []
		elif command[0] == "quit" or command[0] == "exit":
			curses.endwin()
			exit()
		elif command[0] == "help":
			try:
				helpFile=open("./help.txt","r")
				for i in helpFile:
					i = i.strip()
					output.append(i)
					output.append('\n')
				helpFile.close()
			except:
				output.append("There was an error loading the help file.")
		elif '|' in command:
			try:
				first = command[0:command.index('|')]
				second = command[command.index('|')+1:len(command)]
				screen.addstr(20,20,first + ' | ' + second)
				p1 = Popen(first, stdout=PIPE)
				p2 = Popen(second, stdin=p1.stdout, stdout=PIPE)
				programOutput = p2.stdout.read()
				p2.communicate()
				p1.terminate()
				screenPrint = re.split('\n',programOutput)
				for i in screenPrint:
					output.append(i)

			except:
				output.append("An error occurred while piping")
				draw_Screen()
				show_Dir()
				display_Output(output)
				
		elif '>' in command:
			try:
				program = command[0:command.index('>')]
				file = command[command.index('>')+1:len(command)]
				if len(file) > 0:
					outputFile = open(file[0], "w+")
					p1 = Popen(program, stdout=PIPE)
					for i in p1.stdout:
						outputFile.write(i)		
					p1.terminate()

					#screen.addstr(4,20,"test2")
				else:
					output.append("You must include a file to output to")
					display_Output(output)
			except:
				output.append("An error occurred while redirecting")
				draw_Screen()
				show_Dir()
				display_Output(output)

		else:
			try:
				p1 = Popen(command, stdout=PIPE)
				programOutput = p1.stdout.read()
				p1.terminate()
				screenPrint = re.split('\n',programOutput)
				for i in screenPrint:
					output.append(i)
			except:
				output.append("That is not a valid command.")

		display_Output(output)



def clear_Screen():
	i = 2
	while i < height-3:
		screen.hline(i,20," ",width-20)
		i += 1

def factor_Output(output,displayed):
	height,width = screen.getmaxyx()
	for i in output:
		if i < width-20:
			displayed.append(i)

		#Handles printing output that is longer than one line but not separated by '\n'
		else: 
			multiplier = 0
			while (width-20)*multiplier < len(i):
				displayed.append(i[(width-20)*multiplier:(width-20)*(multiplier+1)])
				multiplier += 1

#Draws the shell display lines
def draw_Screen():
	height,width = screen.getmaxyx()
	j = 2
	while j < height:
		screen.hline(j,20," ",width-20)
		j += 1
	screen.hline(1,0,"_", width)
	screen.vline(2,19,"|", height-2)
	screen.hline(height-3,20,"_", width-20)


def display_Output(output):
	height,width = screen.getmaxyx()
	displayed = []
	factor_Output(output,displayed)
	if len(displayed) < height-5:
		j = 2
		for i in displayed:
			screen.addstr(j,20,i)
			j+=1
	else:
		clear_Screen()
		j = 2
		while j < height-3:
			screen.addstr(j,20,displayed[len(displayed)-(height-3)+j])
			j += 1
		

#Sets up curses display
screen = curses.initscr()
curses.noecho()
curses.curs_set(0)
screen.keypad(1)
screen.leaveok(0)
height,width = screen.getmaxyx()

show_Dir()
draw_Screen()
screen.addch(height-1,20, "^")

#Variables for use
extraLength = 0		#length beyond the width of the screen
prevCommand = 0		#keeps track of which previous command if pressing up or down arrows
pos = 0			#keeps track of position if going left or right
cursorPos = 20		#keeps track of cursor position
entryFront = 0		#keeps track of the displaying of the current edited line on the screen
output = []		#keeps track of everything that's displayed as output


#List of all previous commands, and current line variable
commands = []
curLine = ""


#Starts the clock
clock = threading.Thread(target=update_time, args=(screen,))
clock.daemon = True
clock.start()

#Main event loop
while 1:
	#Get events from the user (includes character entries and resizing of the window)
	event = screen.getch()
	
	#Means they pushed enter, act accordingly
	if event == ord("\n"):
		screen.hline(height-2,20, " ", width-20)
		screen.addch(height-1,cursorPos," ")
		screen.addch(height-1,20, "^")		
		#Used to determine if the user is pressing up or down arrow keys to retrieve commands
		if curLine != "":
			if len(commands) > 0:
				if not curLine == commands[len(commands)-1]:
					commands.append(curLine)
			else:
				commands.append(curLine)
			run_Commands(curLine, output)
		curLine = ""
		prevCommand = 0	
		pos = 0	
		cursorPos = 20
		extraLength = 0
		entryFront = 0
	

	#Auto tab complete
	elif event == ord("\t"):
		arg = re.split(" ", curLine)
		if len(arg) > 0:
			if arg[len(arg)-1].startswith('.'):
				tabMatch = glob.glob(arg[len(arg)-1] + '*')
			else:
				tabMatch = tabMatch + glob.glob('/usr/bin/' + arg[len(arg)-1] + '*')
				tabMatch = tabMatch + glob.glob('/bin/' + arg[len(arg)-1] + '*')
			if len(tabMatch) > 1:
				matches = ""
				for i in tabMatch:
					matches += i + "\t"
				output.append(matches)
				display_Output(output)
			elif len(tabMatch) == 1:
				curLine = curLine.replace(arg[len(arg)-1], tabMatch[0])
				if os.path.isdir(tabMatch[0]):
					curLine += "/"
				screen.addstr(height-2,20,curLine[entryFront:entryFront + (width-21)])			
				screen.addch(height-1, cursorPos, ' ')
				if len(curLine) < width-21:
					cursorPos = len(curLine) + 20	
					screen.addch(height-1, cursorPos, '^')
				else:
					cursorPos = width-2
					screen.addch(height-1, cursorPos, '^')
				


	#Handles functionality of the backspace character
	elif event == 127:
		if len(curLine) + pos > 0:
			screen.hline(height-2,20, " ", width-20)
			curLine = curLine[:(len(curLine)+pos)-1] +  curLine[len(curLine)+pos:]
			if extraLength > 0:
				extraLength -= 1
			else:
				screen.addch(height-1,cursorPos, " ")
				cursorPos -= 1
				screen.addch(height-1,cursorPos, "^")
			if entryFront > 0:
				entryFront -= 1
			screen.addstr(height-2,20,curLine[entryFront:entryFront + (width-22)])


	#Handles functionality of the down arrow key
	elif event == curses.KEY_DOWN:
		if prevCommand < 0:
			prevCommand += 1
			screen.hline(height-2,20, " ", width-20)
			if prevCommand == 0:
				curLine = ""
			else:

				screen.addstr(height-2,20, commands[len(commands) + prevCommand])
				curLine = commands[len(commands) + prevCommand]
				extraLength = len(curLine) - (width-20)
			screen.addch(height-1,cursorPos, " ")
			if extraLength > 0:
				cursorPos = width-1
			else:
				cursorPos = len(curLine)+20
			screen.addch(height-1,cursorPos, "^")



	#Handles functionality of the up arrow key
	elif event == curses.KEY_UP:
		if len(commands)+prevCommand > 0:
			prevCommand -= 1
			curLine = commands[len(commands) + prevCommand]
			extraLength = len(curLine) - (width-20)
			screen.hline(height-2,20, " ", width-20)
			screen.addstr(height-2,20, curLine[extraLength:])
			screen.addch(height-1,cursorPos, " ")
			if extraLength > 0:
				cursorPos = width-2
			else:
				cursorPos = len(curLine)+20
			screen.addch(height-1,cursorPos, "^")



	#Handles moving the cursor left
	elif event == curses.KEY_LEFT:
		if cursorPos > 20:
			pos -= 1
			screen.addch(height-1,cursorPos, " ")
			cursorPos -= 1
			screen.addch(height-1,cursorPos, "^")
		elif len(curLine) > -pos:
			pos -= 1
			entryFront -= 1
			screen.addstr(height-2,20, curLine[entryFront:entryFront + (width-22)]) 		#Moves the string leftwards if it extends beyond the screen and the cursor is at the left side
	


	#Handles moving the cursor right
	elif event == curses.KEY_RIGHT:
		if pos < 0:
			pos += 1
			if cursorPos < width-2:
				screen.addch(height-1,cursorPos, " ")
				cursorPos += 1
				screen.addch(height-1,cursorPos, "^")
			else:
				entryFront += 1
				screen.addstr(height-2, 20, curLine[entryFront:entryFront + (width-22)]) 	#Moves the string rightwards if it extends beyond the screen and the cursor is at the right side


	#Handles terminal resizing
	elif event == curses.KEY_RESIZE:
		draw_Screen()
		display_Output(output)
		height,width = screen.getmaxyx()
		screen.addstr(height-2,20,curLine)
		screen.addch(height-1,cursorPos, "^")
		


	else:
		#protects against 9 (tab)
		if event != 9 and event != -1:
			try:
				curLine = curLine[:len(curLine)+pos] + chr(event) + curLine[len(curLine)+pos:]
				if (len(curLine) >= (width-21)):
					extraLength += 1
					entryFront += 1
				if extraLength <= 0:
					screen.addch(height-1, cursorPos, ' ')
					cursorPos += 1	
					screen.addch(height-1, cursorPos, '^')
				screen.addstr(height-2,20,curLine[entryFront:entryFront + (width-21)])
				#screen.addstr(5,20,str(len(curLine)+pos) + " to " + str(len(curLine) + extraLength))				
				#screen.addstr(height-2, 20,curLine)	
				if prevCommand < 0:
					prevCommand = 0		
			except:
				output.append("An unknown error occurred")
				display_Output(output)
curses.endwin()
