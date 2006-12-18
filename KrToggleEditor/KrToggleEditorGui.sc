KrToggleEditorGui : EditorGui {
	var cb;
	guiBody { arg layout;
		var bg;
		bg = layout.background;
		cb = SCButton.new( layout,Rect(0,0,14,14));
		cb.states = [[" ",bg,bg],["X",Color.black,bg]];
		cb.font = Font("Helvetica",9);
		cb.setProperty(\value,model.value);
		cb.action = { model.activeValue_(cb.value).changed(this) };
		if(consumeKeyDowns,{ cb.keyDownAction = {nil}; });
	}
	update { arg changed,changer;
		if(changer !== this,{
			cb.setProperty(\value,model.value);
		});
	}
}


