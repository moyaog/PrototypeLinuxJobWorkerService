# PrototypeLinuxJobWorkerService

## Build Instructions

### Make HardCodes.java
After cloning the repository, it is necessary to finish the HardCodes class. The HardCodes class contains hardcoded values for implementation ease. You will need to provide
- HOST: the IP address of the Server
- PORT: the Server port number
- SERVER_KEY_LOC: the name of the Server's keystore
- CLIENT_KEY_LOC: the name of the Client's keystore
- FAKE_KEY_LOC: the name of a keystore that contains a key that cannot be authenticated by Server (this is for testing purposes only)
- SERVER_PASSWORD: the password for the Server's keystore
- CLIENT_PASSWORD: the password fot the Client's keystore

