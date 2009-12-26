/*
Problem: font setting does not really work yet on OS X. Seems to work with Post Window though.
*/


HelpDoc {
	
	var <>name, <>segments;
	
	*new { |name, segments|
		^super.newCopyArgs(name, segments);
	}
	
	makeGUI {
		// ...
		var view = Window(name ? "Untitled 01");
		// ...
		segments.do { |seg| seg.addToGUI(view) };
	}
	
	asDict {
		^(category: \helpdoc, name: name, segments: segments.collect(_.asDict))
	}
	
	// replace these with platform specific settings
	
	makeDoc {
		var doc = Document(name ? "Untitled 01");
		segments.do { |seg| seg.addToDoc(doc) };
	}
	
	// todo: write as XML, see XML Quark
	// & write as HTML
	
	*bigFont {
		^this.normalFont.size_(18)
	}
	*boldFont {
		^this.normalFont.boldVariant
	}
	*normalFont {
		^Font("Helvetica", 12);
	}
	*codeFont {
		^Font("Monaco", 9);
	}

}



HelpDocNode {

	// editing gui
	
	addToGUI {
		^this.subclassResponsibility(thisMethod)
	}
	
	addTextField { |parent, property, size = 10|
		var oldVal = this.perform(property);
		// determine optimal size (todo)
		^TextView(parent).string_(oldVal ? "add % here".format(property)).action_({ |v|
			this.perform(property.asSetter, v.string)
		});
	}

	// rendering
	
	
	writeAsPlist { |path|
		this.asDict.writeAsPlist(path)
	}
	
	addToDoc { |doc|
		^this.subclassResponsibility(thisMethod)
	}
	
	asDict {
		var res = ();
		this.properties.do { |property| res.put(property, this.perform(property)) };
		^res
	}
	
	addString { |doc, string, font|
		font = font ? HelpDoc.normalFont;
		// doc.setFont(font, doc.selectionStart); // still platform specific
		doc.selectedString = string;
		string.postln;
		doc.rangeText(doc.string.size, 0);
	}
	
		
}

HelpDocHeader : HelpDocNode {
	var <>name, <>headline, <>description;
	
	*new { |name, headline, description|
		^super.newCopyArgs(name, headline, description)
	}
	
	properties {
		^#[\name, \headline, \description]
	}
	
	// editing gui
	
	addToGUI { |parent|
		this.properties.do { |property|
			this.addTextField(parent, property);
		};
	}
	
	// rendering
	
	addToDoc { |doc|
		var superclassstr;
		this.addString(doc, name ++ "\t\t", HelpDoc.bigFont);
		this.addString(doc, headline ++ "\n\n", HelpDoc.boldFont);
		
		if(name.asSymbol.isClassName) {
			superclassstr = name.asSymbol.asClass.superclasses.collect(_.name).join(" : ");
			this.addString(doc, "Inherits from: % \n\n".format(superclassstr), HelpDoc.boldFont);
		};
		
		description !? {
			this.addString(doc, description ++ "\n\n", HelpDoc.normalFont);
		}
	}
	

}

HelpDocExample : HelpDocNode {
	var <>description, <>text;
	
	properties {
		^#[\description, \text]
	}
	
	// editing gui
	
	addToGUI { |parent|
		[\description, \text].do { |property|
			this.addTextField(parent, property);
		};
	}
	
	// rendering
	
	addToDoc { |doc|
		this.addString(doc, description ++ "\n\n", HelpDoc.codeFont);
		this.addString(doc, text ++ "\n\n", HelpDoc.codeFont);
	}

}


/*
HelpXMLStream : Post { // change later
	classvar <open;
	
	*openTag { |tag|
		this << "<" << tag << ">";
		open = open.add(tag);
	}
	
	*closeTag { |tag|
		var stackTag = open.pop;
		tag !? { 
			if(stackTag != tag) { Error("XML tags don't close properly.").throw }
		};
		this << "</" << stackTag << ">";
	}
}
*/


/*
asXML { |stream|
		stream = stream ? HelpXMLStream; // use a different one later
		stream.openTag("XML");
		segments.do { |seg|
			seg.asXML(stream);
		};
		stream.closeTag("XML");
	}
asXML { |stream|
		[\name, \headline, \description].do { |property|
			stream.openTag(property);
			stream << "\n" << this.perform(property).asCompileString << "\n";
			stream.closeTag;
		};
	}
	
*/
