Building Divide.io
===========
##Prerequisites
You must have git, Maven, and JDK (version 6 or above) installed.

##Step 1
Clone the repo:

```
git clone https://github.com/HiddenStage/divide.git
```
##Step 2
**For OS X:**

Go to the `dependency_setup` directory and run `setup.sh`

```
cd dependency_setup
sh setup.sh
```

**For Windows:**

Go to the `dependency_setup` directory and run `setup.bat`

```
cd dependency_setup
setup.bat
```

*If you run into ‘cmd’ is not recognized as an internal or external command. make sure you have “C:\Windows\System32” in your Path variable.*

##Step 3
Go the the root directory then clean and build the project. (This may take awhile as libraries will download and tests will be run)

```
mvn clean install
```

**Notes:**
* SDK API 19 requires all Google APIs and Google Wear APIs.
* You’ll need to have 'Google Cloud Messaging for Android Library’ installed. You may have to search for obsolete libraries in the Android SDK Manager.
