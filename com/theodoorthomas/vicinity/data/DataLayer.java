package com.theodoorthomas.vicinity.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.theodoorthomas.vicinity.data.anotations.Column;
import com.theodoorthomas.vicinity.data.anotations.Table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public abstract class DataLayer {

	protected VicinityOpenHelper db;
	protected HashMap<String, String> columnList = new HashMap<String, String>();
	protected String tableName;

	public DataLayer(Context c) throws DataLayerError {
		db = new VicinityOpenHelper(c);
		for (Field f : getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Column.class)) {
				Column a = f.getAnnotation(Column.class);
				columnList.put(f.getName(), a.type());
				Log.d("VicinityDBM", f.getName());
			}
		}
		if ( getClass().isAnnotationPresent(Table.class) )
			tableName = getClass().getAnnotation(Table.class).name();
		else
			throw new DataLayerError("Table annotation missing for " + getClass().toString());
	}
	
	public boolean exsistsBy(String column) {
		Cursor c = null;		
		SQLiteDatabase conn = db.getReadableDatabase();
		String q = "select * from "+ tableName +" where " + column + "= ?";
		Method getter = getterForColumn(column);
		try {
			c = conn.rawQuery(q, new String[] {String.valueOf(getter.invoke(this))});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
		return c.getCount() == 0 ? false : true;
	}
	
	public void getFirstBy(String column, String value) throws DataLayerError {
		Cursor c = null;		
		SQLiteDatabase conn = db.getReadableDatabase();
		if (columnList.containsKey(column)) {			
			String q = "select * from "+ tableName +" where " + column + "= ?";
			Log.d("VicinityDBM", q );
			c = conn.rawQuery(q, new String[] {String.valueOf(value)});
			c.moveToFirst();
			if (c.getCount() == 0)
				Log.d("VicinityDBM", "No results");
			conn.close();
		} else {
			throw new DataLayerError("No such column: " + column);
		}
		if (c.getCount() != 0)
			parseResult(c);
	}
	
	private Method getterForColumn(String column) {
		Method rv = null;
		try {
			rv = getClass().getMethod( "get" + Character.toUpperCase(
					column.charAt(0)) + column.substring(1));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return rv;
	}
	
	private Method setterForColumn(String column) {
		Method rv = null;
		try {
			rv = getClass().getMethod( "set" + Character.toUpperCase(
					column.charAt(0)) + column.substring(1));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return rv;
	}

	private void parseResult(Cursor c) throws DataLayerError {
		try {
			for (Entry<String, String> entry : columnList.entrySet()) {
				columnTypeParser(c, entry);	
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private void columnTypeParser(Cursor c, Entry<String, String> entry)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, DataLayerError {
		String c_name = "set" + Character.toUpperCase(
				entry.getKey().charAt(0)) + entry.getKey().substring(1);
		Log.d("VicinityDBM", "Preparing to call " + c_name + " for column " + entry.getKey() );
		if (entry.getValue() == "INTEGER") { 
			int i = c.getInt(c.getColumnIndex(entry.getKey()));
					getClass().getMethod(c_name, 
					Integer.class).invoke(this, i);

		} else if (entry.getValue() == "DateTime") {
			DateTime dt = new DateTime(c.getLong(c.getColumnIndex(entry.getKey())));
					getClass().getMethod(c_name, 
					DateTime.class).invoke(this, dt);
			
		} else if (entry.getValue() == "TEXT") {
			String s = c.getString(c.getColumnIndex(entry.getKey()));
					getClass().getMethod(c_name, 
					String.class).invoke(this, s);
					
		} else if (entry.getValue() == "KEY") {
			int s = c.getInt(c.getColumnIndex(entry.getKey()));
					getClass().getMethod(c_name, 
					Integer.class).invoke(this, s);
		
		} else if (entry.getValue() == "Boolean") {
			int s = c.getInt(c.getColumnIndex(entry.getKey()));
					getClass().getMethod(c_name, 
					Boolean.class).invoke(this, s == 0 ? false : true );
		
		} else {
			throw new DataLayerError("Unknown column type: " + columnList.get(entry.getKey()));
		}
	}
	
	public long createOrUpdateBy(String columnName) {
		if (exsistsBy(columnName)) {
			return updateBy(columnName);
		} else {
			return save();
		}
	}
	
	public long updateBy(String columnName) {
		Log.d("VicinityDBM", "Saving Record");
		SQLiteDatabase conn = db.getWritableDatabase();
		long rv = 0;
		try {
			String v = String.valueOf(getterForColumn(columnName).invoke(this));
			ContentValues values = generateContentValues();
			rv = conn.update(tableName, values, columnName +" = ?", 
					new String[]{v});
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
		return rv;
	}

	public long save() {
		Log.d("VicinityDBM", "Saving Record");
		SQLiteDatabase conn = db.getWritableDatabase();
		long rv = 0;
		try {
			ContentValues values = generateContentValues();
			Log.d("VicinityDBM", values.toString());
			rv = conn.insert(tableName, "", values);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
		return rv;
	}

	private ContentValues generateContentValues() throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		ContentValues values = new ContentValues();		
		for (Entry<String, String> entry : columnList.entrySet()) {
			String c_name = "get" + Character.toUpperCase(
					entry.getKey().charAt(0)) + entry.getKey().substring(1);
			Method method = getClass().getMethod(c_name);
			if ( entry.getValue() == "DateTime" ) {
				DateTime dt = (DateTime)method.invoke(this, new Object[]{});
				String s_dt = Long.toString(dt.getMillis());
				values.put(entry.getKey(), s_dt);
		    
			} else if ( entry.getValue() == "Boolean" ) {
				Boolean b = (Boolean)method.invoke(this, new Object[]{});
				if (b != null) {
					String s_b = b.toString();
					values.put(entry.getKey(), s_b);
				}
			} else if (entry.getValue() != "KEY") { 
				values.put(entry.getKey(), String.valueOf(method.invoke(this, new Object[]{})));
			}
		}
		return values;
	}
}