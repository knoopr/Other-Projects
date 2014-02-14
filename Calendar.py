#!/usr/bin/python
import os, sys, re

if len(sys.argv) < 2:
    print "Please include an input file."
    exit()
elif len(sys.argv) > 2:
    print "Please include only one argument as an input file."
    exit()

if not os.path.exists(sys.argv[1]):
    print "Error: no such file exists"
    exit()

print "Please enter the first day of class in the format yyyymmdd"
while 1:
    startDate = raw_input()
    if re.match("^(?![0-9]{8}$).*$",startDate.strip()):
        print "Please enter a date in the format provided (yyyymmddd)"
    else:
        break

print "Please enter the last day of class in the format yyyymmdd"
while 1:
    endDate = raw_input()
    if re.match("^(?![0-9]{8}$).*$",endDate.strip()):
        print "Please enter a date in the format provided (yyyymmddd)"
    else:
        break

scheduleInput = []
file = open(sys.argv[1], "r")
for line in file:
    scheduleInput.append(line.strip())
file.close()

index = [i for i, item in enumerate(scheduleInput) if re.search('[a-zA-Z]{3,4}\*[0-9]{4}.*', item)]


output = open(re.sub("\..*", ".ics", sys.argv[1]), "w+")
output.write("BEGIN:VCALENDAR\n"
             "METHOD:PUBLISH\n"
             "VERSION:2.0\n"
             "X-WR-CALNAME:Classes\n"
             "PRODID:-//Apple Inc.//iCal 5.0.3//EN\n"
             "X-APPLE-CALENDAR-COLOR:#44A703\n"
             "X-WR-TIMEZONE:America/Toronto\n"
             "CALSCALE:GREGORIAN\n"
             "BEGIN:VTIMEZONE\n"
             "TZID:America/Toronto\n"
             "BEGIN:DAYLIGHT\n"
             "TZOFFSETFROM:-0500\n"
             "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=2SU\n"
             "DTSTART:20070311T020000\n"
             "TZNAME:EDT\n"
             "TZOFFSETTO:-0400\n"
             "END:DAYLIGHT\n"
             "BEGIN:STANDARD\n"
             "TZOFFSETFROM:-0400\n"
             "RRULE:FREQ=YEARLY;BYMONTH=11;BYDAY=1SU\n"
             "DTSTART:20071104T020000\n"
             "TZNAME:EST\n"
             "TZOFFSETTO:-0500\n"
             "END:STANDARD\n"
             "END:VTIMEZONE\n"
             )
             
             
             #"BEGIN:VEVENT\n"
             #"DTEND;TZID=America/Toronto:20140101T122000\n"
             #"RRULE:FREQ=WEEKLY;INTERVAL=1;UNTIL=20141202;BYDAY=MO,WE,FR;WKST=\n"
             #"SU\n"
             #"SUMMARY:ACCT*2230\n"
             #"DTSTART;TZID=America/Toronto:20140101T113000\n"
             #"LOCATION:ROZH 105\n"
             #"SEQUENCE:12\n"
             #"END:VEVENT\n")
for i in index:
    output.write("BEGIN:VEVENT\n");
    theClass = re.sub("\*[0-9]{2,4} .*\) ", " ", scheduleInput[i])
    output.write("SUMMARY:" + theClass + " LEC\n"),
    
    
    output.write("DTSTART;TZID=America/Toronto:"+startDate + "T"),
    hour = ((ord(scheduleInput[i+3][0])-48)*10 + ord(scheduleInput[i+3][1])-48)
    min = ((ord(scheduleInput[i+3][3])-48)*10 + ord(scheduleInput[i+3][4])-48)
    if re.match("[0-9]{2}:[0-9]{2}PM.*",scheduleInput[i+3]):
        hour = hour + 12
    output.write("%02d" % hour + "%02d" % min + "00\n")
    

    output.write("DTEND;TZID=America/Toronto:"+startDate + "T")
    hour = ((ord(scheduleInput[i+3][10])-48)*10 + ord(scheduleInput[i+3][11])-48)
    min = ((ord(scheduleInput[i+3][13])-48)*10 + ord(scheduleInput[i+3][14])-48)
    if re.match("[0-9]{2}:[0-9]{2}PM.*",scheduleInput[i+3]):
        hour = hour + 12
    output.write("%02d" % hour + "%02d" % min + "00\n")

    output.write("RRULE:FREQ=WEEKLY;INTERVAL=1;UNTIL=" + endDate + ";BYDAY=")
    if re.match(".*Mon,.*", scheduleInput[i+2]):
        output.write("MO,WE,FR;WKST="
                     "SU\n")
    else:
        output.write("TU,TH;WKST="
                     "SU\n")


    output.write("LOCATION:" + scheduleInput[i+4] + "\n")
    output.write("END:VEVENT\n")
    if re.match("LAB .*", scheduleInput[i+5]):
        output.write("BEGIN:VEVENT\n");
        theClass = re.sub("\*[0-9]{2,4} .*\) ", " ", scheduleInput[i])
        output.write("SUMMARY:" + theClass + " LAB\n"),
        

        output.write("DTSTART;TZID=America/Toronto:"+startDate + "T"),
        hour = ((ord(scheduleInput[i+6][0])-48)*10 + ord(scheduleInput[i+6][1])-48)
        min = ((ord(scheduleInput[i+6][3])-48)*10 + ord(scheduleInput[i+6][4])-48)
        if re.match("[0-9]{2}:[0-9]{2}PM.*",scheduleInput[i+6]):
            hour = hour + 12
        output.write("%02d" % hour + "%02d" % min + "00\n")


        output.write("DTEND;TZID=America/Toronto:"+startDate + "T")
        hour = ((ord(scheduleInput[i+6][10])-48)*10 + ord(scheduleInput[i+6][11])-48)
        min = ((ord(scheduleInput[i+6][13])-48)*10 + ord(scheduleInput[i+6][14])-48)
        if re.match("[0-9]{2}:[0-9]{2}PM.*",scheduleInput[i+6]):
            hour = hour + 12
        output.write("%02d" % hour + "%02d" % min + "00\n")

        output.write("RRULE:FREQ=WEEKLY;INTERVAL=1;UNTIL=" + endDate + ";BYDAY=")
        if scheduleInput[i+5][4] == "M":
            output.write("MO;WKST="
                 "SU\n")
        elif scheduleInput[i+5][4] == "W":
            output.write("WE;WKST="
                 "SU\n")
        elif scheduleInput[i+5][4] == "F":
                 output.write("FR;WKST="
                              "SU\n")
        elif scheduleInput[i+5][4] == "T" and scheduleInput[i+2][5] == "u":
            output.write("TU;WKST="
                 "SU\n")
        else:
            output.write("TH;WKST="
                  "SU\n")
        output.write("LOCATION:" + scheduleInput[i+4] + "\n")
        output.write("END:VEVENT\n")

output.write("END:VCALENDAR")
output.close()

'''
i = 0
scheduleInput = ["12:00", "10:00", "08:00"]
hour = ((ord(scheduleInput[i+2][0])-48)*10 + ord(scheduleInput[i+2][1])-48) + 12
'''



#match = re.findall("[a-zA-z]{3,4}\*[0-9]{4}.*", line.strip())
#for matches in match:
#    theClass = re.sub("\*[0-9]{2,4} .*\) ", " ", matches)
#    scheduleInput.append(theClass)