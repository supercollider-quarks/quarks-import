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


SETO_OSCTUIOServer : SETOServer {
	var interface;
	var idHash, class;
	
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
		idHash = IdentityDictionary.new;
		class = IdentityDictionary[
			108 -> 0,
			109 -> 0,
			110 -> 0,
			111 -> 0,
			112 -> 0,
			113 -> 0,
			0 -> 1
		];
		
		interface = OSCReceiver(('/tuio/'++format.asSymbol).asSymbol, netaddr);
		this.setFunc_{|id, classID ... args|
			idHash[id] = classID;
			
			this.setWithFormat(realFormat, classID, [class[classID]] ++ args);
		};
		this.aliveFunc_{|... argObjectIDs|
			var ids;
			
//			argObjectIDs.postln;
			ids = argObjectIDs.collect{|id| idHash[id]}.select(_.notNil);
//			ids.postln;
			this.alive(ids);
		};
	}
}