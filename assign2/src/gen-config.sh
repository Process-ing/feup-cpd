#!/bin/sh

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <server-host> <server-port>"
    exit 1
fi

SERVER_HOST="$1"
SERVER_PORT="$2"
BUILD_DIR="$(dirname "$0")/build"
SERVER_CONFIG_FILE="server.properties"
CLIENT_CONFIG_FILE="client.properties"

mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR" || exit 1
mv -f "$SERVER_CONFIG_FILE" "$SERVER_CONFIG_FILE.old" 2>/dev/null
mv -f "$CLIENT_CONFIG_FILE" "$CLIENT_CONFIG_FILE.old" 2>/dev/null

touch "$SERVER_CONFIG_FILE" "$CLIENT_CONFIG_FILE"
cat <<EOF > "$SERVER_CONFIG_FILE"
port=$SERVER_PORT
EOF

cat <<EOF > "$CLIENT_CONFIG_FILE"
host=$SERVER_HOST
port=$SERVER_PORT
EOF

echo "Configuration files generated successfully."
