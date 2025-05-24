# CPD Assignment 2

## Prerequisites

In order to run this project, please ensure the following requirements are met:
- `Java SE` (version 21+)
- `Docker` (optional: for running Ollama)

## Setup

### Starting Ollama

In order to run the project with the AI rooms, it is needed to run a local instance of Ollama. This can be done by running the following command while at the home folder:

```bash
docker compose up -d
```

Note: during the **first run**, it is also necessary to run this command after the previous one:

```bash
docker compose exec ollama ollama pull llama3
```

### Configurations and Certificates

By default, the server does not come configured or with the certificates generated. Therefore, the commands below have to be executed (at least on the first run) to complete the setup.

```bash
./gen-config.sh <server-host> <server-port>
./gen-cert.sh
```

After this, the certificates and configurations will be saved in the `src/build` directory.

### Building and Running the Project

Once the setup is ready, we can build the project with the following commands:

```bash
./build.sh
cd build
```

#### Launching the Server

Once inside the build directory, we can launch the server with:

```bash
java server.Server
```

#### Connecting Clients

Similarly to the previous command, clients can be connected with:

```bash
java client.Client <session-suffix>
```

Note: a suffix can optionally be passed to allow running multiple sessions on the same machine

## Usage

Once the client has started, users can start interacting by sending **commands** with the following structure:

```
/<command> <arg1> <arg2> ...
```

For a list of available commands at any stage of the program, use **`/help`**. Below is an example of some commands:

```bash
/help                               # Print available commands
/register JonhDoe p@ssword123       # Registering a new account
/enter MyRoom                       # Enter a given room
Hello there!                        # This sends a message
/leave                              # Leave the current room
/list-rooms                         # See available rooms
/info                               # Get information about session
/logout                             # Logout from application
```

This is just an example of some of the commands that can be used. For more details on the protocol used, please check the reference below. Note that, not all commands require information from the server, therefore, not all commands have a protocol equivalent. Similarly, some protocol messages are used for control and don't have any associated commands.

> See also: [Protocol Overview](../doc/protocol.md)

## Notes

For a better client experience, we recommend executing the project in a terminal that support ASCII escape sequences, as this were used on the CLI for better visuals and clearer distinction of the mesages.

In addition to this, we recommend using the `rlwrap` tool to allow for command history, message restoration and overall better input experience, since no libraries could be used in this project. Once installed, it can be launched with the following command:

```bash
rlwrap java client.Client
```
