JAR import
==========

This module is build against a closed source JAR.  A local maven repository is
created in `m2-local` with the JAR.  This JAR must be imported into this
repository (which is the `m2-local` directory) with the following command:

    mvn \
	  org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file \
	  -Dfile=m2-local/org/jasig/cas/cas-client-ent/2.14.5/cas-client-ent-2.14.5.jar \
	  -DgroupId=org.jasig.cas \
	  -DartifactId=cas-client-ent \
	  -Dversion=2.14.5 \
	  -Dpackaging=jar \
	  -DlocalRepositoryPath=m2-local

`-Dfile` should indicate the path to the JAR concerned and
`-DlocalRepositoryPath` is the path where the JAR should be imported (the path
to the local maven repository).

[1] http://maven.apache.org/plugins/maven-install-plugin/examples/specific-local-repo.html
