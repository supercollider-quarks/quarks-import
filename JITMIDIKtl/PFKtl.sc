PFKtl : MIDIKtl {
	classvar <>verbose = false; 
	var <>softWithin = 0.05, <lastVals;

	init { 
		super.init;
		ctlNames = defaults[this.class];
		lastVals = ();
	}

	*makeDefaults { 

		// just one bank of sliders
		defaults.put(this, 
			(
				sl1: '0_7', 
				sl2: '1_7', 
				sl3: '2_7', 
				sl4: '3_7', 
				sl5: '4_7', 
				sl6: '5_7', 
				sl7: '6_7', 
				sl8: '7_7', 
				sl9: '8_7', 
				sl10: '9_7', 
				sl11: '10_7', 
				sl12: '11_7', 
				sl13: '12_7', 
				sl14: '13_7', 
				sl15: '14_7',
				sl16: '15_7'
			)
		);
	}
	
	mapCC { |ctl= \sl1, action| 
		var ccDictKey = ctlNames[ctl]; // '0_42'
		if (ccDictKey.isNil) { 
			warn("key % : no chan_ccnum found!\n".format(ctl));
		} { 
			ccDict.put(ccDictKey, action);
		}
	}
		// map to 
	mapToPxEdit { |editor, indices, lastIsVol = true| 
		var slKeys, lastSlKey; 
		indices = indices ? (1..8); 
		
		slKeys = indices.collect { |i| ("sl" ++ i).asSymbol }; 
		
		if (lastIsVol) { 
			lastSlKey = slKeys.pop;
			
				// use last slider for proxy volume
			this.mapCC(lastSlKey, { |ch, cc, val| 
				var lastVal = lastVals[lastSlKey];
				var mappedVol = \amp.asSpec.map(val / 127);
				var proxy = editor.proxy;
				if (proxy.notNil) { proxy.softVol_(mappedVol, softWithin, lastVal: lastVal) };
				lastVals[lastSlKey] = mappedVol;
			});
		};
		
		slKeys.do { |key, i|  	
			this.mapCC(key, 
				{ |ch, cc, val| 
					var proxy = editor.proxy;
					var parKey =  editor.editKeys[i];
					var normVal = val / 127;
					var lastVal = lastVals[key];
					if (parKey.notNil and: proxy.notNil) { 
						proxy.softSet(parKey, normVal, softWithin, lastVal: lastVal) 
					};
					lastVals.put(key, normVal);
				}
			)
		};
	}
	
	mapToPxMix { |mixer, splitIndex = 8, lastEdIsVol = true, lastIsMaster = true| 
 	
		var server = mixer.proxyspace.server;
		var slKeys = (1..16).collect { |n| ("sl" ++ n).asSymbol }; 
		var lastKey; 
		
				// add master volume on slider 16
		if (lastIsMaster) { 
			lastKey = slKeys.pop; 
			Spec.add(\mastaVol, [server.volume.min, server.volume.max, \db]);
			this.mapCC(lastKey, { |chan, cc, val| server.volume.volume_(\mastaVol.asSpec.map(val/127)) });
		};			

			// map first n sliders to volumes
		slKeys.keep(splitIndex).do { |key, i| 
			this.mapCC(key, 
				{ |ch, cc, val| 
					var proxy = mixer.pxMons[i].proxy; 
					var lastVal, mappedVal, lastVol;
					if (proxy.notNil) { 
						lastVal = lastVals[key]; 
						mappedVal = \amp.asSpec.map(val / 127); 
						lastVol = if (lastVal.notNil) { \amp.asSpec.map(lastVal) }; 
						proxy.softVol_( \amp.asSpec.map(mappedVal), softWithin, true, lastVol ); 
					};
					lastVals[key] =  mappedVal;
				};
			)
		};
		
		this.mapToPxEdit(mixer.editor, (splitIndex + 1 .. slKeys.size));
	}
}
