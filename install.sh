#!/bin/bash
 
for line in $(cat $1)
do
    echo $line
    ssh -i ~/.ssh/id_rsa ubc_EECE411_S9@$line java -version
    #echo "Starting install on $line"
    #scp -i ~/.ssh/id_rsa jre-7u51.tar.gz ubc_EECE411_S9@$line:jre.tar.gz &
    #scp -i ~/.ssh/id_rsa .bash_profile ubc_EECE411_S9@$line:.bash_profile &
    #echo "Extracting the tarball"  
    #ssh -i ~/.ssh/id_rsa ubc_EECE411_S9@$line tar xzf jre.tar.gz &
done
