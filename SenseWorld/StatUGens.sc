VarianceUGen{

	*kr{ arg input, length=40, mean;
		var variance;
		if ( mean.isNil,
			{
				mean = RunningSum.kr( input, length )/length;
			});
		variance = RunningSum.kr( ( input - mean ).abs.squared, length )/(length-1);
		^variance;
	}

	*categories { ^ #["UGens>Analysis>Statistics"] }
}

StdDevUGen{

	*kr{ arg input, length=40, mean;
		var variance,stddev;
		variance = VarianceUGen.kr( input, length, mean );
		stddev = variance.sqrt;
		^stddev;
	}

	*categories { ^ #["UGens>Analysis>Statistics"] }
}

SkewUGen{ 
	*kr{ arg input, length=40, mean;
		var skew,stddev,good;
		if ( mean.isNil,
			{
			mean = RunningSum.kr( input, length )/length;
			});
		stddev = StdDevUGen.kr( input, length, mean );
		skew = RunningSum.kr( ( input - mean ).cubed, length ) / (length * stddev.cubed );
		// get rid of NaN's in output
		good = BinaryOpUGen('==', CheckBadValues.kr(skew, 0, 0), 0);
		skew = skew * good;
		^skew;
	}

	*categories { ^ #["UGens>Analysis>Statistics"] }
}

FluctuationUGen{

	*kr{ arg input, llength=40, slength=20;
		var sdev, ldev, fluct, good;
		sdev = StdDevUGen.kr( input, slength );
		ldev = StdDevUGen.kr( input, llength );
		fluct = sdev / ldev;
		// get rid of NaN's in output
		good = BinaryOpUGen('==', CheckBadValues.kr(fluct, 0, 0), 0);
		fluct = Gate.kr(fluct,good);
		^[fluct,sdev,ldev];
	}

	*categories { ^ #["UGens>Analysis>Statistics"] }
}

DynamicScaleUGen{
	*kr{ arg input, scalefact=0.75, top=1.1, bottom=0.9, llength=40, slength=20;
		var scale, fluct, ldev, sdev;
		var select, adjtop, adjbot;
		var fluctres;

		fluctres=FluctuationUGen.kr( input, llength, slength );

		fluct = fluctres[0];
		ldev = fluctres[1];
		sdev = fluctres[2];

		scale = LocalIn.kr( 1 );
		adjtop = fluct > top;
		adjbot = fluct < bottom;
		scale = 
		(scalefact * adjtop / ldev) // adjtop is zero when fluct not above top
		+ (scalefact * adjbot / ldev) // adjbot is zero when fluct not below bot
		+ scale * InRange.kr( fluct, bottom, top ); // is 1 when fluct in range
		
		LocalOut.kr( scale );

		^( scale * sdev )
	}

	*categories { ^ #["UGens>Analysis>Statistics"] }
}

MaxDynScaleUGen{

	*kr{ arg inputArray, scalefact=0.75, top=1.1, bottom=0.9, llength=40, slength=20;
		var scales, fluct, ldev, sdev;
		var select, adjtop, adjbot;
		var fluctres;

		fluctres=FluctuationUGen.kr( inputArray, llength, slength );

		fluct = fluctres[0];
		ldev = fluctres[1];
		sdev = fluctres[2];

		scales = LocalIn.kr( inputArray.size );
		adjtop = fluct > top;
		adjbot = fluct < bottom;
		scales = 
		(scalefact * adjtop / ldev) // adjtop is zero when fluct not above top
		+ (scalefact * adjbot / ldev) // adjbot is zero when fluct not below bot
		+ scales * InRange.kr( fluct, bottom, top ); // is 1 when fluct in range

		scales = Array.fill( inputArray.size, scales.maxItem );

		LocalOut.kr( scales );

		^( scales * sdev )
	}

	*categories { ^ #["UGens>Analysis>Statistics"] }
}

MinDynScaleUGen{

	*kr{ arg inputArray, scalefact=0.75, top=1.1, bottom=0.9, llength=40, slength=20;
		var scales, fluct, ldev, sdev;
		var select, adjtop, adjbot;
		var fluctres;

		fluctres=FluctuationUGen.kr( inputArray, llength, slength );

		fluct = fluctres[0];
		ldev = fluctres[1];
		sdev = fluctres[2];

		scales = LocalIn.kr( inputArray.size );
		adjtop = fluct > top;
		adjbot = fluct < bottom;
		scales = 
		(scalefact * adjtop / ldev) // adjtop is zero when fluct not above top
		+ (scalefact * adjbot / ldev) // adjbot is zero when fluct not below bot
		+ scales * InRange.kr( fluct, bottom, top ); // is 1 when fluct in range

		scales = Array.fill( inputArray.size, scales.minItem );

		LocalOut.kr( scales );

		^( scales * sdev )
	}

	*categories { ^ #["UGens>Analysis>Statistics"] }
}