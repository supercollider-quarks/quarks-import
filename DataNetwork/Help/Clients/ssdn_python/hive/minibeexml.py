# -*- coding: utf-8 -*-

#!/usr/bin/python
# Filename: minibeexml.py

import xml.etree.ElementTree as ET

class HiveConfigFile():
  
  #def set_hive( self, hive ):
    #self.hive = hive
    
  def write_file( self, filename, hive ):
    # build a tree structure
    root = ET.Element("xml")

    el_hive = ET.SubElement(root, "hive")
    el_hive.set( "name", hive.name )

    for bid, bee in hive.bees.items():
      #print bee
      el_bee = ET.SubElement(el_hive, "minibee")
      el_bee.set( "serial", str(bee.serial) )
      el_bee.set( "id", str(bee.nodeid) )
      el_bee.set( "revision", str(bee.revision) )
      el_bee.set( "libversion", str(bee.libversion) )
      el_bee.set( "caps", str(bee.caps) )
      if bee.cid > 0:
	el_beeConfig = ET.SubElement( el_bee, "config" )
	el_beeConfig.set( "id", str(bee.config.configid) )
	el_beeConfig.set( "name", str(bee.config.name) )
      else:
	el_beeConfig = ET.SubElement( el_bee, "config" )
	el_beeConfig.set( "id", str(bee.cid) )
	el_beeConfig.set( "name", "" )
      #ET.dump( el_bee )
      #el_beeCustom = ET.SubElement( el_bee, "custom" )
      #for pin in bee.custompins:
	#el_pin = ET.SubElement( el_bee, "pin" )
	#el_pin.set( "id", pin.name )
	#el_pin.set( "type", pin.type )
	#el_pin.set( "size", pin.size )

      #el_beeCustom.set( "id", bee.config.configid )
      #el_beeCustom.set( "name", bee.config.name )

 
    for cid, cfg in hive.configs.items():
      #print cfg
      el_cfg = ET.SubElement(el_hive, "configuration")
      el_cfg.set( "name", str( cfg.name ) )
      el_cfg.set( "id", str( cfg.configid ) )
      el_cfg.set( "message_interval", str( cfg.messageInterval ) )
      el_cfg.set( "samples_per_message", str( cfg.samplesPerMessage ) )
      for pinkey, pincfg in cfg.pins.items():
	el_pin = ET.SubElement( el_cfg, "pin" )
	el_pin.set( "id", pinkey )
	el_pin.set( "config", pincfg )
      for did, dev in cfg.twis.items():
	el_twi = ET.SubElement( el_cfg, "twi" )
	el_twi.set( "id", did )
	el_twi.set( "device", dev )

    # wrap it in an ElementTree instance, and save as XML
    tree = ET.ElementTree(root)
    #tree.indent()
    #print root
    tree.write( filename )
    
  def read_file( self, filename ):
    tree = ET.parse( filename )
    # if you need the root element, use getroot
    root = tree.getroot()
    # ...manipulate tree...
    #print root.tag
    hiveconfig = {}
    for node in root:
      hiveconfig['name'] = node.get( "name" )
      hiveconfig['bees'] = {}
      for bee in node.getiterator("minibee"):
	hiveconfig['bees'][ bee.get( "serial" ) ] = {}
	hiveconfig['bees'][ bee.get( "serial" ) ][ "revision" ] = bee.get( "revision" )
	hiveconfig['bees'][ bee.get( "serial" ) ][ "libversion" ] = int( bee.get( "libversion" ) )
	hiveconfig['bees'][ bee.get( "serial" ) ][ "caps" ] = int( bee.get( "caps" ) )
	hiveconfig['bees'][ bee.get( "serial" ) ][ "serial" ] = bee.get( "serial" )
	hiveconfig['bees'][ bee.get( "serial" ) ][ "mid" ] = int( bee.get( "id" ) )
	for conf in bee.getiterator("config"):
	  hiveconfig['bees'][ bee.get( "serial" ) ][ "configid" ] = int( conf.get( "id" ) )
	  hiveconfig['bees'][ bee.get( "serial" ) ][ "configname" ] = conf.get( "name" )
      hiveconfig['configs'] = {}
      for configs in node.getiterator("configuration"):
	hiveconfig['configs'][ configs.get( "id" ) ] = {}
	hiveconfig['configs'][ configs.get( "id" ) ]["cid"] = int( configs.get( "id" ) )
	hiveconfig['configs'][ configs.get( "id" ) ]["name"] = configs.get( "name" )
	hiveconfig['configs'][ configs.get( "id" ) ]["samples_per_message"] = int( configs.get( "samples_per_message" ) )
	hiveconfig['configs'][ configs.get( "id" ) ]["message_interval"] = int( configs.get( "message_interval" ) )
	hiveconfig['configs'][ configs.get( "id" ) ]["pins"] = {}
	hiveconfig['configs'][ configs.get( "id" ) ]["pinlabels"] = {}
	#print configs.getiterator("pin")
	for pin in configs.getiterator("pin"):
	  #print pin
	  hiveconfig['configs'][ configs.get( "id" ) ]["pins"][ pin.get("id") ] = pin.get( "config" )
	  hiveconfig['configs'][ configs.get( "id" ) ]["pinlabels"][ pin.get("id") ] = pin.get( "name" )
	hiveconfig['configs'][ configs.get( "id" ) ]["twis"] = {}
	hiveconfig['configs'][ configs.get( "id" ) ]["twilabels"] = {}
	for twi in configs.getiterator("twi"):
	  #print twi
	  hiveconfig['configs'][ configs.get( "id" ) ]["twis"][ twi.get("id") ] = twi.get( "device" )
	  hiveconfig['configs'][ configs.get( "id" ) ]["twilabels"][ twi.get("id") ] = twi.get( "name" )
      #print hiveconfig
      return hiveconfig

# main program

if __name__ == "__main__":
  cfgfile = HiveConfigFile()
  cfgfile.read_file( "hiveconfig.xml" )
