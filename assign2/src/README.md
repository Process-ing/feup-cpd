# CPD Assignment 2

## Usage

<!-- TODO(Process-ing): Improve this -->
Start Ollama:
```
docker compose up -d
docker compose exec ollama ollama pull llama3
```

```
./gen-config.sh <server-host> <server-port>
./gen-cert.sh
./build.sh
cd build
java server.Server
java client.Client
```
