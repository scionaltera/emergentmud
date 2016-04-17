# Description
EmergentMUD is a text based game that runs in your browser using HTML5 and Websockets. It's a modern game with an old school feel. Just like most other MUDs back in the 90s, you play a character in a medieval fantasy setting where the world has a rich range of features and ways you can interact both with the environment and with other players. The modern part is that the entire world is procedurally generated. It's gigantic, and most of it has never been visited by any human players (or the developers) yet. You can get immersed in this world in ways that you never could on traditional MUDs.

## Local Installation
The code is built using the Gradle wrapper. The project structure follows the typical Maven structure and is designed to be easy to set up locally for testing using Docker. You will need the following tools installed to run the site locally:

1. Java 8 or later.
1. Docker and Docker Compose.

The configurable settings for the components are stored in [component]-env.properties.sample files which you will need to rename to [component]-env.properties and fill out the information for your integration. The settings are generally for OAuth identification, database configuration, etc. and are required for the software to run.

To start up the site after you set up the properties files, you just need to run the build.sh script from the command line. It will build the projects, the Docker images, and start up the containers.

## Contributing
If you would like to contribute to the project, please feel free to submit a pull request. If you have any questions, just ask one of the committers and we'll be glad to help you out.

For the best chance of success getting your pull request approved, please try to do the following few things:

1. Match the coding style of existing code as best as possible.
1. Write unit tests against your new code.
1. Document your work, or include updates to existing documentation.
1. If you have questions or concerns, discuss them with one of the core committers before submitting your pull request.
1. Be open to feedback and criticism. It's not personal.

## License
EmergentMUD is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).