
TestInstr : UnitTest {
	
	setUp {
		Instr.clearAll
	}
	test_leaves {
		var l;
		l = Instr.leaves;
		this.assertEquals( l.size, 0,"should initially be nothing in leaves");
	}
	
	test_clearAll {
		var l;
		Instr("test_clearAll",{SinOsc.ar});
		Instr.clearAll;
		l = Instr.leaves;
		this.assertEquals( l.size, 0, "should be nothing in leaves after clearAll");
	}
		
	test_putat_dotnotation {
		Instr("TestInstr.test_putat",{ SinOsc.ar });
		
		this.assert( Instr.at("TestInstr.test_putat").notNil, "dot notation should retrieve instr");
		this.assert( Instr.at( ["TestInstr","test_putat"]).notNil,"array notation should retrieve instr");

		this.assert( Instr("TestInstr.test_putat").notNil, "Instr(name) notation should retrieve instr");

	}
	test_putat_array {
		Instr(["TestInstr","test_putat"],{ SinOsc.ar });
		
		this.assert( Instr.at("TestInstr.test_putat").notNil, "dot notation should retrieve instr");
		this.assert( Instr.at( ["TestInstr","test_putat"]).notNil,"array notation should retrieve instr");

		this.assert( Instr("TestInstr.test_putat").notNil, "Instr(name) notation should retrieve instr");
	}
	test_put_at_symbol {
		Instr(\test_put_at_symbol,{SinOsc.ar});
		this.assert( Instr.at(\test_put_at_symbol).notNil, "symbol should retrieve instr");
		this.assert( Instr.at("test_put_at_symbol").notNil, "dot notation should retrieve instr");
		this.assert( Instr.at( ["test_put_at_symbol"]).notNil,"array notation should retrieve instr");
	
		this.assert( Instr(\test_put_at_symbol).notNil, "dot notation should retrieve instr");
	}
	test_defArgAt {
		var d,i;
		Instr(\test_defArgAt,{ arg freq,amp;
				SinOsc.ar(freq,mul: amp)
		});
		d = Instr(\test_defArgAt).defArgAt(0);
		
		// should be nil
		this.assertEquals( d, nil, "no def arg supplied, should be nil");
		
		i = Instr(\test_defArgAt).initAt(0);
		this.assertEquals( i, \freq.asSpec.default,"no def arg supplied, init should be the spec default");
	}
		
	test_asSynthDef {
		var sd;
		Instr("TestInstr.asSynthDef",{ arg freq,amp;
					SinOsc.ar(freq,mul: amp)
		});
		sd = Instr("TestInstr.asSynthDef").asSynthDef;
		this.assert( sd.isKindOf(InstrSynthDef),"should produce an InstrSynthDef succesfully");
	}
}




TestInterfaceDef : UnitTest {
	var f;
	setUp {
		InterfaceDef.clearAll;
		f = {
			// an environment is in place here
			~freq = KrNumberEditor(400,[100,1200,\exp]);
			~syncFreq = KrNumberEditor(800,[100,12000,\exp]); 
			~amp = KrNumberEditor(0.1,\amp); 

			Patch({ arg freq,syncFreq,amp=0.3;
				SyncSaw.ar(syncFreq,freq) * amp
			},[
				~freq,
				~syncFreq,
				~amp
			])

		};
		
	}
	test_clearAll {
		var l;
		InterfaceDef("test_clearAll",f);
		InterfaceDef.clearAll;
		l = InterfaceDef.leaves;
		this.assertEquals( l.size, 0, "should be nothing in leaves after clearAll");
	}
	
	test_putat_dotnotation {
		InterfaceDef("TestInterfaceDef.test_putat",f);
		
		this.assert( InterfaceDef.at("TestInterfaceDef.test_putat").notNil, "dot notation should retrieve instr");
		this.assert( InterfaceDef.at( ["TestInterfaceDef","test_putat"]).notNil,"array notation should retrieve instr");

		this.assert( InterfaceDef("TestInterfaceDef.test_putat").notNil, "InterfaceDef(name) notation should retrieve instr");

	}
	test_putat_array {
		InterfaceDef(["TestInterfaceDef","test_putat"],f);
		
		this.assert( InterfaceDef.at("TestInterfaceDef.test_putat").notNil, "dot notation should retrieve instr");
		this.assert( InterfaceDef.at( ["TestInterfaceDef","test_putat"]).notNil,"array notation should retrieve instr");

		this.assert( InterfaceDef("TestInterfaceDef.test_putat").notNil, "InterfaceDef(name) notation should retrieve instr");
	}
	test_put_at_symbol {
		InterfaceDef(\id_test_put_at_symbol,f);
		this.assert( InterfaceDef.at(\id_test_put_at_symbol).notNil, "symbol should retrieve instr");
		this.assert( InterfaceDef.at("id_test_put_at_symbol").notNil, "dot notation should retrieve instr");
		this.assert( InterfaceDef.at( ["id_test_put_at_symbol"]).notNil,"array notation should retrieve instr");
	
		this.assert( InterfaceDef(\id_test_put_at_symbol).notNil, "dot notation should retrieve instr");
	}
	test_leaves {
		var leaves;
		InterfaceDef(\id_test_leaves,f);
		// even though we put an Instr too
		Instr(\id_test_leaves_instr,{ SinOsc.ar });
		
		leaves = InterfaceDef.leaves;
		this.assertEquals( leaves.size , 1 , "should be one leaf in the library");
	}

}

