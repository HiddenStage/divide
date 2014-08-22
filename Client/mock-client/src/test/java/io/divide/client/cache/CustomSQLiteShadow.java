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

//package io.divide.client.cache;
//
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.CancellationSignal;
//import org.robolectric.annotation.Implementation;
//import org.robolectric.annotation.Implements;
//import org.robolectric.shadows.ShadowSQLiteDatabase;
//
//@Implements(value = SQLiteDatabase.class, inheritImplementationMethods = true)
//public class CustomSQLiteShadow extends ShadowSQLiteDatabase {
//
//    @Implementation
//    public Cursor rawQueryWithFactory (SQLiteDatabase.CursorFactory cursorFactory,
//                                       String sql,
//                                       String[] selectionArgs,
//                                       String editTable,
//                                       CancellationSignal cancellationSignal) {
//        return rawQueryWithFactory(cursorFactory,
//                sql,
//                selectionArgs,
//                editTable);
//    }
//}