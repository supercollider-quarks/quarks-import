/*
Implementation of an OSC server to SETO via the tuio protocol
	http://tuio.lfsaw.de/
	http://modin.yuri.at/publications/tuio_gw2005.pdf

Author: 
	2004, 2005, 2006, 2007
	Till Bovermann 
	Neuroinformatics Group 
	Faculty of Technology 
	Bielefeld University
	Germany
*/

/*
	Changes
		2007-10-29	renamed to SETObject
*/

SETO_OSCServer : SETOServer {
	var interface;

	*new {|format, netaddr, setoClass, interactionClass|
		^super.new(format, setoClass, interactionClass).pr_initSETO_OSCServer(netaddr);
	}
	start{
		interface.start;
	}
	stop{
		interface.stop;
	}

	setFunc_{|function|
		setFunc = function;
		OSCReceiverFunction(interface, \set, setFunc);
	}
	aliveFunc_{|function|
		aliveFunc = function;
		OSCReceiverFunction(interface, \alive, aliveFunc);
	}
	pr_initSETO_OSCServer {|netaddr|
		
		interface = OSCReceiver(('/tuio/'++format.asSymbol).asSymbol, netaddr);
		this.setFunc_(setFunc);
		this.aliveFunc_(aliveFunc);
	}
}