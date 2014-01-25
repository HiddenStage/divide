mvn install:install-file -Dfile=iboxdb-1.4.2.jar -DgroupId=iboxdb -DartifactId=idboxdb -Dversion=1.4.2 -Dpackaging=jar

git clone https://github.com/mosabua/maven-android-sdk-deployer.git

cd maven-android-sdk-deployer

mvn clean install -P 4.4

cd extras/compatibility-v13

mvn clean install

