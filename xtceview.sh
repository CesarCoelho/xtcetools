#!/bin/sh

export JAVA_HOME=/usr/java/default
export JAVA_LIBS=${JAVA_HOME}/jre/lib

XTCETOOLS_JAR=XTCETools.jar

${JAVA_HOME}/bin/java -Xms4G -Dfile.encoding=UTF-8 -cp ${JAVA_LIBS}/jfxrt.jar:${JAVA_LIBS}/javaws.jar:${JAVA_LIBS}/deploy.jar:${JAVA_LIBS}/plugin.jar:${XTCETOOLS_JAR} org.omg.space.xtce.ui.XTCEViewer &
