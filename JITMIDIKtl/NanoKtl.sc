MIDIController { 
		lastVals = ();
		pxOffsets = (1: 0, 2: 0, 3: 0, 4: 0);
		parOffsets = (1: 0, 2: 0, 3: 0, 4: 0);
		 	
					var parKey =  pxEditors[scene].editKeys[i + parOffsets[scene]];
					var normVal = val / 127;
					var lastVal = lastVals[key];
					if (parKey.notNil and: proxy.notNil) { 
						proxy.softSet(parKey, normVal, softWithin, lastVal: lastVal) 
					};
					lastVals.put(key, normVal);
			var lastVal = lastVals[\kn9];
			var mappedVol = \amp.asSpec.map(val / 127);
			var proxy = pxEditors[scene].proxy;
			if (lastVal.notNil) { lastVal = \amp.asSpec.map(lastVal) };
				proxy.softVol_(mappedVol, softWithin, pause: volPause, lastVal: lastVal) 
			};
			lastVals[\kn9] = mappedVol;

			// can't softVol server volume ... hmm. do it by hand? 
		
					var lastVal = lastVals[key]; 
					var mappedVal = \amp.asSpec.map(val / 127); 
					
					var lastVol = if (lastVal.notNil) { \amp.asSpec.map(lastVal) }; 
					try { 
				//		"/// *** softVol_: ".post;
						pxmixers[scene].pxMons[i + pxOffsets[scene]].proxy
					lastVals[key] =  mappedVal;
		