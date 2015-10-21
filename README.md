# titilink-framework
框架

# 工作计划
- auth + passthrough + safefilter <5/17-5/20>

- angularjs + bootstrap3 <5/21-5/23>

- thrid software

- keytool -genkey -dname "CN=huadan-service-01,OU=IT,O=Huawei,L=Xi'an,ST=Shanxi,C=CN" -alias silvan_server -validity 3650 -keyalg RSA -keysize 2048 -keypass huadan@szx666 -storepass huadan@szx666 -keystore server.keystore

- keytool -certreq -alias silvan_server -sigalg SHA256withRSA -file silvan_server.csr -keypass huadan@szx666 -storepass huadan@szx666 -keystore server.keystore 

- keytool -import -alias silvan_server -file -keypass huadan@szx666 -storepass huadan@szx666 silvan_server.csr -keystore server.keystore 
