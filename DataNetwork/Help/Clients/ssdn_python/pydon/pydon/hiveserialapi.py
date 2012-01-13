# -*- coding: utf-8 -*-
import serial
import time
#import sys
import os
#import datetime

#print time

from xbee import XBee
from xbee.helpers.dispatch import Dispatch

import binascii
import struct

#from pydon 
#import minibeexml

#from collections import deque

## {{{ http://code.activestate.com/recipes/510399/ (r1)
"""
HexByteConversion

Convert a byte string to it's hex representation for output or visa versa.

ByteToHex converts byte string "\xFF\xFE\x00\x01" to the string "FF FE 00 01"
HexToByte converts string "FF FE 00 01" to the byte string "\xFF\xFE\x00\x01"
"""

#-------------------------------------------------------------------------------

def ByteToHex( byteStr ):
    """
    Convert a byte string to it's hex string representation e.g. for output.
    """
    
    # Uses list comprehension which is a fractionally faster implementation than
    # the alternative, more readable, implementation below
    #   
    #    hex = []
    #    for aChar in byteStr:
    #        hex.append( "%02X " % ord( aChar ) )
    #
    #    return ''.join( hex ).strip()        

    return ''.join( [ "%02X" % ord( x ) for x in byteStr ] ).strip()

#-------------------------------------------------------------------------------

def HexToByte( hexStr ):
    """
    Convert a string hex byte values into a byte string. The Hex Byte values may
    or may not be space separated.
    """
    # The list comprehension implementation is fractionally slower in this case    
    #
    #    hexStr = ''.join( hexStr.split(" ") )
    #    return ''.join( ["%c" % chr( int ( hexStr[i:i+2],16 ) ) \
    #                                   for i in range(0, len( hexStr ), 2) ] )
 
    bytes = []

    hexStr = ''.join( hexStr.split(" ") )

    for i in range(0, len(hexStr), 2):
        bytes.append( chr( int (hexStr[i:i+2], 16 ) ) )

    return ''.join( bytes )


class HiveSerialAPI(object):
  def __init__(self, serial_port, baudrate = 19200 ):
    #self.init_with_serial( mid, serial, libv, revision, caps)
    try:
      self.serial = serial.Serial( serial_port, baudrate )  # open first serial port
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
    
    self.dispatch = Dispatch( self.serial )
    self.register_callbacks()
    self.xbee = XBee( self.serial, callback=self.dispatch.dispatch, escaped=True)
    
    self.hiveMsgId = 0
    self.logAction = None
    self.verbose = False
    
  def register_callbacks( self ):
    self.dispatch.register(
      "remote_at_response", 
      self.remoteatresponse_handler, 
      lambda packet: packet['id']=='remote_at_response'
    )
    
    self.dispatch.register(
      "rfdata", 
      self.rfdata_handler,
      lambda packet: packet['id']=='rx'
    )

  def remoteatresponse_handler(self, name, packet):
    if self.verbose:
      print "Remote AT response: ", packet

  def rfdata_handler(self, name, packet):
    if self.verbose:
      print "RFData Received: ", packet
    if packet['rf_data'][0] == 'd' : # minibee sending data
      self.recv_data( packet[ 'rf_data' ][1:], packet[ 'source_addr'], packet['rssi'] )
    elif packet['rf_data'][0] == 's':
      if len( packet[ 'rf_data' ] ) > 13 :
	self.parse_serial( packet[ 'rf_data' ][2:10], ord( packet[ 'rf_data' ][10] ), packet[ 'rf_data' ][11], ord( packet[ 'rf_data' ][12] ), ord( packet[ 'rf_data' ][13] ) )
      else:
	self.parse_serial( packet[ 'rf_data' ][2:10], ord( packet[ 'rf_data' ][10] ), packet[ 'rf_data' ][11], ord( packet[ 'rf_data' ][12] ), 1 )
    elif packet['rf_data'][0] == 'w':
      if self.verbose:
	print( "wait config", packet[ 'rf_data' ][2], packet[ 'rf_data' ][3] )
      self.hive.wait_config( ord(packet[ 'rf_data' ][2]), ord(packet[ 'rf_data' ][3]) )
    elif packet['rf_data'][0] == 'c': # configuration confirmation
      self.hive.check_config( ord(packet[ 'rf_data' ][2]), ord(packet[ 'rf_data' ][3] ), [ ord(x) for x in packet[ 'rf_data' ][4:] ] )
    self.log_data( packet )
    
  def set_verbose( self, onoff ):
    self.verbose = onoff
    if onoff:
      print( self.serial )
      print( self.serial.portstr )       # check which port was really used

    
  def set_hive( self, hive ):
    self.hive = hive
    
  def announce( self, nodeid = 0xFFFF ):
    self.send_msg_inc( nodeid, 'A', [] );

  def quit( self ):
    self.send_msg_inc( 0xFFFF, 'Q', [] );
    self.xbee.halt()
    self.serial.close()
    
  def incMsgID( self ):
    self.hiveMsgId = self.hiveMsgId + 1
    if self.hiveMsgId > 255:
      self.hiveMsgId = 0

  def send_me( self, ser, onoff ):
    if self.verbose:
      print( "sending bee me", ser, onoff )
    self.send_msg64( ser, 'M', [ chr(onoff) ] )

  def send_id( self, ser, nodeid, configid = 0 ):
    #print ser
    if self.verbose:
      print( "sending bee id", ser, nodeid, configid )
    self.assign_remote_my( ser, nodeid )
    if configid > 0:
      time.sleep(.02)

      datalist = []
      datalist.append( HexToByte( ser ) )
      #datalist.append( ser )
      datalist.append( chr( nodeid ) )
      datalist.append( chr( configid ) )
      self.send_msg_inc( nodeid, 'I', datalist )
    
  def send_config( self, nodeid, configuration ):
    if self.verbose:
      print( "sending configuration", configuration )
    config = [ chr(x) for x in configuration ]
    self.send_msg_inc( nodeid, 'C', config )

  def assign_remote_my( self, serial, rmmy ):
    rfser = HexToByte( serial )
    #rfser = serial
    destaddr = ''.join( rfser )
    hrm = struct.pack('>H', rmmy)
    self.xbee.send('remote_at', 
          frame_id='A',
          dest_addr_long=destaddr,
          options='\x02',
          command='MY',
          parameter=hrm
          )
    #FIXME: this should be a setting or a separate osc message or something
    #self.store_remote_at64( serial )

  def store_remote_at64( self, serial ):
    rfser = HexToByte( serial )
    #rfser = serial
    destaddr = ''.join( rfser )
    #hrm = struct.pack('>H', rmmy)
    self.xbee.send('remote_at', 
          frame_id='A',
          dest_addr_long=destaddr,
          options='\x02',
          command='WR'
          #parameter=hrm
          )

  def store_remote_at16( self, nodeid ):
    #rfser = HexToByte( serial )
    #rfser = serial
    #destaddr = ''.join( rfser )
    hrm = struct.pack('>H', nodeid)
    self.xbee.send('remote_at', 
          frame_id='A',
          dest_addr=hrm,
          options='\x02',
          command='WR'
          #parameter=hrm
          )

  #def send_data( self, rmmy, data ):
    #self.send_msg( rmmy, 'O', data )

  #def send_custom( self, rmmy, datalistin ):
    #self.send_msg( rmmy, 'X', datalistin )

  def create_beemsg( self, msgtype, msgid, msgdata, mid ):
    datalist = [ msgtype ]
    datalist.append( chr(msgid) )
    datalist.extend( [ chr(int(x)) for x in msgdata ] )
    return datalist

  def send_msg( self, datalistin, rmmy ):
    datalist = []
    datalist.extend( datalistin )
    data = ''.join( datalist )
    hrm = struct.pack('>H', rmmy)
    #print( hrm, datalist, data )
    self.xbee.send('tx',
          dest_addr=hrm,
          data=data
          )
    if self.verbose:
      print( "sending message to minibee", rmmy, hrm, data )
    
  def send_msg_inc( self, rmmy, msgtype, datalistin ):
    self.incMsgID()
    datalist = [ msgtype ]
    datalist.append( chr( self.hiveMsgId ) )
    datalist.extend( datalistin )
    #print datalist, datalistin
    data = ''.join( datalist )
    hrm = struct.pack('>H', rmmy)
    #print( hrm, datalist, data )
    self.xbee.send('tx',
          dest_addr=hrm,
          options='\x02',
          data=data
          )
    if self.verbose:
      print( "sending message to minibee", rmmy, hrm, data )

  def send_msg64( self, ser, msgtype, datalistin ):
    self.incMsgID()
    rfser = HexToByte( ser )
    destaddr = ''.join( rfser )
    datalist = [ msgtype ]
    datalist.append( chr( self.hiveMsgId) )
    datalist.extend( datalistin )
    data = ''.join( datalist )
    #hrm = struct.pack('>H', rmmy)
    #print( hrm, datalist, data )
    self.xbee.send('tx_long_addr',
          dest_addr=destaddr,
          data=data
          )
    if self.verbose:
      print( "sending message to minibee with long addr", ser, rfser, data )

  def send_run( self, mid, run ):
    if self.verbose:
      print( "sending bee run", mid, run )
    self.send_msg_inc( mid, 'R', [ chr(run) ] )

  def send_loop( self, mid, loop ):
    if self.verbose:
      print( "sending bee loop", mid, loop )
    self.send_msg_inc( mid, 'L', [ chr(loop) ] )

  def parse_serial( self, rfser, libv, rev, caps, remConf ): # later also libv, rev, caps
    sser = ByteToHex( rfser )
    if self.verbose:
      print( sser, libv, rev, caps, remConf )
    #self.hive.new_bee_no_config( sser )
    self.hive.new_bee( sser, libv, rev, caps, remConf )

  #def wait_config( self ):
    #if self.verbose:
      #print( "waiting configuration" )
      #print( self.incMsg )
    #self.hive.wait_config( self.incMsg[1], self.incMsg[2] ) # minibee id, config id
    ##print "end waiting configuration"

  #def confirm_config( self ):
    #if self.verbose:
      #print( "confirming configuration" )
      #print( self.incMsg )
    #self.hive.check_config( self.incMsg[1], self.incMsg[2], self.incMsg[3:] )
    ##print "end confirming configuration"

  def recv_data( self, rfdata, source, rfrssi ):
    data = []
    for x in rfdata[1:]:
      data.append( int( ByteToHex( x ), 16 ) )    
    nid = int( ByteToHex( source ), 16 )
    rssi = int( ByteToHex( rfrssi ), 16 )
    msgid = int( ByteToHex( rfdata[1] ), 16 )
    if self.verbose:
      print( "receiving data from minibee", nid, msgid, data, rssi )
    self.hive.new_data( nid, msgid, data, rssi )

  #def active( self ):
    ##print "receiving data"
    ##print self.incMsg
    #if len(self.incMsg) == 2:
      #self.hive.bee_active( self.incMsg[1], self.incMsg[2] )
    ##print "end receiving data"
    #if self.verbose:
      #print( "active minibee", self.incMsg[1], self.incMsg[2] )

  #def pausing( self ):
    ##print "receiving data"
    ##print self.incMsg
    #if len(self.incMsg) == 2:
      #self.hive.bee_pausing( self.incMsg[1], self.incMsg[2] )
    ##print "end receiving data"
    #if self.verbose:
      #print( "pausing minibee", self.incMsg[1], self.incMsg[2] )

  def set_log_action( self, action ):
    self.logAction = action

  def log_data( self, packet ):
    nid = int( ByteToHex( packet[ 'source_addr' ] ), 16 )
    rssi = int( ByteToHex( packet[ 'rssi' ] ), 16 )
    #msgid = int( ByteToHex( packet[ 'rfdata' ][1] ), 16 )    
    data = []
    data.append( nid )
    data.append( rssi )
    data.append( packet[ 'rf_data' ][0] )
    for x in packet[ 'rf_data' ][1:]:
      data.append( int( ByteToHex( x ), 16 ) )    
    #print "receiving data"
    if self.logAction != None :
      self.logAction( data )
    #else:
      #print self.incMsg
    #self.hive.new_data( self.incMsg[1], self.incMsg[2], self.incMsg[3:] )
    #print "end receiving data"

  #def parse_message( self ):
    ##print self.incType
    ##print self.incType, chr( self.incType ), self.incMsg
    #if type( self.incType ) == int:
      #incTypeChr = chr( self.incType )
    #else:
      #incTypeChr = 'u' # unknown message
    ##incTypeChr = self.incType
    #if incTypeChr == 's' : # serial from minibee device
      #self.parse_serial()
    #elif incTypeChr == 'w' : # minibee waiting for configuration
      #self.wait_config()
    #elif incTypeChr == 'c' : # minibee confirming configuration
      #self.confirm_config()
    #elif incTypeChr == 'd' : # minibee sending data
      #self.recv_data()
    #elif incTypeChr == 'a' : # minibee confirming it's active
      #self.active()
    #elif incTypeChr == 'p' : # minibee confirming it's pausing
      #self.pausing()
    #elif incTypeChr == 'i' : # minibee sending info (debugging mostly)
      #self.display_data()
    #elif incTypeChr == 'x' : # minibee sending info (debugging mostly)
      #self.display_data()
    #else:
      #print( incTypeChr, self.incMsg )
    #self.log_data()
    ## add loopback
    ##res = messageTypes.get( chr(incType) )()

# end of HiveSerialAPI
