/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
