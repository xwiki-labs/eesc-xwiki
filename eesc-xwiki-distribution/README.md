Deployement
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
First, edit the `xwiki/WEB-INF/web.xml` file and replace the string
`PUT_HERE_THE_CAS_AUTHENTICATION_SERVER_URL` with the URL of the authentication
server (for example, https://demo.monent.fr/connexion/).

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

Don't forget to create the database named `ecr` and the username `my_login` with the
password `my_password`.

To be able to connect to MySQL, you should also add the MySQL driver to the list
of JAR in XWiki.  Download the driver at
http://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.32/mysql-connector-java-5.1.32.jar
and put it in `xwiki/WEB-INF/lib`.

# Tomcat configuration
Tomcat configuration is based on a default version available on [Apache
Tomcat](http://mirror.switch.ch/mirror/apache/dist/tomcat/tomcat-7/v7.0.53/bin/apache-tomcat-7.0.53.tar.gz).
`tomcat` is the directory where the file is unzipped.

## Link to XWiki
Inside `tomcat/webapps/` lie all the applications that will be served through
Tomcat.  Put a link called `ecr` to the XWiki directory.

    ln -s <path_to_root_ecr>/xwiki ecr

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
we also need to install the ECR service on it.  We will start the XWiki instance
and then use the REST service to upload the application.

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
if the script is executed almost instantly, the effective stop takes a few
seconds.

You can now replace the `web.xml` with the `web.cas.xml` to have the default
configuration with the authentication system.

- Tomcat
- Directory into Tomcat webapp
- Specific script to start Tomcat
  - data directory inside this script
- MySQL
- Config Apache avec AJP
- Change the environment.permanentDirectory in xwiki.properties
- Change in hibernate.cfg.xml
- Change xwiki.db in xwiki.cfg
- Change in jmxDomain in WEB-INF/cache/infinyspan/config.xml
