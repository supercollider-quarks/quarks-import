BusMonitor{
	var <bus;
	var <gnuplot;
	var <data;
	var <>dt = 0.05;
	
	*new{ |bus|
		^super.new.init( bus );
	}

	init{ |b|
		bus = b;
		data = Array.fill( bus.numChannels, 0 );
		gnuplot= GNUPlot.new;
		gnuplot.monitor( { 
			bus.getn( bus.numChannels, { |v| v.do{ |it,i| data[i] = it; } } );
			data.collect{ |it| it.value } 
		}, dt, 1000, bus.numChannels, skip: 20 ); 
		this.setRange;
	}

	setRange{ |min=0.0,max=1.00|
		gnuplot.setYrange( min,max );
	}

	start{
		gnuplot.startMonitor;
	}

	stop{
		gnuplot.stopMonitor;
	}

	cleanUp{
		gnuplot.stop;
	}
}