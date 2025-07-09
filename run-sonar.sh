source .env

if [ -z "$SONAR_TOKEN" ]; then
  echo "Error: missing SONAR_TOKEN in environment"
  exit 1
fi

./mvnw clean sonar:sonar -Dsonar.host.url=http://localhost:9000