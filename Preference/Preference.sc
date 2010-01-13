
Preference {

	classvar <>fileNames, <>startupFilePath, <>backupPath, <>repositoryDirPath;
	classvar <current, <>useQuarkSetups = true, <>openFileAtStartup = true;
	
	*initClass {	
		
		
		StartUp.add {
		
			var dirname;
			if([\osx].includes(thisProcess.platform.name).not) {
				"sorry, preferences currently work only with OS X.".postln;
				^this
			};
			
			dirname = Platform.userExtensionDir.dirname;
			
			// todo: adjust file paths to platforms
			startupFilePath = (dirname +/+ "startup.rtf").escapeChar($ );
			repositoryDirPath = (dirname +/+ "startupfiles").escapeChar($ );
			
			if(pathMatch(repositoryDirPath).isEmpty) {
				systemCmd(postln("mkdir -p" + repositoryDirPath));
				"\n\n".post;
				"**************************************************************\n".post;
				"Preference Quark: Created a Preference folder 'startupfiles'\n".post;
				"  To be sure, please backup your old startup file now, before recompiling .\n".post;
				"***************************************************************\n".post;
			} {
				this.backupOriginalStartupFile(false);
				this.initFilePaths;
				this.initMenu;
				if(openFileAtStartup) { this.openStartupFile };
			};
		
		};
	}
	
		
	*initFilePaths {
		var filePaths = pathMatch(repositoryDirPath +/+ "*_startup*");
		if(useQuarkSetups) {
			filePaths = filePaths ++ pathMatch(this.filenameSymbol.asString.dirname +/+ "setups/*");
		};
		fileNames = ();
		filePaths.do { |path|
			fileNames[path.basename.splitext.first.asSymbol] = path.escapeChar($ );
		};
		
	}
	
	*initMenu {
		Platform.case(\osx, {
		
			var names = fileNames.keys.asArray.sort;
			var parent = CocoaMenuItem.default.findByName("startup");
			
			parent.remove;
			
			CocoaMenuItem.add(["startup", "default"], {
				this.setToDefault;
			});
			
			CocoaMenuItem.add(["startup", "none"], { 
				this.reset; 
			});
			
			parent = CocoaMenuItem.default.findByName("startup");
			
			SCMenuSeparator(parent, 2);

			
			names.do { |name|
				var menuName = name.asString.replace("_", " ");
				var item = CocoaMenuItem.add(["startup", menuName], { this.set(name) });
				item.enabled = (current != name);
			};
			
			SCMenuSeparator(parent, names.size + 3);
			
			CocoaMenuItem.add(["startup", "open repository"], { 
				this.openRepository; 
			});
			
			CocoaMenuItem.add(["startup", "open quarks window"], { 
				Quarks.gui;
			});
			
			CocoaMenuItem.add(["startup", "refresh menu"], { 
				this.initFilePaths; 
				this.initMenu;
			});
			
		})
	}

	
	*backupOriginalStartupFile { |verbose = true|
		if(pathMatch(startupFilePath).notEmpty and: { isSymLink(startupFilePath).not }) {
			backupPath = repositoryDirPath +/+ "backup_of_original_startup.rtf";
			systemCmd("cp" + startupFilePath + backupPath);
			"Preference Quark: backed up original startup to: \n%".postf(repositoryDirPath);
		} {
			if(verbose) { "Preference Quark: nothing to backup".postln };
		}
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
			if(pathMatch(startupFilePath).notEmpty) { 
				if(isSymLink(startupFilePath)) {
					systemCmd("rm" + startupFilePath);
				};
			};
			if(path.notNil) {
				systemCmd("ln -s -F " ++ path + startupFilePath);
				this.openStartupFile(path);
				current = which;
				this.initMenu;
			} {
				 "no file of this name found: %\n".postf(which) 
			};
			
			
	}
	
	*reset {
		if(pathMatch(startupFilePath).notEmpty) { 
				if(isSymLink(startupFilePath)) {
					systemCmd("rm" + startupFilePath);
					current = nil;
				} {
					"Preference: Please backup your startup file manually".postln;
				}
		};
	}

		
	*openRepository {
		var str = "open";
		if(useQuarkSetups) { 
			str = str + (this.filenameSymbol.asString.dirname +/+ "setups/").escapeChar($ )
		};
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

