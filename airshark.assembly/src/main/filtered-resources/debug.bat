rd /s /q configuration\org.eclipse.osgi
del hello.areas.log
java -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Xdebug -jar org.eclipse.osgi-${org.eclipse.osgi}.jar -console