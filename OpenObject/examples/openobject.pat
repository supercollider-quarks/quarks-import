max v2;#N vpatcher 259 140 1227 668;#P window setfont "Sans Serif" 9.;#P window linecount 1;#P message 264 98 129 196617 /ook brussels play vol 0.2;#P comment 577 195 132 196617 integrate with max objects;#P comment 577 53 87 196617 set proxy source;#P comment 36 50 42 196617 control;#P message 83 185 126 196617 /oo brussels fadeTime_ 3;#P button 588 209 15 0;#P newex 588 253 117 196617 scale 0 99 1 4000 1.06;#P newex 588 231 64 196617 random 100;#P newex 132 452 56 196617 route text;#P newex 132 474 120 196617 prepend /oosrc brussels;#P user textedit 132 375 454 446 32896 3 9 {Clip.ar(SinOsc.ar([300\,303]\,0\,SinOsc.ar(0.1).range(0\,0.5))+1*RLPF.ar(LFSaw.ar(LFSaw.kr([3+LFSaw.kr(1.75)\, 2.3333]\,0\, 0.15).exprange(SinOsc.kr(0.08).range(80\, 100).round(10)\, LFPulse.kr(0.046+LFPulse.kr(0.1\,0\,2pi)).range(100\,1000))\,0.7)\, LFPulse.kr(8*LFPulse.kr(0.25)).exprange(200\, 2000))+BPF.ar(SinOsc.ar([100\,104]\,0\,Decay2.ar(SinOsc.ar([1\,2]*1.5+SinOsc.kr(0.05).round(0.15))< 0.66667.neg\,0.01\,SinOsc.kr(0.05).range(0.01\,0.05)))\,[3700\,5000]\, 0.06)\,1.neg\,1)};#P message 588 109 330 196617 /oosrc brussels {|freq= 500| SinOsc.ar(SinOsc.ar(0.4\\\,0\\\,10\\\,freq))};#P message 588 275 351 196617 /oosrc brussels {|freq= 400| SinOsc.ar(SinOsc.ar(0.4\\\,0\\\,10\\\,freq+ \$1))};#P message 588 89 200 196617 /oosrc brussels {SinOsc.ar(400\\\,0)*0.1};#P message 588 69 147 196617 /oosrc brussels {Saw.ar*0.1};#P message 256 69 147 196617 /ook brussels play fadeTime 5;#P message 76 156 180 196617 /oo brussels set freq 600 numharm 4;#P newex 588 376 31 196617 mtof;#P user kslider 588 315 54 0 36 48 31 12 0 128 128 128 128 128 128 255 255 255 0 0 0 0 0 0;#P message 588 398 121 196617 /oo brussels set freq \$1;#P message 55 69 89 196617 /oo brussels play;#P message 62 98 89 196617 /oo brussels stop;#P message 69 127 127 196617 /oo brussels set freq 800;#P newex 32 230 126 196617 udpsend 127.0.0.1 57120;#P comment 246 50 99 196617 keyword arguments;#P newex 266 459 32 196617 sel 3;#P comment 121 358 230 196617 edit and press enter (or fn+return) to set source;#P comment 32 250 170 196617 replace localhost ip \, keep port as is;#P comment 1 22 311 196617 //for the supercollider example to access \, see OpenObject helpfile;#P connect 24 0 5 0;#P connect 17 0 5 0;#P connect 15 0 5 0;#P connect 16 0 5 0;#P connect 14 0 5 0;#P connect 13 0 5 0;#P connect 12 0 5 0;#P connect 9 0 5 0;#P connect 8 0 5 0;#P connect 7 0 5 0;#P connect 6 0 5 0;#P connect 19 0 5 0;#P connect 28 0 5 0;#P connect 3 0 18 0;#P connect 18 0 20 0;#P connect 20 0 19 0;#P connect 18 1 3 0;#P connect 23 0 21 0;#P connect 21 0 22 0;#P connect 22 0 16 0;#P connect 10 0 11 0;#P connect 11 0 9 0;#P pop;