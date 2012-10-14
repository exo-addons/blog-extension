exoblog
=======

A simple blog extension for eXo Platform social intranet. 
It's a showcase of eXo content management capabilities. 

Getting Started
===============

Step 1 :  Build 
----------------

Prerequisite : install [Maven 3](http://maven.apache.org/download.html).

    git clone https://github.com/plamarque/exoblog.git
    cd exoblog
    mvn clean install

Step 2 : Deploy 
---------------

Prerequisite : install [eXo Platform 3.5 Tomcat bundle](http://www.exoplatform.com/company/en/download-exo-platform) and rename it `tomcat/`

    ./deploy.sh


or

    cp config/target/blog-config*.jar tomcat/lib
    cp webapp/target/blog.war tomcat/webapp

Step 3 : Run
------------

Use eXo start script :

    cd tomcat 
    ./start_eXo.sh


Now, point your browser to http://localhost:8080/portal/intranet/blog and login with `john/gtn`
