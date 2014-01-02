package com.jug6ernaut.network.authenticator.client.cache;

import org.robolectric.util.DatabaseConfig;

import java.io.File;
import java.sql.ResultSet;

public class SQLiteMap implements DatabaseConfig.DatabaseMap {

	private String _dbFile;
		
	/**
	 * This constructor will use in-memory database.
	 */
	public SQLiteMap() {}
	
	
	/**
	 * This constructor will use use database file
	 *
	 * @param dbFile: path to the SQLite database file
	 */
	public SQLiteMap(String dbFile) {
		_dbFile = dbFile;
	}
	
	public String getDriverClassName() {
		return "org.sqlite.JDBC";
	}

    @Override
    public String getMemoryConnectionString() {
        return String.format("jdbc:sqlite:%s", (_dbFile == null ? ":memory" : _dbFile));
    }

    @Override
    public String getConnectionString(File file) {
        return String.format("jdbc:sqlite:%s", (_dbFile == null ? ":memory" : _dbFile));
    }
	
	public String getSelectLastInsertIdentity() {
		return "SELECT last_insert_rowid() AS id";
	}
	
	public int getResultSetType() {
		return ResultSet.TYPE_FORWARD_ONLY;
	}
}