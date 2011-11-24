+ Preference {
/*	
	*initMenu {
		
		Platform.case(\osx, {
			try { // make sure that nothing can block other method calls
				var names = fileNames.keys.asArray.sort;
				var parent = CocoaMenuItem.default.findByName("startup");
				var isDefault;
				
				if (parent.notNil) { parent.children.copy(_.remove); };
				
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

				CocoaMenuItem.add(["startup", "Open current startup"], { 
					this.openStartupFile; 
				}).enabled_(current != \none);		

				CocoaMenuItem.add(["startup", "Open startup folder"], { 
					this.openRepository; 
				});

				SCMenuSeparator(parent, 5);

				names.do { |name|
					var menuName = name.asString;
					var item = CocoaMenuItem.add(["startup", menuName], { this.set(name) });
					item.enabled = (current != name);
				};
				
				SCMenuSeparator(parent, names.size + 6);
								
				CocoaMenuItem.add(["startup", "Copy examples from quark"], { 
					this.copyExamplesFromQuark;
					this.initFilePaths; 
					this.initMenu;
				});
				
				CocoaMenuItem.add(["startup", "Refresh this menu"], { 
					this.initFilePaths; 
					this.initMenu;
				});				
			};
		})
	}
*/
}
