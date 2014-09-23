Divide.io
===========

**Currently in alpha. Not intended for production use at this time.**

Divide.io is an open source Backend-as-a-Service (BaaS). It provides tools to easily, securely, and efficiently communicate between your app and a server. It handles data storage, user registration & management, and push notifications.

Deploy anywhere. Use any database you want. Never get locked into a platform.

#### Server
Built on Jersey, it can be deployed to any J2EE server that supports javax.ws.rs.core.Application. DAO implementations are independent and can be written/plugged in for the situation.

```java
public class SomeApplication extends AuthApplication<OrientDBDao> {

    private String encryptionKey = "someKeyForSynchronousEncryption";

    public SomeApplication() {
    	// dao class
        super(OrientDBDao.class, encryptionKey);
    }
    
    public SomeApplication() {
        // dao instance
        super(new OrientDBDao(), encryptionKey);
    }
}
```
#### Client (Android)
```java
public class MyApplication extends Application {

    @Override
    public void onCreate(){
        Backend.init(this,"http://authenticator-test.appspot.com/api/");
    }
}
```
#### Create and Save an Object
```java
BackendObject object = new BackendObject();

// add values, will be serialized with GSON
object.put("somePrimative1",1);          // int
object.put("somePrimative2",1L);         // long
object.put("somePrimative3","Some string"); // String
object.put("someObject",new Object());   // Some POJO...etc.

// store remotely async
BackendServices
	.remote()
	.save(object)
	.subscribe();
        
// store locally async
BackendServices
	.local()
	.save(object)
	.subscribe();
```
#### Perform Query
```java
// create Query
Query query = new QueryBuilder()
    .select()
    .from(BackendObject.class)
    .limit(10).build();

// run query against remote server
BackendServices.remote()
    .query(BackendObject.class, query)
    .subscribe(new Action1<Collection<BackendObject>>() {
           @Override
           public void call(Collection<BackendObject> objects) {
 	     		// do something with objects
           }
     });
```
#License
```
Copyright (C) 2014 Divide.io

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
