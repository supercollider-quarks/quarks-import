BusMonitor{
	var <bus;
	var <gnuplot;
	var <data;
	var <>dt = 0.05;
	var <>hisSize = 1000;
	
	*new{ |bus,hisSize|
		^super.new.init( bus,hisSize );
	}

	init{ |b,hs|
		bus = b;
		hisSize = hs ? hisSize;
		data = Array.fill( bus.numChannels, 0 );
		gnuplot= GNUPlot.new;
		this.initMonitor;
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

	reset{
		gnuplot.monitorReset;
	}

	initMonitor{
		gnuplot.monitor( { 
			bus.getn( bus.numChannels, { |v| v.do{ |it,i| data[i] = it; } } );
			data.collect{ |it| it.value } 
		}, dt, hisSize, bus.numChannels, skip: 20 ); 
	}

	cleanUp{
		gnuplot.stop;
	}
}

BusHistoMonitor : BusMonitor{

	var <>hisSize = 500;

	initMonitor{
		gnuplot.monitorHisto( { 
			bus.getn( bus.numChannels, { |v| v.do{ |it,i| data[i] = it; } } );
			data.collect{ |it| it.value } 
		}, dt, hisSize, bus.numChannels, skip: 20 ); 
	}


	setHistoRange{ |min,max|
		gnuplot.histoMin = min;
		gnuplot.histoMax = max;
	}

	setHistoStep{ |step|
		gnuplot.histoStep = step;
	}
	
}