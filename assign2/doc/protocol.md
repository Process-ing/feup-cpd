# Protocol Specification

## Overview

The chat-protocol is ...

## Unit Format

```text
<command> [arg1 [arg2 ...]]
```

### Protocol Units

<!-- TODO: check this later -->

| Command       | Direction | Arguments                 | Purpose                                                    |
| ------------- | --------- | ------------------------- | ---------------------------------------------------------- |
| `login-token` | C → S     | `token`                   | Re-authenticate using previous session                     |
| `login`       | C → S     | `username` `password`     | Login                                                      |
| `register`    | C → S     | `username` `password`     | Register a new user                                        |
| `logout`      | C → S     |                           |                                                            |
| `enter`       | C → S     | `room`                    | Join (or create, if not exists) a chat room                |
| `leave`       | C → S     |                           | Exit current room                                          |
| `send`        | C → S     | `message`                 | Send message to current room                               |
| `recv`        | C ← S     | `id` `username` `message` |                                                            |
| `sync`        | C → S     | `lastId`                  |                                                            |
| `ok`          | C ← S     | `data`                    | Generic success (e.g., session token)                      |
| `err`         | C ← S     | `errorCode`               | Error response using `ProtocolErrorIdentifier` enumeration |
| `ping`        | C ↔ S     |                           | Keep-alive                                                 |
| `pong`        | C ↔ S     |                           | Response to `ping`                                         |

### Legend

| Direction | Flow             |
| --------- | ---------------- |
| **C → S** | Client to Server |
| **C ← S** | Server to Client |
| **C ↔ S** | Bidirectional    |
