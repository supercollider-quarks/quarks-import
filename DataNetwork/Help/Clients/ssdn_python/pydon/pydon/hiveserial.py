# -*- coding: utf-8 -*-
import serial
#import time
#import sys
import os
#import datetime

#print time

#from pydon 
#import minibeexml

#from collections import deque

#### convenience function
#def find_key(dic, val):
  ##
  #"""return the key of dictionary dic given the value"""
  ##
  #return [k for k, v in dic.iteritems() if v == val][0]

#### end convenience function

# beginning of message queue

#class HiveBeeQueue(object):
  #def __init__(self):
    #queue = deque()

  #def addBee( self, bee ):
    
## beginning of message queue


# beginning of serial interface:

class HiveSerial(object):
  def __init__(self, serial_port, baudrate = 19200 ):
    #self.init_with_serial( mid, serial, libv, revision, caps)
    try:
      self.serial = serial.Serial( serial_port, baudrate, timeout=0, rtscts=1)  # open first serial port
      self.serialOpened = True
    except:
      self.serialOpened = False
      print( "could not open serial port", serial_port )
      print( "Please make sure your coordinator node is connected to the computer and pass in the right serial port location upon startup, e.g. \'python swpydonhive.py -s /dev/ttyUSB1\'" )
      os._exit(1)
      #raise SystemExit
      #sys.exit()
      #raise KeyboardInterrupt
    #self.hive = hive
    self.escape = False
    self.incType = 0
    self.incMsg = []
    self.hiveMsgId = 0
    self.logAction = None
    self.verbose = False
    
  def set_verbose( self, onoff ):
    self.verbose = onoff
    if onoff:
      print( self.serial )
      print( self.serial.portstr )       # check which port was really used

    
  def set_hive( self, hive ):
    self.hive = hive
    
  def announce( self ):
    msg = bytearray(b" A\n")
    msg[0] = chr( 92 )
    #print msg
    self.serial.write( msg )

  def quit( self ):
    msg = bytearray(b" Q\n")
    msg[0] = chr( 92 )
    #self.serial.write( msg )
    self.serial.close()
    
  def incMsgID( self ):
    self.hiveMsgId = self.hiveMsgId + 1
    if self.hiveMsgId > 255:
      self.hiveMsgId = 0

  def appendToMsg( self, msg, dat ):
    dat = int( dat )
    if dat == 10:
      msg += chr( 92 )
    if dat == 13:
      msg += chr( 92 )
    if dat == 92:
      msg += chr( 92 )
    msg += chr( dat )
    return msg

  def send_me( self, ser, onoff ):
    if self.verbose:
      print( "sending bee me", ser, onoff )
    self.incMsgID()
    msg = bytearray(b" M")
    msg[0] = chr( 92 )
    msg = self.appendToMsg( msg, self.hiveMsgId )
    msg += msg.join( ser.split() )
    msg = self.appendToMsg( msg, onoff )
    msg += b"\n"
    self.serial.write( msg )

  def send_id( self, ser, nodeid, configid ):
    if self.verbose:
      print( "sending bee id", ser, nodeid, configid )
    self.incMsgID()
    msg = bytearray(b" I")
    msg[0] = chr( 92 )
    msg = self.appendToMsg( msg, self.hiveMsgId )
    msg += msg.join( ser.split() )
    msg = self.appendToMsg( msg, nodeid )
    if configid > 0 :
      msg = self.appendToMsg( msg, configid )
    msg += b"\n"
    self.serial.write( msg )

  def send_config( self, nodeid, configuration ):
    if self.verbose:
      print( "sending configuration", configuration )
    self.incMsgID()
    msg = bytearray(b" C")
    msg[0] = chr( 92 )
    msg = self.appendToMsg( msg, self.hiveMsgId )
    msg = self.appendToMsg( msg, nodeid )
    for conf in configuration:
      msg = self.appendToMsg( msg, conf )
    msg += b"\n"
    #print msg
    self.serial.write( msg )

  #def send_data( self, mid, data ):
    #self.incMsgID()
    #msg = bytearray(b" O")
    #msg[0] = chr( 92 )
    #msg = self.appendToMsg( msg, mid )
    #msg = self.appendToMsg( msg, self.hiveMsgId )
    ###msg += chr(configid)
    #for dat in data:
      #msg = self.appendToMsg( msg, dat )
    #msg += b"\n"
    #self.send_msg( msg )
    ##self.serial.write( msg )
    ##if self.verbose:
      ##print( "sending data to minibee", mid, data, msg )

  def send_msg( self, msg, targetid=0 ):
    self.serial.write( msg )
    if self.verbose:
      print( "sending message", msg )

  def create_beemsg( self, msgtype, mid, msgid, msgdata ):
    msg = bytearray(b" O")
    msg[0] = chr( 92 )
    msg[1] = msgtype
    msg = self.appendToMsg( msg, mid )
    msg = self.appendToMsg( msg, msgid )
    for dat in msgdata:
      msg = self.appendToMsg( msg, dat )
    msg += b"\n"

  #def send_custom( self, mid, data ):
    #self.incMsgID()
    ##self.hiveMsgId = self.hiveMsgId + 1
    #msg = bytearray(b" E")
    #msg[0] = chr( 92 )
    #msg = self.appendToMsg( msg, mid )
    #msg = self.appendToMsg( msg, self.hiveMsgId )
    ##msg += chr(configid)
    #for dat in data:
      #msg = self.appendToMsg( msg, dat )
    #msg += b"\n"
    #self.serial.write( msg )
    #if self.verbose:
      #print( "sending custom data to minibee", mid, data, msg )
    
  def send_xmsg( self, mid, data ):
    #self.hiveMsgId = self.hiveMsgId + 1
    self.incMsgID()
    msg = bytearray(b" X")
    msg[0] = chr( 92 )
    #msg[1] = mtype
    msg = self.appendToMsg( msg, mid )
    msg = self.appendToMsg( msg, self.hiveMsgId )
    #msg += chr(configid)
    for dat in data:
      msg = self.appendToMsg( msg, dat )
    msg += b"\n"
    self.serial.write( msg )

  def send_run( self, mid, run ):
    #self.hiveMsgId = self.hiveMsgId + 1
    self.incMsgID()
    msg = bytearray(b" R")
    msg[0] = chr( 92 )
    msg = self.appendToMsg( msg, mid )
    msg = self.appendToMsg( msg, self.hiveMsgId )
    if run:
      msg += chr( 1 )
    else:
      msg += chr( 0 )
    msg += b"\n"
    self.serial.write( msg )

  def send_loop( self, mid, loop ):
    #self.hiveMsgId = self.hiveMsgId + 1
    self.incMsgID()
    msg = bytearray(b" L")
    msg[0] = chr( 92 )
    msg = self.appendToMsg( msg, mid )
    msg = self.appendToMsg( msg, self.hiveMsgId )
    if loop:
      msg += chr( 1 )
    else:
      msg += chr( 0 )
    msg += b"\n"
    self.serial.write( msg )

  #def parse_string( string ):
    #length = len( string )
    #esc = string.find( '\\' )
    ##msgtype = string[1]  
    #print length, esc #, msgtype

  def parse_serial( self ):
    length = len( self.incMsg )
    if self.verbose:
      print( "parsing serial", length )
    if length == 18:
      ser = "".join( map(chr, self.incMsg[1:length-3] ) )
      libv = self.incMsg[length-3]
      rev = self.incMsg[length-2]
      caps = self.incMsg[length-1]
      if self.verbose:
	print( ser )
      self.hive.new_bee( ser, libv, rev, caps )
    else:
      ser = "".join( map(chr, self.incMsg[1:length-3] ) )
      libv = self.incMsg[length-3]
      rev = self.incMsg[length-2]
      caps = self.incMsg[length-1]
      print( "received malformatted serial number", ser, self.incMsg )
    #print "end parsing serial"

  def wait_config( self ):
    if self.verbose:
      print( "waiting configuration" )
      print( self.incMsg )
    self.hive.wait_config( self.incMsg[1], self.incMsg[2] ) # minibee id, config id
    #print "end waiting configuration"

  def confirm_config( self ):
    if self.verbose:
      print( "confirming configuration" )
      print( self.incMsg )
    self.hive.check_config( self.incMsg[1], self.incMsg[2], self.incMsg[3:] )
    #print "end confirming configuration"

  def recv_data( self ):
    #print "receiving data"
    #print self.incMsg
    if len(self.incMsg) > 3:
      self.hive.new_data( self.incMsg[1], self.incMsg[2], self.incMsg[3:] )
    #print "end receiving data"
    if self.verbose:
      print( "receiving data from minibee", self.incMsg[1], self.incMsg[2], self.incMsg[3:] )

  def active( self ):
    #print "receiving data"
    #print self.incMsg
    if len(self.incMsg) == 2:
      self.hive.bee_active( self.incMsg[1], self.incMsg[2] )
    #print "end receiving data"
    if self.verbose:
      print( "active minibee", self.incMsg[1], self.incMsg[2] )

  def pausing( self ):
    #print "receiving data"
    #print self.incMsg
    if len(self.incMsg) == 2:
      self.hive.bee_pausing( self.incMsg[1], self.incMsg[2] )
    #print "end receiving data"
    if self.verbose:
      print( "pausing minibee", self.incMsg[1], self.incMsg[2] )


  def display_data( self ):
    #print "receiving data"
    if self.verbose:
      print( chr(self.incMsg[0]), self.incMsg )
    #self.hive.new_data( self.incMsg[1], self.incMsg[2], self.incMsg[3:] )
    #print "end receiving data"

  def set_log_action( self, action ):
    self.logAction = action

  def log_data( self ):
    #print "receiving data"
    if self.logAction != None :
      self.logAction( self.incMsg )
    #else:
      #print self.incMsg
    #self.hive.new_data( self.incMsg[1], self.incMsg[2], self.incMsg[3:] )
    #print "end receiving data"

  def parse_message( self ):
    #print self.incType
    #print self.incType, chr( self.incType ), self.incMsg
    if type( self.incType ) == int:
      incTypeChr = chr( self.incType )
    else:
      incTypeChr = 'u' # unknown message
    #incTypeChr = self.incType
    if incTypeChr == 's' : # serial from minibee device
      self.parse_serial()
    elif incTypeChr == 'w' : # minibee waiting for configuration
      self.wait_config()
    elif incTypeChr == 'c' : # minibee confirming configuration
      self.confirm_config()
    elif incTypeChr == 'd' : # minibee sending data
      self.recv_data()
    elif incTypeChr == 'a' : # minibee confirming it's active
      self.active()
    elif incTypeChr == 'p' : # minibee confirming it's pausing
      self.pausing()
    elif incTypeChr == 'i' : # minibee sending info (debugging mostly)
      self.display_data()
    elif incTypeChr == 'x' : # minibee sending info (debugging mostly)
      self.display_data()
    else:
      print( incTypeChr, self.incMsg )
    self.log_data()
    # add loopback
    #res = messageTypes.get( chr(incType) )()
    
  def read_byte( self, nrbytes ):
    #global escape, incMsg, incType
    b = self.serial.read( nrbytes )
    if len( b ) > 0 :
      for byt in b:
	newbyte = ord( byt )
	#print len(b), byt, newbyte
	if self.escape:
	  if newbyte in [ 10, 13, 92 ] :
	    self.incMsg.append( newbyte )
	  else :
	    self.incMsg.append( newbyte )
	    self.incType = newbyte
	  self.escape = False
	else :
	  if newbyte == 92:
	    self.escape = True
	  elif newbyte == 10:
	    #end of line
	    self.parse_message()
	    self.incMsg = []
	    self.incType = 'n'
	  else :
	    self.incMsg.append( newbyte )

  def read_data( self ):
    bytes_toread = self.serial.inWaiting()  
    self.read_byte( bytes_toread )

# end of HiveSerial
