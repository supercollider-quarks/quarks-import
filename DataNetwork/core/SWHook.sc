SWHookSet {

	var collection;

	*new{
		^super.new.init;
	}

	init{
		collection = IdentityDictionary.new;
	}

	add{ |type,id,action|
		//	"adding hook to HookSet".postln;
		collection.put( (type++id).asSymbol, SWHook.new( type,id,action ) );
	}

	perform{ |type,id|
		var myhook,mykey;
		mykey = (type++id).asSymbol;
		myhook = collection.at( mykey );
		if ( myhook.notNil ){
			myhook.perform;
			collection.removeAt( mykey ); // remove the hook after executing it
		}{
			("no hooks for" + type + id ).postln;
		};
	}

}

SWHook {

	var <>type, <>id, <>action;

	*new{ |...args|
		^super.newCopyArgs( *args );
	}

	perform{
		"performing hook action".value;
		this.action.value;
	}

}