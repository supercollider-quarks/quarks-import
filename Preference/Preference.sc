
Preference {

	classvar <>fileNames, <>startupFilePath, <>repositoryDirPath, <current;
	classvar <>openFileAtStartup = true, <>examplesFolder;
	
	*initClass {	
		
		
		StartUp.add {
		
			if([\osx, \linux].includes(thisProcess.platform.name).not) {
				"sorry, preferences currently work only with OS X.".postln;
				^this
			};
			
			// for now, user dir only
			startupFilePath = startupFilePath ?? { thisProcess.platform.startupFiles.last };			repositoryDirPath = repositoryDirPath ?? {
				(startupFilePath.dirname +/+ "startupfiles").escapeChar($ );
			}; 
			examplesFolder = examplesFolder ?? { 
				this.filenameSymbol.asString.dirname +/+ "startup_examples/";
			}; 
			startupFilePath = startupFilePath.escapeChar($ );
			if(pathMatch(startupFilePath).notEmpty and: { isSymLink(startupFilePath).not }) {
				"************************************************************************\n"
				"Preference: in order to use preference switching, move your startup file "
				"to the startupfiles folder first, and recompile.\n"
				"Preference.openRepository;\n"
				"************************************************************************".postln
			};
			
			if(pathMatch(repositoryDirPath).isEmpty) {
				systemCmd(postln("mkdir -p" + repositoryDirPath));
			};
			
			if(pathMatch(repositoryDirPath +/+ "*").isEmpty) {
				this.copyExamplesFromQuark;
			};
			
			this.initFilePaths;
			this.findCurrentStartup;
			this.initMenu;
			if(openFileAtStartup) { this.openStartupFile };
			
		
		};
	}
	
		
	*initFilePaths {
		var filePaths = pathMatch(repositoryDirPath +/+ "*startup*");
		fileNames = ();
		filePaths.do { |path|
			var key = path.basename.asSymbol;
			fileNames[key] = path.escapeChar($ );
		}
	}
	
	*findCurrentStartup {
		var startupPointsTo, stat, index;
		if(pathMatch(startupFilePath).isEmpty) { current = \none; ^this };
		stat = unixCmdGetStdOut("stat -F" + startupFilePath);
		index = stat.find("->");
		if(index.notNil) {
			startupPointsTo = stat[index + 2 ..];
			if(startupPointsTo.last == Char.nl) {
				startupPointsTo = startupPointsTo.drop(-1)
			};
			current = startupPointsTo.basename.asSymbol;
		} {
			current = \default;
		};
		"Current startup file: %\n".postf(current);
	}
	
	*copyExamplesFromQuark {
			var filePaths = pathMatch(this.filenameSymbol.asString.dirname 
												+/+ "example_setups/*");
			filePaths.do { |path|
				path = path.escapeChar($ );
				// cp -n: copy, do not overwrite existing file
				systemCmd("cp -n % %".format(path, repositoryDirPath +/+ path.basename));
			};
	}
	
	
	*setToDefault {
		// look for a simple startup.rtf or similar
		fileNames.keysValuesDo { |name, val|
			var str = name.asString;
			if(str.splitext.first == "startup") {
				this.set(name);
				this.initMenu;
				^this
			};
		};
		this.reset;
	}
	
	*set { |which|
			var path = fileNames.at(which.asSymbol);
			if(pathMatch(startupFilePath).isEmpty or: { isSymLink(startupFilePath) }) {
				if(path.notNil) {
					systemCmd("ln -s -F " ++ path + startupFilePath);
					this.openStartupFile(path);
					current = which;
					this.initMenu;
				} {
				 	"Preference: no file of this name found: %\n".postf(which) 
				};
			};
	}

	
	*reset {
		if(pathMatch(startupFilePath).notEmpty) { 
				if(isSymLink(startupFilePath)) {
					systemCmd("rm" + startupFilePath);
					current = \none;
				} {
					"Preference: Please remove your startup file manually".postln;
				}
		};
		this.initMenu;
	}

		
	*openRepository {
		var str = "open";
		str = str + repositoryDirPath;
		systemCmd(str)
	}
	
	*openStartupFile { |path|
		var name;
		path = path ?? startupFilePath;
		name = startupFilePath.basename.asSymbol;
		Document.allDocuments.do { |doc| 
			if(doc.title == name) {
				doc.front;
				^this
			};
		};
		systemCmd("open -a SuperCollider" + path);
	}
	
	*initMenu {
		
		Platform.case(\osx, {
			try { // make sure that nothing can block other method calls
				var names = fileNames.keys.asArray.sort;
				var parent = CocoaMenuItem.default.findByName("startup");
				var isDefault;
				
				parent.remove;
				
				isDefault = (current == \default) or: { current == \default_startup };
				
				CocoaMenuItem.add(["startup", "default"], {
					this.setToDefault;
				}).enabled_(isDefault.not);
				
				CocoaMenuItem.add(["startup", "none"], { 
					this.reset; 
					this.initMenu;
				}).enabled_(current != \none);
				
				parent = CocoaMenuItem.default.findByName("startup");
				
				SCMenuSeparator(parent, 2);
	
				
				names.do { |name|
					var menuName = name.asString;
					var item = CocoaMenuItem.add(["startup", menuName], { this.set(name) });
					item.enabled = (current != name);
				};
				
				SCMenuSeparator(parent, names.size + 3);
				
				CocoaMenuItem.add(["startup", "Open repository"], { 
					this.openRepository; 
				});
				
				CocoaMenuItem.add(["startup", "Open quarks window"], { 
					Quarks.gui;
				});
				
				CocoaMenuItem.add(["startup", "init", "Copy examples from quark"], { 
					this.copyExamplesFromQuark;
					this.initFilePaths; 
					this.initMenu;
				});
				
				CocoaMenuItem.add(["startup", "init", "Refresh this menu"], { 
					this.initFilePaths; 
					this.initMenu;
				});
				
				CocoaMenuItem.add(["startup", "init", "Reset post window"], {
					var str = Document.listener.text;
					Document.listener.close;
					Document.new(" post", str, true);
					
				});
			
			};
			
		})
	}
	

}

+ String {
	isSymLink { 
		^unixCmdGetStdOut("ls -la" + this).at(0) == $l 
	}
}

