exoblog
=======

A simple blog extension for eXo Platform social intranet. 
It's a showcase of eXo content management capabilities. 


How to build 
------------

Install [Maven 3](http://maven.apache.org/download.html).

    git clone https://github.com/plamarque/exoblog.git
    cd exoblog
    mvn clean install

How to run 
----------

* Install [eXo Platform 3.5 Tomcat bundle](http://www.exoplatform.com/company/en/download-exo-platform) and rename it `tomcat/`
    ./deploy.sh
or
    cp config/target/blog-config*.jar tomcat/lib
    cp webapp/target/blog.war tomcat/webapp
* Start Tomcat
    cd tomcat 
    ./start_eXo.sh
* Point your browser to http://localhost:8080/portal/intranet/blog
* Login with `john/gtn`
* Enjoy your brand new blog