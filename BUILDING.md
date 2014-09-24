Building Divide.io
===========
##Step 1
Clone the repo:

```
git clone https://github.com/HiddenStage/divide.git
```
##Step 2 (For OS X)
Go to the `dependency_setup` directory and run `setup.sh`

```
cd dependency_setup
sh setup.sh
```

##Step 3
Go the the root directory then clean and build the project. (This may take awhile as libraries will download and tests will be run)

```
cd ..
mvn clean install
```

**Note:** Windows instructions are coming soon.
