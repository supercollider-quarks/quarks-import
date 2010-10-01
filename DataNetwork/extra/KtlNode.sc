MIDIKtlNode {
	var <>ktl, <>network, <>ids, <>name;

	var <cbnodes;

	*new{ |ktl,network,ids,name|
		^super.newCopyArgs( ktl, network, ids, name ).init;
	}

	init{
		cbnodes = IdentityDictionary.new;

		if ( ktl.hasScenes ){
			ktl.ctlNames.sortedKeysValuesDo{ |ky,dict,i|
				network.addExpected( ids[i], ( name ++ "_" ++ ky).asSymbol, dict.size );
				cbnodes.put( ky, SWCombineNode.new( ids[i], network, dict.size ) );
				dict.sortedKeysValuesDo{ |k2,v,j|
					network.add( name ++ "_" ++ k2, [ ids[i], j ] );
					ktl.mapCCS( ky, k2, { |val| cbnodes[ ky ].set( j, [ val/127 ] ); });
				};
			};
		}{
			var dict = ktl.ctlNames;
			if ( ids.isKindOf( Array ) ){
				ids = ids[0];
			};
			network.addExpected( ids, name.asSymbol, ktl.dict.size );
			cbnodes = SWCombineNode.new( ids, network, dict.size );
			dict.sortedKeysValuesDo{ |k2,v,j|
				network.add( name ++ "_" ++ k2, [ ids, j ] );
				ktl.mapCCS( k2, { |val| cbnodes.set( j, [ val/127 ] ); });
			};
		}
	}
}

HIDNode {
	var <>hid, <>network, <>id, <>name;
	var <>mode;

	var <cbnode;

	*new{ |hid,network,id,name,mode=\specOnly|
		^super.newCopyArgs( hid, network, id, name, mode ).init;
	}

	init{
		var b;
		network.addExpected( id, name );
		if ( mode == \specOnly ){
			cbnode = SWCombineNode.new(id,network,hid.spec.map.size);
			hid.spec.map.sortedKeysValuesDo{ |key,it,i| 
				network.add( (name ++ "_" ++ key).asSymbol, [id, i] );
				hid[ key ].action_({ |slot| cbnode.set( i, [ slot.value ] ) });
			};
		}{
			b = hid.slots.asSortedArray.collect{ |it| it[1].asSortedArray.collect{ |jt| [it[0],jt[0]] } }.flatten;
			cbnode = SWCombineNode.new(id,network,b.size);
			b.do{ |it,i|
				hid.slots[ it[0] ][ it[1] ].action_{ |slot| cbnode.set( i, [slot.value])};
			};
			hid.spec.map.keysValuesDo{ |key,it,i| 
				var id2 = b.selectIndex( { |jt| it == jt } ).first;
				if ( id2.notNil ){ b.put( id2, key )  };
			};
			b.do{ |it,i|
				network.add( (name++"_"++it).asSymbol, [id,i] );
			};
		}
	}
}