# -*- coding: utf-8 -*-
import serial
import time
import sys
import os
import datetime
import math

import optparse
#print time

#from pydon 
import minibeexml
from hiveserial import HiveSerial # AT based communication
from hiveserialapi import HiveSerialAPI # API based communication (in development)

from collections import deque

## {{{ http://code.activestate.com/recipes/210459/ (r2)
class Queue:
    """A sample implementation of a First-In-First-Out
       data structure."""
    def __init__(self):
        self.in_stack = []
        self.out_stack = []
    def push(self, obj):
        self.in_stack.append(obj)
    def pop(self):
        if not self.out_stack:
            self.in_stack.reverse()
            self.out_stack = self.in_stack
            self.in_stack = []
        if len( self.out_stack ) > 0:
	  return self.out_stack.pop()
	else:
	  return None
## end of http://code.activestate.com/recipes/210459/ }}}

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


# beginning of MiniHive:

class MiniHive(object):
  def __init__(self, serial_port, baudrate = 57600, apiMode = False,
               poll = None ):
    #self.minibeeCount = 0
    self.name = ""
    self.bees = {}
    self.mapBeeToSerial = {}
    self.configs = {}
    self.apiMode = apiMode
    if self.apiMode:
      self.serial = HiveSerialAPI( serial_port, baudrate )
    else:
      self.serial = HiveSerial( serial_port, baudrate )
    self.serial.set_hive( self )
    self.serial.announce()
    self.running = True
    self.newBeeAction = None
    self.verbose = False
    self.redundancy = 10
    self.create_broadcast_bee()
    self.poll = poll
    
  def set_verbose( self, onoff ):
    self.verbose = onoff
    self.serial.set_verbose( onoff )
  
  def set_id_range( self, minid, maxid ):
    self.idrange = range( minid, maxid )
    self.idrange.reverse()
    
  def get_new_minibee_id( self, rid = None ):
    #print self.idrange
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
      if not self.apiMode:
	self.serial.read_data()
      for beeid, bee in self.bees.items():
	#print beeid, bee
	bee.countsincestatus = bee.countsincestatus + 1
	if bee.countsincestatus > 12000:
	  bee.set_status( 'off' )
	if bee.status == 'waiting':
	  bee.waiting = bee.waiting + 1
	  if bee.waiting > 1000:
	    self.wait_config( beeid, bee.cid )
	    self.serial.send_me( bee.serial, 1 )
	else:
	  bee.send_data( self.verbose )
	  bee.repeat_output( self.serial, self.redundancy )
	  bee.repeat_custom( self.serial, self.redundancy )
	  bee.repeat_run( self.serial, self.redundancy )
	  bee.repeat_loop( self.serial, self.redundancy )
	  #if bee.status == 'receiving':
	    #bee.count = bee.count + 1
	    #if bee.count > 5000:
	      #bee.count = 0
	      #self.serial.send_me( bee.serial, 0 )
      if self.poll:
        self.poll()
      else:
        time.sleep(0.001)

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
      #print( cid, config )
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
    #print( len( config ), numberPins, numberTWIs )
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
      #TODO: how to make labels for this?
      pinid = pinid + 3
    # update any minibees which should have this config (only if their config number was updated recently
    for beeid, bee in self.bees.items():
      if bee.cid == cid and bee.has_new_config_id():
	bee.set_config( cid, newconfig )
	self.serial.send_id( bee.serial, bee.nodeid, bee.cid )
	bee.set_status( 'waiting' )
	bee.waiting = 0
    return True
  
  def store_ids( self ):
    if self.apiMode:
      self.serial.store_remote_at16( 0xFFFF )
    
  def store_minibee_id( self, mid ):
    if self.apiMode:
      if mid in self.bees:
	minibee = self.bees[ mid ]
	self.serial.store_remote_at64( minibee.serial )

  def announce_minibee_id( self, mid ):
    if self.apiMode:
      if mid in self.bees:
	#minibee = self.bees[ mid ]
	self.serial.announce( mid )

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
	
  def create_broadcast_bee( self ):
    mid = 0xFFFF;
    minibee = MiniBee( mid, serial )
    minibee.set_lib_revision( 5, 'D', 0 )
    self.bees[ mid ] = minibee
    if self.newBeeAction: # and firsttimenewbee:  
      self.newBeeAction( minibee )    

  def new_bee_no_config( self, serial ):
    firsttimenewbee = False
    # see if we already have this serial number in our config or minibee set, if so use that minibee
    #self.minibeeCount += 1
    if serial in self.mapBeeToSerial:
      # we already know the minibee, so send it its id and configid
      minibee = self.bees[ self.mapBeeToSerial[ serial ] ]
    else:
      # new minibee, so generate a new id
      mid = self.get_new_minibee_id()
      minibee = MiniBee( mid, serial )
      minibee.set_lib_revision( 5, 'D', 0 )
      self.bees[ mid ] = minibee
      self.mapBeeToSerial[ serial ] = mid
      firsttimenewbee = True
      
    self.serial.send_id( serial, minibee.nodeid )
    if self.newBeeAction: # and firsttimenewbee:  
      self.newBeeAction( minibee )

  def new_bee( self, serial, libv, rev, caps, remConf = True ):
    firsttimenewbee = False
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
      firsttimenewbee = True
     
    #print minibee
    if remConf == 1:
      if minibee.cid > 0:
	self.serial.send_id( serial, minibee.nodeid, minibee.cid )
	#minibee.set_status( 'waiting' )
	minibee.waiting = 0
      elif firsttimenewbee: # this could be different behaviour! e.g. wait for a new configuration to come in
	print( "no configuration defined for minibee", serial, minibee.nodeid, minibee.name )
	filename ="newconfig_" + time.strftime("%Y_%b_%d_%H-%M-%S", time.localtime()) + ".xml"
	self.write_to_file( filename )
	print( "configuration saved to " + filename + ". Please adapt (at least define a config id other than -1 for the node), save to a new name," )
	print( "and restart the program with that configuration file. Alternatively send a message with a new configuration (via osc, or via the datanetwork)." )
	print( "Check documentation for details." )
      #sys.exit()
    else:
      self.serial.send_id( serial, minibee.nodeid )
      if firsttimenewbee: # this could be different behaviour! e.g. wait for a new configuration to come in
	print( "no configuration defined for minibee", serial, minibee.nodeid, minibee.name )
    if self.newBeeAction: # and firsttimenewbee:
      self.newBeeAction( minibee )
    
  def set_newBeeAction( self, action ):
    self.newBeeAction = action
  
  def new_data( self, beeid, msgid, data, rssi = 0 ):
    if self.verbose:
      print( "received new data", beeid, msgid, data )
    # find minibee, set data to it
    if beeid in self.bees:
      self.bees[beeid].parse_data( msgid, data, self.verbose )
    else:
      print( "received data from unknown minibee", beeid, msgid, data )
      if self.apiMode and beeid == 0xFFFA: #unconfigured minibee
	self.serial.announce( 0xFFFA )

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
    if hiveconf != None :
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
	self.configs[ int( cid ) ].setTwiSlotLabels( config[ 'twislots' ] )
	#print self.configs[ int( cid ) ]
      for ser, bee in hiveconf[ 'bees' ].items():
	#print bee
	mid = self.get_new_minibee_id( bee[ 'mid' ] )
	if mid == bee[ 'mid' ]:
	  self.mapBeeToSerial[ ser ] = bee[ 'mid' ]
	  if self.apiMode:
	    if len( bee['serial'] ) == 14:
	      self.bees[ bee[ 'mid' ] ] = MiniBee( bee[ 'mid' ], "00" + bee[ 'serial' ] )
	    else:
	      self.bees[ bee[ 'mid' ] ] = MiniBee( bee[ 'mid' ], bee[ 'serial' ] )
	  else: # not api mode
	    if len( bee['serial'] ) == 16:
	      self.bees[ bee[ 'mid' ] ] = MiniBee( bee[ 'mid' ], bee[ 'serial' ][2:] )
	    else:
	      self.bees[ bee[ 'mid' ] ] = MiniBee( bee[ 'mid' ], bee[ 'serial' ] )
	  self.bees[ bee[ 'mid' ] ].set_lib_revision( bee[ 'libversion' ], bee[ 'revision' ], bee[ 'caps' ] )
	else:
	  print( "warning trying to assign duplicate minibee id %i"%bee[ 'mid' ] )
	#print bee[ 'configid' ]
	#thisconf = self.configs[ bee[ 'configid' ] ]
	if bee[ 'configid' ] > 0:
	  self.bees[ bee[ 'mid' ] ].set_config( bee[ 'configid' ], self.configs[ bee[ 'configid' ] ] )
	if 'customdata' in bee:
	  self.bees[ bee[ 'mid' ] ].set_custom( bee[ 'customdata' ] )

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
  miniBeeTwiDataLabels = { 'ADXL345': ['accel_x','accel_y','accel_z'], 'LIS302DL': ['accel_x','accel_y','accel_z'], 'BMP085': ['temperature','barometric_pressure','altitude'], 'TMP102': ['temperature'], 'HMC58X3': ['magn_x','magn_y','magn_z'] };

  def __init__(self, cfgid, cfgname, cfgspm, cfgmint ):
    self.name = cfgname
    self.pins = {}
    self.twis = {}
    self.pinlabels = {}
    self.twilabels = {}
    self.twislotlabels = {}
    self.configid = cfgid
    self.samplesPerMessage = cfgspm
    self.messageInterval = cfgmint
    self.sampleInterval = self.messageInterval / self.samplesPerMessage
    self.dataInSizes = []
    self.dataScales = []
    self.dataOffsets = []
    self.dataOutSizes = []
    self.logDataFormat = []
    self.logDataLabels = []
    self.digitalIns = 0

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

  def setTwiSlotLabels( self, filepins ):
    #print filepins
    for pinname, pinfunc in filepins.items():
      #print pinname, pinfunc
      #for twislot, twislotlabel in pinfunc.items():
      self.twislotlabels[ pinname ] = pinfunc

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
    #print( digpins, anapins )
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

#MiniBeeConfig
  def check_config( self, libv, rev ):
    #print "-----CHECKING CONFIG------"
    self.digitalIns = 0
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
    #print( digpins, anapins )

    if libv >= 4:
      for pinname in digpins + anapins: # iterate over all pins
	if pinname in self.pins:
	  if self.pins[ pinname ] == 'DigitalIn':
	    self.digitalIns = self.digitalIns + 1
	    #self.dataInSizes.append( 1 )
	    self.dataScales.append( 1 )
	    self.dataOffsets.append( 0 )
	    self.logDataFormat.append( 1 )
	    self.logDataLabels.append( self.pinlabels[ pinname ] )
	  elif self.pins[ pinname ] == 'DigitalOut':
	    self.dataOutSizes.append( 1 )

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

    if libv <= 3:
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
	      #print( self.dataInSizes )
	    elif rev == 'B':
	      self.dataInSizes.extend( [2,2,2] )
	      #self.dataScales.extend( [1,1,1] )
	      self.dataScales.extend( [8191,8191,8191] )
	      self.dataOffsets.extend( [0,0,0] )
	      self.logDataFormat.append( 3 )
	      self.logDataLabels.extend( MiniBeeConfig.miniBeeTwiDataLabels['ADXL345'] )
	      #print( self.dataInSizes )
	  elif libv > 2:
	    #print "libv 3, checking twis"
	    sortedTwis = [ (k,self.twis[k]) for k in sorted(self.twis.keys())]
	    #print sortedTwis
	    for twiid, twidev in sortedTwis:
	      #print twiid, twidev
	      self.dataInSizes.extend( MiniBeeConfig.miniBeeTwiDataSize[ twidev ] )
	      self.dataScales.extend( MiniBeeConfig.miniBeeTwiDataScale[ twidev ] )
	      self.dataOffsets.extend( MiniBeeConfig.miniBeeTwiDataOffset[ twidev ] )
	      self.logDataFormat.append( len( MiniBeeConfig.miniBeeTwiDataOffset[ twidev ] ) / len( MiniBeeConfig.miniBeeTwiDataLabels[ twidev ] ) )
	      if twiid in self.twislotlabels:
		sortedSlotLabels = [ (k,self.twislotlabels[twiid][k]) for k in sorted(self.twislotlabels[ twiid ].keys())]
		for index, twislotlabel in sortedSlotLabels:
		  #print ( "before", index, twislotlabel )
		  if twislotlabel == None: # use the default
		    twislotlabel = MiniBeeConfig.miniBeeTwiDataLabels[ twidev ][ index ]
		  self.logDataLabels.append( twislotlabel )
		#print ("after", index, twislotlabel )
	      else:
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

    #if self.verbose:
    #print self.digitalIns, self.dataInSizes, self.dataOutSizes, self.dataScales
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
    self.msgID = 0
    self.lastRecvMsgID = 255
    self.name = "";
    self.customDataInSizes = []
    self.dataOffsets = []
    self.dataScales = []
    self.hasCustom = False
    self.customLabels = []
    self.customDataScales = []
    self.customDataOffsets = []
    self.dataQueue = Queue()
    #self.time_since_last_message = 0
    self.time_since_last_update = 0
      
  def incMsgID( self ):
    self.msgID = self.msgID + 1
    self.msgID = self.msgID%255
  
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
    
    self.countsincestatus = 0

    self.outrepeated = 0
    self.outMessage = None
    self.customrepeated = 0
    self.customMessage = None
    self.runrepeated = 0
    self.runMessage = None
    self.looprepeated = 0
    self.loopMessage = None
    
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
    self.dataScales = self.customDataScales
    self.dataOffsets = self.customDataOffsets
    self.dataScales.extend( self.config.dataScales )
    self.dataOffsets.extend( self.config.dataOffsets )

  def set_custom(self, customconf ):
    self.customLabels = []
    self.customDataScales = []
    self.customDataOffsets = []
    sortedConf = [ (k,customconf[k]) for k in sorted(customconf.keys())]
    #print sortedConf
    for cid, cdat in sortedConf:
      #print cid, cdat
      self.hasCustom = True
      self.customLabels.append( cdat[ "name" ] )
      self.customDataScales.append( cdat[ "scale" ] )
      self.customDataOffsets.append( cdat[ "offset" ] )
      self.customDataInSizes.append( cdat[ "size" ] )
    self.dataScales = self.customDataScales
    self.dataScales.extend( self.config.dataScales )
    self.dataOffsets = self.customDataOffsets
    self.dataOffsets.extend( self.config.dataOffsets )
    #print( self.customLabels, self.dataScales, self.dataOffsets, self.customDataInSizes )
    #if len(self.dataScales) == 0:
      #self.dataScales = self.config.dataScales
      #self.dataOffsets = self.config.dataOffsets

  def set_log_action( self, action ):
    self.logAction = action

  def set_action( self, action ):
    self.dataAction = action

  def set_status_action( self, action ):
    self.statusAction = action

  def set_first_action( self, action ):
    self.firstDataAction = action

  def create_msg( self, msgtype, data, serPort ):
    self.incMsgID()
    msgdata = serPort.create_beemsg( msgtype, self.msgID, data, self.nodeid )
    return msgdata
    
  def send_data( self, verbose = False ):
    if self.cid > 0:
      if self.config.samplesPerMessage > 1:
	self.time_since_last_update = self.time_since_last_update + 1
	#if time_since_last_message > self.messageInterval:
	  # timeout on data
	if self.time_since_last_update >= self.config.sampleInterval: # if time to send new sample:
	  newdata = self.dataQueue.pop()
	  if newdata != None:
	    self.parse_single_data( newdata, verbose )
	    self.time_since_last_update = 0

  def repeat_output( self, serPort, redundancy ):
    if self.outMessage != None:
      if self.outrepeated < redundancy :
	self.outrepeated = self.outrepeated + 1
	serPort.send_msg( self.outMessage, self.nodeid )
	#serPort.send_data( self.nodeid, self.msgID, self.outdata )

  def send_output( self, serPort, data ):
    if self.cid > 0:
      if len( data ) == sum( self.config.dataOutSizes ) :
	self.outdata = data
	self.outrepeated = 0
	self.outMessage = self.create_msg( 'O', self.outdata, serPort )
	serPort.send_msg( self.outMessage, self.nodeid )
	#serPort.send_data_inclid( self.nodeid, self.msgID, data )
    elif self.nodeid == 0xFFFF: #broadcast node
      self.outdata = data
      self.outrepeated = 0
      self.outMessage = self.create_msg( 'O', self.outdata, serPort )
      serPort.send_msg( self.outMessage, self.nodeid )

  def repeat_custom( self, serPort, redundancy ):
    if self.customMessage != None:
      if self.customrepeated < redundancy :
	self.customrepeated = self.customrepeated + 1
	serPort.send_msg( self.customMessage, self.nodeid )
	#serPort.send_data( self.nodeid, self.msgID, self.outdata )

  def send_custom( self, serPort, data ):
    self.customdata = data
    self.customrepeated = 0
    self.customMessage = self.create_msg( 'E', self.customdata, serPort )
    serPort.send_msg( self.customMessage, self.nodeid )
    #if len( data ) == sum( self.config.customOutSizes ) :
    #serPort.send_custom( self.nodeid, data )

  def send_run( self, serPort, status ):
    self.runrepeated = 0
    self.runMessage = self.create_msg( 'R', [ status ], serPort )
    serPort.send_msg( self.runMessage, self.nodeid )

  def send_loopback( self, serPort, status ):
    self.looprepeated = 0
    self.loopMessage = self.create_msg( 'L', [ status ], serPort )
    serPort.send_msg( self.loopMessage, self.nodeid )

  def repeat_run( self, serPort, redundancy ):
    if self.runMessage != None:
      if self.runrepeated < redundancy :
	self.runrepeated = self.runrepeated + 1
	serPort.send_msg( self.runMessage, self.nodeid )
	#serPort.send_data( self.nodeid, self.msgID, self.outdata )

  def repeat_loop( self, serPort, redundancy ):
    if self.loopMessage != None:
      if self.looprepeated < redundancy :
	self.looprepeated = self.looprepeated + 1
	serPort.send_msg( self.loopMessage, self.nodeid )
	#serPort.send_data( self.nodeid, self.msgID, self.outdata )

  #def set_run( self, serPort, status ):
    # TODO: add redundancy
    #serPort.send_run( self.nodeid, status )
  
  ##def set_loopback( self, serPort, status ):
    # TODO: add redundancy
    #serPort.send_loop( self.nodeid, status )
    
  def set_status( self, status, msgid = 0, verbose = False ):
    if self.statusAction != None :
      if self.status != status:
	self.statusAction( self.nodeid, status )
    self.status = status
    self.countsincestatus = 0
    if verbose:
      print( "minibee status changed: ", self.nodeid, self.status ) 

  def parse_data( self, msgid, data, verbose = False ):
    # to do: add msgid check
    if verbose:
      print( "msg ids", msgid, self.lastRecvMsgID )
    if msgid != self.lastRecvMsgID:
      self.lastRecvMsgID = msgid
      #self.time_since_last_message = 0
      if self.cid > 0: # the minibee has a configuration
	if self.config.samplesPerMessage == 1:
	  self.parse_single_data( data, verbose )
	else: # multiple samples per message:
	  # TODO: adjust message interval to actually measured interval
	  # clump data into number of samples
	  blocks = self.config.samplesPerMessage
	  blocksize = len( data ) / blocks
	  for i in range( blocks ):
	    self.dataQueue.push( data[ i*blocksize: i*blocksize + blocksize ] )
      elif verbose:
	print( "no config defined for this minibee", self.nodeid, data )

	  
  def parse_single_data( self, data, verbose = False ):
    idx = 0
    parsedData = []
    scaledData = []
    for sz in self.customDataInSizes:
      parsedData.append( data[ idx : idx + sz ] )
      idx += sz
  #print "index after custom data in size", idx
    if self.config.digitalIns > 0:
      # digital data as bits
      nodigbytes = int(math.ceil(self.config.digitalIns / 8.))
      digstoparse = self.config.digitalIns
      digitalData = data[idx : idx + nodigbytes]
      idx += nodigbytes
      #print "index after digitalIn", idx, nodigbytes, digitalData
      for byt in digitalData:
	for j in range(0, min(digstoparse,8) ):
	  parsedData.append( [ min( (byt & ( 1 << j )), 1 ) ] )
	digstoparse -= 8
    #else: 
      # digital data as bytes

    for sz in self.config.dataInSizes:
      parsedData.append( data[ idx : idx + sz ] )
      idx += sz
    #print parsedData, self.dataScales, self.customDataScales

    for index, dat in enumerate( parsedData ):
      #print index, dat, self.dataOffsets[ index ], self.dataScales[ index ]
      if len( dat ) == 3 :
	scaledData.append(  float( dat[0] * 65536 + dat[1]*256 + dat[2] - self.dataOffsets[ index ] ) / float( self.dataScales[ index ] ) )
      if len( dat ) == 2 :
	scaledData.append(  float( dat[0]*256 + dat[1] - self.dataOffsets[ index ] ) / float( self.dataScales[ index ] ) )
      if len( dat ) == 1 :
	scaledData.append( float( dat[0] - self.dataOffsets[ index ] ) / float( self.dataScales[ index ] ) )
    self.data = scaledData
    if len(self.data) == ( len( self.config.dataScales ) + len( self.customDataInSizes ) ):
      if self.status != 'receiving':
	if self.firstDataAction != None:
	  self.firstDataAction( self.nodeid, self.data )
	  #self.serial.send_me( self.bees[beeid].serial, 0 )
	  print ( "receiving data from minibee %i."%(self.nodeid) )
      self.set_status( 'receiving' )
      if self.dataAction != None :
	self.dataAction( self.data, self.nodeid )
	#if verbose:
	  #print( "did data action", self.dataAction )
      if self.logAction != None :
	self.logAction( self.nodeid, self.getLabels(), self.getLogData() )
      if verbose:
	print( "data length ok", len(self.data), len( self.config.dataScales ), len( self.customDataInSizes ) )
    else:
      print( "data length not ok", len(self.data), len( self.config.dataScales ), len( self.customDataInSizes ) )
    if verbose:
      print( "data parsed and scaled", self.nodeid, self.data )
    
  def getLabels( self ):
    labels = self.customLabels
    labels.extend( self.config.logDataLabels )
    #print( labels )
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
      return len( self.dataScales )
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
      #print( "CONFIG INFO", configid, confirmconfig, verbose, len( confirmconfig ) )
      #self.digitalIns = 0
      #self.dataScales = []
      #self.dataOffsets = []
      if len( confirmconfig ) > 4:
	customIns = confirmconfig[5]
	customDataSize = confirmconfig[6]
	customPinCfgs = confirmconfig[7:]
	customPinSizes = 0

	self.customPins = {}

	if len( self.customDataInSizes ) > 0:
	  # there is custom config info in the configuration file, so we take the data from there
	  myindex = 0
	  customError = False
	  self.dataScales = self.customDataScales
	  self.dataOffsets = self.customDataOffsets
	  for c in self.customDataInSizes:
	    #print c,myindex
	    #if ( self.customDataInSizes 
	    myindex = myindex + 1
	  #print( self.customDataInSizes )
	else:
	  # we create our own set based on the info sent by the minibee
	  self.customDataInSizes = [ 0 for x in range( customIns ) ]
	  for i in range( len( customPinCfgs ) / 2 ):
	    #print ( i, customPinCfgs[i*2], customPinCfgs[i*2 + 1] )
	    self.customPins[ customPinCfgs[i*2] ] = customPinCfgs[i*2 + 1]
	    customPinSizes = customPinSizes + customPinCfgs[i*2 + 1]
	    if customPinCfgs[i*2 + 1]>0:
	      self.customDataInSizes.append( customPinCfgs[i*2 + 1] )
	  for i in range( customIns ):
	    self.customDataInSizes[i] = (customDataSize - customPinSizes) / customIns
	  self.dataScales = []
	  self.dataOffsets = []
	  for size in self.customDataInSizes:
	    self.dataOffsets.append( 0 )
	    self.dataScales.append( 1 )

      self.dataScales.extend( self.config.dataScales )
      self.dataOffsets.extend( self.config.dataOffsets )
      #print( self.dataScales, self.dataOffsets )
      if confirmconfig[0] == self.config.samplesPerMessage:
	if verbose:
	  print( "samples per message correct", confirmconfig[0], self.config.samplesPerMessage )
      else:
	configres = False
	print( "ERROR: samples per message NOT correct", confirmconfig[0], self.config.samplesPerMessage )
      if (confirmconfig[1]*256 + confirmconfig[2]) == self.config.messageInterval :
	if verbose:
	  print( "message interval correct", confirmconfig[1:3], self.config.messageInterval )
      else:
	configres = False
	print( "ERROR: message interval NOT correct", confirmconfig[1:3], self.config.messageInterval )
      if confirmconfig[3] == (self.config.digitalIns + sum( self.config.dataInSizes ) + sum( self.customDataInSizes )):
	if verbose:
	  print( "data input size correct", confirmconfig[3], self.config.digitalIns, self.config.dataInSizes, self.customDataInSizes )
      else:
	configres = False
	print( "ERROR: data input size NOT correct", confirmconfig[3], self.config.digitalIns, self.config.dataInSizes, self.customDataInSizes )
      if confirmconfig[4] == sum( self.config.dataOutSizes ):
	if verbose:
	  print( "data output size correct", confirmconfig[4], self.config.dataOutSizes )
      else:
	configres = False
	print( "ERROR: data output size NOT correct", confirmconfig[4], self.config.dataOutSizes )
      # to add custom in and out
    else:
      configres = False
      print( "ERROR: wrong config number", configid, self.cid )
    if configres:
      self.set_status( 'configured' )
    return configres
      

  def __str__(self):
    return "<minibee {id: %s, serial: %s, libversion: %s, revision: %s, caps: %s, configid: %s}>" % (self.nodeid, self.serial, self.libversion, self.revision, self.caps, self.cid )

# end of class minibee

# main program:


if __name__ == "__main__":
  parser = optparse.OptionParser(description='Create a pydonhive to get data from the minibee network.')
  parser.add_option('-c','--config', action='store', type="string", dest="config",default="pydon/configs/hiveconfig.xml",
		  help='the name of the configuration file for the minibees [default:%s]'% 'pydon/configs/hiveconfig.xml')
  parser.add_option('-a','--apimode', action='store', type="string", dest="apimode",default=False,
		  help='use API mode for communication with the minibees [default:%s]'% False)
  parser.add_option('-m','--nr_of_minibees', type=int, action='store',dest="minibees",default=20,
		  help='the number of minibees in the network [default:%i]'% 20)
  parser.add_option('-v','--verbose', action='store',dest="verbose",default=False,
		  help='verbose printing [default:%i]'% False)
  parser.add_option('-s','--serial', action='store',type="string",dest="serial",default="/dev/ttyUSB0",
		  help='the serial port [default:%s]'% '/dev/ttyUSB0')

  (options,args) = parser.parse_args()

  def printDataAction( data, nodeid ):
    print( nodeid, data )

  hive = MiniHive( options.serial, 57600, options.apimode )
  hive.set_id_range( 2, options.minibees + 1 )
  hive.set_verbose( options.verbose )
  
  hive.load_from_file( options.config )
  #hive.bees[ 1 ].set_action( printDataAction )
  hive.bees[ 2 ].set_action( printDataAction )
  #hive.bees[ 3 ].set_action( printDataAction )
  #hive.bees[ 4 ].set_action( printDataAction )
  #print hive
    
  #hive.write_to_file( "hiveconfig2.xml" )
  try:
    hive.run()
  except (SystemExit, RuntimeError, KeyboardInterrupt, IOError ) :
    hive.exit()
    print( "Done; goodbye" )    
    sys.exit()

