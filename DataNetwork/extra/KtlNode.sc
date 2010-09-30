MIDIKtlNode {
	var <>ktl, <>network, <>ids, <>name;

	var <cbnodes;

	*new{ |ktl,network,ids,name|
		^super.newCopyArgs( ktl, network, ids, name ).init;
	}

	init{
		cbnodes = IdentityDictionary.new;
		ktl.ctlNames.sortedKeysValuesDo{ |ky,dict,i|
			network.addExpected( ids[i], ( name ++ "_" ++ ky).asSymbol, dict.size );
			cbnodes.put( ky, SWCombineNode.new( ids[i], network, dict.size ) );
			dict.sortedKeysValuesDo{ |k2,v,j|
				network.add( name ++ "_" ++ k2, [ ids[i], j ] );
				ktl.mapCCS( ky, k2, { |val| cbnodes[ ky ].set( j, [ val/127 ] ); });
			};
		};
	}
}