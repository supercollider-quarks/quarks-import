PFKtl : MIDIKtl {
	classvar <>verbose = false; 
	var <>softWithin = 0.05, <lastVals;

	init { 
		super.init;
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

PDKtl : MIDIKtl {
	classvar <>verbose = false; 

	var <>step = 0.01; 
	
	*makeDefaults { 

		// implement nudge! 
	/*	all midi chan 0, 
	scene 1: 0 - 15
	scene2: 	16 - 31
	scene 3: 32 - 47
	scene4: 48 - 63
	*/

		// just one bank of sliders
		defaults.put(this, (
			1: 	(	kn01: '0_0', 
					kn02: '0_1', 
					kn03: '0_2', 
					kn04: '0_3', 
					kn05: '0_4', 
					kn06: '0_5', 
					kn07: '0_6', 
					kn08: '0_7', 
					kn09: '0_8', 
					kn10: '0_9', 
					kn11: '0_10', 
					kn12: '0_11', 
					kn13: '0_12', 
					kn14: '0_13', 
					kn15: '0_14',
					kn16: '0_15'
			),

			2: 	(	kn01: '0_16', 
					kn02: '0_17', 
					kn03: '0_18', 
					kn04: '0_19', 
					kn05: '0_20', 
					kn06: '0_21', 
					kn07: '0_22', 
					kn08: '0_23', 
					kn09: '0_24', 
					kn10: '0_25', 
					kn11: '0_26', 
					kn12: '0_27', 
					kn13: '0_28', 
					kn14: '0_29', 
					kn15: '0_30',
					kn16: '0_31'
			),

			3: 	(	kn01: '0_32', 
					kn02: '0_33', 
					kn03: '0_34', 
					kn04: '0_35', 
					kn05: '0_36', 
					kn06: '0_37', 
					kn07: '0_38', 
					kn08: '0_39', 
					kn09: '0_40', 
					kn10: '0_41', 
					kn11: '0_42', 
					kn12: '0_43', 
					kn13: '0_44', 
					kn14: '0_45', 
					kn15: '0_46',
					kn16: '0_47'
			),

			4: 	(	kn01: '0_48', 
					kn02: '0_49', 
					kn03: '0_50', 
					kn04: '0_51', 
					kn05: '0_52', 
					kn06: '0_53', 
					kn07: '0_54', 
					kn08: '0_55', 
					kn09: '0_56', 
					kn10: '0_57', 
					kn11: '0_58', 
					kn12: '0_59', 
					kn13: '0_60', 
					kn14: '0_61', 
					kn15: '0_62',
					kn16: '0_63'
			)
		));
	}
	
		// map to 
	mapToPxEdit { |editor, scene = 1, indices, lastIsVol = true| 
		var knobKeys, lastKnobKey; 
		indices = indices ? (1..8); 
		
		knobKeys = indices.collect { |i| ("kn" ++ (100 + i).asString.drop(1)).asSymbol}.postcs; 
		
		if (lastIsVol) { 
			lastKnobKey = knobKeys.pop;
			
				// use last knob for proxy volume
			this.mapCCS(scene, lastKnobKey, { |ch, cc, val| 
				var proxy = editor.proxy;
				if (proxy.notNil) { proxy.nudgeVol(val - 64 * step) };
			});
		};
		
		knobKeys.do { |key, i|  	
			this.mapCCS(scene, key, 
				{ |ch, cc, val| 
					var proxy = editor.proxy;
					var parKey =  editor.editKeys[i];
					if (parKey.notNil and: proxy.notNil) { 
						proxy.nudge(parKey, val - 64 * step) 
					};
				}
			)
		};
	}
	
	mapToPxMix { |mixer, scene = 1, splitIndex = 8, lastEdIsVol = true, lastIsMaster = true| 
 	
		var server = mixer.proxyspace.server;
		var knobKeys = (1..16).collect { |i| ("kn" ++ (100 + i).asString.drop(1)).asSymbol }; 
		var lastKey; 
		
				// add master volume on knob 16
		if (lastIsMaster) { 
			lastKey = knobKeys.pop; 
			Spec.add(\mastaVol, [server.volume.min, server.volume.max, \db]);
			this.mapCCS(scene, lastKey, { |chan, cc, val| 
				var oldNormVal = \mastaVol.asSpec.unmap(server.volume.volume).postln;
				var nudgedVol = (oldNormVal + (64 - val * step)).clip(0, 1);
				server.volume.volume_(nudgedVol.ampdb) 
			});
		};			

			// map first n knobs to volumes
		knobKeys.keep(splitIndex).do { |key, i| 
			
			this.mapCCS(scene, key, 
				{ |ch, cc, val| 
					var proxy = mixer.pxMons[i].proxy; 
					if (proxy.notNil) { 
						proxy.nudgeVol(64 - val * step); 
					};
				};
			)
		};
		
		this.mapToPxEdit(mixer.editor, scene, (splitIndex + 1 .. knobKeys.size));
	}
}
