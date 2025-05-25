# CPD Assignment 2

## Prerequisites

In order to run this project, please ensure the following requirements are met:
- `Java SE` (version 21+)
- `Docker` (optional: for running Ollama, preferably with `docker-compose`)

## Setup

### Starting Ollama

In order to run the project with the AI rooms, it is needed to run a local instance of Ollama. If you have `docker-compose`, this can be done by running the following command while in the project home folder (where this file is located):

```bash
docker compose up -d
```

Alternatively, you can run Ollama manually by executing the following command:

```bash
sudo docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama14 ollama/ollama
```

**Note:** during the **first run**, it is also necessary to run this command right after, inside the Ollama prompt:

```bash
docker compose exec ollama ollama pull llama3
```

### Configurations and Certificates

By default, the server does not come configured or with the certificates generated. Therefore, the commands below have to be executed (at least on the first run) to complete the setup.

```bash
./gen-config.sh <server-host> <server-port>  # Generate server+client configurations
./gen-cert.sh                                # Generate SSL certificates
```

After this, the certificates and configurations will be saved in the `src/build` directory, in the files `server.properties` and `client.properties`. If you want to change the configurations, e.g. to use a different server host or port, you must edit these files directly.

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

#### Running Bots

This project also includes a few bots (clients that login and enter a room automatically, and send messages without user feedback). Inside the `build/` directory, they can be launched using the following commands:

```bash
java client.bot.FixedIntervalBot <room>   # Sends messages with a fixed period (default 1 seconds)
java client.bot.RandomIntervalBot <room>  # Sends messages with a variable period (max period of 2 seconds by default)
java client.bot.UnstableBot <room>        # On every message send, has a low probability of disconnecting, retrying right after
```

## Usage

Once the client has started, users can start interacting by sending **commands** with the following structure:

```bash
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
/exit                               # Exit the application
```

This is just an example of some of the commands that can be used. For more details on the protocol used, please check the reference below. Note that, not all commands require information from the server, therefore, not all commands have a protocol equivalent. Similarly, some protocol messages are used for control and don't have any associated commands.

> See also: [Protocol Overview](../doc/protocol.md)

## Notes

For a better client experience, we recommend executing the project in a terminal that support ASCII escape sequences, as this were used on the CLI for better visuals and clearer distinction of the messages.

In addition to this, we recommend using the `rlwrap` tool to allow for command history, message restoration and overall better input experience, since no libraries could be used in this project. Once installed, it can be launched with the following command:

```bash
rlwrap java client.Client <session-suffix>
```
