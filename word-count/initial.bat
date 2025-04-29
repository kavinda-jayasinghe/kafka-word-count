@echo off



echo Starting Node 2345
start cmd /k "java -Dserver.port=8081 -Dnode.value=2345 -jar target/word-count-0.0.1-SNAPSHOT.jar"
timeout /T 2

echo Starting Node 3456
start cmd /k "java -Dserver.port=8082 -Dnode.value=3456 -jar target/word-count-0.0.1-SNAPSHOT.jar"
timeout /T 2

echo Starting Node 4567
start cmd /k "java -Dserver.port=8083 -Dnode.value=4567 -jar target/word-count-0.0.1-SNAPSHOT.jar"
timeout /T 2

echo Starting Node 4569
start cmd /k "java -Dserver.port=8084 -Dnode.value=4569 -jar target/word-count-0.0.1-SNAPSHOT.jar"
