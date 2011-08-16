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

	removeAt{ |id,type=\newnode|
		var mykey;
		mykey = (type++id).asSymbol;
		collection.removeAt( mykey );
	}

	perform{ |type,id, args|
		var myhook,mykey;
		mykey = (type++id).asSymbol;
		myhook = collection.at( mykey );
		if ( myhook.notNil ){
			myhook.perform( *args );
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

	perform{ |...args|
		("performing hook action" + type + id).postln;
		this.action.value( *args );
	}

}