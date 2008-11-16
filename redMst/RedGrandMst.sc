RedGrandMst {
	classvar <song, <lastSong, <songDict;
	*initClass {
		songDict= ();
		lastSong= 'default';
	}
	*song_ {|name|
		name= name.asSymbol;
		if(lastSong!=name, {
			song= name;
			songDict.put(lastSong, RedMst.getState);	//store
			if(songDict[song].notNil, {
				RedMst.setState(songDict[song]);		//recall
			});
			lastSong= song;
		});
	}
}
