//redFrik 130612

//4-bit fibonacci LFSR, https://en.wikipedia.org/wiki/Linear_feedback_shift_register


//--pseudo random numbers 0-15
RedLFSR4 {

	*ar {|trig= 0, iseed= 2r1000|
		var buf= LocalBuf(1).set(iseed);
		var b= Demand.ar(trig, 0, Dbufrd(buf));//read
		var output= (b&1).bitXor(b&2>>1);//linear function
		b= (b>>1)|(output<<3);//shift
		Demand.ar(trig, 0, Dbufwr(b, buf));//write
		^b;
	}

	*kr {|trig= 0, iseed= 2r1000|
		var buf= LocalBuf(1).set(iseed);
		var b= Demand.kr(trig, 0, Dbufrd(buf));//read
		var output= (b&1).bitXor(b&2>>1);//linear function
		b= (b>>1)|(output<<3);//shift
		Demand.kr(trig, 0, Dbufwr(b, buf));//write
		^b;
	}
}

//--pseudo random waveform 0/1
RedLFSR4BitStream {

	*ar {|freq= 4, iseed= 2r1000|
		var lfsr= RedLFSR4.ar(Impulse.ar(freq), iseed);
		^Demand.ar(Impulse.ar(freq*4), 0, Dseq([lfsr&8>>3, lfsr&4>>2, lfsr&2>>1, lfsr&1], inf));
	}

	*kr {|freq= 4, iseed= 2r1000|
		var lfsr= RedLFSR4.kr(Impulse.kr(freq), iseed);
		^Demand.kr(Impulse.kr(freq*4), 0, Dseq([lfsr&8>>3, lfsr&4>>2, lfsr&2>>1, lfsr&1], inf));
	}
}
