package com.bmxgates.logger;

import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bmxgates.logger.data.SprintDatabaseHelper;

public class BMXSprintApplication extends Application {
	private static String BMX_SPRINT_APPLICATION ="BMXSprintApplication";

	BluetoothSerial bluetoothSerial;

	SQLiteDatabase database;

	Handler serialHandler;

	@Override
	public void onCreate() {
		super.onCreate();

		SQLiteOpenHelper sprintDatabaseHelper = new SprintDatabaseHelper(this);
		openDatabase(sprintDatabaseHelper);

		bluetoothSerial = new BluetoothSerial(this, new BluetoothSerial.MessageHandler() {

			@Override
			public int read(int bufferSize, byte[] buffer) {
				return doRead(bufferSize, buffer);
			}
		}, "HC-0");
		bluetoothSerial.onResume();
	}

	protected int doRead(int bufferSize, byte[] buffer) {

		// ignore everything until ready
		if (serialHandler == null)
			return bufferSize;

		//wait for complete message
		if (bufferSize < 2)
			return 0;

		try {
//			//find end of message
//			int bytesRead = -1;
//			for (int i=0;i<bufferSize;i++){
//				if (buffer[i]==0x0A){ <-- Bad must check position in buffer
//					bytesRead = i;
//					break;
//				}
//			}
//
//			//return if msg incomplete
//			if (bytesRead == -1){
//				return 0;
//			}

			int read = 0;
			int split = (ui(buffer[read++]) << 8) | (ui(buffer[read++]));

			Log.v(BMX_SPRINT_APPLICATION, "Split: " + split);
//			long checksum = ul(buffer[read++]) << 24 | ul(buffer[read++]) << 16 | ul(buffer[read++]) << 8 | ul(buffer[read++]);
//			Bundle data = new Bundle();
//			data.putLong("checksum", checksum);

			Message message = serialHandler.obtainMessage();
			message.arg1 = split;
//			message.setData(data);

			serialHandler.sendMessage(message);

//			//add one for newline character
//			return read+1;
			
			return read;
		} catch (Throwable t) {
			Log.i(BMX_SPRINT_APPLICATION, "Failed processing message: " + t.getMessage());
		}

		return 0;

	}

	public void reconnect() {
		bluetoothSerial.close();
		bluetoothSerial.connect();
	}

	public Handler getSerialHandler() {
		return serialHandler;
	}

	public void setSerialHandler(Handler serialHandler) {
		this.serialHandler = serialHandler;
	}

	public boolean isConnected() {
		return bluetoothSerial.connected;
	}

	public AsyncTask<SQLiteOpenHelper, Void, SQLiteDatabase> openDatabase(SQLiteOpenHelper sqliteOpenHelper){
		return new AsyncTask<SQLiteOpenHelper, Void, SQLiteDatabase>() {
			@Override
			protected SQLiteDatabase doInBackground(SQLiteOpenHelper... params) {
				SQLiteDatabase database = params[0].getWritableDatabase();

				return database;
			}

			@Override
			protected void onPostExecute(SQLiteDatabase sqlitedatabase) {
				super.onPostExecute(sqlitedatabase);

				database = sqlitedatabase;

				Intent intent = new Intent(SprintDatabaseHelper.DATABASE_OPENED_ACTION);
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}


		}.execute(sqliteOpenHelper);
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}


	public static long ul(byte b){
		return (long) (b & 0xff);
	}

	public static int ui(byte b){
		return (int) (b & 0xff);
	}

}
