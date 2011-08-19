#! /usr/bin/env python
# -*- coding: utf-8 -*-

import optparse
# from Python v2.7 on should become argparse
import sys

import time

from pydon.pydon import pydon
from pydon.pydonhive import pydonhive

class SWPydonHive( object ):
  def __init__(self, hostip, myport, myip, myname, swarmSize, serialPort, serialRate, config, idrange, verbose ):
    self.datanetwork = pydon.DataNetwork( hostip, myport, myname, 1, swarmSize, myip )
    self.datanetwork.setVerbose( verbose )
    
    self.hive = pydonhive.MiniHive( serialPort, serialRate )
    self.hive.set_id_range( idrange[0], idrange[1] )
    self.hive.load_from_file( config )
    self.hive.set_verbose( verbose )
    
    self.datanetwork.setHive( self.hive )

    # self.datanetwork.setterCallback(
      
    self.hive.set_newBeeAction( self.hookBeeToDatanetwork )
    self.datanetwork.set_mapAction( self.mapMiniBee )
    self.datanetwork.set_mapCustomAction( self.mapMiniBeeCustom )
    self.datanetwork.set_unmapAction( self.unmapMiniBee )
    self.datanetwork.set_unmapCustomAction( self.unmapMiniBeeCustom )
    
    self.labelbase = "minibee"
    
  def start( self ):
    try :
      while not self.datanetwork.osc.registered:
        print( "waiting to be registered; is the DataNetwork host running?" )
        #print time
        print self.datanetwork.osc.registered
        time.sleep( 1.0 )
      print( "now running hive" )
      #try :
      self.hive.run()
      print( "hello" )
    except (SystemExit, RuntimeError,KeyboardInterrupt) :
      self.datanetwork.osc.unregister()
      print( "\nClosing OSCServer." )
      self.datanetwork.osc.osc.close()
      print( "Waiting for Server-thread to finish" )
      self.datanetwork.osc.thread.join() ##!!!
      print( "Done; goodbye" )
      sys.exit()

# mapping support
  def mapMiniBee( self, nodeid, mid ):
    self.datanetwork.osc.subscribeNode( nodeid, lambda nid: self.setMapAction( nodeid, mid ) )
  
  def setMapAction( self, nodeid, mid ):
    self.datanetwork.nodes[ nodeid ].setAction( lambda data: self.dataNodeDataToMiniBee( data, mid ) )

  def unmapMiniBee( self, nodeid, mid ):
    self.datanetwork.osc.unsubscribeNode( nodeid )
    self.datanetwork.nodes[ nodeid ].setAction( None )

  def mapMiniBeeCustom( self, nodeid, mid ):
    self.datanetwork.osc.subscribeNode( nodeid, lambda nid: self.setMapCustomAction( nodeid, mid ) )
  
  def unmapMiniBeeCustom( self, nodeid, mid ):
    self.datanetwork.osc.unsubscribeNode( nodeid )
    self.datanetwork.nodes[ nodeid ].setAction( None )

  def setMapCustomAction( self, nodeid, mid ):
    self.datanetwork.nodes[ nodeid ].setAction( lambda data: self.dataNodeDataToMiniBeeCustom( data, mid ) )

# labeling
  def set_labelbase( self, newlabel ):
    self.labelbase = newlabel

# bee to datanode
  def hookBeeToDatanetwork( self, minibee ):
    print( minibee,  minibee.getInputSize(),  minibee.getOutputSize() )
    self.datanetwork.osc.infoMinibee( minibee.nodeid, minibee.getInputSize(), minibee.getOutputSize() )
    minibee.set_first_action( self.addAndSubscribe )
    minibee.set_action( self.minibeeDataToDataNode )

  def addAndSubscribe( self, nid, data ):
    name = (self.labelbase + str(nid) )
    mybee = self.hive.bees[ nid ]
    self.datanetwork.osc.addExpected( nid, [ mybee.getInputSize(), name ] )
    self.datanetwork.osc.subscribeNode( nid )

  def minibeeDataToDataNode( self, data, nid ):
    self.datanetwork.sendData( nid, data )

# data node to minibee
  def dataNodeDataToMiniBee( self, data, nid ):
    self.hive.bees[ nid ].send_output( self.hive.serial, data )

  def dataNodeDataToMiniBeeCustom( self, data, nid ):
    self.hive.bees[ nid ].send_custom( self.hive.serial, data )



# main program:
if __name__ == "__main__":

  parser = optparse.OptionParser(description='Create a datanetwork client to communicate with the minibee network.')
  parser.add_option('-p','--port', type=int, action='store',dest="port",default=57600,
		  help='the port on which the client will listen [default:%i]'% 57600 )
  parser.add_option('-i','--ip', type="string", action='store',dest="ip",default="0.0.0.0",
		  help='the ip on which the client will listen [default:%s]'% "0.0.0.0" )
  parser.add_option('-n','--name', action='store', type="string", dest="name",default="pydonhive",
		  help='the name of the client in the datanetwork [default:%s]'% "pydonhive" )
  parser.add_option('-c','--config', action='store', type="string", dest="config",default="pydon/configs/hiveconfig.xml",
		  help='the name of the configuration file for the minibees [default:%s]'% 'pydon/configs/hiveconfig.xml')
  parser.add_option('-m','--nr_of_minibees', type=int, action='store',dest="minibees",default=20,
		  help='the number of minibees in the network [default:%i]'% 20)
  parser.add_option('-d','--host_ip', action='store',type="string", dest="host",default="127.0.0.1",
		  help='the ip address of the datanetwork host [default:%s]'% "127.0.0.1")
  parser.add_option('-v','--verbose', action='store',dest="verbose",default=False,
		  help='verbose printing [default:%i]'% False)
  parser.add_option('-s','--serial', action='store',type="string",dest="serial",default="/dev/ttyUSB0",
		  help='the serial port [default:%s]'% '/dev/ttyUSB0')
  parser.add_option('-b','--baudrate', action='store',type=int,dest="baudrate",default=57600,
		  help='the serial port [default:%i]'% 57600)

  (options,args) = parser.parse_args()
  #print args.accumulate(args.integers)
  #print options
  #print args
  print( options.host )
  
  swhive = SWPydonHive( options.host, options.port, options.ip, options.name, options.minibees, options.serial, options.baudrate, options.config, [1,options.minibees], options.verbose )
  
  swhive.start()
