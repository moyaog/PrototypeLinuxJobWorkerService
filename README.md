# PrototypeLinuxJobWorkerService

## Clone Repository
To clone the repository, use the following commands
```bash
$ git clone https://github.com/moyaog/PrototypeLinuxJobWorkerService.git
$ cd PrototypeLinuxJobWorkerService
```

## Make HardCodes.java
After cloning the repository, it is necessary to finish the HardCodes class. The HardCodes class contains hardcoded values for implementation ease. You will need to provide
- HOST: the IP address of the Server
- PORT: the Server port number
- SERVER_KEY_LOC: the name of the Server's keystore
- CLIENT_KEY_LOC: the name of the Client's keystore
- FAKE_KEY_LOC: the name of a keystore that contains a key that cannot be authenticated by Server (this is for testing purposes only)
- SERVER_PASSWORD: the password for the Server's keystore
- CLIENT_PASSWORD: the password fot the Client's keystore

## Build Project
Build the project using the following commands
```bash
$ javac Constants.java HardCodes.java Request.java Response.java BuildJson.java ParseJson.java ParsedRequest.java ParsedResponse.java Credentials.java ErrorInfo.java ExecuteJobs.java Client.java Server.java
```

## Start Server

## Start Client
