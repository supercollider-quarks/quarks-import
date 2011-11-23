#! /usr/bin/env python
# -*- coding: utf-8 -*-
import optparse

from pydon import pydonhive
import time
import csv

def openNewFile():
  global hiveWriter, hiveFile
  print( "opening new file" )
  hiveFile.close()
  filename = 'logs/hivelog' + time.strftime("_%j_%H_%M_%S") + '.csv'
  hiveFile = open(filename, 'wb')
  hiveWriter = csv.writer(hiveFile, dialect='excel-tab')

def writeDataAction( data, nodeid ):
  global rows
  rows = rows + 1
  hiveWriter.writerow( [time.strftime("%j:%H:%M:%S")] + [str(time.time() - starttime)] + [nodeid] + data )
  if rows > maxrows:
    openNewFile()
    rows = 0

def openNewLogFile():
  global hiveLogWriter, hiveLogFile
  print( "opening new log file" )
  hiveLogFile.close()
  filename = 'logs/hivedebuglog' + time.strftime("_%j_%H_%M_%S") + '.csv'
  hiveLogFile = open(filename, 'wb')
  hiveLogWriter = csv.writer(hiveLogFile, dialect='excel-tab')

def writeLogAction( data ):
  global drows
  drows = drows + 1
  hiveLogWriter.writerow( [time.strftime("%j:%H:%M:%S")] + [str(time.time() - starttime)] + data )
  if drows > maxrows:
    openNewLogFile()
    drows = 0

maxrows = 60 * 60 * 10 * 4
rows = 0
drows = 0

# make filename with a date stamp
filename = 'logs/hivelog' + time.strftime("_%j_%H_%M_%S") + '.csv'
hiveFile = open(filename, 'wb')
hiveWriter = csv.writer(hiveFile, dialect='excel-tab')

# make filename with a date stamp
filename = 'logs/hivedebuglog' + time.strftime("_%j_%H_%M_%S") + '.csv'
hiveLogFile = open(filename, 'wb')
hiveLogWriter = csv.writer(hiveLogFile, dialect='excel-tab')

starttime = time.time()

def hookBeeToLog( minibee ):
  #self.datanetwork.osc.infoMinibee( minibee.nodeid, minibee.getInputSize(), minibee.getOutputSize() )
  #minibee.set_first_action( self.addAndSubscribe )
  minibee.set_action( writeDataAction )
  #minibee.set_status_action( self.sendStatusInfo )



parser = optparse.OptionParser(description='Create a logger to communicate with the minibee network.')
parser.add_option('-c','--config', action='store', type="string", dest="config",default="pydon/configs/hiveconfig.xml",
		  help='the name of the configuration file for the minibees [default:%s]'% 'pydon/configs/hiveconfig.xml')
parser.add_option('-m','--nr_of_minibees', type=int, action='store',dest="minibees",default=20,
		  help='the number of minibees in the network [default:%i]'% 20)
parser.add_option('-o','--minibee_offset', type=int, action='store',dest="mboffset",default=1,
		  help='the offset of the number range for the minibees in the network [default:%i]'% 1)
parser.add_option('-v','--verbose', action='store',dest="verbose",default=False,
		  help='verbose printing [default:%i]'% False)
parser.add_option('-s','--serial', action='store',type="string",dest="serial",default="/dev/ttyUSB0",
		  help='the serial port [default:%s]'% '/dev/ttyUSB0')
parser.add_option('-b','--baudrate', action='store',type=int,dest="baudrate",default=57600,
		  help='the serial port [default:%i]'% 57600)
parser.add_option('-a','--apimode', action='store', type="string", dest="apimode",default=False,
		  help='use API mode for communication with the minibees [default:%s]'% False)

(options,args) = parser.parse_args()
  #print args.accumulate(args.integers)
  #print options
  #print args
print( "--------------------------------------------------------------------------------------" )
print( "HiveLogger - a program to communicate with the minibee network and log the data." )
print( " --- to find out more about the startup options start with \'hivelogger.py -h\'" )
print( " --- The client has been started with these options:" )
print( options )
print( "--------------------------------------------------------------------------------------" )


hive = pydonhive.MiniHive( options.serial, options.baudrate, options.apimode )
hive.set_id_range( options.mboffset, options.minibees )
hive.set_verbose( options.verbose )
  
hive.load_from_file( options.config )

hive.set_newBeeAction( hookBeeToLog )
hive.serial.set_log_action( writeLogAction )
    
hive.run()

#hive.exit()

