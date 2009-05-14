
   /////////////////////////////////////////////////////////
  // GLOBOL 2009 : EVERYTHING, EVERYWHERE, ALL THE TIME ///
 /////////////////////////////////////////////////////////

// ADC, JRH; Donnerstag, 1. Januar 1970 01:02:42 //
//  TODO: AUTOMATIC NETWORK SYNC WITH PUBLIC  //


GLOBOL {

	classvar space, <isRunning = false, <numChannels;
	classvar server, responder, sender, id;
	classvar <classmethdict;
	classvar prevPreProcessor, prevNetFlag;
	
	*run { | n = 8 |
		if(isRunning) { "GLOBOL IS RUNNING ALREADY!".warn; ^this };
		prevPreProcessor = thisProcess.interpreter.preProcessor;
		thisProcess.interpreter.preProcessor = { |string| 
			interpret(process(this, string));
			"''"; // BLOCK SC REPL
		};
		numChannels = n;
		Server.local.options.numAudioBusChannels = n * 128;
		space = ProxySpace.push(Server.local.reboot);
		isRunning = true;
		this.buildDict;
		this.connect;
		id = inf.asInteger.rand;
	}
	
	*end { | time = 8 |
		thisProcess.interpreter.preProcessor = prevPreProcessor;
		space.clear(time).pop;
		isRunning = false;
		this.disconnect;
	}
	
	*buildDict {
		classmethdict = ();
		
		Class.allClasses.do { |class|
				classmethdict.put(class.name.asString.toUpper.asSymbol, class.name.asString);
				class.methods.do { |method|
					classmethdict.put(
						*[method.name.asString.toUpper.asSymbol, method.name.asString]
					)
				}
		};
		
	}
	
	*process { | string |
		var lines = split(string, Char.nl);
		if(isPermitted(this, string).not) { "GLOBOL DOES NOT PERMIT STRINGS".warn; ^this };
		
		this.distribute(string);
		string.postln; // GLOBOL REPL
		
		^join(collect(lines, { | line |
			var newline = "".copy;
			var allCaps = List.new;
			var capStart, capLength = 0;
			
			line = line.replace(":", ".");
			line = " " ++ line ++ "  ";
			
			// FIND GLOBOL VARIABLES
			
			(line.size - 2).do { |index|
				var left, middle, right;
				#left, middle, right = line[index..index+2];
				
				// REPLACE UPPER WITH LOWER 
				
				if(	left.isAlpha.not 
					and: { middle.isAlpha }
					and: { middle.isUpper }
					and: { right.isAlpha.not }) 
					{
						middle = middle.toLower;
						if ( (right == $.) or: { line[index + [2, 3]].join == " =" }) {
							newline = newline ++ "~%.ar(%); ".format(middle, numChannels);
							newline = newline ++ "~" ++ middle;
							
						} {
							newline = newline ++ "~%.ar(%)".format(middle, numChannels);
						}
					} {
						newline = newline.add(middle);
					};
				};
				
				// REPLACE CAP-STRINGS WITH PROPER CLASS AND METHOD NAMES
				newline.do { |char, index|
					
					if(char.isAlpha and: { char.isUpper }) { 
						if (capLength == 0) { 
							capStart = index; 
						};
						capLength = capLength + 1;
					} { 
						if (capLength > 1) { 
							allCaps.add([capStart, capLength]);
						};
						capLength = 0;
					};
				}; 
				newline = newline.copy;
				allCaps.do { |list| 
					var start, length, bigName, smallName; 
					#start, length = list;
					
					bigName = newline[start..start + length - 1].asSymbol;
					smallName = classmethdict[bigName];
					smallName !? {
						newline.overWrite(smallName, start);
					};
				};
				if(newline.every(_.isSpace).not) { newline = newline ++ ";" };
				newline
		}), Char.nl);
	}
	
	
	
	
	// NETWORKING //
	
	*isPermitted { | string |
		^string.includes($").not and: { string.find("asString").isNil }
	}
	
	*distribute { | string |
		sender.sendMsg("/GLOBOL2009", string);
	}
	
	*connect {
		responder = OSCresponder(nil, "/GLOBOL2009", { |r,t,msg|
				var code = msg[1].asString;
				var inID = msg[2];
				// GLOBOL DOES NOT PERMIT STRINGS
				if(inID != id and: { isPermitted(this, code) }) 
				{
					interpret(process(this, code));
				}
		}).add;
		sender = this.broadcast;
		prevNetFlag = NetAddr.broadcastFlag;
		NetAddr.broadcastFlag = true;
	}
	
	*disconnect {
		responder.remove;
		sender.disconnect;
		NetAddr.broadcastFlag = prevNetFlag;
	}
	
	*broadcastIP { | prefix = "", device = "" |
		var  res,k,delimiter=$ ;
		res = Pipe.findValuesForKey(prefix+/+"ifconfig"+device, "broadcast");
		res = res ++ Pipe.findValuesForKey(prefix+/+"ifconfig"+device, "Bcast", $:);

		if(res.size > 1) { postln(toUpper("the first of the following devices were chosen: " 
			++ res)) };
		res.do{ |it,i|
			k = it.find(delimiter.asString) ?? { it.size } - 1;
			res[i] = (it[0..k]);
		};
		^res.first
	}
	
	*broadcast { | port = 57120, prefix = "" |
		var hostname = this.broadcastIP(prefix);
		if(hostname.isNil) { 
			hostname = "127.0.0.1"; 
			"no network with broadcast available."
			" provisionally used loopback instead.".toUpper.warn;
		};
		^NetAddr(hostname, port)
	}

}

