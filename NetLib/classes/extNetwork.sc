
+NetAddr {
	// check this again!!
	*myIP { |prefix=""|
		var j, k, res, bc;
		// prefix argument, since on Linux ifconfig does not lie in the user's path, but in /sbin/, so it can be passed as an argument (nescivi, April 2008)
		res = Pipe.findValuesForKey(prefix++"ifconfig", "inet");
		bc = this.broadcastIP;
		if(bc.notNil) {
			bc = bc.keep(7);
			res = res.select { |x| x.beginsWith(bc) }; // choose match with broadcast
		};
		
		 // don't want to check for netmask here.
		// need a simpler and better solution later.
		res = res.reject(_ == "127.0.0.1");
		
		if(res.size > 1) { postln("the first of those devices was chosen: " ++ res) };
		res = res.first;

		// fix for Linux output: (nescivi, April 2008)
		res = res.replace("addr:","");
		
		^res ?? { 
			"chosen loopback IP, no other ip available".warn; 
			"127.0.0.1"
		}
	}

	// convenience method (added by nescivi, April 2008)
	*atMyIP { |port=57110,prefix=""|
		var hostname = this.myIP(prefix);
		^NetAddr(hostname, port )
	}

	*broadcastIP {
		var  res;
		res = Pipe.findValuesForKey("ifconfig", "broadcast");
		if(res.size > 1) { postln("the first of those devices were chosen: " ++ res) };
		^res.first
	}
	*broadcast { arg port = 57120;
		var hostname = this.broadcastIP(port);
		if(hostname.isNil) { 
			hostname = "127.0.0.1"; 
			"no network with broadcast available. provisionally used loopback instead.".warn;
		};
		^NetAddr(hostname, port)
	}
	
	// assume a broadcast (see NamedNetAddr)
	sendNamedMsg { arg ... args;
		this.sendMsg(*args.insert(1, \broadcast))
	}
	listSendNamedMsg { arg args;
		this.sendMsg(*args.insert(1, \broadcast))
	}

}

+Pipe {
	*do { arg commandLine, func; 
		var line, pipe = this.new(commandLine, "r"), i=0;
		{
			line = pipe.getLine;
			while { line.notNil } {
				func.value(line, i);
				i = i + 1;
				line = pipe.getLine;
			}
		}.protect { pipe.close };
		
	}
	*findValuesForKey { arg commandLine, key, delimiter=$ ;
		var j, k, indices, res, keySize;
		key = key ++ delimiter;
		keySize = key.size;
		Pipe.do(commandLine, { |l|			
			indices = l.findAll(key);
			indices !? {
				indices.do { |j|
					j = j + keySize;
					while { l[j] == delimiter } { j = j + 1 };
					k = l.find(delimiter.asString, offset:j) ?? {Êl.size } - 1;
					res = res.add(l[j..k])
				};
			};
		});
		^res
	}
	
}

+String {
	// a markov set would maybe be better
	
	*rand { arg length = 8, nCapitals = 0, pairProbability = 0.2;
		var consonants = "bcdfghjklmnpqrstvwxz";
		var vowels = "aeiouy";
		var cweight = #[ 0.07, 0.03, 0.07, 0.06, 0.07, 0.03, 0.01, 0.07, 0.07, 0.06, 0.07, 0.06, 
								0.01, 0.06, 0.07, 0.07, 0.01, 0.03, 0.01, 0.04 ];
		var vweigth = #[ 0.19, 0.19, 0.19, 0.19, 0.19, 0.07 ];
		var lastWasVowel = false;
		var last, res, ci, breakCluster=false;
		res = this.fill(length, { |i|
						var vowel = if(breakCluster.not and: {pairProbability.coin}) 
									{ÊbreakCluster = true; lastWasVowel.not } 
									{ breakCluster = false; lastWasVowel };
						if(vowel) {
							lastWasVowel = false;
							last = vowels.wchoose(vweigth)
						} { 
							lastWasVowel = true;
							last = if(last == $q) { $u } { 
								consonants.wchoose(cweight)
							};
						};
		});
		if(nCapitals > 0) {
			ci = [0] ++ (2..length-2).scramble.keep(nCapitals - 1);
			if(ci.size < nCapitals) { ci = ci.add(length-1) };
			if(ci.size < nCapitals) { ci = ci.add(1) };
			ci.do {|i|
				res[i] = res[i].toUpper;
			};
		};
		^res
	}
}

+Symbol {
	*rand { arg length=8, nCapitals=0;
		^String.rand(length, nCapitals).asSymbol
	}
}

+ Nil { // nil matches everything
		
	pairsDo {
		 ^this
	}
}

