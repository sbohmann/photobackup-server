#!/usr/bin/env bash

echo password:
read -s password
echo repeat:
read -s repeated_password

echo server ip address:
read server_ip_address

if [[ "$password" != "$repeated_password" ]]
then
  echo "Passwords do not match"
  exit 1
fi

if [[ "$1" == "root" ]]
then
  rm config/*.p12 config/*.pem
  
  keytool -genkeypair -alias root -dname "cn=photobackup-server-root" -validity 10000 -keyalg RSA -keysize 2048 -ext bc:c -storetype PKCS12 -keystore config/root.p12 -storepass "$password"
  keytool -genkeypair -alias ca -dname "cn=photobackup-server-ca" -validity 10000 -keyalg RSA -keysize 2048 -ext bc:c -storetype PKCS12 -keystore config/ca.p12 -storepass "$password"
  
  # generate root certificate
  
  keytool -exportcert -rfc -storetype PKCS12 -keystore config/root.p12 -alias root -storepass "$password" > config/root.pem
  
  # generate a certificate for ca signed by root (root -> ca)
  
  keytool -storetype PKCS12 -keystore config/ca.p12 --certreq -alias ca -storepass "$password" | keytool -storetype PKCS12 -keystore config/root.p12 -gencert -alias root -ext bc=0 -ext san=dns:ca -rfc -storepass "$password" > config/ca.pem
  
  # import ca cert chain into ca.p12
  
  keytool -storetype PKCS12 -keystore config/ca.p12 -importcert -trustcacerts -noprompt -alias root -file config/root.pem -storepass "$password"
  keytool -storetype PKCS12 -keystore config/ca.p12 -importcert -alias ca -file config/ca.pem -storepass "$password"
  
  # import ca cert chain into config/keystore.p12
  
  keytool -storetype PKCS12 -keystore config/keystore.p12 -storepass "$password" -importcert -trustcacerts -noprompt -alias root -file config/root.pem
  keytool -storetype PKCS12 -keystore config/keystore.p12 -storepass "$password" -importcert -alias ca -file config/ca.pem
else
  keytool -delete -alias server -keystore config/keystore.p12 -storepass "$password"
fi
  
# generate private keys (for server)
  
keytool -genkeypair -alias server -dname cn="photobackup-server-local.yeoman.at" -ext san="ip:${server_ip_address}" -validity 10000 -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore config/keystore.p12 -storepass "$password"

# generate a certificate for server signed by ca (root -> ca -> server)
  
keytool -storetype PKCS12 -keystore config/keystore.p12 -storepass "$password" -certreq -alias server -ext san="ip:${server_ip_address}" | keytool -storetype PKCS12 -keystore config/ca.p12 -storepass "$password" -gencert -alias ca -ext ku:c=dig,keyEnc -ext san="ip:${server_ip_address}" -ext eku=sa,ca -rfc > config/server.pem

# import server cert into config/keystore.p12

keytool -storetype PKCS12 -keystore config/keystore.p12 -storepass "$password" -importcert -alias server -file config/server.pem
