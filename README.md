[![AGPL License](https://img.shields.io/badge/license-AGPL-green.svg "AGPL License")](http://www.gnu.org/licenses/agpl.txt)
![Project Version](https://img.shields.io/badge/version-0.1.0-brightgreen.svg "Project Version")
[ ![Codeship Status for scionaltera/emergentmud](https://codeship.com/projects/14384cd0-e8c6-0133-1109-0a601490f276/status?branch=master)](https://codeship.com/projects/147332)

# Vision
EmergentMUD is a text based game that runs in your browser using HTML5 and Websockets. It's a modern game with an old school feel. Just like most other MUDs back in the 90s, you play a character in a medieval fantasy setting where the world has a rich range of features and ways you can interact both with the environment and with other players. The modern part is that the entire world is procedurally generated. It's gigantic, and most of it has never been visited by any human players (or the developers) yet. You can get immersed in this world in ways that you never could on traditional MUDs.

# Current State
EmergentMUD is a brand new project and the groundwork is still being laid out. There is currently no actively running server to connect to and play because the project is still at the stage where the database needs to be reset frequently during the development process.

The following major features are already complete:

* Players can authenticate through their Facebook or Google accounts
* Players can create multiple essences (characters) within their accounts
* Players can log into the game and see a familiar SMAUG-like text UI
* SAY, LOOK and directional commands work
* Players can talk to each other using SAY
* Rooms are dynamically created in batches as you walk around the world, making it very, very large: Room coordinates can range from `-2^63` to `2^63-1` although the rooms themselves are not created until they are visited

A few of the things on the roadmap to v1.0:

* Room elevation using the [diamond square algorithm](https://en.wikipedia.org/wiki/Diamond-square_algorithm), water tables, soil drainage, and sea level to produce dynamic terrain types for each room
* Time, day/night cycle, seasons
* Weather
* Objects, containers and wear locations
* Plants and foliage
* Dynamically generated room descriptions
* Character attributes (gender, species, race, height, weight, etc.)
* Socials (smile, nod, wink, etc.)
* Skills
* NPCs and animals
* Much, much more!

# Terminology
EmergentMUD uses slightly different terminology from other MUDs, mostly because the word "Character" is already used by `java.lang.Character` and making your own class `Character` seems to really confuse most Java IDEs. So, EmergentMUD uses three main classes when talking about players: `Account`, `Essence`, and `Entity`.

![emergentmud-models.png](https://bitbucket.org/repo/LBXMzk/images/3867473848-emergentmud-models.png)

Your `Account` is what is linked to your social network, such as Facebook or Google. It stores information about who the human being is that is connected to EmergentMUD.

Each account can have multiple `Essence` instances associated with it. An `Essence` is to an `Entity` as a class is to an instance in Java. It's basically your character sheet.

The `Entity` is the body that goes out into the world. If the `Essence` is the character sheet, the `Entity` is the character herself. If the `Entity` is killed while exploring the world, the `Essence` remains and we can use it to create another one.

## Local Installation
The code is built using the Gradle wrapper. The project structure follows the typical Maven structure and is designed to be easy to set up locally for testing using Docker and Docker Compose. You will need the following tools installed to run the site locally:

1. Java 8 or later.
1. Docker and Docker Compose.

The configurable settings for integrating with Facebook and Google are stored in a file called `core-secrets.env`. There is a sample of this file included in the git repository which you will need to rename and fill out the information for your integrations. In order to get the API tokens you will need to register your application on the developer sites for Facebook and Google.

To start up the site after you set up the env file, you just need to run the build.sh script from the command line. It will build the project, the Docker images, and start up the containers.

For a production deployment it is recommended that you run your own carefully configured Redis and MongoDB servers to provide high availability and redundancy instead of relying on the Docker images. The Docker containers for the data stores are sufficient for development but are **not configured for security or performance** at all.

## Contributing
If you would like to contribute to the project, please feel free to submit a pull request. For the best chance of success getting your pull request merged, please do the following few things:

1. Discuss your proposed change with the dev team before doing the work.
1. Match the coding style of existing code as best as possible.
1. Make sure the code you are contributing is covered by unit tests.
1. Document your work, or include updates to the existing documentation.
1. Include the license header in any new files that you create.
1. Be sure to include a version bump in your pull request. EmergentMUD is versioned following [Semantic Versioning](http://semver.org).

## License
EmergentMUD is licensed under the [GNU AFFERO GENERAL PUBLIC LICENSE](http://www.gnu.org/licenses/agpl.txt). This license ensures that EmergentMUD and all derivative works will always be free open source for everyone to enjoy, distribute and modify. The Affero license stipulates that you must be able to provide a copy of your source code to **anyone who plays your game**.