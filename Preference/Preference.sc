
Preference {

	classvar <>fileNames, <>startupFilePath, <>repositoryDirPath, <current;
	classvar <>openFileAtStartup = true;
	
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
			startupFilePath = startupFilePath.escapeChar($ );
			if(pathMatch(startupFilePath).notEmpty and: { isSymLink(startupFilePath).not }) {
				"************************************************************************"
				"Preference: in order to use preference switching, move your startup file "
				"to the startupfiles folder first, renaming it to 'XXX_startup'.\n"
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
		var filePaths = pathMatch(repositoryDirPath +/+ "*_startup*");
		fileNames = ();
		filePaths.do { |path|
			fileNames[path.basename.splitext.first.asSymbol] = path.escapeChar($ );
		};
	}
	
	*findCurrentStartup {
		var startupPointsTo, stat, index;
		if(pathMatch(startupFilePath).isEmpty) { current = \none; ^this };
		stat = unixCmdGetStdOut("stat -F" + startupFilePath);
		index = stat.find("->");
		if(index.notNil) {
			startupPointsTo = stat[index + 2 ..];
			current = startupPointsTo.basename.splitext.first.asSymbol;
		} {
			current = \default;
		};
	}
	
	*copyExamplesFromQuark {
			var filePaths = pathMatch(this.filenameSymbol.asString.dirname 
												+/+ "example_setups/*");
			filePaths.do { |path|
				path = path.escapeChar($ );
				systemCmd("cp % %".format(path, repositoryDirPath +/+ path.basename));
			};
	}
	
	*initMenu {
		
		Platform.case(\osx, {
			try { // make sure that nothing can block other method calls
				var names = fileNames.keys.asArray.sort;
				var parent = CocoaMenuItem.default.findByName("startup");
				
				parent.remove;
				
				CocoaMenuItem.add(["startup", "default"], {
					this.setToDefault;
				}).enabled_(current != \default and: { current != \default_startup });
				
				CocoaMenuItem.add(["startup", "none"], { 
					this.reset; 
					this.initMenu;
				}).enabled_(current != \none);
				
				parent = CocoaMenuItem.default.findByName("startup");
				
				SCMenuSeparator(parent, 2);
	
				
				names.do { |name|
					var menuName = name.asString.replace("_", " ");
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
	
	*setToDefault {
		if(fileNames.at(\default_startup).notNil) {
			this.set(\default_startup);
		} {
			if(fileNames.at(\backup_of_original_startup).notNil) {
				this.set(\backup_of_original_startup);
			} {
				"Preferences: no default startup file exists.".postln;
			}
		}
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
		name = startupFilePath.basename.splitext.first.asSymbol;
		Document.allDocuments.do { |doc| 
			if(doc.title == name) {
				doc.front;
				^this
			};
		};
		systemCmd("open" + path);
	}
	

}

+ String {
	isSymLink { 
		^unixCmdGetStdOut("ls -la" + this).at(0) == $l 
	}
}

