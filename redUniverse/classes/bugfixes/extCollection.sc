//bugfix for redUniverse

+Collection {
	species {^this.class}
}

//ugly but needed for JPen: bndl.add([ '/method', penID, \add ])...
+List {
	species {^Array}
}

//also ugly but needed for pauls f.def.argNames.collect...
+SymbolArray {
	species {^Array}
}
