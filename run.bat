@echo off
set MS_VERSION=164
set MS_SUBVERSION=0
set WZ_XML_PATH=C:\Users\elfenlied\Desktop\wz\v%MS_VERSION%\
@title �����ŃT�[�o�[ v%MS_VERSION%.%MS_SUBVERSION%
set CLASSPATH=.;dist\*
java -server -Dnet.sf.odinms.wzpath=%WZ_XML_PATH% -Djavax.net.ssl.keyStore=filename.keystore -Djavax.net.ssl.keyStorePassword=passwd -Djavax.net.ssl.trustStore=filename.keystore -Djavax.net.ssl.trustStorePassword=passwd server.Start %MS_VERSION% %MS_SUBVERSION%
pause
