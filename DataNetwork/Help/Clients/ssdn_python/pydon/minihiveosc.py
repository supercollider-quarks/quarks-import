#! /usr/bin/env python
# -*- coding: utf-8 -*-

import optparse
# from Python v2.7 on should become argparse
import sys
import time

import OSC
import threading

from pydon import pydonhive

class MiniHiveOSC(object):
  #def __init__(self, port, dnosc ):
    #ServerThread.__init__(self, port)
    #self.dnosc = dnosc
    
  def setVerbose( self, onoff ):
    self.verbose = onoff;
    self.osc.print_tracebacks = onoff
    
  def add_handlers( self ):
    #self.osc.addMsgHandler( "/minibee", self.handler_output )
    self.osc.addMsgHandler( "/minibee/output", self.handler_output )
    self.osc.addMsgHandler( "/minibee/custom", self.handler_custom )
    
    self.osc.addMsgHandler( "/minibee/configuration", self.handler_mbconfig )
    self.osc.addMsgHandler( "/minibee/configuration/query", self.handler_mbconfig_query )

    self.osc.addMsgHandler( "/minihive/configuration/save", self.handler_cfsave )
    self.osc.addMsgHandler( "/minihive/configuration/load", self.handler_cfload )
    
    self.osc.addMsgHandler( "/minihive/configuration", self.handler_config )
    self.osc.addMsgHandler( "/minihive/configuration/delete", self.handler_config_delete )    
    self.osc.addMsgHandler( "/minihive/configuration/query", self.handler_config_query )
    
    self.osc.addMsgHandler( "/minihive/configuration/short", self.handler_config_short )

    self.osc.addMsgHandler( "/minihive/configuration/pin", self.handler_configpin )
    self.osc.addMsgHandler( "/minihive/configuration/pin/query", self.handler_configpin_query )
    self.osc.addMsgHandler( "/minihive/configuration/twi", self.handler_configtwi )
    self.osc.addMsgHandler( "/minihive/configuration/twi/query", self.handler_configtwi_query )
    
    self.osc.addMsgHandler('default', self.osc.noCallback_handler)

  def call_callback( self, ctype, cid ):
    if ctype in self.callbacks:
      if cid in self.callbacks[ ctype ]:
	self.callbacks[ ctype ][ cid ]( cid )
	return
    #print ctype, cid

  #@make_method('/datanetwork/announce', 'si')
  #def announced( self, path, args, types ):
  def handler_output( self, path, types, args, source ):    
    self.setOutput( args[0], args[1:] )
    if self.verbose:
      print( "MiniBee Output:", args )

  def handler_custom( self, path, types, args, source ):    
    self.setCustom( args[0], args[1:] )
    if self.verbose:
      print( "MiniBee Custom:", args )

  def handler_mbconfig( self, path, types, args, source ):    
    self.setMiniBeeConfiguration( args )
    if self.verbose:
      print( "MiniBee configuration:", args )

  def handler_mbconfig_query( self, path, types, args, source ):    
    self.queryMiniBeeConfiguration( args[0] )
    if self.verbose:
      print( "MiniBee configuration query:", args )

  def handler_config( self, path, types, args, source ):    
    self.setConfiguration( args[0], args[1:] )
    if self.verbose:
      print( "MiniHive configuration:", args )

  def handler_config_delete( self, path, types, args, source ):    
    self.deleteConfiguration( args[0] )
    if self.verbose:
      print( "MiniHive delete configuration:", args )

  def handler_config_short( self, path, types, args, source ):    
    self.setConfiguration( args[0], args[1:] )
    if self.verbose:
      print( "MiniHive configuration:", args )

  def handler_config_query( self, path, types, args, source ):    
    self.queryConfiguration( args[0] )
    if self.verbose:
      print( "MiniHive configuration query:", args )

  def handler_configpin( self, path, types, args, source ):    
    self.setPinConfiguration( args[0], args[1:] )
    if self.verbose:
      print( "MiniHive pin configuration:", args )

  def handler_configpin_query( self, path, types, args, source ):    
    self.queryPinConfiguration( args[0], args[1] )
    if self.verbose:
      print( "MiniHive pin configuration query:", args )

  def handler_configtwi( self, path, types, args, source ):    
    self.setTwiConfiguration( args[0], args[1:] )
    if self.verbose:
      print( "MiniHive twi configuration:", args )

  def handler_configtwi_query( self, path, types, args, source ):    
    self.queryTwiConfiguration( args[0], args[1] )
    if self.verbose:
      print( "MiniHive twi configuration query:", args )

  def handler_cfsave( self, path, types, args, source ):    
    self.saveConfiguration( args[0] )
    if self.verbose:
      print( "MiniBee save configuration:", args )

  def handler_cfload( self, path, types, args, source ):    
    self.loadConfiguration( args[0] )
    if self.verbose:
      print( "MiniBee load configuration:", args )

  def fallback(self, path, args, types, src):
    print( "got unknown message '%s' from '%s'" % (path, src.get_url()) )
    for a, t in zip(args, types):
      print( "argument of type '%s': %s" % (t, a) )
      
## message sending
  def sendMessage( self, path, args ):
    msg = OSC.OSCMessage()
    msg.setAddress( path )
    #print args
    for a in args:
      msg.append( a )
    try:
      self.host.send( msg )
      if self.verbose:
	print( "sending message", msg )
    except OSC.OSCClientError:
      if self.verbose:
	print( "error sending message", msg )

  def infoMiniBee( self, serial, mid, insize, outsize ):
    self.sendMessage( "/minibee/info", [ serial, mid, insize, outsize ] )

  def dataMiniBee( self, mid, data ):
    alldata = [ mid ]
    alldata.extend( data )
    self.sendMessage( "/minibee/data", alldata )
    if self.verbose:
      print( "sending osc message with data", mid, data )

  def setOutput( self, mid, data ):
    #print( self.hive, mid, data )
    self.hive.oscToMiniBee( mid, data )

  def setCustom( self, mid, data ):
    self.hive.oscToMiniBee( mid, data )
    
  def setMiniBeeConfiguration( self, config ):
    if len( config ) == 3:
      # set minibee with serial number to given id
      if not self.hive.hive.map_serial_to_bee( config[2], config[0] ):
	# send error message
	self.sendMessage( "/minibee/configuration/error", config )
	return
    # continue with setting the configuration
    self.hive.hive.set_minibee_config( config[0], config[1] )
    self.sendMessage( "/minibee/configuration/done", config )

  def queryMiniBeeConfiguration( self, mid ):
    print( "Query MiniBee configuration %i"%mid )

  def queryConfiguration( self, cid ):
    print( "Query configuration %i"%cid )

  def queryPinConfiguration( self, cid, pid ):
    print( "Query configuration %i, pin %s"%(cid,pid) )

  def queryTwiConfiguration( self, cid, tid ):
    print( "Query configuration %i, twi %i"%(cid,tid) )

  def deleteConfiguration( self, cid ):
    if not self.hive.hive.delete_configuration( cid ):
      self.sendMessage( "/minihive/configuration/error", cid )
    else:
      self.sendMessage( "/minihive/configuration/delete/done", cid )

  def setConfiguration( self, cid, config ):
    allconfig = [ cid ]
    allconfig.extend( config )
    if not self.hive.hive.set_configuration( cid, config ):
      self.sendMessage( "/minihive/configuration/error", allconfig )
    else:
      self.sendMessage( "/minihive/configuration/done", allconfig )

  def setPinConfiguration( self, cid, pincfg ):
    allconfig = [ cid ]
    allconfig.extend( pincfg )
    if not self.hive.hive.set_pin_configuration( cid, pincfg ):
      self.sendMessage( "/minihive/configuration/pin/error", allconfig )
    self.sendMessage( "/minihive/configuration/pin/done", allconfig )

  def setTwiConfiguration( self, cid, twicfg ):
    allconfig = [ cid ]
    allconfig.extend( twicfg )
    if not self.hive.hive.set_twi_configuration( cid, twicfg ):
      self.sendMessage( "/minihive/configuration/twi/error", allconfig )
    else:
      self.sendMessage( "/minihive/configuration/twi/done", allconfig )

  def loadConfiguration( self, filename ):
    self.hive.hive.load_from_file( filename )
    print( "loaded configuration from:", filename )

  def saveConfiguration( self, filename ):
    self.hive.hive.write_to_file( filename )
    print( "saved configuration to:", filename )

# end class DNOSCServer

# begin class DataNetworkOSC
#class DataNetworkOSC(object):
  def __init__(self, hostip, hostport, myip, myport, hive ):
    self.verbose = False
    self.hive = hive
    self.hostport = hostport
    self.hostip = hostip
    self.port = myport    
    self.myip = myip

    self.host = OSC.OSCClient()
    send_address = ( self.hostip, self.hostport )
    self.host.connect( send_address )

    receive_address = ( self.myip, self.port )
    self.osc = OSC.OSCServer( receive_address )
    self.add_handlers()
    self.thread = threading.Thread( target = self.osc.serve_forever )
    self.thread.start()
    


class SWMiniHiveOSC( object ):
  def __init__(self, hostip, hostport, myip, myport, swarmSize, serialPort, serialRate, config, idrange, verbose, apiMode ):
    
    self.hive = pydonhive.MiniHive( serialPort, serialRate, apiMode )
    self.hive.set_id_range( idrange[0], idrange[1] )
    self.hive.load_from_file( config )
    self.hive.set_verbose( verbose )

    self.osc = MiniHiveOSC( hostip, hostport, myip, myport, self )
    self.osc.setVerbose( verbose )
    
    self.verbose = verbose

    self.hive.set_newBeeAction( self.hookBeeToOSC )
        
  def start( self ):
    try :
      self.hive.run()
    except (SystemExit, RuntimeError,KeyboardInterrupt, IOError ) :
      self.osc.osc.close()
      print( "Waiting for Server-thread to finish" )
      self.osc.thread.join() ##!!!
      print( "Done; goodbye" )
      self.hive.exit()
      sys.exit()

  
  #def setMapAction( self, nodeid, mid ):
    #self.datanetwork.nodes[ nodeid ].setAction( lambda data: self.dataNodeDataToMiniBee( data, mid ) )
  
# bee to datanode
  def hookBeeToOSC( self, minibee ):
    self.osc.infoMiniBee( minibee.serial, minibee.nodeid, minibee.getInputSize(), minibee.getOutputSize() )
    minibee.set_action( self.minibeeDataToOSC )
    if self.verbose:
      print( "hooking bee to OSC out", minibee,  minibee.getInputSize(),  minibee.getOutputSize() )

  def minibeeDataToOSC( self, data, nid ):
    self.osc.dataMiniBee( nid, data )
    if self.verbose:
      print( nid,  data )

# data node to minibee
  def oscToMiniBee( self, nid, data ):
    self.hive.bees[ nid ].send_output( self.hive.serial, data )
    if self.verbose:
      print( nid,  data )

  def oscToMiniBeeCustom( self, nid, data ):
    self.hive.bees[ nid ].send_custom( self.hive.serial, data )

  #def dataNodeDataToMiniBeeCustom( self, data, nid ):
    #self.hive.bees[ nid ].send_custom( self.hive.serial, data )



# main program:
if __name__ == "__main__":

  parser = optparse.OptionParser(description='Create a program that speaks OSC to communicate with the minibee network.')
  parser.add_option('-p','--port', type=int, action='store',dest="port",default=57600,
		  help='the port on which the minihiveosc will listen [default:%i]'% 57600 )
  parser.add_option('-i','--ip', type="string", action='store',dest="ip",default="0.0.0.0",
		  help='the ip on which the client will listen [default:%s]'% "0.0.0.0" )
  parser.add_option('-c','--config', action='store', type="string", dest="config",default="pydon/configs/hiveconfig.xml",
		  help='the name of the configuration file for the minibees [default:%s]'% 'pydon/configs/hiveconfig.xml')
  parser.add_option('-m','--nr_of_minibees', type=int, action='store',dest="minibees",default=10,
		  help='the number of minibees in the network [default:%i]'% 10)
  parser.add_option('-d','--host_ip', action='store',type="string", dest="host",default="127.0.0.1",
		  help='the ip address of the application that has to receive the OSC messages [default:%s]'% "127.0.0.1")
  parser.add_option('-t','--host_port', type=int, action='store',dest="hport",default=57120,
		  help='the port on which the application that has to receive the OSC messages will listen [default:%i]'% 57120 )
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
  #print( options.host )
  
  print( "MiniHiveOSC - communicating via OSC with the MiniBee network" )
  swhive = SWMiniHiveOSC( options.host, options.hport, options.ip, options.port, options.minibees, options.serial, options.baudrate, options.config, [1,options.minibees], options.verbose, options.apimode )
  print( "Created OSC listener at (%s,%i) and OSC sender to (%s,%i) and opened serial port at %s. Now waiting for messages."%(options.ip, options.port, options.host, options.hport, options.serial ) )
  swhive.start()
