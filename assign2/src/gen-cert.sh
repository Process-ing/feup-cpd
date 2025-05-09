#!/bin/sh

print_usage() {
    echo "Usage: $0"
}

update_property() {
    local file=$1
    local key=$2
    local value=$3

    if grep -q "^${key}=" "$file"; then
        # Replace the existing value
        sed -i "s|^${key}=.*|${key}=${value}|" "$file"
    else
        # Add the key-value pair if it doesn't exist
        echo "${key}=${value}" >> "$file"
    fi
}

if [ "$#" -ne 0 ]; then
    print_usage
    exit 1
fi

SERVER_PASSWORD=$(openssl rand -base64 32)
CLIENT_PASSWORD=$(openssl rand -base64 32)

BUILD_DIR="$(dirname "$0")/build"
KEYSTORE="server.keystore"
TRUSTSTORE="client.truststore"
CERT="server.crt"
SERVER_CONFIG_FILE="server.properties"
CLIENT_CONFIG_FILE="client.properties"

mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR" || exit 1
rm -f "$KEYSTORE" "$TRUSTSTORE" "$CERT"

# Generate the server keystore
keytool -genkeypair -alias server -keyalg RSA -keystore "$KEYSTORE" -storepass "$SERVER_PASSWORD" -keypass "$SERVER_PASSWORD" -dname 'CN=cpd, OU=fe, O=up, L=Porto, S=Porto, C=Portugal' || exit 1

# Export the server certificate
keytool -export -alias server -keystore "$KEYSTORE" -file "$CERT" -storepass "$SERVER_PASSWORD" || exit 1

# Create the client truststore and import the server certificate
keytool -import -alias server -file "$CERT" -keystore "$TRUSTSTORE" -storepass "$CLIENT_PASSWORD" -noprompt || exit 1

# Write passwords to files
update_property "$SERVER_CONFIG_FILE" "keystore" "$KEYSTORE"
update_property "$SERVER_CONFIG_FILE" "keystore-password" "$SERVER_PASSWORD"
update_property "$CLIENT_CONFIG_FILE" "truststore" "$TRUSTSTORE"
update_property "$CLIENT_CONFIG_FILE" "truststore-password" "$CLIENT_PASSWORD"

echo
echo "Keystore and truststore generated successfully."
echo "Server Keystore: $KEYSTORE (Password: $SERVER_PASSWORD)"
echo "Client Truststore: $TRUSTSTORE (Password: $CLIENT_PASSWORD)"