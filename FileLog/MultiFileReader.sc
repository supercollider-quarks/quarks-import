/*

	Copyright 2009 (c) - Marije Baalman (nescivi)
	Part of the FileLog quark

	Released under the GNU/GPL license, version 2 or higher

*/

// writes multiple files from disk, as created by MultiFileWriter,
// uses unzip and tar for unzipping and unbundling

MultiFileReader{

	var <indexfn;
	var <indexFile;

	var <>fileClass;

	var <pathDir;
	var <fileName;
	//	var <extension;

	var <tarBundle;
	var <zipSingle;

	var <curFile;
	
	*new{ |fn,fc|
		^super.new.init(fn).fileClass_( fc ? TabFileReader );
	}

	init{ |fn|
		var path = PathName(fn);
		fileName = path.fileNameWithoutExtension;
		tarBundle = (path.extension == "tar");
		pathDir = PathName(path.asAbsolutePath).pathOnly;
		//		pathDir = pathDir ;
		if ( tarBundle ){
			indexfn = fileName +/+ fileName ++ "_index";
			("tar -f" + fn + "-x" + indexfn ).systemCmd;
			this.openIndexFile;
			//.unixCmdThen( {this.openIndexFile} );
		}{
			indexfn = pathDir +/+ fileName ++ "_index";
			this.openIndexFile;
		};
		
	}

	openIndexFile{
		var line;
		indexFile = TabFilePlayer.new( indexfn );

		// read the first line:
		line = indexFile.next;
		indexFile.reset; // reset the file again.
		zipSingle = (PathName(line.last).extension == "gz");
		//	extension = PathName(line.last).fileNameWithoutExtension
	}

	openFile{ |ind|
		var line, path;
		line = indexFile.readAtLine( ind );

		if ( line.isNil ){
			^nil
		};

		path = line.last;

		if ( tarBundle ){
			("tar -f" + pathDir +/+ fileName ++ ".tar" + "-x" + fileName +/+ path ).systemCmd;
		};
		if ( zipSingle ){
			("gzip -d" + fileName +/+ path ).systemCmd;
			path = PathName( line.last ).fileNameWithoutExtension;
		};
		^curFile = fileClass.new( fileName +/+ path );
	}

	closeFile{
		if ( curFile.notNil ){
			curFile.close;
		};
	}

	close{
		indexFile.close;
		this.closeFile;
	}

}