Projet EESC
=============

[![XWiki labs logo](https://raw.githubusercontent.com/xwiki-labs/xwiki-labs-logo/master/projects/xwikilabs/xlabs-project.png "XWiki labs")](https://labs.xwiki.com/xwiki/bin/view/Main/WebHome)

# Installation
You can clone the git repository from
[Github](https://github.com/xwiki-labs/eesc-xwiki).  Don't forget to also
initialize submodules

	git clone https://github.com/xwiki-labs/eesc-xwiki.git
	cd eesc-xwiki
	git submodule init
	git submodule update
	mvn clean install

# Deploy
To deploy, you can use the `eesc-deploy` script that will help you to deploy the
platform.  Look at the `eesc-deploy.conf` file to see what configuration
information you can change.  You can also look into the submodule
`eesc-xwiki-distribution` for a `README.md` file.
