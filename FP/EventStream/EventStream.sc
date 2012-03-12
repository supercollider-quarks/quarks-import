/*
   Functional Reactive Programming
   based on reactive-core
   http://www.reactive-web.co.cc/

   ////////////////////////////////////////////////////////////////////////////////////////////////////////
   Original license:
   https://github.com/nafg/reactive/blob/master/LICENSE.txt
   ////////////////////////////////////////////////////////////////////////////////////////////////////////
   Note, this is a draft and may be changed at any time.

   You may use this software under the following conditions:
   A. You must not use it in any way that encourages transgression of the Seven Noahide Laws (as defined by traditional Judaism; http://en.wikipedia.org/wiki/Seven_Laws_of_Noah is pretty good). They are:
     1. Theft
     2. Murder
     3. Adultery
     4. Polytheism
     5. Cruelty to animals (eating a limb of a living animal)
     6. Cursing G-d
   And they require a fair judicial system.

   B. You must not use it in any way that transgresses the Apache Software License.
   ////////////////////////////////////////////////////////////////////////////////////////////////////////

   translated to SuperCollider by Miguel Negr�o.
*/

EventStream{ }

EventSource : EventStream {
    var <listeners;

    new{
        ^super.new.initEventSource
    }

    initEventSource {
        listeners = [];
    }

    addListener { |f| listeners = listeners ++ [f] }

    removeListener { |f| listeners.remove(f) }
    
    removeAllListeners { listeners = [] }

	proc { |initialState, f|
		^ChildEventSource( initialState ).initChildEventSource(this,f)
	}

    collect { |f|
        ^CollectedES(this,f)
    }

    select { |f|
        ^SelectedES(this, f)
    }

    fold { |initial, f|
        ^FoldedES(this, initial, f);
    }

    flatCollect { |f, initialState|
        ^FlatCollectedES( this, f, initialState)
    }

    | { |otherES|
        ^MergedES( this, otherES )
    }

    merge { |otherES|
    	^MergedES( this, otherES )
    }

    takeWhile { |f|
        ^TakeWhileES( this, f)
    }

    do { |f|
        this.addListener(f);
        ^Unit
    }

    fire { |event|
	    //("running fire "++event++" "++this.hash).postln;
        listeners.copy.do( _.value(event) )
    }

	//returns the corresponding signal
    hold { |initialValue|
    	^HoldFPSignal(this, initialValue)
    }

    remove { }
}

HoldFPSignal : FPSignal {
	var <now;
	var <change;
	var <listener;

	*new { |eventStream, initialValue|
		^super.new.init(eventStream, initialValue)
	}

	init { |eventStream, initialValue|
		change = eventStream;
		now = initialValue;
		listener = { |v| now = v};
		eventStream.addListener( listener )
	}

}

ChildEventSource : EventSource {
    var <state;
    var <parent;
    var <listenerFunc;
    var <handler; //: (T, S) => S

    *new{ |initialState|
        ^super.new.initState( initialState )
    }

    initState{ |initialState|
        state = initialState;
    }

    initChildEventSource { |p,h, initialFunc|
        parent = p;
        handler = h;
        listenerFunc = { |value| state = handler.value(value, state) };
        parent.addListener(listenerFunc)
    }

    remove {
        listeners.do( _.removeListener( listenerFunc ) )
    }
}

CollectedES : ChildEventSource {

    *new { |parent, f|
        ^super.new.init(parent, f)
    }

    init { |parent, f|
        this.initChildEventSource(parent, { |event|
            this.fire( f.(event) );
        })
    }
}

SelectedES : ChildEventSource {

    *new { |parent, f|
        ^super.new.init(parent, f)
    }

    init { |parent, f|
        this.initChildEventSource(parent, { |event|
             if( f.(event) ) {
                 this.fire( event )
             }
        })
    }
}

FoldedES : ChildEventSource {

    *new { |parent, initial, f|
        ^super.new(initial).init(parent, f)
    }

    init { |parent, f|
        this.initChildEventSource(parent, { |event, state|
             var next = f.(state, event);
             this.fire( next );
             next
        })
    }
}

FlatCollectedES : ChildEventSource {

    *new { |parent, f, initial|
        ^super.new(initial).init(parent, f)
    }

    init { |parent, f|
        var thunk = { |x|
         	//"firing the FlatCollectedES".postln;
         	this.fire(x) 
        };
        state !? _.addListener(thunk);
        this.initChildEventSource(parent, { |event, lastES|
             var nextES;
             lastES !? _.removeListener( thunk );
             nextES = f.(event);
             nextES !? _.addListener( thunk );
             nextES
        })
    }
}

TakeWhileES : ChildEventSource {

    *new { |parent, f|
        ^super.new.init(parent, f)
    }

    init { |parent, f|
        this.initChildEventSource(parent, { |event|
            if( f.(event) ) {
                this.fire( event )
            } {
                parent.removeListener(listenerFunc);
            }
        })
    }
}

MergedES : EventSource {

    *new { |parent, parent2|
        ^super.new.init(parent, parent2)
    }

    init { |parent1, parent2|
        var thunk = this.fire(_);
        parent1.addListener( thunk );
        parent2.addListener( thunk );
    }
}

TimerES : EventSource {
    var <routine;

    *new{ |delta, maxTime|
        ^super.new.init(delta, maxTime)
    }

    init { |delta, maxTime|

        var t = 0;
        routine = fork{
            100.do{
                delta.wait;
                if( t > maxTime) {
                    routine.stop;
                };
                t = t + delta;
                this.fire(t);
            }
        }

    }
}

EventPlayerES : EventSource {
    var <routine;

    *new{ |array|
        ^super.new.init(array)
    }

    init { |array|

        var t = 0;
        routine = fork{
        	array.do{ |tx|
	        	( tx[0] -t ).wait;
	        	this.fire( tx[1] );
	        	t = tx[0];
        	}
        }
    }
}