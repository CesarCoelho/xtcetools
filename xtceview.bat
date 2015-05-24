@ECHO ON

SET JAVA_HOME=C:\Program Files\Java\jdk1.7.0_72
SET JAVA_LIBS=%JAVA_HOME%\jre\lib

"%JAVA_HOME%\bin\java.exe" -Dfile.encoding=UTF-8 -cp "%JAVA_LIBS%\jfxrt.jar";"%JAVA_LIBS%\javaws.jar";"%JAVA_LIBS%deploy.jar";"%JAVA_LIBS%\plugin.jar";C:\GitRepositories\XTCETools\dist\XTCETools.jar org.omg.space.xtce.ui.XTCEViewer
