#! /usr/bin/env python
# -*- coding: utf-8 -*-

import time as myTime

#print myTime

import sys
import urllib2
#import pycurl

import OSC
import threading

#import liblo
#from liblo import *

# begin class DNOSCServer
#class DNOSCServer( ServerThread ):
class DataNetworkOSC(object):
  #def __init__(self, port, dnosc ):
    #ServerThread.__init__(self, port)
    #self.dnosc = dnosc
    
  def setVerbose( self, onoff ):
    self.verbose = onoff;
    self.osc.print_tracebacks = onoff
    
  def add_handlers( self ):
    self.osc.addMsgHandler( "/datanetwork/announce", self.handler_announced )
    self.osc.addMsgHandler( "/datanetwork/quit", self.handler_hasQuit )
    self.osc.addMsgHandler( "/registered", self.handler_registered )
    self.osc.addMsgHandler( "/unregistered", self.handler_unregistered )
    self.osc.addMsgHandler( "/error", self.handler_error_msg )
    self.osc.addMsgHandler( "/warn", self.handler_warn_msg )
    self.osc.addMsgHandler( "/ping", self.handler_pingpong )
    
    self.osc.addMsgHandler( "/info/expected", self.handler_node_expected )
    self.osc.addMsgHandler( "/info/node", self.handler_node_info )
    self.osc.addMsgHandler( "/info/slot", self.handler_slot_info )
    self.osc.addMsgHandler( "/info/client", self.handler_client_info )
    self.osc.addMsgHandler( "/info/setter", self.handler_node_setter )
    
    self.osc.addMsgHandler( "/subscribed/node", self.handler_node_subscribed )
    self.osc.addMsgHandler( "/unsubscribed/node", self.handler_node_unsubscribed )
    self.osc.addMsgHandler( "/subscribed/slot", self.handler_slot_subscribed )
    self.osc.addMsgHandler( "/unsubscribed/slot", self.handler_slot_unsubscribed )

    self.osc.addMsgHandler( "/removed/node", self.handler_node_removed )

    self.osc.addMsgHandler( "/data/node", self.handler_node_data )
    self.osc.addMsgHandler( "/data/slot", self.handler_slot_data )

    #hive and minibee management
    self.osc.addMsgHandler( "/registered/hive", self.handler_registered_hive )
    self.osc.addMsgHandler( "/unregistered/hive", self.handler_unregistered_hive )
    
    self.osc.addMsgHandler( "/map/minibee/output", self.handler_map_minibee )
    self.osc.addMsgHandler( "/unmap/minibee/output", self.handler_unmap_minibee )    

    self.osc.addMsgHandler( "/map/minibee/custom", self.handler_map_minibee_custom )
    self.osc.addMsgHandler( "/unmap/minibee/custom", self.handler_unmap_minibee_custom )    

    self.osc.addMsgHandler( "/mapped/minibee/output", self.handler_mapped_minibee )
    self.osc.addMsgHandler( "/unmapped/minibee/output", self.handler_unmapped_minibee )    

    self.osc.addMsgHandler( "/mapped/minibee/custom", self.handler_mapped_minibee_custom )
    self.osc.addMsgHandler( "/unmapped/minibee/custom", self.handler_unmapped_minibee_custom )    

    #begin# map to broadcast bee
    self.osc.addMsgHandler( "/map/minihive/output", self.handler_map_minihive )
    self.osc.addMsgHandler( "/unmap/minihive/output", self.handler_unmap_minihive )    

    self.osc.addMsgHandler( "/map/minihive/custom", self.handler_map_minihive_custom )
    self.osc.addMsgHandler( "/unmap/minihive/custom", self.handler_unmap_minihive_custom )    

    self.osc.addMsgHandler( "/mapped/minihive/output", self.handler_mapped_minihive )
    self.osc.addMsgHandler( "/unmapped/minihive/output", self.handler_unmapped_minihive )    

    self.osc.addMsgHandler( "/mapped/minihive/custom", self.handler_mapped_minihive_custom )
    self.osc.addMsgHandler( "/unmapped/minihive/custom", self.handler_unmapped_minihive_custom )    

    #end# map to broadcast bee

    self.osc.addMsgHandler( "/info/minibee", self.handler_info_minibee )
    self.osc.addMsgHandler( "/status/minibee", self.handler_status_minibee )
    self.osc.addMsgHandler( "/info/hive", self.handler_info_hive )
    self.osc.addMsgHandler( "/info/configuration", self.handler_info_config )

    self.osc.addMsgHandler( "/query/configurations", self.handler_config_query )

    self.osc.addMsgHandler( "/configure/minibee", self.handler_mbconfig )
    self.osc.addMsgHandler( "/configured/minibee", self.handler_mbconfiged )
    self.osc.addMsgHandler( "/minihive/configuration/create", self.handler_config )
    self.osc.addMsgHandler( "/minihive/configuration/created", self.handler_configed )
    self.osc.addMsgHandler( "/minihive/configuration/delete", self.handler_config_delete )
    self.osc.addMsgHandler( "/minihive/configuration/deleted", self.handler_config_deleted )
    self.osc.addMsgHandler( "/minihive/configuration/save", self.handler_cfsave )
    self.osc.addMsgHandler( "/minihive/configuration/saved", self.handler_cfsaved )
    self.osc.addMsgHandler( "/minihive/configuration/load", self.handler_cfload )
    self.osc.addMsgHandler( "/minihive/configuration/loaded", self.handler_cfloaded )


    self.osc.addMsgHandler('default', self.osc.noCallback_handler)

  def call_callback( self, ctype, cid ):
    if ctype in self.callbacks:
      if cid in self.callbacks[ ctype ]:
	self.callbacks[ ctype ][ cid ]( cid )
	return
    #print ctype, cid

  #@make_method('/datanetwork/announce', 'si')
  #def announced( self, path, args, types ):
  def handler_announced( self, path, types, args, source ):    
    self.setHost( args[0], args[1] )
    print( "DataNetwork announced at:", args )

  #@make_method('/datanetwork/quit', 'si')
  #def hasquit( self, path, args, types ):
  def handler_hasQuit( self, path, types, args, source ):
    #print self
    self.quitHost( args[0], args[1] )
    print( "DataNetwork quit at:", args )
  
  #@make_method('/registered', 'is')
  def handler_registered( self, path, types, args, source ):
    # could add a check if this is really me
    self.set_registered( True )
    print( "Registered as client:", args )

  #@make_method('/unregistered', 'is')
  def handler_unregistered( self, path, types, args, source ):
    self.set_registered( False )
    print( "Unregistered as client:", args )

  #@make_method('/error', 'ssi')
  def handler_error_msg( self, path, types, args, source ):
    # could add a check if this is really me
    print( "Error from datanetwork:", args )

  #@make_method('/warn', 'ssi')
  def handler_warn_msg( self, path, types, args, source ):
    # could add a check if this is really me
    print( "Warning from datanetwork:", args )

  #@make_method('/ping', 'is')
  def handler_pingpong( self, path, types, args, source ):
    self.sendSimpleMessage( "/pong" )
    
  #@make_method('/info/expected', None )
  def handler_node_expected( self, path,  types, args, source ):
    self.expected_node( args[0] )
    print( "Expected node:", args )

  #@make_method('/info/node', 'isii' )
  def handler_node_info( self, path,  types, args, source ):
    self.info_node( args[0], args[1], args[2], args[3] )
    #print "Present node:", args

  #@make_method('/info/slot', 'iisi' )
  def handler_slot_info( self, path,  types, args, source ):
    self.info_slot( args[0], args[1], args[2], args[3] )
    #print "Present slot:", args

  #@make_method('/info/client', 'sis' )
  def handler_client_info( self, path,  types, args, source ):
    # could do something more with this info
    print( "Present client:", args )

  #@make_method('/info/setter', 'isii' )
  def handler_node_setter( self, path,  types, args, source ):
    # could do something more with this info
    self.am_setter( args[2] )
    #print "Present setter:", args

  #@make_method('/subscribed/node', 'isi' )
  def handler_node_subscribed( self, path, types, args, source ):
    self.call_callback( 'subscribe', args[2] )
    #if 'subscribe' in self.callbacks:
      ##print self.dnosc.callbacks['subscribe']
      #if args[2] in self.callbacks['subscribe']:
	##print self.dnosc.callbacks['subscribe'][ args[2] ] 
	#self.callbacks['subscribe'][ args[2] ]( args[2] )
	#return
    #print "Subscribed to node:", args

  #@make_method('/unsubscribed/node', 'isi' )
  def handler_node_unsubscribed( self, path,  types, args, source ):
    self.call_callback( 'unsubscribe', args[2] )
    #if 'unsubscribe' in self.callbacks:
      ##print self.dnosc.callbacks['subscribe']
      #if args[2] in self.callbacks['unsubscribe']:
	##print self.dnosc.callbacks['subscribe'][ args[2] ] 
	#self.callbacks['unsubscribe'][ args[2] ]( args[2] )
	#return
    #print "Unsubscribed from node:", args

  #@make_method('/subscribed/slot', 'isii' )
  def handler_slot_subscribed( self, path,  types, args, source ):
    self.call_callback( 'subscribeSlot', args[2] )
    #if 'subscribeSlot' in self.callbacks:
      ##print self.dnosc.callbacks['subscribe']
      #if args[2] in self.callbacks['subscribeSlot']:
	##print self.dnosc.callbacks['subscribe'][ args[2] ] 
	#self.callbacks['subscribeSlot'][ args[2] ]( args[2] )
	#return
    #print "Subscribed to slot:", args

  #@make_method('/unsubscribed/slot', 'isii' )
  def handler_slot_unsubscribed( self, path, types, args, source ):
    self.call_callback( 'unsubscribeSlot', args[2] )
    # could add a check if this is really me
    #if 'unsubscribeSlot' in self.callbacks:
      ##print self.dnosc.callbacks['subscribe']
      #if args[2] in self.callbacks['unsubscribeSlot']:
	##print self.dnosc.callbacks['subscribe'][ args[2] ] 
	#self.callbacks['unsubscribeSlot'][ args[2] ]( args[2] )
	#return
    #print "Unsubscribed from slot:", args

  #@make_method('/removed/node', 'i' )
  def handler_node_removed( self, path, types, args, source ):
    self.call_callback( 'remove', args[2] )
    # could add a check if this is really me
    #if 'remove' in self.callbacks:
      ##print self.dnosc.callbacks['subscribe']
      #if args[2] in self.callbacks['remove']:
	##print self.dnosc.callbacks['subscribe'][ args[2] ] 
	#self.callbacks['remove'][ args[2] ]( args[2] )
	#return
    #print "Node removed:", args

  #@make_method('/data/node', None )
  def handler_node_data( self, path, types, args, source ):
    self.data_for_node( args[0], args[1:] )
    #print "Node data:", args

  #@make_method('/data/slot', 'iis' )
  def handler_slot_data( self, path, types, args, source ):
    self.data_for_slot( args[0], args[1], args[2] )
    #print "Slot string data:", args

  #@make_method('/data/slot', 'iif' )
  #def handler_slot_dataf( self, path, args, types ):
    #self.dnosc.data_for_slot( args[0], args[1], args[2] )
    #print "Slot numerical data:", args

# start hive and minibee management

  #@make_method('/registered/hive', 'isii')
  def handler_registered_hive( self, path, types, args, source ):
    self.set_registered_hive( True, args[2], args[3] )
    print( "Registered as hive client:", args )

  #@make_method('/unregistered/hive', 'is')
  def handler_unregistered_hive( self, path, types, args, source ):
    self.set_registered_hive( False )
    print( "Unregistered as hive client:", args )


  #@make_method('/map/minibee/output', 'ii')
  def handler_map_minibee( self, path, types, args, source ):
    self.map_minibee( args[0], args[1] )
    print( "Map minibee:", args )

  #@make_method('/unmap/minibee/output', 'ii')
  def handler_unmap_minibee( self, path, types, args, source ):
    self.unmap_minibee( args[0], args[1] )
    print( "Unmap Minibee:", args )

  #@make_method('/map/minibee/custom', 'ii')
  def handler_map_minibee_custom( self, path, types, args, source ):
    self.map_minibee_custom( args[0], args[1] )
    print( "Map minibee custom:", args )

  #@make_method('/unmap/minibee/custom', 'ii')
  def handler_unmap_minibee_custom( self, path, types, args, source ):
    self.unmap_minibee_custom( args[0], args[1] )
    print( "Unmap Minibee custom:", args )

  #@make_method('/mapped/minibee/output', 'ii')
  def handler_mapped_minibee( self, path, types, args, source ):
    self.mapped_minibee( args[0], args[1] )
    print( "Mapped minibee:", args )

  #@make_method('/unmapped/minibee/output', 'ii')
  def handler_unmapped_minibee( self, path, types, args, source ):
    self.unmapped_minibee( args[0], args[1] )
    print( "Unmapped Minibee:", args )

  #@make_method('/mapped/minibee/custom', 'ii')
  def handler_mapped_minibee_custom( self, path, types, args, source ):
    self.mapped_minibee_custom( args[0], args[1] )
    print( "Mapped minibee custom:", args )

  #@make_method('/unmapped/minibee/custom', 'ii')
  def handler_unmapped_minibee_custom( self, path, types, args, source ):
    self.unmapped_minibee_custom( args[0], args[1] )
    print( "Unmapped Minibee custom:", args )


### map to hive ###

  #@make_method('/map/minihive/output', 'ii')
  def handler_map_minihive( self, path, types, args, source ):
    self.map_minihive( args[0] )
    print( "Map minihive:", args )

  #@make_method('/unmap/minihive/output', 'ii')
  def handler_unmap_minihive( self, path, types, args, source ):
    self.unmap_minihive( args[0] )
    print( "Unmap Minihive:", args )

  #@make_method('/map/minihive/custom', 'ii')
  def handler_map_minihive_custom( self, path, types, args, source ):
    self.map_minihive_custom( args[0] )
    print( "Map minihive custom:", args )

  #@make_method('/unmap/minihive/custom', 'ii')
  def handler_unmap_minihive_custom( self, path, types, args, source ):
    self.unmap_minihive_custom( args[0] )
    print( "Unmap Minihive custom:", args )

  #@make_method('/mapped/minihive/output', 'ii')
  def handler_mapped_minihive( self, path, types, args, source ):
    self.mapped_minihive( args[0] )
    print( "Mapped minihive:", args )

  #@make_method('/unmapped/minihive/output', 'ii')
  def handler_unmapped_minihive( self, path, types, args, source ):
    self.unmapped_minihive( args[0] )
    print( "Unmapped Minihive:", args )

  #@make_method('/mapped/minihive/custom', 'ii')
  def handler_mapped_minihive_custom( self, path, types, args, source ):
    self.mapped_minihive_custom( args[0] )
    print( "Mapped minihive custom:", args )

  #@make_method('/unmapped/minihive/custom', 'ii')
  def handler_unmapped_minihive_custom( self, path, types, args, source ):
    self.unmapped_minihive_custom( args[0] )
    print( "Unmapped Minihive custom:", args )

### end map to hive

  #@make_method('/info/minibee', None )
  def handler_info_minibee( self, path, types, args, source ):
    self.info_minibee( args[0], args[1], args[2] )
    if self.verbose:
      print( "Info minibee:", args )

  def handler_status_minibee( self, path, types, args, source ):
    self.status_minibee( args[0], args[1] )
    if self.verbose:
      print( "Status minibee:", args )

  #@make_method('/info/hive', None )
  def handler_info_hive( self, path, types, args, source ):
    #self.dnosc.info_minibee( args[0] )
    print( "Info hive:", args )


#### minibee configuration stuff ###

  #@make_method('/info/hive', None )
  def handler_info_config( self, path, types, args, source ):
    #self.dnosc.info_minibee( args[0] )
    print( "Info config:", args )

  def handler_config_query( self, path, types, args, source ):    
    self.queryConfiguration( args[0] )
    if self.verbose:
      print( "MiniHive configuration query:", args )

  def handler_mbconfig( self, path, types, args, source ):    
    self.setMiniBeeConfiguration( args )
    if self.verbose:
      print( "MiniBee configuration:", args )

  def handler_mbconfiged( self, path, types, args, source ):    
    print( "MiniBee configured:", args )

  def handler_config( self, path, types, args, source ):    
    self.setConfiguration( args[0], args[1:] )
    if self.verbose:
      print( "MiniHive configuration:", args )

  def handler_configed( self, path, types, args, source ):    
    print( "MiniHive configuration created:", args )

  def handler_config_delete( self, path, types, args, source ):    
    self.deleteConfiguration( args[0] )
    if self.verbose:
      print( "MiniHive delete configuration:", args )

  def handler_config_deleted( self, path, types, args, source ):    
    print( "MiniHive configuration deleted:", args )

  def handler_config_query( self, path, types, args, source ):    
    ## TODO: query all configs!
    self.queryConfigurations()
    if self.verbose:
      print( "MiniHive configuration query" )
      
  def handler_cfsave( self, path, types, args, source ):    
    self.saveConfiguration( args[0] )
    if self.verbose:
      print( "MiniBee save configuration:", args )

  def handler_cfsaved( self, path, types, args, source ):    
    print( "MiniHive configuration saved:", args )

  def handler_cfload( self, path, types, args, source ):    
    self.loadConfiguration( args[0] )
    if self.verbose:
      print( "MiniBee load configuration:", args )

  def handler_cfloaded( self, path, types, args, source ):    
    print( "MiniHive configuration loaded:", args )

# end minibee management

  #@make_method(None, None)
  def fallback(self, path, args, types, src):
    print( "got unknown message '%s' from '%s'" % (path, src.get_url()) )
    for a, t in zip(args, types):
      print( "argument of type '%s': %s" % (t, a) )

# end class DNOSCServer

# begin class DataNetworkOSC
#class DataNetworkOSC(object):
  def __init__(self, hostip, myport, myname, network, cltype=0, nonodes=0, myhost='0.0.0.0' ):
    self.registered = False
    self.auto_register = True
    self.network = network
    self.callbacks = {}
    self.verbose = False
    self.createOSC( hostip, myport, myname, cltype, nonodes, myhost )
    
  def add_hive( self, hive ):
    self.hive = hive
    
  def createOSC( self, hostip, myport, myname, cltype, nonodes, myhost ):
    self.name = myname
    self.hostIP = hostip
    self.port = myport
    self.myIP = myhost
    self.findHost( hostip )
    print( "Found host at", self.hostIP, self.hostPort )
    self.resetHost()
    self.createClient()
    self.cltype = cltype
    self.nonodes = nonodes
    self.doRegister()
    
  def doRegister(self):
    if self.auto_register:
      if self.cltype == 0:
	self.register()
      if self.cltype == 1:
	self.registerHive(self.nonodes)
    
  def createClient(self):
    #try:
    receive_address = ( self.myIP, self.port )
    #receive_address = ( '127.0.0.1', self.port )
    self.osc = OSC.OSCServer( receive_address )
    self.add_handlers()
    self.thread = threading.Thread( target = self.osc.serve_forever )
    self.thread.start()
      #self.server = DNOSCServer( self.port, self )
      #self.server.start()
    #except ServerError, err:
      #print str(err)
      #sys.exit()
      
  def setHost( self, hip, hop ):
    self.hostIP = hip
    self.hostPort = hop
    self.resetHost()
    self.doRegister()
  
  def quitHost( self, hip, hop ):
    #print hip, hop
    if self.hostIP == hip:
      if self.hostPort == hop:
	self.findHost( self.hostIP ) # try and find host again, if not we just wait for an announce message
    
  def resetHost(self):
    self.host = OSC.OSCClient()
    send_address = ( self.hostIP, self.hostPort )
    self.host.connect( send_address )
    #self.host.connect( (self.hostIP, self.hostPort) )
    # could also be a forking or threading server?
    
    #try:
      #self.host = liblo.Address( self.hostIP, self.hostPort )
    #except liblo.AddressError, err:
      #print str(err)
      #sys.exit()
      
  def findHost( self, hostip ):
    """retrieve the host port number"""
    url = "http://"
    url += hostip
    url += "/SenseWorldDataNetwork"
    #print url
    response = urllib2.urlopen(url)
    self.hostPort = int( response.read() )
    
## data!
  def data_for_node( self, nodeid, data ):
    self.network.setNodeData( nodeid, data )

  def data_for_slot( self, nodeid, slotid, data ):
    self.network.setSlotData( nodeid, slotid, data )

  def expected_node( self, nodeid ):
    self.network.addExpectedNode( nodeid )

  def info_node( self, nodeid, label, size, dntype ):
    #print "info_node", nodeid, label, size, dntype
    self.network.infoNode( nodeid, label, size, dntype )
    self.call_callback( 'info', nodeid )


  def info_slot( self, nodeid, slotid, label, dntype ):
    self.network.infoSlot( nodeid, slotid, label, dntype )


  def am_setter( self, nodeid ):
    self.network.add_setter( nodeid )
    if 'setter' in self.callbacks:
      self.callbacks['setter']( nodeid )


## registering

  def register( self, mycallback = None ):
    if mycallback != None:
      #if 'register' not in self.callbacks:
      self.callbacks[ 'register' ] = mycallback
    self.sendSimpleMessage( "/register" )
    
  def unregister( self, mycallback = None ):
    if mycallback != None:
      #if 'register' not in self.callbacks:
      self.callbacks[ 'unregister' ] = mycallback
    self.sendSimpleMessage( "/unregister" )

  def set_registered( self, state ):
    if state:
      if not self.registered:
	self.queryAll()
	self.network.resend_state()
      if 'register' in self.callbacks:
	self.callbacks['register']( state )
    else:
      if 'unregister' in self.callbacks:
	self.callbacks['unregister']( state )
      self.doRegister()

    self.registered = state;
    #if self.registered:
      #print( "query and resend state" )
    if self.verbose:
      print( "registered", self.registered )

## queries
  def queryAll( self ):
    self.sendSimpleMessage( "/query/all" )

  def queryExpected( self ):
    self.sendSimpleMessage( "/query/expected" )

  def queryNodes( self ):
    self.sendSimpleMessage( "/query/nodes" )

  def querySlots( self ):
    self.sendSimpleMessage( "/query/slots" )

  def queryClients( self ):
    self.sendSimpleMessage( "/query/clients" )

  def querySetters( self ):
    self.sendSimpleMessage( "/query/setters" )

  def querySubscriptions( self ):
    self.sendSimpleMessage( "/query/subscriptions" )

## end queries

## subscribe

  def subscribeAll( self ):
    self.sendSimpleMessage( "/subscribe/all" )

  def unsubscribeAll( self ):
    self.sendSimpleMessage( "/unsubscribe/all" )

  def removeAll( self ):
    self.sendSimpleMessage( "/remove/all" )

  def setterCallback( self, mycallback ):
      self.callbacks[ 'setter' ] = mycallback

  def add_callback( self, ctype, cid, mycallback ):
      if ctype not in self.callbacks:
	self.callbacks[ ctype ] = {}
      self.callbacks[ ctype ][ cid ] = mycallback

  def subscribeNode( self, nodeid, mycallback = None ):
    if mycallback != None:
      self.add_callback( 'subscribe', nodeid, mycallback )
      #if 'subscribe' not in self.callbacks:
	#self.callbacks[ 'subscribe' ] = {}
      #self.callbacks[ 'subscribe' ][ nodeid ] = mycallback
    self.sendMessage( "/subscribe/node", [ nodeid ] )
    #msg = liblo.Message( "/subscribe/node", self.port, self.name, nodeid )
    ##self.sendMessage( msg )
    #del msg

  def unsubscribeNode( self, nodeid, mycallback = None ):
    if mycallback != None:
      self.add_callback( 'unsubscribe', nodeid, mycallback )
    self.sendMessage( "/unsubscribe/node", [ nodeid ] )
    #msg = liblo.Message( "/unsubscribe/node", self.port, self.name, nodeid )
    #self.sendMessage( msg )
    #del msg

  def subscribeSlot( self, nodeid, slotid ):
    # could add callback
    self.sendMessage( "/subscribe/slot", [ nodeid, slotid ] )
    #msg = liblo.Message( "/subscribe/slot", self.port, self.name, nodeid, slotid )
    #self.sendMessage( msg )
    #del msg

  def unsubscribeSlot( self, nodeid, slotid ):
    # could add callback
    self.sendMessage( "/unsubscribe/slot", [ nodeid, slotid ] )
    #msg = liblo.Message( "/unsubscribe/slot", self.port, self.name, nodeid, slotid )
    #self.sendMessage( msg )
    #del msg

  def getNode( self, nodeid ):
    self.sendMessage( "/get/node", [ nodeid ] )
    #msg = liblo.Message( "/get/node", self.port, self.name, nodeid )
    #self.sendMessage( msg )
    #del msg

  def getSlot( self, nodeid, slotid ):
    self.sendMessage( "/get/slot", [ nodeid, slotid ] )
    #msg = liblo.Message( "/get/slot", self.port, self.name, nodeid, slotid )
    #self.sendMessage( msg )
    #del msg

  def setData( self, nodeid, data ):
    alldata = [ nodeid ]
    alldata.extend( data )
    self.sendMessage( "/set/data", alldata )
    #msg = liblo.Message( "/set/data", self.port, self.name, nodeid )
    #for d in data:
      #msg.add( d )
    #self.sendMessage( msg )
    #del msg

  def labelNode( self, nodeid, label ):
    self.sendMessage( "/label/node", [ nodeid, label ] )
    #msg = liblo.Message( "/label/node", self.port, self.name, nodeid, label )
    #self.sendMessage( msg )
    #del msg

  def labelSlot( self, nodeid, slotid, label ):
    self.sendMessage( "/label/slot", [ nodeid, slotid, label ] )
    #msg = liblo.Message( "/label/slot", self.port, self.name, nodeid, slotid, label )
    #self.sendMessage( msg )
    #del msg

  def removeNode( self, nodeid ):
    self.sendMessage( "/remove/node", [ nodeid ] )
    #msg = liblo.Message( "/remove/node", self.port, self.name, nodeid )
    #self.sendMessage( msg )
    #del msg

  def addExpected( self, nodeid, info ):
    #print nodeid, info
    #print info.prepend( nodeid )
    #print [ nodeid ].extend( info )
    allinfo = [ nodeid ]
    allinfo.extend( info )
    self.sendMessage( "/add/expected", allinfo )
    #msg = liblo.Message( "/add/expected", self.port, self.name, nodeid )
    #for d in info:
      #msg.add( d )
    #self.sendMessage( msg )
    #del msg

## minibees and hives

  def registerHive( self, number ):
    if self.verbose:
      print( "sending a register hive message" )
    self.sendMessage( "/register/hive", [ number ] )
    #self.sendSimpleMessage( "/register/hive" )
    #msg = liblo.Message( "/register/hive", self.port, self.name, number )
    #self.sendMessage( msg )
    #del msg
    
  def unregisterHive( self ):
    self.sendSimpleMessage( "/unregister/hive" )
    
  def set_registered_hive( self, state, minid, maxid ):
    self.set_registered( state )
    #if self.registered:
      #FIXME: should be doing this actually!
      #self.hive.setNodeRange( minid, maxid )
    if self.verbose:
      print( "registered", self.registered )


  def queryHives( self ):
    self.sendSimpleMessage( "/query/hives" )

  def queryBees( self ):
    self.sendSimpleMessage( "/query/minibees" )

  # sending map request for a minibee
  def mapMinibee( self, nodeid, mid ):
    self.sendMessage( "/map/minibee/output", [ nodeid, mid ] )
    #msg = liblo.Message( "/map/minibee/output", self.port, self.name, nodeid, mid )
    #self.sendMessage( msg )
    #del msg

  # sending unmap request for a minibee
  def unmapMinibee( self, nodeid, mid ):
    self.sendMessage( "/unmap/minibee/output", [ nodeid, mid ] )
    #msg = liblo.Message( "/unmap/minibee/output", self.port, self.name, nodeid, mid )
    #self.sendMessage( msg )
    #del msg

  # sending map request for a minibee
  def mapMinibeeCustom( self, nodeid, mid ):
    self.sendMessage( "/map/minibee/custom", [ nodeid, mid ] )
    #msg = liblo.Message( "/map/minibee/custom", self.port, self.name, nodeid, mid )
    #self.sendMessage( msg )
    #del msg

  # sending unmap request for a minibee
  def unmapMinibeeCustom( self, nodeid, mid ):
    self.sendMessage( "/unmap/minibee/custom", [ nodeid, mid ] )
    #msg = liblo.Message( "/unmap/minibee/custom", self.port, self.name, nodeid, mid )
    #self.sendMessage( msg )
    #del msg

  # receiving confirmation of mapped minibee
  def mapped_minibee( self, nodeid, mid ):
    print( "mapped minibee output", nodeid, mid )

  # receiving confirmation of mapped minibee
  def unmapped_minibee( self, nodeid, mid ):
    print( "unmapped minibee output", nodeid, mid )

  # receiving confirmation of mapped minibee
  def mapped_minibee_custom( self, nodeid, mid ):
    print( "mapped minibee custom", nodeid, mid )

  # receiving confirmation of mapped minibee
  def unmapped_minibee_custom( self, nodeid, mid ):
    print( "unmapped minibee custom", nodeid, mid )


  # receiving confirmation of mapped minihive
  def mapped_minihive( self, nodeid ):
    print( "mapped minihive output", nodeid )

  # receiving confirmation of mapped minihive
  def unmapped_minihive( self, nodeid ):
    print( "unmapped minihive output", nodeid )

  # receiving confirmation of mapped minihive
  def mapped_minihive_custom( self, nodeid ):
    print( "mapped minihive custom", nodeid )

  # receiving confirmation of mapped minihive
  def unmapped_minihive_custom( self, nodeid ):
    print( "unmapped minihive custom", nodeid )

  # receiving minibee information
  def info_minibee( self, mid, nin, nout ):
    if self.verbose:
      print( "minibee info:", mid, nin, nout )

  def status_minibee( self, mid, status ):
    if self.verbose:
      print( "minibee status:", mid, status )

  # sending info about a minibee created
  def infoMinibee( self, mid, nin, nout ):
    self.sendMessage( "/info/minibee", [ mid, nin, nout ] )
    #msg = liblo.Message( "/info/minibee", self.port, self.name, mid, nin, nout )
    #self.sendMessage( msg )
    #del msg

  # sending status message about a minibee
  def statusMinibee( self, mid, status ):
    self.sendMessage( "/status/minibee", [ mid, status ] )

  # receiving map request output
  def map_minibee( self, nodeid, mid ):
    #self.subscribeNode( nodeid )
    # map data from subscribed node to minibee's data output
    self.network.mapAction( nodeid, mid )
    self.sendMessage( "/mapped/minibee/output", [ nodeid, mid ] )
    #msg = liblo.Message( "/mapped/minibee/output", self.port, self.name, nodeid, mid )
    #self.sendMessage( msg )
    #del msg

  # receiving map request custom
  def map_minibee_custom( self, nodeid, mid ):
    #self.subscribeNode( nodeid )
    # map data from subscribed node to minibee's custom output
    self.network.mapCustomAction( nodeid, mid )
    self.sendMessage( "/mapped/minibee/custom", [ nodeid, mid ] )
    #msg = liblo.Message( "/mapped/minibee/custom", self.port, self.name, nodeid, mid )
    #self.sendMessage( msg )
    #del msg

  # receiving unmap request output
  def unmap_minibee( self, nodeid, mid ):
    # unmap data from subscribed node to minibee's data output
    self.network.unmapAction( nodeid, mid )
    self.sendMessage( "/unmapped/minibee/output", [ nodeid, mid ] )

  # receiving map request custom
  def unmap_minibee_custom( self, nodeid, mid ):
    # unmap data from subscribed node to minibee's custom output
    self.network.unmapCustomAction( nodeid, mid )
    self.sendMessage( "/unmapped/minibee/custom", [ nodeid, mid ] )    


  # receiving map request output
  def map_minihive( self, nodeid ):
    #self.subscribeNode( nodeid )
    # map data from subscribed node to minihive's data output
    self.network.mapAction( nodeid, 0xFFFF )
    self.sendMessage( "/mapped/minihive/output", [ nodeid ] )
    #msg = liblo.Message( "/mapped/minihive/output", self.port, self.name, nodeid, mid )
    #self.sendMessage( msg )
    #del msg

  # receiving map request custom
  def map_minihive_custom( self, nodeid ):
    #self.subscribeNode( nodeid )
    # map data from subscribed node to minihive's custom output
    self.network.mapCustomAction( nodeid, 0xFFFF )
    self.sendMessage( "/mapped/minihive/custom", [ nodeid ] )
    #msg = liblo.Message( "/mapped/minihive/custom", self.port, self.name, nodeid, mid )
    #self.sendMessage( msg )
    #del msg

  # receiving unmap request output
  def unmap_minihive( self, nodeid ):
    # unmap data from subscribed node to minihive's data output
    self.network.unmapAction( nodeid, 0xFFFF )
    self.sendMessage( "/unmapped/minihive/output", [ nodeid ] )

  # receiving map request custom
  def unmap_minihive_custom( self, nodeid ):
    # unmap data from subscribed node to minihive's custom output
    self.network.unmapCustomAction( nodeid, 0xFFFF )
    self.sendMessage( "/unmapped/minihive/custom", [ nodeid ] )    


  def setMiniBeeConfiguration( self, config ):
    if not self.network.hive == None:
      if len( config ) == 3:
	# set minibee with serial number to given id
	if not self.network.hive.map_serial_to_bee( config[2], config[0] ):
	  # send error message
	  config.insert( 0, "/configure/minibee" )
	  self.sendMessage( "/minihive/error", config )
	  return
      # continue with setting the configuration
      self.network.hive.set_minibee_config( config[0], config[1] )
      self.sendMessage( "/configured/minibee", config )

  def queryConfigurations( self ):
    if not self.network.hive == None:
      self.network.hive.query_configurations( self )
      #print( "Query configuration %i"%cid )
      
  def infoConfig( self, configinfo ):
    self.sendMessage( "/info/configuration", configinfo )

  def deleteConfiguration( self, cid ):
    if not self.network.hive == None:
      if not self.network.hive.delete_configuration( cid ):
	self.sendMessage( "/minihive/error", ["/minihive/configuration/delete", cid] )
      else:
	self.sendMessage( "/minihive/configuration/deleted", [cid] )

  def setConfiguration( self, cid, config ):
    if not self.network.hive == None:
      allconfig = [ cid ]
      allconfig.extend( config )
      if not self.network.hive.set_configuration( cid, config ):
	allconfig.insert( 0, "/minihive/configuration/create" )
	self.sendMessage( "/minihive/error",  allconfig )
      else:
	self.sendMessage( "/minihive/configuration/created", allconfig )

  def loadConfiguration( self, filename ):
    if not self.network.hive == None:
      self.network.hive.load_from_file( filename )
      self.sendMessage( "/minihive/configuration/loaded", [filename] )      
      print( "loaded configuration from: ", filename )

  def saveConfiguration( self, filename ):
    if not self.network.hive == None:
      self.network.hive.write_to_file( filename )
      self.sendMessage( "/minihive/configuration/saved", [filename] )
      print( "saved configuration to: ", filename )

## message sending
  def sendMessage( self, path, args ):
    msg = OSC.OSCMessage()
    msg.setAddress( path )
    msg.append( self.port )
    msg.append( self.name )
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

    #try:
      #self.server.send( self.host, msg )
    #except liblo.AddressError, err:
      #print str(err)
  
  def sendSimpleMessage( self, path ):
    msg = OSC.OSCMessage()
    msg.setAddress( path )
    msg.append( self.port )
    msg.append( self.name )
    try:
      self.host.send( msg )
      if self.verbose:
	print( "sending message", msg )
    except OSC.OSCClientError:
      if self.verbose:
	print( "error sending message", msg )
    #try:
      #self.server.send( self.host, path, self.port, self.name )
    #except liblo.AddressError, err:
      #print str(err)
## end message sending
      
# end class DataNetworkOSC

# begin class DataNode
class DataNode(object):
  def __init__(self, network, nid, insize, label, dtype ):
    self.network = network
    self.nodeid = nid
    self.size = insize
    self.label = label
    self.dtype = dtype
    self.action = None
    #print insize
    self.data = list( 0 for i in range( 1, insize+1 ) )    
    self.slotlabels = list( "slot_{0!s}_{1!s}".format( nid, i ) for i in range(1,insize+1) )

    
  def setAction( self, action ):
    self.action = action

  def setSize( self, size ):
    self.size = size

  def setType( self, dtype ):
    self.dtype = dtype

  def setLabel( self, label ):
    self.label = label

  def setLabelSlot( self, slotid, label ):
    if slotid < self.size:
      self.slotlabels[slotid] = label

  def setDataSlot( self, slotid, data ):
    if slotid < self.size:
      self.data[slotid] = data
 
  def setData( self, data ):
    if len( data ) == self.size :
      self.data = data
      if self.action != None:
	self.action( self.data )

  def sendData( self ):
    self.network.sendData( self.nodeid, self.data )

  def sendLabel( self ):
    self.network.sendLabel( self.nodeid, self.label )


# end class DataNode

# begin class DataNetwork
class DataNetwork(object):
  def __init__(self, hostip, myport, myname, cltype=0, nonodes = 0, myhost='0.0.0.0' ):
    self.osc = DataNetworkOSC( hostip, myport, myname, self, cltype, nonodes, myhost )
    self.nodes = {} # contains the nodes we are subscribed to
    self.expectednodes = set([]) # contains node ids that are expected and could be subscribed to
    self.setters = set([]) # contains the nodes we are the setters of
    
    self.hive = None
   
    self.mapAction = None
    self.unmapAction = None
    self.mapCustomAction = None
    self.unmapCustomAction = None
  
  def setHive( self, hive ):
    self.hive = hive
  
  def setVerbose( self, onoff ):
    self.osc.setVerbose( onoff )
    
  def add_setter( self, nodeid ):
    self.setters.add( nodeid )
    
  def set_mapAction( self, action ):
    self.mapAction = action

  def set_unmapAction( self, action ):
    self.unmapAction = action

  def set_mapCustomAction( self, action ):
    self.mapCustomAction = action

  def set_unmapCustomAction( self, action ):
    self.unmapCustomAction = action

  def addExpectedNode( self, nodeid ):
    self.expectednodes.add( nodeid )
    #print "Expected nodes:", self.expectednodes
    
  def resend_state( self ):
    #print( "resend state", self.nodes, self.setters )
    for nodeid,node in self.nodes.items():
      if self.osc.verbose:
	print( "subscription", nodeid, node )
      self.osc.subscribeNode( nodeid )
    for sid in self.setters:
      if self.osc.verbose:
	print( "setter", sid )
      self.osc.addExpected( nodeid, [ self.nodes[ nodeid ].label, self.nodes[ nodeid ].size ] )
      self.nodes[nodeid].sendData()

  def infoNode( self, nodeid, label, size, dntype ):
    if nodeid not in self.nodes:
      self.nodes[ nodeid ] = DataNode( self, nodeid, size, label, dntype )
    else:
      #try:
      self.nodes[ nodeid ].setLabel( label )
      self.nodes[ nodeid ].setSize( size )
      self.nodes[ nodeid ].setType( dntype )
      #except:
    if nodeid not in self.nodes:
      print( "InfoNode: nodeid ", nodeid, "not in nodes", self.nodes )

  def infoSlot( self, nodeid, slotid, label, dntype ):
    if nodeid in self.nodes:
      self.nodes[ nodeid ].setLabelSlot( slotid, label )
    else:
      print( "InfoSlot: nodeid ", nodeid, "not in nodes", self.nodes )

  def setNodeData( self, nodeid, data ):
    if nodeid in self.nodes:
      self.nodes[ nodeid ].setData( data )
    else:
      print( "DataNode: nodeid ", nodeid, "not in nodes", self.nodes )

  def setSlotData( self, nodeid, slotid, data ):
    if nodeid in self.nodes:
      self.nodes[ nodeid ].setDataSlot( slotid, data )
    else:
      print( "SlotData: nodeid ", nodeid, "not in nodes", self.nodes )

  def sendData( self, nodeid, data ):
    self.osc.setData( nodeid, data )

  def sendLabel( self, nodeid, data ):
    self.osc.setLabel( nodeid, data )

# end class DataNetwork


#def wait( tim ):
  #time.sleep( tim )

if __name__ == "__main__":
  
  parser = optparse.OptionParser(description='Create a datanetwork client to communicate with the SenseWorld DataNetwork.')
  parser.add_option('-p','--port', type=int, action='store',dest="port",default=57600,
		  help='the port on which the client will listen [default:%i]'% 57600 )
  parser.add_option('-n','--name', action='store', type="string", dest="name",default="pydonhive",
		  help='the name of the client in the datanetwork [default:%s]'% "pydonhive" )
  parser.add_option('-d','--host_ip', action='store',type="string", dest="host",default="127.0.0.1",
		  help='the ip address of the datanetwork host [default:%s]'% "127.0.0.1")
  parser.add_option('-v','--verbose', action='store',dest="verbose",default=False,
		  help='verbose printing [default:%i]'% False)

  (options,args) = parser.parse_args()
  #print args.accumulate(args.integers)
  #print options
  #print args
  #print( options.host )

  def dataAction( data ):
    print( "dataAction", data )
    
  def setDataAction( nodeid ):
    global datanetwork
    datanetwork.nodes[ nodeid ].setAction( dataAction )

#  datanetwork = DataNetwork( '172.31.15.144', 6000, "pydond", 0, 20 )
  datanetwork = DataNetwork( options.host, options.port, options.name, 0, 10 )
  datanetwork.setVerbose( options.verbose )
  
  #wait( 2.0 )
  #time.sleep( 1.0 )
  
  #print datanetwork.nodes
  
  while not datanetwork.osc.registered:
    print( "waiting to be registered" )
    #print time
    myTime.sleep( 1.0 )
  
  datanetwork.osc.subscribeNode( 1, setDataAction )
  
  try :
    while 1 :
        myTime.sleep(5)

  except KeyboardInterrupt :
    datanetwork.osc.unregister()
    print( "\nClosing OSCServer." )
    datanetwork.osc.osc.close()
    print( "Waiting for Server-thread to finish" )
    datanetwork.osc.thread.join() ##!!!
    print( "Done" )
