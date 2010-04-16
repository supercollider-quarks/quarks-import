
+ ArrayedCollection {
	gnuplot { GNUPlot.plot( this ) }
}

+ Env {
	gnuplot { GNUPlot.plotenv( this ) }
}

+ AbstractFunction{
	
	gnuplot{ arg a=0,b=1,n = 500;		
		var spec1 = [a,b,\linear].asSpec;
		var spec2 = [0,n-1].asSpec;
		GNUPlot.plot(Array.fill(n,{Ê|i| this.(spec1.map(spec2.unmap(i))) }));
	}
	
	surf3{ |rect,grid = 20,hidden3d = true, pm3d = true|
	
		var xyz, gnuplot, grain, specX, specY;
		
		rect = rect ? Rect(0,0,1,1);
		specX = [rect.left,rect.left+rect.width,\linear].asSpec;
		specY = [rect.top,rect.top+rect.width,\linear].asSpec;
			
		gnuplot = GNUPlot.new;
		grain = 1/grid;  
		xyz =  (0,grain .. 1).collect{|x|  (0,grain .. 1).collect{|y| 
			 [specX.map(x),specY.map(y),this.value(specX.map(x),specY.map(y))]
		 } };
		gnuplot.surf3(xyz, "a Function", hidden3d, pm3d);
		
		
	}	

}