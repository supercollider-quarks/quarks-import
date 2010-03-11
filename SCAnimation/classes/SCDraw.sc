SCDraw {
	var score, firstFrame, lastFrame, frameRate, clear;
	
	*new { arg list, rate=25.0, sort=true;
		^super.new.init(list, rate, sort);
	}
	
	init { arg list, rate, sort=true;
		score = Array.new;
		firstFrame = 0;
		lastFrame = 0;
		frameRate = rate;
		clear = 1;
		list.do({ arg it, i;
				var arr, dict=Dictionary.new();
				arr = Array.new();
				arr = arr.add((it[1]*frameRate).asInteger);
				arr = arr.add((it[2]*frameRate).asInteger+arr[0]);
				arr = arr.add(it[0]);
				arr = arr.add(it[1]);
				arr = arr.add(it[2]);
				dict.put('start', it[1]);
				dict.put('duration', it[2]);
				lastFrame = lastFrame.max(arr[1]);
				if(it.size > 3, { ((it.size-3)/2).do({ arg j; dict.put(it[j*2+3], it[j*2+4]); }); });
				arr = arr.add(dict);
				score = score.add(arr);
			});
		score = score.add([ lastFrame+1, 0 ]);
		if(sort, { score.sort({arg a, b; a[0] < b[0]}); });

	}
			
	preview { arg width=500, height=500, color=Color.black;
		var win, view, frame=firstFrame, index = 0;
		var queue = [];
		win = Window.new("scdraw preview", Rect(200, 200, width, height), resizable: false).front;
		view = UserView(win, win.view.bounds).background_(color).clearOnRefresh_(false);
		win.view.background_(color);
		SystemClock.sched(0.0, {
			if(frame < lastFrame, {
				while { (index < (score.size-1)) && (frame == score[index][0]) }
				{ 	queue = queue.add([ 0, score[index][1] - score[index][0], index ]);
					index = index + 1; 
					};
				view.drawFunc = {
				var removeThese = Array.new();
				if(clear == 1, { Pen.fillColor = color; Pen.fillRect(win.view.bounds); }, { clear = 1; });
				queue.do({ arg it, i;
					if(it[0] == (it[1]-1), { removeThese = removeThese.add(i.asInteger); });
					if(score[it[2]][2] == \noRefresh, { clear = 0; },
						{ (score[it[2]][2]).value(it[0]/(it[1]-1), score[it[2]][5]); });
					it[0] = it[0] + 1;
					});
				removeThese.reverse.do({ arg it; queue.removeAt(it); });
				};
				frame = frame+1;
				{ view.refresh }.defer;
				frameRate.reciprocal;
				},
				{
				if(frame == lastFrame, { win.close; nil; });
				});
			});
		win.onClose_({
			frame = lastFrame;
			"finished!".postln;
			});
	}
		
	render { arg path,  width=500, height=500, color=Color.black, ext="png";
		var img, frame=firstFrame, index = 0;
		var queue = [];
		var removeThese = [];
		img = SCImage.color(width@height, color);
		
		{
		while { frame < lastFrame } {
			removeThese = [];
			while { (index < (score.size-1)) && (frame == score[index][0]) }
				{ 	queue = queue.add([ 0, score[index][1] - score[index][0], index ]);
					index = index + 1; 
					};
			
			img.lockFocus;
						
			if(clear == 1, { Pen.fillColor = color; Pen.fillRect(img.bounds); }, { clear = 1; });	
			queue.do({ arg it, i;
					if(it[0] == (it[1]-1), { removeThese = removeThese.add(i.asInteger); });
					if(score[it[2]][2] == \noRefresh, { clear = 0; },
						{ (score[it[2]][2]).value(it[0]/(it[1]-1), score[it[2]][5]); });
					it[0] = it[0] + 1;
					});
			removeThese.reverse.do({ arg it; queue.removeAt(it); });
			img.unlockFocus;
			
			img.write((path++"_"++(frame+1)++"."++ext).standardizePath);
			("frame: "++(frame+1)++" of "++lastFrame++", time: "++((frame+1)/frameRate)++", rendered!").postln;
			frame = frame+1;
			if(frame == lastFrame) { ("All done! file is "++(frame/frameRate)++" in duration.").postln; img.free; };
			0.0.wait;
			};
		}.fork(AppClock);

	}
			
	*preview	{ arg list, rate=25.0, width=500, height=500, color=Color.black, sort=true;
		this.new(list, rate, sort).preview(width, height, color);
	}
	
	*render	{ arg path, list, rate=25.0, width=500, height=500, color=Color.black, ext="png", sort=true;
		this.new(list, rate, sort).render(path, width, height, color, ext);
	}
}