#! /usr/bin/env python
# -*- coding: utf-8 -*-

import sys, optparse
try:
  import optparse_gui
  haveGui = True
except:
  haveGui = False


import mapper
import pydon
import pydonhive

class LMPydonHive( object ):
  def __init__(self, hostip, myport, myip, myname, swarmSize, serialPort, serialRate, config, idrange, verbose, apiMode ):

    self.device = mapper.device("hive", 9000)
    self.output_signals = []

    self.hive = pydonhive.MiniHive( serialPort, serialRate, apiMode,
                                    poll = lambda: self.device.poll(5) )
    self.hive.set_id_range( idrange[0], idrange[1] )
    self.hive.load_from_file( config )
    self.hive.set_verbose( verbose )

    self.hive.set_newBeeAction( self.hookBeeToMapper )
    self.labelbase = "minibee"
    
  def start( self ):
    try :
      while not self.device.ready():
        print( "waiting to be registered.." )
        self.device.poll(100)
      print( "now running hive" )
      self.hive.run()
    except (SystemExit, RuntimeError, KeyboardInterrupt, IOError ) :
      print( "Done; goodbye" )
      self.hive.exit()
      del self.device
      sys.exit()

# mapping support
# LM: what is this?
  # def mapMiniBee( self, nodeid, mid ):
  #   self.datanetwork.osc.subscribeNode( nodeid, lambda nid: self.setMapAction( nid, mid ) )
  
  # def setMapAction( self, nodeid, mid ):
  #   if nodeid not in self.datanetwork.nodes:
  #     self.datanetwork.osc.add_callback( 'info', nodeid, lambda nid: self.setMapAction( nid, mid ) )
  #   else:
  #     self.datanetwork.nodes[ nodeid ].setAction( lambda data: self.dataNodeDataToMiniBee( data, mid ) )

  # def unmapMiniBee( self, nodeid, mid ):
  #   del self.device
  #   self.output_signals = []

  # def mapMiniBeeCustom( self, nodeid, mid ):
  #   self.datanetwork.osc.subscribeNode( nodeid, lambda nid: self.setMapCustomAction( nodeid, mid ) )
  
  # def unmapMiniBeeCustom( self, nodeid, mid ):
  #   self.datanetwork.osc.unsubscribeNode( nodeid )
  #   self.datanetwork.nodes[ nodeid ].setAction( None )

  # def setMapCustomAction( self, nodeid, mid ):
  #   if nodeid not in self.datanetwork.nodes:
  #     self.datanetwork.osc.add_callback( 'info', nodeid, lambda nid: self.setMapCustomAction( nid, mid ) )
  #   else:
  #     self.datanetwork.nodes[ nodeid ].setAction( lambda data: self.dataNodeDataToMiniBeeCustom( data, mid ) )

# labeling
  def set_labelbase( self, newlabel ):
    # LM: change device name?
    self.labelbase = newlabel

# bee to datanode
  def hookBeeToMapper( self, minibee ):
    # self.datanetwork.osc.infoMinibee( minibee.nodeid, minibee.getInputSize(), minibee.getOutputSize() )
    minibee.set_first_action( self.addAndSubscribe )
    minibee.set_action( self.minibeeDataToSignals )
    minibee.set_status_action( self.sendStatusInfo )
    
  def sendStatusInfo( self, nid, status ):
    print 'sendStatusInfo', nid, status
    # LM: Send the status info via a string signal?

  def addAndSubscribe( self, nid, data ):
    mybee = self.hive.bees[ nid ]
    if mybee.name == "":
      mybee.name = (self.labelbase + str(nid) )
    n = 0
    for fmt in mybee.config.logDataFormat:
      for i in range(fmt):
        self.output_signals.append(self.device.add_output(
            mybee.name + "/" + mybee.config.logDataLabels[n], 'f'))
        n += 1
    self.minibeeDataToSignals( data, nid )

  def minibeeDataToSignals( self, data, nid ):
    mybee = self.hive.bees[ nid ]
    n = 0
    for fmt in mybee.config.logDataFormat:
      for i in range(fmt):
        self.output_signals[n].update(data[n])
        n += 1

# data node to minibee
# LM: minibee inputs are TO DO
  # def dataNodeDataToMiniBee( self, data, nid ):
  #   self.hive.bees[ nid ].send_output( self.hive.serial, data )

  # def dataNodeDataToMiniBeeCustom( self, data, nid ):
  #   self.hive.bees[ nid ].send_custom( self.hive.serial, data )

# main program:
if __name__ == "__main__":

  if 1 == len( sys.argv ) and haveGui:
    option_parser_class = optparse_gui.OptionParser
  else:
    option_parser_class = optparse.OptionParser

  parser = option_parser_class(description='Create a libmapper client to communicate with the minibee network.')
  parser.add_option('-s','--serial', action='store',type="string",dest="serial",default="/dev/ttyUSB0",
		  help='the serial port [default:%s]'% '/dev/ttyUSB0')
  parser.add_option('-a','--apimode', action='store_true', dest="apimode",default=False,
		  help='use API mode for communication with the minibees [default:%s]'% False)
  parser.add_option('-v','--verbose', action='store_true', dest="verbose",default=False,
		  help='verbose printing [default:%i]'% False)
  parser.add_option('-c','--config', action='store', type="string", dest="config",default="pydon/configs/hiveconfig.xml",
		  help='the name of the configuration file for the minibees [default:%s]'% 'pydon/configs/hiveconfig.xml')
  parser.add_option('-n','--name', action='store', type="string", dest="name",default="pydonhive",
		  help='the name of the client in the datanetwork [default:%s]'% "pydonhive" )
  parser.add_option('-m','--nr_of_minibees', type=int, action='store',dest="minibees",default=20,
		  help='the number of minibees in the network [default:%i]'% 20)
  parser.add_option('-o','--minibee_offset', type=int, action='store',dest="mboffset",default=1,
		  help='the offset of the number range for the minibees in the network [default:%i]'% 1)
  parser.add_option('-d','--host_ip', action='store',type="string", dest="host",default="127.0.0.1",
		  help='the ip address of the datanetwork host [default:%s]'% "127.0.0.1")
  parser.add_option('-b','--baudrate', action='store',type=int,dest="baudrate",default=57600,
		  help='the serial port [default:%i]'% 57600)
  parser.add_option('-i','--ip', type="string", action='store',dest="ip",default="0.0.0.0",
		  help='the ip on which the client will listen [default:%s]'% "0.0.0.0" )
  parser.add_option('-p','--port', type=int, action='store',dest="port",default=57600,
		  help='the port on which the client will listen [default:%i]'% 57600 )

  (options,args) = parser.parse_args()
  #print args.accumulate(args.integers)
  #print options
  #print args
  print( "--------------------------------------------------------------------------------------" )
  print( "LMPydonHive - a libmapper client to communicate with the minibee network." )
  print( " --- to find out more about the startup options start with \'lmpydonhive.py -h\'" )
  print( " --- The client has been started with these options:" )
  print( options )
  print( "--------------------------------------------------------------------------------------" )
  
  lmhive = LMPydonHive( options.host, options.port, options.ip, options.name, options.minibees, options.serial, options.baudrate, options.config, [options.mboffset,options.minibees], options.verbose, options.apimode )
  
  lmhive.start()
