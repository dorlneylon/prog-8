# prog-8
## Movie Tier List Manager.
### Usage
1. Set `server_new/server.scfg` settings: DB URL for JDBC, username and password, extra XML collection storage.
2. Create DB tables as shown in `tables.sql`.
3. Use `mvn install` to compile the server side.
4. Run `target/server_new.jar`.
5. Use `mvn install` to compile the client side.
6. Run the `target/client.jar`.
### Functionality
1. Efficiently processing high-load requests (up to 10000 insert requests per user in a script).
2. Auto-updates for visualizer.
3. Public and private lists for each user.
4. Sign up/in requests.
