BCRKtl : MIDIKtl {
	classvar <>verbose = false;

	*getDefaults {

		var dict = Dictionary.new;

		8.do{ |i|

			//4 encoder groups
			4.do{ |j|
				// top knob push mode
				dict.put(( "tr" ++ ["A","B","C","D"][j] ++ (i+1) ).asSymbol,("0_"++(33+(8*j)+i)).asSymbol);
				// knobs (top row)
				dict.put(( "kn" ++ ["A","B","C","D"][j] ++ (i+1) ).asSymbol,("0_"++(1+(8*j)+i)).asSymbol);
			};

			// buttons 1st row
			dict.put(("btA"++(i+1)).asSymbol,("0_"++(65+i)).asSymbol);
			// buttons 2nd row
			dict.put(("btB"++(i+1)).asSymbol,("0_"++(73+i)).asSymbol);
			// knobs (lower 3 rows)
			dict.put(("knE"++(i+1)).asSymbol,("0_"++(81+i)).asSymbol);
			dict.put(("knF"++(i+1)).asSymbol,("0_"++(89+i)).asSymbol);
			dict.put(("knG"++(i+1)).asSymbol,("0_"++(97+i)).asSymbol);
		};

		// buttons (4 bottom right ones)
		dict.putAll((
			prA1: '0_105',
			prA2: '0_106',
			prB1: '0_107',
			prB2: '0_108'
		));

		^dict
	}

	*makeDefaults {
		defaults.put( this, this.getDefaults );
	}
}

BCRPagedKtl : MIDIPagedKtl {
	classvar <>verbose = false;

	*makeDefaults {
		// lookup for all scenes and ctlNames, \sl1, \kn1, \bu1, \bd1,
		defaults.put( this, BCRKtl.getDefaults );
	}

	init{
		super.init;

		//buttons are used to flip between scenes
		this.mapAll(\prB1,{  this.sendCtl(currentScene,\prB1,0); this.previousScene; });
		this.mapAll(\prB2,{ this.sendCtl(currentScene,\prB2,0); this.nextScene;});
	}

}