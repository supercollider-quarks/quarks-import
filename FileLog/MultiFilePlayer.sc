/*

	Copyright 2009 (c) - Marije Baalman (nescivi)
	Part of the FileLog quark

	Released under the GNU/GPL license, version 2 or higher

*/

// writes multiple files from disk, as created by MultiFileWriter,
// uses unzip and tar for unzipping and unbundling

MultiFilePlayer : MultiFileReader{

	var <curid = -1;
	
	*new{ |fn,fc|
		^super.new(fn, fc ? TabFilePlayer );
	}

	fileClass_{ |fc|
		fc.postln;
		if ( fc.asClass.isKindOfClass( FilePlayer ) ){
			fileClass = fc;
		}{
			"fileClass must be a (subclass of) FilePlayer".warn;
		}
	}

	readAt{ |fileid, line|
		if ( curid != fileid ){
			this.closeFile;
			curid = -1;
			if ( this.openFile( fileid ).isNil ){
				("File with id"+fileid+"does not exist").warn;
				^nil;
			};
			curid = fileid;
		};
		^curFile.readAtLine( line );
	}

}