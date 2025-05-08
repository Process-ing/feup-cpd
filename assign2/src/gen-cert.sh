#!/bin/sh

if [ "$#" -ne 0 ]; then
    echo "Usage: $0"
    exit 1
fi

SERVER_PASSWORD=$(openssl rand -base64 32)
CLIENT_PASSWORD=$(openssl rand -base64 32)

BUILD_DIR="$(dirname "$0")/build"
KEYSTORE="server.keystore"
TRUSTSTORE="client.truststore"
CERT="server.crt"
SERVER_PASSWORD_FILE="server-pass.txt"
CLIENT_PASSWORD_FILE="client-pass.txt"

rm -f "$KEYSTORE" "$TRUSTSTORE" "$CERT" server-pass.txt client-pass.txt
cd "$BUILD_DIR" || exit 1

# Generate the server keystore
keytool -genkeypair -alias server -keyalg RSA -keystore "$KEYSTORE" -storepass "$SERVER_PASSWORD" -keypass "$SERVER_PASSWORD" -dname "CN=cpd, OU=fe, O=up, L=Porto, S=Porto, C=Portugal" || exit 1

# Export the server certificate
keytool -export -alias server -keystore "$KEYSTORE" -file "$CERT" -storepass "$SERVER_PASSWORD" || exit 1

# Create the client truststore and import the server certificate
keytool -import -alias server -file "$CERT" -keystore "$TRUSTSTORE" -storepass "$CLIENT_PASSWORD" -noprompt || exit 1

# Write passwords to files
echo -n "$SERVER_PASSWORD" > "$SERVER_PASSWORD_FILE"
echo -n "$CLIENT_PASSWORD" > "$CLIENT_PASSWORD_FILE"

echo
echo "Keystore and truststore generated successfully."
echo "Server Keystore: $KEYSTORE (Password: $SERVER_PASSWORD)"
echo "Client Truststore: $TRUSTSTORE (Password: $CLIENT_PASSWORD)"