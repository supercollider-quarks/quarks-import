FunctionDict : IdentityDictionary {

	valueAll{ |...args|
		this.do{ |it| it.value( *args ) };
	}

	valueAt{ | ...args | // should be a more elegant way to pass this in?
		this.at( args[0] ).value( *args.copyToEnd( 1 ) );
	}
}