@echo off
echo Setting JAVA_HOME
set JAVA_HOME=C:\Users\RJ\Documents\Development\tools\Java\JDK
echo setting PATH
set PATH=C:\Users\RJ\Documents\Development\tools\Java\JDK\bin;%PATH%
echo Display java version
java -version
rd _classes /S /Q
del datacrow-core.jar
call ant
