import os
import paramiko
import tarfile
from shutil import copyfile
from difflib import context_diff
from datetime import datetime, timedelta
from os import path
import sys

WeeklyPath =  path.split(path.realpath(__file__))[0]

try:
	ssh = paramiko.SSHClient()
	ssh.load_system_host_keys()
	ssh.connect('159.203.24.208', username='halwell', key_filename='/home/halwell/.ssh/website') 
except Exception as e:
	print "Error: There was a problem loading the known_hosts, or error connecting; exiting - " + datetime.now().strftime('%Y-%m-%d at %H:%M:%S')
	print e
	sys.exit()


try:
	#getting a list of all the backups and sorting to get the most recent backup
	stdin, stdout, stderr = ssh.exec_command("ls -1  /var/www/html/halwell/wp-content/uploads/backwpup-4182d1-backups")
	FileList = []
	for file in stdout.readlines():
		if file[:-1].endswith(".tar.gz"):
			FileList.append(file)
	FileList.sort()
	RecentFileName = FileList[len(FileList)-1][:-1]
except:
	print "Error: A problem occurred loading a listing of all the backup scripts, exiting - "  + datetime.now().strftime('%Y-%m-%d at %H:%M:%S')
	ssh.close()
	sys.exit()

try:
	sftp = ssh.open_sftp()
	path = "/var/www/html/halwell/wp-content/uploads/backwpup-4182d1-backups/"

	sftp.get(path+ RecentFileName, RecentFileName) 	

	#Up to 300MB in memory temp file
#	RecentTemp = tempFile(314572800)
#	sftp.getfo(path + RecentFileName, RecentTemp)
#	RecentTemp.seek(0)
	
#	PreviousTemp = tempFile(314572800)
#	sftp.getfo(path + "LastWeek.tar.gz", PreviousTemp)
#	PreviousTemp.seek(0)
except Exception as e:
	print "Error: A problem occurred copying the most recent tar File, exiting - " + datetime.now().strftime('%Y-%m-%d at %H:%M:%S')
	print e
	sftp.close()
	ssh.close()
	sys.exit()

sftp.close()
ssh.close()

try:
	RecentTar = tarfile.open(RecentFileName)
	LastFileName = WeeklyPath + "/LastWeek.tar.gz"
	PreviousTar = tarfile.open(LastFileName)
except Exception as e:
	print "Error: a problem occurred when opening one of the two tar files, exiting - " + datetime.now().strftime('%Y-%m-%d at %H:%M:%S')
	print e
	sys.exit()

try:	
	RecentFiles = RecentTar.getnames()
	PreviousFiles = PreviousTar.getnames()	

	Today = datetime.now()
	FolderPath = WeeklyPath + "/Weekly backup: " + Today.strftime('%Y-%m-%d')
	os.mkdir(FolderPath)
except:
	print "Error: A problem occurred when creating the folder, exiting. - "  + datetime.now().strftime('%Y-%m-%d at %H:%M:%S')
	sys.exit()
		
#except:
#	print "something went wrong!"





FileChanges = [[] for x in range(3)]

for difference in context_diff(PreviousFiles, RecentFiles, n=0):
	if "backwpup" not in difference and not difference[2:].startswith("wp-content/plugins"):
		if difference.startswith("+ "):
			FileChanges[0].append(difference[2:])
		elif difference.startswith("- "):
			FileChanges[1].append(difference[2:])


for FileName in RecentFiles:
	if FileName in PreviousFiles and FileName not in FileChanges[0] and not FileName.startswith("wp-content/plugins") and not "backwpup" in FileName:
		TarFileInfo = RecentTar.getmember(FileName)
		FileTime = datetime.fromtimestamp(TarFileInfo.mtime)
		if FileTime > Today - timedelta(days=7) and TarFileInfo.isfile():
			FileChanges[2].append(FileName)

	
with open(FolderPath + '/Backup.txt' , 'w+') as TXTFile:
	TXTFile.write("File Additions:\n")
	for row in FileChanges[0]:
		TXTFile.write(row + "\n")
		RecentTar.extract(row, FolderPath)

	TXTFile.write("\n\nFile Removals:\n")
	for row in FileChanges[1]:
		TXTFile.write(row + "\n")	
		
	TXTFile.write("\n\nFile Changes:\n")
	for row in FileChanges[2]:
		TXTFile.write(row + "\n")	
		RecentTar.extract(row, FolderPath)

	TXTFile.close()


#Copying most recent for next weeks comparison
try:
	copyfile(RecentFileName,WeeklyPath + "/LastWeek.tar.gz")
	os.remove(RecentFileName)
except:
	print "Error: There was an error copying or removing the file, exiting - "  + datetime.now().strftime('%Y-%m-%d at %H:%M:%S')
	sys.exit()

print "Success: The backup was completed succesfully, exiting - " + datetime.now().strftime('%Y-%m-%d at %H:%M:%S')
