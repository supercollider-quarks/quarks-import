# -*- coding: utf-8 -*-
import serial
import time
import sys
import os

#print time

from pydon.minibeexml import minibeexml

from collections import deque

### convenience function
def find_key(dic, val):
  #
  """return the key of dictionary dic given the value"""
  #
  return [k for k, v in dic.iteritems() if v == val][0]

### end convenience function

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

  def send_data( self, mid, data ):
    self.incMsgID()
    #self.hiveMsgId = self.hiveMsgId + 1
    msg = bytearray(b" O")
    msg[0] = chr( 92 )
    msg = self.appendToMsg( msg, mid )
    msg = self.appendToMsg( msg, self.hiveMsgId )
    #msg += chr(configid)
    for dat in data:
      msg = self.appendToMsg( msg, dat )
    msg += b"\n"
    self.serial.write( msg )
    if self.verbose:
      print( "sending data to minibee", mid, data, msg )

  def send_custom( self, mid, data ):
    self.incMsgID()
    #self.hiveMsgId = self.hiveMsgId + 1
    msg = bytearray(b" E")
    msg[0] = chr( 92 )
    msg = self.appendToMsg( msg, mid )
    msg = self.appendToMsg( msg, self.hiveMsgId )
    #msg += chr(configid)
    for dat in data:
      msg = self.appendToMsg( msg, dat )
    msg += b"\n"
    self.serial.write( msg )
    if self.verbose:
      print( "sending custom data to minibee", mid, data, msg )
    
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
    msg = bytearray(b" O")
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


# beginning of MiniHive:

class MiniHive(object):
  def __init__(self, serial_port, baudrate = 57600 ):
    #self.minibeeCount = 0
    self.name = ""
    self.bees = {}
    self.mapBeeToSerial = {}
    self.configs = {}
    self.serial = HiveSerial( serial_port, baudrate )
    self.serial.set_hive( self )
    self.serial.announce()
    self.running = True
    self.newBeeAction = None
    self.verbose = False
    self.redundancy = 5
    
  def set_verbose( self, onoff ):
    self.verbose = onoff
    self.serial.set_verbose( onoff )
  
  def set_id_range( self, minid, maxid ):
    self.idrange = range( minid, maxid )
    self.idrange.reverse()
    
  def get_new_minibee_id( self, rid = None ):
    if len( self.idrange ) == 0:
      print( "no more minibee ids available; increase the available number in the startup options!", self.idrange )
      return None
    if rid != None:
      # get a given id, if within range
      if rid in self.idrange:
	self.idrange.remove( rid )
	return rid
      else:
	print( "requested minibee id not in available range", rid, self.idrange )
	return None
    else: # get a new id
      rid = self.idrange.pop()
      #print self.idrange, rid
      return rid

  def run( self ):
    while self.running:
      self.serial.read_data()
      for beeid, bee in self.bees.items():
	#print beeid, bee
	if bee.status == 'waiting':
	  bee.waiting = bee.waiting + 1
	  if bee.waiting > 1000:
	    self.wait_config( beeid, bee.cid )
	    self.serial.send_me( bee.serial, 1 )
	else:
	  bee.repeat_output( self.serial, self.redundancy )
	  if bee.status == 'receiving':
	    bee.count = bee.count + 1
	    if bee.count > 5000:
	      bee.count = 0
	      self.serial.send_me( bee.serial, 0 )
      time.sleep(0.02)

  def exit( self ):
    self.serial.quit()
    
  def map_serial_to_bee( self, serial, mid ):
    if serial in self.mapBeeToSerial:
      oldid = self.mapBeeToSerial[ serial ]
      if oldid == mid: # do nothing
	return True
      else: # the minibee had an id, and we are assigning a new one
	minibee = self.bees[ oldid ]
	# remove the bee from the list at the old id
	del self.bees[ oldid ]
	# add it with the new id
	minibee.set_nodeid = mid
	self.bees[ mid ] = minibee
    else:
      if mid in self.bees: # another bee has this ID, return an error message!
	print( "There is already a MiniBee with ID %i (with serial number %s), please assign a different ID"%(mid,self.bees[mid].serial) )
	return False
      else: # there is no minibee with that id, and no minibee yet with the serial, so create a new one:
	minibee = MiniBee( mid, serial )
	self.bees[ mid ] = minibee
	self.mapBeeToSerial[ serial ] = mid
    # if we got to the end, all went well
    return True

  def set_pin_configuration( self, cid, pincfg ):
    if not cid in self.configs:
      print( "There is no configuration with ID %i"%(cid) )
      return False
    config = self.configs[ cid ]
    config.setPinConfig( pincfg[0], pincfg[1] )
    config.setPinLabel( pincfg[0], pincfg[2] )
    return True

  def set_twi_configuration( self, cid, twicfg ):
    if not cid in self.configs:
      print( "There is no configuration with ID %i"%(cid) )
      return False
    config = self.configs[ cid ]
    config.setTwiConfig( twicfg[0], twicfg[1] )
    config.setTwiLabel( twicfg[0], twicfg[2] )
    return True

  def delete_configuration( self, cid ):
    if not cid in self.configs:
      print( "There is no configuration with ID %i"%(cid) )
      return False
    del self.configs[ cid ]
    return True
    
  def query_configurations( self, network ):
    for cid, config in self.configs.items():
      print( cid, config )
      network.infoConfig( config.getConfigInfo() )

  def set_configuration( self, cid, config ):
    if cid in self.configs:
      # cid already exists - should not overwrite it
      print( "There is already a configuration with ID %i, please use a different ID, or delete this configuration first"%(cid) )
      return False
    newconfig = MiniBeeConfig( cid, config[0], config[1], config[2] ) # cid, name, samples per message, message_interval
    self.configs[ cid ] = newconfig
    numberPins = config[3]
    numberTWIs = config[4]
    print( len( config ), numberPins, numberTWIs )
    if numberTWIs > 0:
      newconfig.setPinConfig( 'A5', 'TWIClock' )
      newconfig.setPinConfig( 'A4', 'TWIData' )
    pinid = 5
    for pin in range(numberPins):
      newconfig.setPinConfig( config[ pinid ], config[ pinid+1 ] )
      newconfig.setPinLabel( config[ pinid ], config[ pinid+2 ] )
      pinid = pinid + 3
    #twis = config[(numberPins*2 + 5): (numberPins*2+numberTWIs*2+4) ]
    #print ( len( twis ) )
    for tw in range(numberTWIs):
      newconfig.setTwiConfig( config[ pinid ], config[ pinid+1 ] )
      newconfig.setTwiLabel( config[ pinid ], config[ pinid+2 ] )
      pinid = pinid + 3
    # update any minibees which should have this config (only if their config number was updated recently
    for beeid, bee in self.bees.items():
      if bee.cid == cid and bee.has_new_config_id():
	bee.set_config( cid, newconfig )
	self.serial.send_id( bee.serial, bee.nodeid, bee.cid )
	bee.set_status( 'waiting' )
	bee.waiting = 0
    return True
    
  def set_minibee_config( self, mid, cid ):
    if mid in self.bees: # if the minibee exists
      minibee = self.bees[ mid ]      
      if cid in self.configs:
	config = self.configs[ cid ]
	minibee.set_config( cid, config )
      else:
	minibee.set_config_id( cid )
      self.serial.send_id( minibee.serial, minibee.nodeid, minibee.cid )
      minibee.set_status( 'waiting' )
      minibee.waiting = 0
      if self.newBeeAction:
	self.newBeeAction( minibee )

  def new_bee( self, serial, libv, rev, caps ):
    # see if we already have this serial number in our config or minibee set, if so use that minibee
    #self.minibeeCount += 1
    if serial in self.mapBeeToSerial:
      # we already know the minibee, so send it its id and configid
      minibee = self.bees[ self.mapBeeToSerial[ serial ] ]
    else:
      # new minibee, so generate a new id
      mid = self.get_new_minibee_id()
      minibee = MiniBee( mid, serial )
      minibee.set_lib_revision( libv, rev, caps )
      self.bees[ mid ] = minibee
      self.mapBeeToSerial[ serial ] = mid
     
    #print minibee
    if minibee.cid > 0:
      self.serial.send_id( serial, minibee.nodeid, minibee.cid )
      minibee.set_status( 'waiting' )
      minibee.waiting = 0
    else: # this could be different behaviour! e.g. wait for a new configuration to come in
      print( "no configuration defined for minibee", serial, minibee.nodeid )
      self.write_to_file( "newconfig.xml" )
      print( "newconfig.xml saved, please adapt and save to a new name and restart the swpydonhive with that configuration file" )
      print( "or send a message with a new configuration (via osc, or via the datanetwork)" )
      #sys.exit()
    
    if self.newBeeAction:
      self.newBeeAction( minibee )
    
  def set_newBeeAction( self, action ):
    self.newBeeAction = action
  
  
  def new_data( self, beeid, msgid, data ):
    if beeid in self.bees:
      self.bees[beeid].parse_data( msgid, data, self.verbose )
    else:
      print( "received data from unknown minibee", beeid, msgid, data )
    if self.verbose:
      print( "received new data", beeid, msgid, data )
    # find minibee, set data to it

  def bee_active( self, beeid, msgid ):
    if beeid in self.bees:
      self.bees[beeid].set_status( 'active', msgid )
    else:
      print( "received active message from unknown minibee", beeid, msgid )
    if self.verbose:
      print( "received active message", beeid, msgid )
    # find minibee, set data to it

  def bee_paused( self, beeid, msgid ):
    if beeid in self.bees:
      self.bees[beeid].set_status( 'pausing', msgid )
    else:
      print( "received paused message from unknown minibee", beeid, msgid )
    if self.verbose:
      print( "received paused message", beeid, msgid )
    # find minibee, set data to it

  def wait_config( self, beeid, configid ):
    #print "sending configuration"
    if beeid in self.bees:
      #print beeid, configid
      if configid == self.bees[ beeid ].cid:
	self.serial.send_me( self.bees[ beeid ].serial, 1 )
	configuration = self.configs[ configid ]
	configMsg = configuration.getConfigMessage( self.bees[ beeid ].revision )
	self.bees[ beeid ].set_status( 'waiting' )
	self.bees[ beeid ].waiting = 0
	if self.verbose:
	  print( "sent configmessage to minibee", configMsg )
	self.serial.send_config( beeid, configMsg )
      else:
	print( "received wait for config from known minibee, but with wrong config", beeid, configid )
    else:
      print( "received wait for config from unknown minibee", beeid, configid )
    #print "end sending configuration"

  def check_config( self, beeid, configid, confirmconfig ):
    #print "confirming configuration"
    if beeid in self.bees:
      if not self.bees[beeid].check_config( configid, confirmconfig, self.verbose ):
	self.wait_config( beeid, configid )
	print( "minibee", beeid, "is not configured yet" )
      else:
	print( "minibee %i is configured"%beeid )
	self.serial.send_me( self.bees[beeid].serial, 0 )
    else:
      print( "received configuration confirmation from unknown minibee", beeid, configid, confirmconfig )
    #minibee.set_config( configuration )
    #serial.send_config( configuration )
    #print "end confirming configuration"
    
    
  def load_from_file( self, filename ):
    cfgfile = minibeexml.HiveConfigFile()
    hiveconf = cfgfile.read_file( filename )
    #print hiveconf
    #print hiveconf[ 'configs' ]
    self.name = hiveconf[ 'name' ]
    for cid, config in hiveconf[ 'configs' ].items():
      #print cid, config
      self.configs[ int( cid ) ] = MiniBeeConfig( config[ 'cid' ], config[ 'name' ], config[ 'samples_per_message' ], config[ 'message_interval' ] )
      #print config[ 'pins' ]
      self.configs[ int( cid ) ].setPins( config[ 'pins' ] )
      self.configs[ int( cid ) ].setPinLabels( config[ 'pinlabels' ] )
      self.configs[ int( cid ) ].setTWIs( config[ 'twis' ] )
      self.configs[ int( cid ) ].setTwiLabels( config[ 'twilabels' ] )
      #print self.configs[ int( cid ) ]
    for ser, bee in hiveconf[ 'bees' ].items():
      #print bee
      self.mapBeeToSerial[ ser ] = bee[ 'mid' ]
      self.bees[ bee[ 'mid' ] ] = MiniBee( bee[ 'mid' ], bee[ 'serial' ] )
      self.bees[ bee[ 'mid' ] ].set_lib_revision( bee[ 'libversion' ], bee[ 'revision' ], bee[ 'caps' ] )
      #print bee[ 'configid' ]
      thisconf = self.configs[ bee[ 'configid' ] ]
      self.bees[ bee[ 'mid' ] ].set_config( bee[ 'configid' ], self.configs[ bee[ 'configid' ] ] )

  def write_to_file( self, filename ):
    cfgfile = minibeexml.HiveConfigFile()
    #hiveconf = {}
    #hiveconf[ 'name' ] = filename
    #hiveconf[ 'configs' ] = {}
    #for confid, conf in self.configs.items():
      #hiveconf[ 'configs' ][ confid ] = conf.getConfigForFile()
    #hiveconf[ 'bees' ] = {}
    #for beeid, bee in self.bees.items():
      #hiveconf[ 'bees' ][ beeid ] = bee.getBeeForFile()

    cfgfile.write_file( filename, self )

    #self.bees = {}
    #self.mapBeeToSerial = {}
    #self.configs = {}
    


# end of MiniHive

# MiniBee Config

class MiniBeeConfig(object):

  analogPins = ['A0', 'A1', 'A2', 'A3', 'A4', 'A5', 'A6', 'A7']
  digitalPins = [ 'D2', 'D3', 'D4', 'D5', 'D6', 'D7', 'D8', 'D9', 'D10', 'D11', 'D12', 'D13' ]
  #pinNames =  
  miniBeePinConfig = { 'NotUsed': 0, 'DigitalIn': 1, 'DigitalOut': 2, 'AnalogIn': 3, 'AnalogOut': 4, 'AnalogIn10bit': 5, 'SHTClock': 6, 'SHTData': 7, 'TWIClock': 8, 'TWIData': 9, 'Ping': 10, 'Custom': 100, 'Me': 150, 'UnConfigured': 200 };

  miniBeeTwiConfig = { 'ADXL345': 10, 'LIS302DL': 11, 'BMP085': 20, 'TMP102': 30, 'HMC58X3': 40 };
  miniBeeTwiDataSize = { 'ADXL345': [2,2,2], 'LIS302DL': [2,2,2], 'BMP085': [2,3,3], 'TMP102': [2], 'HMC58X3': [2,2,2] };
  miniBeeTwiDataScale = { 'ADXL345': [8191,8191,8191], 'LIS302DL': [255,255,255], 'BMP085': [100,100,100], 'TMP102': [16], 'HMC58X3': [2047,2047,2047] }; # 
  miniBeeTwiDataOffset = { 'ADXL345': [0,0,0], 'LIS302DL': [0,0,0], 'BMP085': [27300,0,10000], 'TMP102': [2048], 'HMC58X3': [2048,2048,2048] };
  miniBeeTwiDataLabels = { 'ADXL345': ['acceleration'], 'LIS302DL': ['acceleration'], 'BMP085': ['temperature','altitude','barometric_pressure'], 'TMP102': ['temperature'], 'HMC58X3': ['magnetometer'] };

  def __init__(self, cfgid, cfgname, cfgspm, cfgmint ):
    self.name = cfgname
    self.pins = {}
    self.twis = {}
    self.pinlabels = {}
    self.twilabels = {}
    self.configid = cfgid
    self.samplesPerMessage = cfgspm
    self.messageInterval = cfgmint
    self.dataInSizes = []
    self.dataScales = []
    self.dataOffsets = []
    self.dataOutSizes = []
    self.logDataFormat = []
    self.logDataLabels = []

  #def getConfigForFile( self ):
    #fileconf = {}
    #fileconf[ 'cid' ] = self.configid
    #fileconf[ 'name' ] = self.name
    #fileconf[ 'samples_per_message' ] = self.samplesPerMessage
    #fileconf[ 'message_interval' ] = self.messageInterval
    #fileconf[ 'pins' ] = self.getPinsForFile()
    #return fileconf

  #def getPinsForFile( self ):
    #filepins = {}
    #for pinname, pinconf in self.pins:
      #filepins[ pinname ] = pinconf
    #return filepins
      
  def setPins( self, filepins ):
    #print filepins
    for pinname, pinfunc in filepins.items():
      #print pinname, pinfunc
      self.setPinConfig( pinname, pinfunc ) 

  def setTWIs( self, filetwis ):
    #print filepins
    for pinname, pinfunc in filetwis.items():
      #print pinname, pinfunc
      self.setTwiConfig( pinname, pinfunc ) 

  def setPinLabels( self, filepins ):
    #print filepins
    for pinname, pinfunc in filepins.items():
      #print pinname, pinfunc
      self.setPinLabel( pinname, pinfunc ) 

  def setTwiLabels( self, filepins ):
    #print filepins
    for pinname, pinfunc in filepins.items():
      #print pinname, pinfunc
      self.setTwiLabel( pinname, pinfunc ) 

  def setPinLabel( self, pinname, pinconfig ):
    self.pinlabels[ pinname ] = pinconfig

  def setTwiLabel( self, pinname, pinconfig ):
    self.twilabels[ pinname ] = pinconfig

  def setPinConfig( self, pinname, pinconfig ):
    if isinstance( pinconfig, int ):
      pinconfig = find_key( MiniBeeConfig.miniBeePinConfig, pinconfig )
    self.pins[ pinname ] = pinconfig

  def setTwiConfig( self, tid, twidev ):
    if isinstance( twidev, int ):
      pinconfig = find_key( MiniBeeConfig.miniBeeTwiConfig, twidev )
    self.twis[ str(tid) ] = twidev

  def getConfigInfo( self ):
    #print "-----MAKING CONFIG MESSAGE------"
    configInfo = []
    configInfo.append( self.configid )
    configInfo.append( self.name )
    configInfo.append( self.samplesPerMessage )
    configInfo.append( self.messageInterval )

    configInfo.append( len( self.pins ) )
    configInfo.append( len( self.twis ) )
    
    for pid, pincf in self.pins.items():
      configInfo.append( pid )
      configInfo.append( pincf )
      
    for twid, twidev in self.twis.items():
      configInfo.append( "TWI%s"%twid )
      configInfo.append( twidev )
    return configInfo

  def getConfigMessage( self, revision ):
    #print "-----MAKING CONFIG MESSAGE------"
    configMessage = []
    configMessage.append( self.configid )
    configMessage.append( self.messageInterval / 256 )
    configMessage.append( self.messageInterval % 256 )
    configMessage.append( self.samplesPerMessage )
    if revision == 'Z':
      digpins = MiniBeeConfig.digitalPins
      anapins = MiniBeeConfig.analogPins[:6]
    else :
      digpins = MiniBeeConfig.digitalPins[1:]
      anapins = MiniBeeConfig.analogPins
    print( digpins, anapins )
    for pinname in digpins:
      if pinname in self.pins:
	configMessage.append( MiniBeeConfig.miniBeePinConfig[ self.pins[ pinname ] ] )
      else:
	configMessage.append( MiniBeeConfig.miniBeePinConfig[ 'UnConfigured' ] )

    for pinname in anapins:
      if pinname in self.pins:
	configMessage.append( MiniBeeConfig.miniBeePinConfig[ self.pins[ pinname ] ] )
      else:
	configMessage.append( MiniBeeConfig.miniBeePinConfig[ 'UnConfigured' ] )

    configMessage.append( len( self.twis ) )
    #print self.twis
    for twid, twidev in self.twis.items():
      #print twid, twidev
      configMessage.append( MiniBeeConfig.miniBeeTwiConfig[ twidev ] )
    #print configMessage
    #print "-----END MAKING CONFIG MESSAGE------"
    return configMessage

  def check_config( self, libv, rev ):
    #print "-----CHECKING CONFIG------"
    self.dataInSizes = []
    self.dataScales = []
    self.dataOffsets = []
    self.dataOutSizes = []
    self.logDataFormat = []
    self.logDataLabels = []

    if rev == 'Z':
      digpins = MiniBeeConfig.digitalPins
      anapins = MiniBeeConfig.analogPins[:6]
    else :
      digpins = MiniBeeConfig.digitalPins[1:]
      anapins = MiniBeeConfig.analogPins
    print( digpins, anapins )

    for pinname in anapins: # iterate over analog pins
      if pinname in self.pins:
	if self.pins[ pinname ] == 'AnalogIn':
	  self.dataInSizes.append( 1 )
	  self.dataScales.append( 255 )
	  self.dataOffsets.append( 0 )
	  self.logDataFormat.append( 1 )
	  self.logDataLabels.append( self.pinlabels[ pinname ] )

	elif self.pins[ pinname ] == 'AnalogIn10bit':
	  self.dataInSizes.append( 2 )
	  self.dataScales.append( 1023 )
	  self.dataOffsets.append( 0 )
	  self.logDataFormat.append( 1 )
	  self.logDataLabels.append( self.pinlabels[ pinname ] )

    for pinname in digpins: # iterate over digital pins
      if pinname in self.pins:
	if self.pins[ pinname ] == 'AnalogOut':
	  self.dataOutSizes.append( 1 )

    for pinname in digpins + anapins: # iterate over all pins
      if pinname in self.pins:
	if self.pins[ pinname ] == 'DigitalIn':
	  self.dataInSizes.append( 1 )
	  self.dataScales.append( 1 )
	  self.dataOffsets.append( 0 )
	  self.logDataFormat.append( 1 )
	  self.logDataLabels.append( self.pinlabels[ pinname ] )
	elif self.pins[ pinname ] == 'DigitalOut':
	  self.dataOutSizes.append( 1 )

    for pinname in anapins: # iterate over analog pins
      if pinname in self.pins:
	if self.pins[ pinname ] == 'TWIData':
	  #print "library version" , libv
	  if libv <= 2:
	    #print "libv 2, revision" , rev
	    if rev == 'A':
	      self.dataInSizes.extend( [1,1,1] )
	      self.dataScales.extend( [255,255,255] )
	      self.dataOffsets.extend( [0,0,0] )
	      self.logDataFormat.append( 3 )
	      self.logDataLabels.extend( MiniBeeConfig.miniBeeTwiDataLabels['LIS302DL'] )
	      print( self.dataInSizes )
	    elif rev == 'B':
	      self.dataInSizes.extend( [2,2,2] )
	      #self.dataScales.extend( [1,1,1] )
	      self.dataScales.extend( [8191,8191,8191] )
	      self.dataOffsets.extend( [0,0,0] )
	      self.logDataFormat.append( 3 )
	      self.logDataLabels.extend( MiniBeeConfig.miniBeeTwiDataLabels['ADXL345'] )
	      print( self.dataInSizes )
	  elif libv > 2:
	    #print "libv 3, checking twis"
	    for twiid, twidev in self.twis.items():
	      self.dataInSizes.extend( MiniBeeConfig.miniBeeTwiDataSize[ twidev ] )
	      self.dataScales.extend( MiniBeeConfig.miniBeeTwiDataScale[ twidev ] )
	      self.dataOffsets.extend( MiniBeeConfig.miniBeeTwiDataOffset[ twidev ] )
	      self.logDataFormat.append( len( MiniBeeConfig.miniBeeTwiDataOffset[ twidev ] ) / len( MiniBeeConfig.miniBeeTwiDataLabels[ twidev ] ) )
	      self.logDataLabels.extend( MiniBeeConfig.miniBeeTwiDataLabels[ twidev ] )
	      #print self.dataInSizes

    for pinname in digpins + anapins: # iterate over all pins
      if pinname in self.pins:
	if self.pins[ pinname ] == 'SHTData':
	  self.dataInSizes.extend( [2,2] )
	  self.dataScales.extend( [1,1] )
	  self.dataOffsets.extend( [0,0] )
	  self.logDataFormat.extend( [1,1] )
	  self.logDataLabels.extend( ['temperature','humidity'] )

    for pinname in digpins + anapins: # iterate over all pins
      if pinname in self.pins:
	if self.pins[ pinname ] == 'Ping':
	  self.dataInSizes.append( 2 )
	  self.dataScales.append( 61.9195 )
	  self.dataOffsets.append( 0 )
	  self.logDataFormat.append( 1 )
	  self.logDataLabels.append( self.pinlabels[ pinname ] )  

    #print self.dataInSizes, self.dataOutSizes, self.dataScales
    #print self.logDataFormat, self.logDataLabels
    #print "-----END CHECKING CONFIG------"
   
# end MiniBee Config

#minibee_example_config = [ 0, 50, 1 ]
#minibee_example_pinconfig = [
#// null, config id, msgInt high byte, msgInt low byte, samples per message
  #'NotUsed', 'NotUsed', 'NotUsed', 'NotUsed', 'NotUsed', 'NotUsed',  #// D3 to D8
  #'NotUsed', 'NotUsed', 'NotUsed', 'NotUsed', 'NotUsed',  #// D9,D10,D11,D12,D13
  #'NotUsed', 'NotUsed', 'NotUsed', 'NotUsed', 'TWIClock', 'TWIData', 'NotUsed', 'NotUsed' #// A0, A1, A2, A3, A4, A5, A6, A7
#];



# class minibee
class MiniBee(object):
  def __init__(self, mid, serial ):
    self.init_with_serial( mid, serial )
  
  def set_lib_revision( self, libv, revision, caps ):
    self.libversion = libv
    if isinstance( revision, int ):
      self.revision = chr( revision )
    else:
      self.revision = revision
    self.caps = caps    
    
  def init_with_serial(self, mid, serial ):
    self.nodeid = mid
    self.serial = serial
    self.cid = -1
    self.status = 'init'
    self.logAction = None
    self.statusAction = None
    self.dataAction = None
    self.firstDataAction = None
    #self.configid = -1
    self.waiting = 0
    self.count = 0
    self.outrepeated = 0
    self.outdata = None
    
  def set_nodeid( self, mid ):
    self.nodeid = mid

  #def getBeeForFile( self ):
    #filebee = {}
    #filebee[ 'mid' ] = self.nodeid
    #filebee[ 'serial' ] = self.serial
    #filebee[ 'libversion' ] = self.libversion
    #filebee[ 'revision' ] = self.revision
    #filebee[ 'caps' ] = self.caps
    #filebee[ 'configid' ] = self.configid
    #return filebee

  def set_config_id( self, cid ):
    self.cid = cid
    
  def has_new_config_id( self ):
    if self.config.configid != self.cid:
      return True
    return False

  def set_config(self, cid, configuration ):
    self.cid = cid
    self.config = configuration
    self.config.check_config( self.libversion, self.revision )

  def set_log_action( self, action ):
    self.logAction = action

  def set_action( self, action ):
    self.dataAction = action

  def set_status_action( self, action ):
    self.statusAction = action

  def set_first_action( self, action ):
    self.firstDataAction = action
    
  def repeat_output( self, serPort, redundancy ):
    if self.outdata != None:
      if self.outrepeated < redundancy :
	self.outrepeated = self.outrepeated + 1
	serPort.send_data( self.nodeid, self.outdata )

  def send_output( self, serPort, data ):
    if len( data ) == sum( self.config.dataOutSizes ) :
      self.outdata = data
      self.outrepeated = 0
      serPort.send_data( self.nodeid, data )

  def send_custom( self, serPort, data ):
    #if len( data ) == sum( self.config.customOutSizes ) :
    serPort.send_custom( self.nodeid, data )

  def set_run( self, serPort, status ):
    serPort.send_run( self.nodeid, status )
  
  def set_loopback( self, serPort, status ):
    serPort.send_loop( self.nodeid, status )
    
  def set_status( self, status, msgid = 0, verbose = False ):
    self.status = status
    if self.statusAction != None :
      self.statusAction( self.nodeid, self.status )
    if verbose:
      print( "minibee status changed: ", self.nodeid, self.status ) 

  def parse_data( self, msgid, data, verbose = False ):
    # to do: add msgid check
    idx = 0
    parsedData = []
    scaledData = []
    for sz in self.config.dataInSizes:
      parsedData.append( data[ idx : idx + sz ] )
      idx += sz
    for index, dat in enumerate( parsedData ):
      if len( dat ) == 3 :
	scaledData.append(  float( dat[0] * 65536 + dat[1]*256 + dat[2] - self.config.dataOffsets[ index ] ) / float( self.config.dataScales[ index ] ) )
      if len( dat ) == 2 :
	scaledData.append(  float( dat[0]*256 + dat[1] - self.config.dataOffsets[ index ] ) / float( self.config.dataScales[ index ] ) )
      if len( dat ) == 1 :
	scaledData.append( float( dat[0] - self.config.dataOffsets[ index ] ) / float( self.config.dataScales[ index ] ) )
    self.data = scaledData
    if self.status != 'receiving':
      if self.firstDataAction != None:
	self.firstDataAction( self.nodeid, self.data )
    self.set_status( 'receiving' )
    if len(self.data) == len( self.config.dataInSizes ):
      if self.dataAction != None :
	self.dataAction( self.data, self.nodeid )
      if self.logAction != None :
	self.logAction( self.nodeid, self.getLabels(), self.getLogData() )
      if verbose:
	print( "data length ok", len(self.data), len( self.config.dataInSizes ) )
    #print self.nodeid, data, parsedData, scaledData
    else:
      print( "data length not ok", len(self.data), len( self.config.dataInSizes ) )
    if verbose:
      print( "data parsed and scaled", self.nodeid, self.data ) 
  
  def getLabels( self ):
    labels = self.config.logDataLabels
    #labels = self.config.pinlabels
    #labels.extend( self.config.twilabels )
    return labels
    
  def getLogData( self ):
    logdata = []
    index = 0
    for datasize in self.config.logDataFormat:
      logdata.append( self.data[ index : index + datasize ] )
      index += datasize
    return logdata

  def getInputSize( self ):
    if self.cid > 0:
      return len( self.config.dataInSizes )
    return 0

  def getOutputSize( self ):
    if self.cid > 0:
      return len( self.config.dataOutSizes )
    return 0

  def check_config( self, configid, confirmconfig, verbose ):
    configres = True
    if configid == self.cid:
      self.config.check_config( self.libversion, self.revision )
      #print confirmconfig
      if confirmconfig[0] == self.config.samplesPerMessage:
	if verbose:
	  print( "samples per message correct", confirmconfig[0], self.config.samplesPerMessage )
      else:
	configres = False
	print( "ERROR: samples per message NOT correct", confirmconfig[0], self.config.samplesPerMessage )
      if (confirmconfig[1]*256 + confirmconfig[2]) == self.config.messageInterval :
	if verbose:
	  print( "message interval correct", confirmconfig[1:2], self.config.messageInterval )
      else:
	configres = False
	print( "ERROR: message interval NOT correct", confirmconfig[1:2], self.config.messageInterval )
      if confirmconfig[3] == sum( self.config.dataInSizes ):
	if verbose:
	  print( "data input size correct", confirmconfig[3], self.config.dataInSizes )
      else:
	configres = False
	print( "ERROR: data input size NOT correct", confirmconfig[3], self.config.dataInSizes )
      if confirmconfig[4] == sum( self.config.dataOutSizes ):
	if verbose:
	  print( "data output size correct", confirmconfig[4], self.config.dataOutSizes )
      else:
	configres = False
	print( "ERROR: data output size NOT correct", confirmconfig[4], self.config.dataOutSizes )
      # to add custom in and out
      self.set_status( 'configured' )
    else:
      configres = False
      print( "ERROR: wrong config number", configid, self.cid )
    return configres
      

  def __str__(self):
    return "<minibee {id: %s, serial: %s, libversion: %s, revision: %s, caps: %s, configid: %s}>" % (self.nodeid, self.serial, self.libversion, self.revision, self.caps, self.cid )

# end of class minibee

# main program:


if __name__ == "__main__":
  parser = optparse.OptionParser(description='Create a pydonhive to get data from the minibee network.')
  parser.add_option('-c','--config', action='store', type="string", dest="config",default="pydon/configs/hiveconfig.xml",
		  help='the name of the configuration file for the minibees [default:%s]'% 'pydon/configs/hiveconfig.xml')
  parser.add_option('-m','--nr_of_minibees', type=int, action='store',dest="minibees",default=20,
		  help='the number of minibees in the network [default:%i]'% 20)
  parser.add_option('-v','--verbose', action='store',dest="verbose",default=False,
		  help='verbose printing [default:%i]'% False)
  parser.add_option('-s','--serial', action='store',type="string",dest="serial",default="/dev/ttyUSB0",
		  help='the serial port [default:%s]'% '/dev/ttyUSB0')

  (options,args) = parser.parse_args()

  def printDataAction( data, nodeid ):
    print( nodeid, data )

  hive = MiniHive( options.serial, 57600 )
  hive.set_id_range( 1, options.minibees )
  
  hive.load_from_file( options.config )
  hive.bees[ 1 ].set_action( printDataAction )
  hive.bees[ 2 ].set_action( printDataAction )
  hive.bees[ 3 ].set_action( printDataAction )
  hive.bees[ 4 ].set_action( printDataAction )
  #print hive
    
  #hive.write_to_file( "hiveconfig2.xml" )
  
  hive.run()

    #x = ser.readline()
    #if len( x ) > 0:
      #print x
      #hive_parsestring( x )

  hive.exit()
