OSCKtl : Ktl {
	classvar <>verbose = false;
	//srcID and destID should be NetAddr instances

	removeResp {
		resp.do(_.remove);
	}

	makeResp {
		this.removeResp;
		resp = List.new;
		ktlNames.pairsDo{ |name,osckey|
			resp.add(OSCresponderNode(srcID,osckey,{ |t, r, msg|
				if (this.class.verbose, { ("OSCKtl<-: "++osckey++", "++ msg[1]).postln });
				if( ktlDict[osckey].notNil ) {
					ktlDict[osckey].valueAll(msg[1]);
				}
			}).add);
		}
	}

	ktlNames_{ |dict|
		ktlNames = dict;
		this.makeResp
	}

	//if you don't want to destinguish between the key and the osckey
	ktlNamesFromArray_{ |array|
		ktlNames = Dictionary.new;
		array.do{ |key|
			ktlNames.put(key,key)
		};
		this.makeResp
	}

	sendCtl { arg key ... args;
		if (this.class.verbose, { ("OSCKtl->: "++key++": "++ktlNames[key]++ ": " ++ args).postln });
		if( ktlNames[key].notNil ) {
			destID.sendMsg(*(ktlNames[key].asArray++args))
		}
	}

}
