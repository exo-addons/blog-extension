#!/bin/bash

# reset embedded db to make sure to reload our configs
DATADIR="tomcat/gatein/data"
if [ -d "$DATADIR" ] ; then 
  rm -R $DATADIR/*
  echo "cleaned $DATADIR" 
fi




# ensure that our war will be reloaded
BLOGDIR="tomcat/webapps/blog"
if [ -d "$BLOGDIR" ] ; then 
    rm -R $BLOGDIR
    echo "removed $BLOGDIR"
fi

# deploy extension
cp -v config/target/blog-config-*.jar tomcat/lib
cp -v webapp/target/blog.war tomcat/webapps



