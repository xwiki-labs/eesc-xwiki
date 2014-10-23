Deployment
===========

There are different services that can be deployed like ECR or PRJ.  The
deployment is similar for both.  Here is some explanations to deploy ECR service
but same steps can be used for PRJ deployment.

The ECR service is aboout collaborative writing.  It rely on a XWiki instance
served through Tomcat.

# XWiki deployment
In your root directory (for example `/appli/ecr`), create a `xwiki` directory.  In this directory, you can unzip
the WAR file `eesc-xwiki-web-ecr-hsqldb-1.0-SNAPSHOT.war`.

There is now a few configuration steps that needs to be done.

## `xwiki/WEB-INF/web.xml`
You may want to create a version of the `web.xml` file that bypass the
authentication system.  To do this, create 2 copies of ` web.xml` under
`web.cas.xml` and `web.nocas.xml`.  In `web.nocas.xml`, comment these lines.

    <filter-mapping>
      <filter-name>CAS Single Sign Out Filter</filter-name>
      <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
      <filter-name>CAS Authentication Filter</filter-name>
      <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
      <filter-name>Cas Validation Filter</filter-name>
      <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
      <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
      <url-pattern>/*</url-pattern>
    </filter-mapping>

## `xwiki/WEB-INF/xwiki.properties`
First, configure the data directory of XWiki.  Look for the
`environment.permanentDirectory` variable and set it to a already existing
directory like the following example.

    environment.permanentDirectory=/appli/ecr/xwiki.data/

Don't forget to uncomment the line.

Then, the EESC service for XWiki needs to be configured too.  Add the following
lines at the end of the file.

    eesc.service=default
    eesc.webservice.url=https://demo.monent.fr/interop

It will configure the default implementation of the EESC service (there is also
a `test` implementation that can work without external service).  The external
service is configured through the second variable.

## `xwiki/WEB-INF/xwiki.cfg`
In this file, you'll need to configure the name of the database used.

    xwiki.db=ecr

Don't forget to uncomment the line too.

## `xwiki/WEB-INF/hibernate.cfg.xml`
We also need to configure the database system connection.  You should comment
the HSQL part and uncomment the MySQL part.  You should have something like
that.

    <property name="connection.url">jdbc:mysql://localhost/ecr</property>
	<property name="connection.username">my_login</property>
	<property name="connection.password">my_password</property>
	<property name="connection.driver_class">com.mysql.jdbc.Driver</property>
	<property name="dialect">org.hibernate.dialect.MySQL5InnoDBDialect</property>
	<property name="dbcp.ps.maxActive">20</property>
	<mapping resource="xwiki.hbm.xml"/>
	<mapping resource="feeds.hbm.xml"/>
	<mapping resource="activitystream.hbm.xml"/>
	<mapping resource="instance.hbm.xml"/>

Add also the following properties for the encoding (CAREFUL, it seems that you must put
these properties before any `<mapping>` element).

	<property name="connection.useUnicode">true</property>
	<property name="connection.characterEncoding">UTF-8</property>

Don't forget to create the database named `ecr` and the username `my_login` with the
password `my_password`.  To create a database called `my_database`, you can use the following
command.

	create database my_database default character set utf8 collate utf8_bin

The databases for the service ECR is called `ecr` and for the service PRJ is
`prj`.

To be able to connect to MySQL, you should also add the MySQL driver to the list
of JAR in XWiki.  Download the driver at
http://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.32/mysql-connector-java-5.1.32.jar
and put it in `xwiki/WEB-INF/lib`.

## `xwiki/WEB-INF/cache/infinispan/config.xml`
You should give a uniq ID (for each XWiki instance on the machine) to the
`jmxDomain`.  Look for the `<globalJmxStatistics>` element and change its
`jmxDomain` attribute from `org.xwiki.infinispan` to something like
`org.xwiki.infinispan.ecr`.

# Tomcat configuration
Tomcat configuration is based on a default version available on [Apache
Tomcat](http://mirror.switch.ch/mirror/apache/dist/tomcat/tomcat-7/v7.0.53/bin/apache-tomcat-7.0.53.tar.gz).
`tomcat` is the directory where the file is unzipped.

## Link to XWiki
Inside `tomcat/webapps/` lie all the applications that will be served through
Tomcat.  Put a link called `ecr` to the XWiki directory.

	ln -s <path_to_root_ecr>/xwiki ecr

For example, for the ECR service, it would be

	ln -s /appli/ecr/xwiki ecr

## Customize the startup script
In `tomcat/bin/` directory, the `startup.sh` needs to be customized in order
to be able to run XWiki.  We will allocate more memory to run the JVM (needed
for XWiki).

Create a copy of `startup.sh` into `startup_xwiki.sh` and add the following line
inside.

    export CATALINA_OPTS="-Xmx1024M -XX:MaxPermSize=256m"

## Configure Tomcat
The port on which Tomcat services are served should be configured.  Edit the
file `tomcat/conf/server.xml` and find all the lines that describe a
`<Connector>` element.  There should be 2 by default: one for HTTP on the port
8080 and one for AJP on 8009.  You may change the port at this level but keep in
mind the value for AJP since it's needed for Apache configuration (see next
step).

The `<Server>` element have also a port.  You may change it if you run multiple
instances of Tomcat.

You should also add `URIEncoding="UTF-8"` to all the `<Connector>` elements.

## Working with Apache
If the server is managed with Apache, you'll use the AJP protocol to create a
communication between Apache and Tomcat.  Put the following line in your Apache
configuration file.

    ProxyPass /ecr/ ajp://localhost:8009/ecr/

Don't forget to reload the configuration.

    service apache2 reload

# Initialize the wiki
The unzipped WAR contains everything to have a basic XWiki instance.  However,
we also need to install the ECR service on it (i.e. populate the database).  We
will start the XWiki instance and then use the REST service to upload the
application.

First of all, deactivate the CAS by copying the `web.nocas.xml` into `web.xml`
(see the directory `xwiki/WEB-INF/`).

You can now start the Tomcat instance by running the
`tomcat/bin/startup_xwiki.sh` script.  Look at the log in
`tomcat/logs/catalina.out`.

Then we'll use `curl` to upload the XAR file `

    export XAR_FILE="eesc-xwiki-ui-ecr-all.xar"
    curl \
      --verbose \
      --request POST \
      --user superadmin:system \
      --header "Content-type: application/octet-stream" \
      --data-binary "@$XAR_FILE" \
      "http://localhost/ecr/rest/wikis/xwiki?backup=true&history=RESET"

If you're on a HTTPS connection, you may want to add the `--insecure` to get rid
of certificates or add the certificate file with `--cacert`.

You can now stop the Tomcat instance with `tomcat/bin/shutdown.sh` script.  Even
if the script is executed almost instantly, the effective shutdown of the JVM
takes a few seconds: a immediate restart WILL not work.

You can now replace the `web.xml` with the `web.cas.xml` to have the default
configuration with the authentication system and then restart the Tomcat.
