#!/bin/bash

cp tomcat_jar/QnA.war tomcat/webapps/

#Run tomcat in foreground
tomcat/bin/catalina.sh run

#tomcat/bin/catalina.sh stop

#tomcat/bin/catalina.sh start

#/bin/bash

#while true; do sleep 1; done