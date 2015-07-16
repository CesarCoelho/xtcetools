#!/bin/sh

cd `dirname $0`

JAVA_HOME=/usr/java/default
JAVA_LIBS=${JAVA_HOME}/jre/lib
COTS_JARS=${JAVA_LIBS}/jfxrt.jar:${JAVA_LIBS}/javaws.jar:${JAVA_LIBS}/deploy.jar:${JAVA_LIBS}/plugin.jar

XTCETOOLS_JAR=XTCETools.jar

${JAVA_HOME}/bin/java -Xms4G \
                      -Dfile.encoding=UTF-8 \
                      -cp ${COTS_JARS}:${XTCETOOLS_JAR} \
                      org.omg.space.xtce.ui.XTCEViewer

