/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//package io.divide.client.android.cache;
//
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.text.TextUtils;
//import io.divide.shared.logging.Logger;
//
//import java.util.*;
//
//public class SQLiteUtils {
//
//    private static final String NAME_COLUMN = "name";
//    private static final String TYPE_COLUMN = "type";
//    Logger logger = Logger.getLogger(SQLiteUtils.class);
//    SQLiteDatabase db;
//
//    public SQLiteUtils(SQLiteDatabase db){
//        this.db = db;
//    }
//
//    public boolean tableExist(String tableName){
//        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+tableName+"';",null);
//        boolean exists = c.getCount() > 0;
//        c.close();
//        return exists;
//    }
//
//    public void createTable(String tableName, Collection<String> columns){
//
//        StringBuilder sb = new StringBuilder();
//        sb.append("CREATE TABLE IF NOT EXISTS `" + tableName +"` ( ");
//
//        Iterator<String> iterator = columns.iterator();
//        if (iterator.hasNext()) {
//            String column = iterator.next();
//            if(!column.equals("object_key"))
//                column.concat(" TEXT");
//            else
//                column.concat(" TEXT PRIMARY KEY");
//        }
//
//        String columnString = TextUtils.join(",",columns);
//
//        sb.append(columnString);
//        sb.append(");");
//
//        logger.debug("createTable: " + sb.toString());
//        db.execSQL(sb.toString());
//    }
//
//    public List<String> getColumns(String tableName){
//
//        Cursor c = db.rawQuery("pragma table_info("+tableName+")",null);
//        List<String> colums = new ArrayList<String>();
//
//        int NAME_INDEX = c.getColumnIndex(NAME_COLUMN);
////        int TYPE_INDEX = c.getColumnIndex(TYPE_COLUMN);
//        while(c.moveToNext()){
//            colums.add(c.getString(NAME_INDEX));
//        }
//        c.close();
//
//        return colums;
//    }
//
//    public void addColumn(String table, String column){
//        addColumns(table, Arrays.asList(column));
//    }
//
//    public void addColumns(String table, Collection<String> columns){
//        for(String column : columns){
//            String sql = "ALTER TABLE '"+table+"' ADD COLUMN '" + column + "' TEXT;";
//            logger.debug("addColumns: " + sql);
//            db.execSQL(sql);
//        }
//    }
//}
