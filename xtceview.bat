@ECHO OFF

cd /d %~dp0%

SET JAVA_HOME=C:\Program Files\Java\jdk1.7.0_72
SET JAVA_LIBS=%JAVA_HOME%\jre\lib

@ECHO ON

"%JAVA_HOME%\bin\java.exe" -Xms4G -Dfile.encoding=UTF-8 -cp "%JAVA_LIBS%\jfxrt.jar";"%JAVA_LIBS%\javaws.jar";"%JAVA_LIBS%deploy.jar";"%JAVA_LIBS%\plugin.jar";dist\XTCETools.jar;XTCETools.jar org.omg.space.xtce.ui.XTCEViewer
