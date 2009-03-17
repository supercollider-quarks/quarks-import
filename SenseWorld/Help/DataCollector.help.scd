// =====================================================================
// Data collector help
// =====================================================================

d = DataCollector.new( "test" );
d.collection;

d.addData( [1,2,3] );
d.collection
   
d.addMetaData( [5,6,7], d.collection.first[\date] );
d.collection

d.backup

e = DataCollector.new( "test" );

e.recover
e.collection;

