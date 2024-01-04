# Movie Tier List Manager

## Overview

It is a client-server application designed to efficiently manage and visualize movie tier lists. The project is implemented in Java, featuring a multithreaded TCP server and a JavaFX-based GUI on the client side. The application leverages auto-localization for multiple languages and integrates with a PostgreSQL database using JDBC for seamless data storage and retrieval.

## Key Features

1. High-Load Processing: The system excels in handling high-load requests, capable of processing up to 10,000 insert requests per user in a single script efficiently.
2. Auto-Updates: Enjoy real-time visual updates as the system automatically refreshes the visualizer, providing users with the latest information.
3. Public and Private Lists: Users can create both public and private movie tier lists, allowing for a personalized and customizable experience.
4. Authentication System: The application supports sign-up and sign-in requests, ensuring secure access and personalized user experiences.

## Usage

### Server Setup:
1. Configure server settings in `server_new/server.scfg`, including the JDBC database URL, username, password, and extra XML collection storage.
2. Start PostgreSQL and execute `tables.sql` by entering `\i ./tables.sql`.
3. Compile the server side using `mvn install` in the `new_server` directory.
4. Run the server using `target/server.jar`.
### Client Setup:
1. Compile the client side using `mvn install` in the `client_ui` directory.
2. Run the client using `target/client.jar`.

## Demo
https://github.com/dorlneylon/prog-8/assets/65495639/88ad49ef-84b4-4f52-b680-dc137cba18b2

