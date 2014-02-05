#!/bin/bash

apt-get update
apt-get upgrade -y
apt-get install apache2 -y

apt-get install default-jre -y
apt-get install default-jdk -y
apt-get install scala -y
apt-get install unzip -y

wget http://downloads.typesafe.com/play/2.2.0/play-2.2.0.zip
unzip play-2.2.0.zip

mv play-2.2.0 /opt
ln -s /opt/play-2.2.0 /opt/play
ln -s /opt/play/play /usr/local/bin/play

git clone https://github.com/wadeanderson7/CS462.git

play start


