//package io.divide.client.android.cache;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import java.io.IOException;
//
//public class DatabaseInitializer extends SQLiteOpenHelper{
//
//    private static String DB_PATH;// = "/data/data/com.bfil.ormliterepo/databases/";
//    private static String DB_NAME = "db.sqlite";
//
//    private SQLiteDatabase database;
//    private final Context context;
//
//    public DatabaseInitializer(Context context) throws IOException {
//    	super(context, DB_NAME, null, 1);
//        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
//        this.context = context;
//
////        createDatabase();
//    }
//
//    public SQLiteDatabase getDB(){
//        return this.getWritableDatabase();
//    }
//
////    public void createDatabase() throws IOException{
////
////    	boolean dbExist = checkDatabase();
////
////    	if(!dbExist){
////        	this.getReadableDatabase();
////        	try {
////    			copyDatabase();
////    		} catch (IOException e) {
////        		throw new Error("Error copying database");
////        	}
////    	}
////
////    }
////
////    private boolean checkDatabase(){
////
////    	SQLiteDatabase checkDB = null;
////
////    	try{
////    		String myPath = DB_PATH + DB_NAME;
////            System.out.println(myPath);
////    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
////
////    	}catch(SQLiteException e){
////            e.printStackTrace();
////    	}
////
////    	if(checkDB != null){
////    		checkDB.close();
////    	}
////
////    	return checkDB != null ? true : false;
////    }
////
////    private void copyDatabase() throws IOException{
////
////    	InputStream myInput = context.getAssets().open(DB_NAME);
////
////    	String outFileName = DB_PATH + DB_NAME;
////
////    	OutputStream myOutput = new FileOutputStream(outFileName);
////
////    	byte[] buffer = new byte[1024];
////    	int length;
////    	while ((length = myInput.read(buffer))>0){
////    		myOutput.write(buffer, 0, length);
////    	}
////
////    	myOutput.flush();
////    	myOutput.close();
////    	myInput.close();
////
////    }
//
//    @Override
//	public synchronized void close() {
//	    if(database != null)
//		    database.close();
//
//	    super.close();
//	}
//
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//	}
//
//}
