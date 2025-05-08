#!/bin/sh

# Check if two password arguments are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <server_password> <client_password>"
    exit 1
fi

SERVER_PASSWORD=$1
CLIENT_PASSWORD=$2
KEYSTORE="server.keystore"
TRUSTSTORE="client.truststore"
CERT="server.crt"

cd "$(dirname "$0")/build" || exit 1

# Generate the server keystore
keytool -genkeypair -alias server -keyalg RSA -keystore "$KEYSTORE" -storepass "$SERVER_PASSWORD" -keypass "$SERVER_PASSWORD" -dname "CN=cpd, OU=fe, O=up, L=Porto, S=Porto, C=Portugal"

# Export the server certificate
keytool -export -alias server -keystore "$KEYSTORE" -file "$CERT" -storepass "$SERVER_PASSWORD"

# Create the client truststore and import the server certificate
keytool -import -alias server -file "$CERT" -keystore "$TRUSTSTORE" -storepass "$CLIENT_PASSWORD" -noprompt

echo
echo "Keystore and truststore generated successfully."
echo "Server Keystore: $KEYSTORE (Password: $SERVER_PASSWORD)"
echo "Client Truststore: $TRUSTSTORE (Password: $CLIENT_PASSWORD)"