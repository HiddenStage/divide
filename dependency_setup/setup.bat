SET CURRENTDIR="%cd%"
 
if exist maven-android-sdk-deployer (
    cd maven-android-sdk-deployer
        git pull
) else (
    git clone https://github.com/mosabua/maven-android-sdk-deployer.git
        cd maven-android-sdk-deployer
)
 
call mvn clean install -P 4.4a
 
cd extras\compatibility-v13
 
call mvn clean install -Dextras.compatibility.v13.groupid=com.android.support -

Dextras.compatibility.v13.artifactid=support-v13
 
cd ..\compatibility-v4
 
call mvn clean install -Dextras.compatibility.v4.groupid=com.android.support -Dextras.compatibility.v4.artifactid=support-

v4
 
cd ..\gcm\gcm-server
 
call mvn clean install
 
cd /d %CURRENTDIR%
