//package com.jug6ernaut.network.authenticator.client.cache;
//
//import java.util.List;
//
//import android.app.Activity;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//
//import org.apache.commons.lang3.builder.EqualsBuilder;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import static org.junit.Assert.*;
//
//import com.example.androidsqlite.data.*;
//
//@RunWith(SQLiteTestRunner.class)
//public class RoleTest {
//	private SQLiteDatabase _db;
//	private static final String CREATED_BY = "testuser1";
//	private static final String UPDATED_BY = "testuser2";
//	private static final String NAME = "TestRole";
//	private static final String DESCRIPTION = "TestRole description";
//
//	private role constructNewTestRole(String name, String description) {
//		role r = new role();
//		r.set_created_by(CREATED_BY);
//		r.set_name((name != null ? name : NAME));
//		r.set_description((description != null ? description : DESCRIPTION));
//		return r;
//	}
//
//	private role updateTestRole(role r) {
//		String newName = r.get_name() + " Updated";
//		String newDesc = r.get_description() + " Updated";
//
//		r.set_updated_by(UPDATED_BY);
//		r.set_name(newName);
//		r.set_description(newDesc);
//
//		return r;
//	}
//
//	public boolean equals(Object obj1, Object obj2) {
//		 return EqualsBuilder.reflectionEquals(obj1, obj2);
//	}
//
//	@Before
//	public void setup() {
//		Context c = new Activity();
//		DBHelper dbh = new DBHelper(c,"testdb", null, 1);
//		_db = dbh.getWritableDatabase();
//	}
//
//	@After
//	public void after() {
//		if (_db != null) {
//			role_DAL rdal = new role_DAL(_db);
//			rdal.deleteAll();
//			_db.close();
//		}
//	}
//
//	@Test
//	public void test_role_Insert() {
//		role r = this.constructNewTestRole(null,null);
//		role_DAL rdal = new role_DAL(_db);
//		role inserted = rdal.insert(r);
//		assertTrue(inserted != null &&
//				inserted.get__id() > 0 &&
//				inserted.get_created_by().equals(r.get_created_by()) &&
//				inserted.get_name().equals(r.get_name()) &&
//				inserted.get_description().equals(r.get_description()) &&
//				inserted.get_date_created() != null);
//	}
//
//	@Test
//	public void test_role_DeleteAll() {
//		role_DAL rdal = new role_DAL(_db);
//
//		role r1 = this.constructNewTestRole("Role1","Description1");
//		role r2 = this.constructNewTestRole("Role2","Description2");
//		role r3 = this.constructNewTestRole("Role3","Description3");
//
//		rdal.insert(r1);
//		rdal.insert(r2);
//		rdal.insert(r3);
//
//		int result = rdal.deleteAll();
//		assert(result == 3);
//	}
//
//	@Test
//	public void test_role_Update() {
//		role r = this.constructNewTestRole(null,null);
//		role_DAL rdal = new role_DAL(_db);
//		role inserted = rdal.insert(r);
//
//		role tmp = updateTestRole(inserted);
//		role updated = rdal.update(tmp);
//		assertTrue(updated != null &&
//				updated.get__id() == inserted.get__id() &&
//				updated.get_date_updated() != null &&
//				updated.get_created_by().equals(inserted.get_created_by()) &&
//				updated.get_date_created().equals(inserted.get_date_created()) &&
//				updated.get_description().equals(tmp.get_description()) &&
//				updated.get_updated_by().equals(tmp.get_updated_by()) &&
//				updated.get_name().equals(tmp.get_name()));
//	}
//
//	@Test
//	public void test_role_Get() {
//		role r = this.constructNewTestRole(null,null);
//
//		role_DAL rdal = new role_DAL(_db);
//		role inserted = rdal.insert(r);
//
//		List<role> results = rdal.get("name = ?",
//				new String[]{inserted.get_name()},
//				null, null, null);
//
//		assertTrue(results != null &&
//				results.size() == 1 &&
//				equals(results.get(0), inserted));
//	}
//
//	@Test
//	public void test_role_GetById() {
//		role r = this.constructNewTestRole(null, null);
//		role_DAL rdal = new role_DAL(_db);
//		role inserted = rdal.insert(r);
//
//		role result = rdal.getById(inserted.get__id());
//
//		assertTrue(result != null &&
//				equals(result, inserted));
//	}
//
//	@Test
//	public void test_role_GetAll() {
//		role_DAL rdal = new role_DAL(_db);
//
//		role r1 = this.constructNewTestRole("Role1","Description1");
//		role r2 = this.constructNewTestRole("Role2","Description2");
//		role r3 = this.constructNewTestRole("Role3","Description3");
//
//		rdal.insert(r1);
//		rdal.insert(r2);
//		rdal.insert(r3);
//
//		List<role> results = rdal.getAll("_id");
//
//		assert(results != null &&
//				results.size() == 3 &&
//				equals(results.get(0), r1) &&
//				equals(results.get(1), r2) &&
//				equals(results.get(2), r3));
//	}
//
//	@Test
//	public void test_role_Delete() {
//		role r = this.constructNewTestRole(null, null);
//		role_DAL rdal = new role_DAL(_db);
//		role inserted = rdal.insert(r);
//
//		int result = rdal.delete(inserted);
//		assert(result == 1);
//	}
//
//	@Test
//	public void test_role_DeleteMulti() {
//		role_DAL rdal = new role_DAL(_db);
//
//		role r1 = this.constructNewTestRole("Role1","Description1");
//		role r2 = this.constructNewTestRole("Role2","Description2");
//		role r3 = this.constructNewTestRole("Role3","Description3");
//
//		rdal.insert(r1);
//		rdal.insert(r2);
//		rdal.insert(r3);
//
//		int result = rdal.deleteMulti(
//				new String[] {
//						Long.toString(r1.get__id()),
//						Long.toString(r2.get__id()),
//					});
//
//		role intact = rdal.getById(r3.get__id());
//
//		assert(result == 2 && intact != null);
//	}
//}
