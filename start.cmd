@echo off
java -Xms512M -Xmx2048M -jar target/ElasticsearchDemo-0.0.1-SNAPSHOT.jar --spring.profile.active=prod --logging.config=config\logback.xml
echo started application

