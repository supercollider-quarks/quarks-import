#! /usr/bin/env python
# -*- coding: utf-8 -*-

import pydonhive
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

hive = pydonhive.MiniHive( "/dev/ttyUSB0", 57600 )
hive.set_id_range( 1, 11 )
  
#hive.load_from_file( "hiveconfig_atmos.xml" )

hive.load_from_file( "hiveconfig.xml" )

hive.bees[ 1 ].set_action( writeDataAction )
hive.bees[ 2 ].set_action( writeDataAction )
hive.bees[ 3 ].set_action( writeDataAction )
hive.bees[ 4 ].set_action( writeDataAction )

hive.serial.set_log_action( writeLogAction )
    
hive.run()

hive.exit()

