JAR import
==========

This module is build against a closed source JAR.  A local maven repository is
created in `m2-local` with the JAR.  This JAR must be imported into this
repository (which is the `m2-local` directory) with the following command:

    mvn \
      org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file \
      -Dfile=m2-local/org/xwiki/platform/xwiki-platform-oldcore/6.2/xwiki-platform-oldcore-6.2.jar \
      -DgroupId=org.xwiki.platform \
      -DartifactId=xwiki-platform-oldcore \
      -Dversion=6.2 \
      -Dpackaging=jar \
      -Dclassifier=object-policy \
      -DlocalRepositoryPath=m2-local

`-Dfile` should indicate the path to the JAR concerned and
`-DlocalRepositoryPath` is the path where the JAR should be imported (the path
to the local maven repository).

[1] http://maven.apache.org/plugins/maven-install-plugin/examples/specific-local-repo.html
