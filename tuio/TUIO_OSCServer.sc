/*
Implementation of an OSC server to TUIOs
	http://tuio.lfsaw.de/
	http://modin.yuri.at/publications/tuio_gw2005.pdf

Author: 
	2004, 2005, 2006
	Till Bovermann 
	Neuroinformatics Group 
	Faculty of Technology 
	Bielefeld University
	Germany
*/

TUIO_OSCServer : TUIOServer {
	var interface;

	*new {|format, netaddr, tuioClass, interactionClass|
		^super.new(format, tuioClass, interactionClass).pr_initTUIO_OSCServer(netaddr);
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
	pr_initTUIO_OSCServer {|netaddr|
		
		interface = OSCReceiver(('/tuio/'++format.asSymbol).asSymbol, netaddr);
		this.setFunc_(setFunc);
		this.aliveFunc_(aliveFunc);
	}
}