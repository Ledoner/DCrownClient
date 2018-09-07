@echo off
echo Setting JAVA_HOME
set JAVA_HOME=C:\Users\RJ\Data\Development\tools\Java\JDK
echo setting PATH
set PATH=C:\Users\RJ\Data\Development\tools\Java\JDK\bin;%PATH%
echo Display java version
echo Creation release %VERSION%
java -version
java -Xmx512m -classpath ../datacrow-server/lib/hsqldb/hsqldb.jar org.hsqldb.util.DatabaseManager %1 %2 %3 %4 %5 %6 %7 %8 %9
pause