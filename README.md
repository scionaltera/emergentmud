[![AGPL License](https://img.shields.io/badge/license-AGPL-green.svg "AGPL License")](http://www.gnu.org/licenses/agpl.txt)
[![Website](https://img.shields.io/website/https/emergentmud.com.svg)](https://emergentmud.com)
[![Codeship Status for scionaltera/emergentmud](https://img.shields.io/codeship/14384cd0-e8c6-0133-1109-0a601490f276/master.svg?maxAge=2592000)](https://codeship.com/projects/147332)
[![Dev Blog](https://img.shields.io/badge/devblog-active-green.svg "Dev Blog")](http://blog.emergentmud.com)
[![Project Tracker](https://img.shields.io/badge/tracker-taiga-green.svg "Dev Blog")](https://tree.taiga.io/project/scionaltera-emergentmud/)  
[![Docker Automated buil](https://img.shields.io/docker/automated/scionaltera/emergentmud.svg?maxAge=2592000)](https://hub.docker.com/r/scionaltera/emergentmud/)
[![Docker Pulls](https://img.shields.io/docker/pulls/scionaltera/emergentmud.svg?maxAge=2592000)](https://hub.docker.com/r/scionaltera/emergentmud/)
[![Docker Stars](https://img.shields.io/docker/stars/scionaltera/emergentmud.svg?maxAge=2592000)](https://hub.docker.com/r/scionaltera/emergentmud/)

# Vision
EmergentMUD is a text based game that runs in your browser using HTML5 and Websockets. It will be a modern game with an old school feel. Just like most other MUDs back in the 90s, you will play a character in a medieval fantasy setting where the world has a rich range of features and ways you can interact both with the environment and with other players. The modern part is that the entire world is procedurally generated and fully interactive. It's gigantic, and most of it has never been visited by any human players (or the developers) yet. You will be able to get immersed in this world in ways that you never could on traditional MUDs.

# Current State
EmergentMUD is a brand new project and the groundwork is still in its very first stages of development. This is the actively running server for demonstration purposes. If you are interested in seeing what the current state of the MUD looks like, please drop in and take a look around. Please be aware that the version is fixed at v0.1.0-SNAPSHOT for a reason: while I make my best effort to keep it up and running, there are no guarantees at this point that it will be available or fast, that anything will work properly, or that it will be fun to play. It is likely to be rebooted often and the database may be wiped at any time.

That being said, I do welcome visitors and would love for people to drop in and look around.

The following major features are already complete:

* Players can authenticate through their Facebook or Google accounts
* Players can create multiple essences (characters) within their accounts
* Players can log into the game and see a familiar SMAUG-like text UI
* LOOK and directional commands work
* Players can talk to each other using TELL, SAY, SHOUT and GOSSIP
* Rooms are computed on the fly as you walk around the world, making it very, very large: Room coordinates can range from `-2^63` to `2^63-1` in 3 dimensions

A few of the things on the [roadmap to v1.0](https://bitbucket.org/scionaltera/emergentmud/wiki/Product%20Roadmap):

* Characteristics for each room computed using layers of [OpenSimplex](https://gist.github.com/KdotJPG/b1270127455a94ac5d19) noise maps
* High availability (run multiple instances behind a load balancer, clustered datastores, real STOMP message broker)
* Detailed terrain types
* Plants and foliage
* Sparsely populated Room entities to overlay changes made by players onto the world map
* Time, day/night cycle, seasons
* Weather
* Temperature adjustments based on elevation and latitude
* Objects, containers and wear locations
* Dynamically generated room descriptions
* Character attributes (gender, species, race, height, weight, etc.)
* Socials (smile, nod, wink, etc.)
* Skills
* NPCs and animals
* Much, much more!

# Running Locally
## Required Tools
If you want to run a copy of EmergentMUD locally from the Docker image, you need to have [Docker](https://www.docker.com/products/docker) installed. Make sure that when you install Docker on your machine you also get [Docker Compose](https://docs.docker.com/compose/). In most cases the installer will install both tools for you at once.

These instructions assume that you are somewhat familiar with using the command line or terminal for typing commands in on your machine, that you have a working network connection, a programmer's text editor such as `vim` or `Sublime Text`, a web browser, and that you are comfortable with installing software.

## Required Configuration
Make an empty directory somewhere on your computer, where you want the MUD's config files to live. You'll need to create two new text files in that directory: `secrets.env` and `docker-compose.yaml`.

The first file is called `secrets.env`. The file should look like [secrets.env.sample](https://bitbucket.org/scionaltera/emergentmud/src) in the git repository except that you need to fill in all the missing values. To do that, you'll need to go to [Facebook](https://developers.facebook.com) and [Google](https://console.developers.google.com) to register your application and get their IDs and secrets for OAuth. The details of how to do this are out of scope for this document, but both sites have pretty good help for how to get started. Please remember that the OAuth secrets are *secret*, and should be treated as such.

The second file is called `docker-compose.yaml`. The file should look something like this:

```yaml
version: "2"
services:
  redis:
    image: redis
    ports:
     - "6379:6379"
  mongo:
    image: mongo
    ports:
     - "27017"
  emergentmud:
    image: scionaltera/emergentmud:latest
    ports:
     - "8080:8080"
     - "5005:5005"
    links:
     - redis
     - mongo
    env_file: secrets.env
```

## Starting the Server
The `docker-compose.yaml` file tells Docker Compose which services to start up, which ports they use, and how they link together. `secrets.env` contains environment variables that allow your particular copy of EmergentMUD to integrate with Facebook and Google and let people log in to the server using their social media accounts.

The command to get everything started is to run `docker-compose up`. You should see it download and extract all the Docker images, then the logs as the services start up. When they're done booting, point your browser at http://localhost:8080 (or the IP for your docker VM if you're using boot2docker) and you should see the front page for EmergentMUD. If you have configured everything correctly for OAuth in Facebook and Google, you should be able to log in and play.

# Local Development
## Terminology
EmergentMUD uses slightly different terminology from other MUDs, mostly because the word "Character" is already used by `java.lang.Character` and making your own class `Character` seems to really confuse most Java IDEs. So, EmergentMUD uses three main classes when talking about players: `Account`, `Essence`, and `Entity`.

![emergentmud-models.png](https://bitbucket.org/repo/LBXMzk/images/3867473848-emergentmud-models.png)

Your `Account` is what is linked to your social network, such as Facebook or Google. It stores information about who the human being is that is connected to EmergentMUD.

Each account can have multiple `Essence` instances associated with it. An `Essence` is to an `Entity` as a class is to an instance in Java. It's basically your character sheet.

The `Entity` is the body that goes out into the world. If the `Essence` is the character sheet, the `Entity` is the character herself. If the `Entity` is killed while exploring the world, the `Essence` remains and we can use it to create another one.

## Required Tools
The code is built using the Gradle wrapper. The project structure follows the typical Maven structure and is designed to be easy to set up locally for testing using Docker and Docker Compose. You will need the following tools installed and properly configured to run the site locally:

1. Java 8 or later.
1. Docker and Docker Compose.

## Required Configuration
The configurable settings for integrating with Facebook and Google are stored in a file called `secrets.env`. There is a sample of this file included in the git repository which you will need to rename and fill out the information for your integrations. In order to get the API tokens you will need to register your application on the developer sites for [Facebook](https://developers.facebook.com) and [Google](https://console.developers.google.com). Please remember that the OAuth secrets are *secret* and should be treated as such. Make sure not to check them in to your version control or share them with other people.

## Compiling the Project
To start up the site after you set up the env file, you just need to run `./gradlew clean buildDocker` from the command line. That will build the project and the Docker image. To start everything up after it's done building, type `docker-compose up`.

## Running the Project
The first time you run `docker-compose up` will take some time because it needs to download the Redis and MongoDB containers. After they are downloaded and unpacked, you should see the logs for all of the services starting up. Once everything has started up, point your browser at http://localhost:8080 (or your docker VM if you're using boot2docker) and you should see the front page. If you have configured everything correctly in Facebook and Google, you should be able to log in and play.

## Considerations for Production Deployments
### Secrets
I recommend registering both a production and a test app in Facebook and Google. Things like allowed redirect URIs and analytics can get difficult if you don't keep dev separate from production.

Facebook has built in functionality for creating a "test" version of your application in their console, while for Google you just need to generate two sets of credentials for your application. Put one `secrets.env` on your production box and the other in your dev environment, and you're all set.

### Data Stores
The Docker containers for the Redis and MongoDB data stores are sufficient for development but are **not configured for security or performance** at all. They are just the default containers off the web. On my machine they both [complain about Transparent Huge Pages being enabled](https://www.digitalocean.com/company/blog/transparent-huge-pages-and-alternative-memory-allocators/) and will most likely gobble up large amounts of memory and eventually crash if you just leave them running long term.

If you plan to run EmergentMUD for real, it would be a good idea to carefully configure your Redis and MongoDB instances according to the best practices spelled out in their documentation. You should also consider running them as clusters so they are highly available. How to do all of this is well out of scope for this document, but there are lots of resources on the internet that will tell you how to do it if you are curious.

My plan for a production deployment of EmergentMUD is to build customized Docker containers for the data stores, based on the ones in use today but with an extra layer that applies the configuration changes that I need for deployment. That way I can still run my production cluster of services using `docker-compose` and I could even run my properly tuned data stores during development. Today, however, I'm just running the off-the-shelf images until they become a problem.

### Reverse Proxy
It is important to understand that OAuth2 is **not secure without HTTPS**. That means that for any production deployment you *must* purchase an **SSL certificate** and configure a SSL enabled reverse proxy in front of your copy of EmergentMUD.

![Service Architecture](https://bitbucket.org/repo/LBXMzk/images/96926331-em-docker-compose.png)

What I have done with [EmergentMUD's dev server](https://emergentmud.com) is to add an [nginx reverse proxy Docker container](https://hub.docker.com/r/jwilder/nginx-proxy/) to my `docker-compose.yaml` and configure it with my SSL certificate. Incoming connections are automatically upgraded to SSL at nginx, and through some sort of wicked sorcery the requests are dynamically routed to the MUD's container. The MUD doesn't need to know anything about SSL, and everybody is happy and safe.

# Contributing
If you would like to contribute to the project, please feel free to submit a pull request. For the best chance of success getting your pull request merged, please do the following few things:

1. Check the tickets on [Taiga](https://tree.taiga.io/project/scionaltera-emergentmud/) to see if what you want to do is there already. If it isn't, check the [Roadmap](https://bitbucket.org/scionaltera/emergentmud/wiki/Product%20Roadmap) to see if it's something I'm planning to work on later. Discuss your proposed change with the dev team before doing the work. I can either assign the ticket to you or create a new ticket as necessary. If what you want to do isn't in line with the vision for EmergentMUD, you are still more than welcome to fork it and develop the code on your own.
1. Go ahead and fork a copy of the project.
1. Match the coding style of existing code as best as possible.
1. Make sure the code you are contributing is covered by unit tests.
1. Document your work, or include updates to the existing documentation as necessary.
1. Include the license header in any new files that you create. Please note that contributing your code means you will give up ownership of it in the legal sense. I will of course still recognize and appreciate your contribution but I will not be able to pull your code back out if you change your mind later.
1. Finally, submit your pull request from your fork back to the project. I will work with you to get it reviewed and merged.

# Contact
So far the dev team consists of just me. I am not looking for partners or MUD staff at this time but I welcome discussion about the future direction of EmergentMUD and I welcome pull requests and forks. I'd love to know if you have used any of my code for your own project. The best motivation for me to continue work on the project is to know that other people are interested and making use of it.

The best ways to contact me about this project are to message me on [Telegram](http://telegram.me/scionaltera) or just hop onto the [MUD](https://www.emergentmud.com) and see if I'm hanging around there. If you'd prefer to email you can make an educated guess (it's something pretty obvious at emergentmud dot com) or send me a message through Bitbucket and we can go from there.

# License
EmergentMUD is licensed under the [GNU AFFERO GENERAL PUBLIC LICENSE](http://www.gnu.org/licenses/agpl.txt). This license ensures that EmergentMUD and all derivative works will always be free open source for everyone to enjoy, distribute and modify. The Affero license stipulates that you must be able to provide a copy of your source code to **anyone who plays your game**.