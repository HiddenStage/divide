pwd=$(pwd)

mvn install:install-file -Dfile=iboxdb-1.4.2.jar -DgroupId=iboxdb -DartifactId=iboxdb -Dversion=1.4.2 -Dpackaging=jar

if [ -d "maven-android-sdk-deployer" ]
then
    cd maven-android-sdk-deployer
	git pull
else
    git clone https://github.com/mosabua/maven-android-sdk-deployer.git
	cd maven-android-sdk-deployer
fi

mvn clean install -P 4.4

cd extras/compatibility-v13

mvn clean install

cd ../gcm/gcm-server

mvn clean install

cd $pwd