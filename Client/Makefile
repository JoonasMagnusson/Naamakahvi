CLASSPATH=".:httpcomponents-client-4.2/lib/commons-logging-1.1.1.jar:httpcomponents-client-4.2/lib/fluent-hc-4.2.jar:httpcomponents-client-4.2/lib/httpclient-4.2.jar:httpcomponents-client-4.2/lib/httpclient-cache-4.2.jar:httpcomponents-client-4.2/lib/httpcore-4.2.jar:httpcomponents-client-4.2/lib/httpmime-4.2.jar:httpcomponents-core-4.2.1/lib/httpcore-4.2.1.jar:httpcomponents-core-4.2.1/lib/httpcore-ab-4.2.1.jar:httpcomponents-core-4.2.1/lib/httpcore-nio-4.2.1.jar"

client: naamakahvi/client/Client.java
	javac -cp $(CLASSPATH) naamakahvi/client/Client.java

run-client: client
	java -cp $(CLASSPATH) naamakahvi/client/Client

