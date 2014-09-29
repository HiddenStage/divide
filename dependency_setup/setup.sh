pwd=$(pwd)

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

mvn clean install -Dextras.compatibility.v13.groupid=com.android.support -Dextras.compatibility.v13.artifactid=support-v13

cd ../compatibility-v4

mvn clean install -Dextras.compatibility.v4.groupid=com.android.support -Dextras.compatibility.v4.artifactid=support-v4

cd ../gcm/gcm-server

mvn clean install

cd ../../../../iboxdb

mvn clean install

cd $pwd
