#!/bin/bash

USER="cliftonc"
GROUP="users"

echo "This script will install Masiva in /opt/masiva (you must run this as ROOT), do you want to continue? [y|n]"
read vb

if [ "$vb" = 'n' ] || [ "$vb" = 'N' ]; then
        echo "Exiting."
        exit 0
elif [ -z "$vb" ] || [ "$vb" = 'y' ] || [ "$vb" = 'Y' ]; then

       echo "* Installing Masiva..." 
       #Add repositories

	mkdir tmp

	echo "Install Sun Java SDK 1.6? [y|n]"
	read vb
	if [ -z "$vb" ] || [ "$vb" = 'y' ] || [ "$vb" = 'Y' ]; then
		echo "sun-java6-bin shared/accepted-sun-dlj-v1-1 boolean true" | debconf-set-selections
		apt-get install -y python-software-properties
		add-apt-repository "deb http://archive.canonical.com/ maverick partner"
		add-apt-repository "deb http://archive.canonical.com/ lucid partner"
		apt-get update	
	       apt-get install -y sun-java6-jdk
	fi
	
	echo "Install Groovy 1.7.5? [y|n]"
	read vb
	if [ -z "$vb" ] || [ "$vb" = 'y' ] || [ "$vb" = 'Y' ]; then
       	echo "* Downloading Groovy 1.7.2 ..."	 
		wget -O tmp/groovy-binary-1.7.5.zip http://dist.groovy.codehaus.org/distributions/groovy-binary-1.7.5.zip
		unzip tmp/groovy-binary-1.7.5.zip -d ../lib
		rm tmp/groovy-binary-1.7.5.zip
		ln -s ../lib/groovy-1.7.5 ../lib/groovy
	fi

	echo "Install Grails 1.3.5? [y|n]"
	read vb
	if [ -z "$vb" ] || [ "$vb" = 'y' ] || [ "$vb" = 'Y' ]; then
	        echo "* Downloading Grails 1.3.5 ..."
		wget -O tmp/grails-1.3.5.zip http://dist.codehaus.org/grails/grails-1.3.5.zip
		unzip tmp/grails-1.3.5.zip -d ../lib
		rm tmp/grails-1.3.5.zip
		ln -s ../lib/grails-1.3.5 ../lib/grails
	fi

	echo "Install MongoDB? [y|n]"
	read vb
	if [ -z "$vb" ] || [ "$vb" = 'y' ] || [ "$vb" = 'Y' ]; then

		echo "* Installing MongoDB [Stable] ..."
	       echo "deb http://downloads.mongodb.org/distros/ubuntu 10.4 10gen" >> /etc/apt/sources.list
	       apt-key add mongodb-repository.key
	       apt-get update > /dev/null
	       apt-get -y install mongodb-stable
	fi

	echo "Install Erlang and RabbitMQ? [y|n]"
	read vb
	if [ -z "$vb" ] || [ "$vb" = 'y' ] || [ "$vb" = 'Y' ]; then
		 echo "* Installing Erlang ..."
		apt-get install -y erlang
	
		echo "* Installing RabbitMQ"
		wget -O tmp/rabbitmq-server_2.2.0-1_all.deb http://www.rabbitmq.com/releases/rabbitmq-server/v2.2.0/rabbitmq-server_2.2.0-1_all.deb	
		dpkg -i tmp/rabbitmq-server_2.2.0-1_all.deb
		rm tmp/rabbitmq-server_2.2.0-1_all.deb
	fi

	echo "* Copying shell scripts ..."
	cp scripts/* ..

	echo "* Updating ownership and permissions ..."
	chown -R $USER /opt/masiva
	chgrp -R $GROUP /opt/masiva

       echo "All dependencies installed ..."
       echo "Install git, and check out the latest master branch into the apps folder."

fi

exit 0
