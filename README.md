eXo Blog Extension
===================

A simple multi-user blog extension for eXo Platform social intranet. 
It's a showcase of eXo content management capabilities. 

You can discuss or find help on [Blog Extension Community Space](http://community.exoplatform.com/portal/g/:spaces:blog_extension/)

Target : eXo Platform 4.0

How to Install
==============

You can install the extension very easily using the [add-ons manager](http://blog.exoplatform.com/en/2013/12/20/boost-platform-new-add-ons-manager)

Using the Add-ons Manager  (recommanded)
--------------------------

    ./addon.sh --install exo-blog-extension:1.1.0


If you don't have it already, you can quickly install the add-ons manager :

    cd platform-4.0.5/
    curl -L http://sourceforge.net/projects/exo/files/Addons/Add-ons%20Manager/addons-manager-1.0.0-alpha-2.zip/download -o addons-manager-1.0.0-alpha-2.zip
    unzip addons-manager-1.0.0-alpha-2.zip
    


Using the Extensions Installer
----------------------------

If you don't want to install the add-ons manager on your eXo, you can still rely on the build-in extension installer script that ships with eXo Platform 4.0.

Download exo-blog-extension-1.1.0.zip  from [Sourceforge](https://sourceforge.net/projects/exo/files/Addons/Blog%20Extension/) and unzip in your extensions folder :

    cd platform-4.0.5/extensions
    curl -L http://sourceforge.net/projects/exo/files/Addons/Blog%20Extension/exo-blog-extension-1.1.0.zip/download -o exo-blog-extension-1.1.0.zip 
    unzip exo-blog-extension-1.1.0.zip
    cd ..
    unzip exo-blog-extension-1.1.0.zip
    ./extension.sh -i exo-blog-extension-1.1.0




How to Build
============

Step 1 :  Build 
----------------

Prerequisite : install [Maven 3](http://maven.apache.org/download.html).

    git clone https://github.com/exo-addons/blog-extension.git
    cd exoblog

then build the project with maven :

    mvn clean install

or

    ./deploy.sh -m


Step 2 : Deploy 
---------------

Prerequisite : install [eXo Platform 4.0 Tomcat bundle](http://www.exoplatform.com/company/en/download-exo-platform) and rename it `tomcat/`

    ./deploy.sh -u


or

    cp config/target/blog-config*.jar tomcat/lib
    cp webapp/target/blog.war tomcat/webapp

Step 3 : Run
------------

Use eXo start script :

    cd tomcat 
    ./start_eXo.sh

or

    ./deploy.sh -r

Now, point your browser to [http://localhost:8080/portal/intranet/blog](http://localhost:8080/portal/intranet/blog) and login with `john/gtn`

deploy.sh help
===============

    ./deploy.sh -h

    Script usage :
      ./deploy.sh [-r] [-c] [-t TOMCAT_HOME_DIRECTORY_PATH] [-d PLF_DATA_DIRECTORY_PATH] [-w PLF_WEBAPP_DIRECTORY_PATH]
         -r              start or restart the Tomcat server     (default: do nothing)
         -m              build / rebuild with maven             (default: do nothing)
         -u              update the project binaries in Tomcat  (default: do nothing)
         -c              cleanup the Tomcat Platform            (default: do nothing)
                           (data + logs + temp)
         -t <path>       the Platform Tomcat directory          (default: tomcat)
         -d <path>       the Platform data directory            (default: <TOMCAT-DIR>/gatein/data)

      You can combine the r / m / u / c switches together to chain several actions.
      For exemple you can rebuild the project, update the binaries and restart the tomcat with the following command :
         ./deploy.sh -m -u -r

