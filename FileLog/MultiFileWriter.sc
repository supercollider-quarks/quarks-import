/*

	Copyright 2009 (c) - Marije Baalman (nescivi)
	Part of the FileLog quark

	Released under the GNU/GPL license, version 2 or higher

*/

// writes multiple files to disk,
// keeps an index file of the files written
// and zips individual files or bundles together at request
//   to limit file size.
// uses gzip and tar for zipping and bundling

MultiFileWriter{

	classvar <>maxFileLength = 30;

	var <pathDir;
	var <fileName;
	var <extension;

	var <>tarBundle = true;
	var <>zipSingle = true;

	//	var <indexFile;

	var <curFile;
	var <curfn;
	var <index = 0;

	var <>fileClass;
	var <>stringMethod = \asString;


	*new{ |fn|
		^super.new.init(fn).fileClass_(TabFileWriter);
	}

	init{ |fn|
		var path = PathName(fn);
		fileName = path.fileNameWithoutExtension;
		extension = path.extension;
		pathDir = PathName(path.asAbsolutePath).pathOnly;
		if ( PathName(pathDir).files.size > 0 ){
			("mkdir"+pathDir+/+fileName).systemCmd;
			pathDir = pathDir +/+ fileName;
		};
		pathDir = pathDir +/+ "/";
	}

	createPosixFn{ |fn,app,ext|
		var newfn = fn ++ app;
		if ( newfn.size < maxFileLength ){
			^(newfn++ext);
		}{
			newfn = ( fn ++ app ).keep( maxFileLength - app.size );
			newfn = newfn ++ app ++ ext;
			^newfn;
		};
	}

	open{
		var indexFile;
		var indexfn = pathDir +/+ fileName ++ "_index";
		curfn = PathName(pathDir).pathOnly +/+ this.createPosixFn( fileName, "_" ++ index ++ "_" ++ Date.localtime.stamp, "." ++ extension );
		indexFile = TabFileWriter.new( indexfn, "a", true );
		//	indexFile.dump;
		//	indexfn.postcs;
		curFile = fileClass.new( curfn, "w" );
		//	curFile.dump;
		//	curfn.postcs;
		if ( fileClass.isKindOf( FileWriter ) ){
			curFile.stringMethod = stringMethod;
		};
		if ( zipSingle ){
			indexFile.writeLine( [ index, PathName(curfn).fileName ++ ".gz" ]);
		}{
			indexFile.writeLine( [ index, PathName(curfn).fileName ]);
		};
		indexFile.close;
	}

	close{
		var newf;
		if ( curFile.notNil ){
		if ( curFile.isOpen ){
			newf= pathDir +/+ PathName(curfn).fileName;
			curFile.close;
			index = index + 1;
			fork{
				//		Task({
				if ( zipSingle ){
					( 
						"mv" + curfn + pathDir ++ ";" +
						"gzip" + newf ++ ";"
						// + "rm" + newf // file is removed automagically
					).systemCmd;
					newf = newf ++ ".gz";
				}{
					(
						"mv" + curfn + pathDir ++ ";"
					).systemCmd;
				};
				//	1.0.wait;
				this.createTarBundle( newf );
			};
		};
		}
		//		}).play(AppClock);
	}

	createTarBundle{ |newf|
		var tarName;
		//	newf.postln;
		if ( tarBundle ){
			tarName = PathName( pathDir ).fullPath ++ ".tar";
			if ( File.exists( tarName ) ){
				//	"adding to tar".postln;
				(
					"cd" + PathName(pathDir).pathOnly ++ ";" 
					+ "tar -uf"
					+ tarName + 
					PathName(pathDir).fileName ++ ";"
					+ "rm" + newf
				).unixCmd;
			}{
				//	"new tar".postln;
				(
					"cd" + PathName(pathDir).pathOnly ++ ";" 
					+ "tar -cf"
					+ tarName 
					+ PathName(pathDir).fileName ++ ";"
					+ "rm" + newf
				).unixCmd;
			};
		};
	}

	doesNotUnderstand { arg selector ... args;
		^curFile.performList( selector, args );
	}

}