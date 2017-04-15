[![AGPL License](https://img.shields.io/badge/license-AGPL-green.svg?maxAge=2592000 "AGPL License")](http://www.gnu.org/licenses/agpl.txt)
[![Website](https://img.shields.io/website/https/emergentmud.com.svg?label=game "Game Website")](https://emergentmud.com)
[![Dev Blog](https://img.shields.io/website/https/emergentmud.blogspot.com.svg?label=blog&maxAge=2592000 "Development Blog")](https://emergentmud.blogspot.com)
[![Tracker](https://img.shields.io/website/https/tree.taiga.io.svg?label=tracker&maxAge=2592000 "Project Tracker")](https://tree.taiga.io/projects/scionaltera-emergentmud)  
[![Codeship Status for scionaltera/emergentmud](https://img.shields.io/codeship/14384cd0-e8c6-0133-1109-0a601490f276/master.svg?maxAge=2592000)](https://codeship.com/projects/147332)
[![Docker Automated buil](https://img.shields.io/docker/automated/scionaltera/emergentmud.svg?maxAge=2592000)](https://hub.docker.com/r/scionaltera/emergentmud/)
[![Docker Pulls](https://img.shields.io/docker/pulls/scionaltera/emergentmud.svg?maxAge=2592000)](https://hub.docker.com/r/scionaltera/emergentmud/)
[![Docker Stars](https://img.shields.io/docker/stars/scionaltera/emergentmud.svg?maxAge=2592000)](https://hub.docker.com/r/scionaltera/emergentmud/)

# Vision
EmergentMUD is a free, text based "Multi-User Domain" that you play in your browser. It's a modern MUD with an old school retro feel. Just like most other MUDs back in the 90s you play a character in a medieval fantasy setting. The modern aspect is that the entire game world is procedurally generated on the fly and is fully interactive. All parts of the world from the species of plants and animals, societies of sentient creatures, geography, weather, and even quests are created on demand as players explore. All the different game systems interact with one another to create fun and unexpected *emergent behavior*. This world is alive.

Help an NPC gather resources to build his house and he'll build his house - not just continue asking everyone he sees for resources. Steal the gold from the King's vault and he won't be able to fund the war he's waging - having a direct effect on international politics. Burn down a village and maybe it will be rebuilt - but maybe it won't. Help someone in need and make an ally you can rely on later. Start a business and hire NPCs to work for you. Head out in a direction that isn't on the map yet and it will be created as you begin to walk through it - complete with new plants, animals, NPCs, religions, cultures and discoveries that the world has never seen before. Everything you do in this world has a real effect. You won't see any quest vendors and you won't experience the same "content" that everyone else has already devoured before you. You can forge your own path, create your own destiny, and leave your own mark upon the world in the process.

# Current State
The code has been in active development for about a year now, and still going strong although there is still a very long way to go. Please [drop in](https://emergentmud.com) and take a look around, and pardon the dust. Let me know what you think. New things are being added on a regular basis.

If you're a programmer, check out the MUD's source code and see what you think. If you're a gamer, I'd love to hear your feedback. I talk a lot about the development process on the [blog](http://emergentmud.blogspot.com) and you can track my work on [Bitbucket](https://bitbucket.org/scionaltera/emergentmud) and [Taiga](https://tree.taiga.io/project/scionaltera-emergentmud/) to see what features are currently being worked on. **Thanks for visiting!**

Our current release is called `Playable World`. It is focused on all the most basic necessities of a MUD.

* Application framework and architecture
* Production deployment with Docker
* Administrative commands and tools
* Basic room generation
* Communication, emotes and movement commands
* Help files

Our next release is called `People`. It will focus on developing the natural world inside the MUD.

* Character attributes (gender, strength, etc.)
* Animals and NPCs
* Plants and Trees
* Minerals, Metals and Other Natural Resources
* Items and Equipment
* Bodies of Water

# Running Locally
## Required Tools
If you want to run a copy of EmergentMUD locally from the public Docker image, you need to have [Docker](https://www.docker.com/products/docker) installed. Make sure that when you install Docker on your machine you also get [Docker Compose](https://docs.docker.com/compose/). In most cases the installer will install both tools for you at once.

These instructions assume that you are somewhat familiar with using the command line or terminal for typing commands in on your machine, that you have a working network connection, a programmer's text editor such as `vim` or `Sublime Text`, a web browser, and that you are comfortable with installing software.

## Required Configuration
Make an empty directory somewhere on your computer, where you want the MUD's files to live. You'll need to create two new text files in that directory: `secrets.env` and `docker-compose.yaml`.

The first file is called `secrets.env`. The file should look like [secrets.env.sample](https://bitbucket.org/scionaltera/emergentmud/src) in the git repository except that you need to fill in all the missing values. To do that, you'll need to go to [Facebook](https://developers.facebook.com) and [Google](https://console.developers.google.com) to register your application and get their IDs and secrets for OAuth. The details of how to do this are out of scope for this document, but both sites have pretty good help for how to get started. Please remember that the OAuth secrets are *secret*, and should be treated as such. Don't share them with other people and don't check them into your version control.

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

This file tells Docker that you want a [Redis](https://redis.io) instance, a [MongoDB](https://www.mongodb.com) instance, and an EmergentMUD instance. It describes which ports to open on each, and how the networking should link together between them.

## Starting the Server
The command to get everything started is `docker-compose up`. You should see it download and extract all the Docker images, then the logs will scroll by as the services start up. When they're done booting, point your browser at http://localhost:8080 (or the IP for your docker VM if you're using boot2docker) and you should see the front page for EmergentMUD. If you have configured everything correctly for OAuth in Facebook and Google, you should be able to log in and play.

# Local Development
## Terminology
EmergentMUD uses slightly different terminology from other MUDs, mostly because the word "Character" is already used by `java.lang.Character` and making your own `Character` class seems to really confuse most Java IDEs. So, EmergentMUD uses three main classes when talking about players: `Account`, `Essence`, and `Entity`.

![emergentmud-models.png](https://bitbucket.org/repo/LBXMzk/images/3867473848-emergentmud-models.png)

Your `Account` is what is linked to your social network, such as Facebook or Google. It stores information about who the human being is that is connected to EmergentMUD.

Each account can have multiple `Essence` instances associated with it. An `Essence` is to an `Entity` as a class is to an instance in Java. It's basically your character sheet.

The `Entity` is the body that goes out into the world. If the `Essence` is the character sheet, the `Entity` is the character herself. If the `Entity` is killed while exploring the world, the `Essence` remains and we can use it to create another one.

## Required Tools
The code is built using the [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html). The project structure follows the typical [Maven structure](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) and is designed to be easy to set up locally for testing using Docker and Docker Compose. You will need the following tools installed and properly configured to run the site locally:

1. Java 8 or later.
1. Docker and Docker Compose.

## Required Configuration
The configurable settings for integrating with Facebook and Google are stored in a file called `secrets.env`. There is a sample of this file included in the git repository which you will need to rename and fill out the information for your integrations. In order to get the API tokens you will need to register your application on the developer sites for [Facebook](https://developers.facebook.com) and [Google](https://console.developers.google.com). Please remember that the OAuth secrets are *secret* and should be treated as such. Make sure not to check them in to your version control or share them with other people.

## Compiling the Project
To start up the site after you set up the env file, you just need to run `./gradlew clean buildDocker` from the command line. That will build the project, run the tests, and finally build the Docker image. To start everything up after it's done building, type `docker-compose up`.

## Running the Project
The first time you run `docker-compose up` will take some time because it needs to download the Redis and MongoDB containers. After they are downloaded and unpacked, you should see the logs for all of the services starting up. Once everything has started up, point your browser at `http://localhost:8080` (or your docker VM if you're using boot2docker) and you should see the front page. If you have configured everything correctly in Facebook and Google, you should be able to log in and play.

## Developing New Code
If you want to run your own copy of EmergentMUD there are probably a thousand things you want to customize. Your best bet is going to be to fork the project on Bitbucket, write your new code and build your own Docker containers from your fork.

## Debugging
The Docker image produced by the Gradle build is already set up to enable the remote debugging port on port 5005. I use IDEA for development, which makes it very easy to [set up a remote debug profile](http://stackoverflow.com/questions/21114066/attach-intellij-idea-debugger-to-a-running-java-process). Once you're attached, you can set breakpoints and inspect variables to your heart's content.

## Considerations for Production Deployments
### Secrets
I recommend registering both a production and a test app in Facebook and Google. Things like allowed redirect URIs and analytics can get difficult if you don't keep dev separate from production.

Facebook has built in functionality for creating a "test" version of your application in their console, while for Google you just need to generate two sets of credentials for your application. Put one `secrets.env` on your production box and the other in your dev environment, and you're all set.

In Google you can simply create two sets of credentials for the same application.

### Data Stores
The Docker containers for the Redis and MongoDB data stores are sufficient for development but are **not configured for security or performance** at all. They are just the default containers off the web. On my machine they both [complain about Transparent Huge Pages being enabled](https://www.digitalocean.com/company/blog/transparent-huge-pages-and-alternative-memory-allocators/) and will most likely gobble up large amounts of memory and eventually crash if you just leave them running long term. So far though, that's exactly what I've been doing and it has worked out well enough. I expect that will change when there is a regular player base.

If you plan to run EmergentMUD for real, it would be a good idea to carefully configure your Redis and MongoDB instances according to the best practices spelled out in their documentation. You should also consider running them as clusters so they are highly available. How to do all of this is well out of scope for this document, but there are lots of resources on the internet that will tell you how to do it if you are curious.

My plan for a production deployment of EmergentMUD is to build customized Docker containers for the data stores, based on the ones in use today but with an extra layer that applies the configuration changes that I need for deployment. That way I can still run my production cluster of services using `docker-compose`, [Docker Swarm](https://docs.docker.com/engine/swarm/), [Kubernetes](https://kubernetes.io/) or something similar. Today, however, I'm just running the off-the-shelf images until they become a problem.

### Reverse Proxy
It is important to understand that OAuth2 is **not secure without HTTPS**. That means that for any production deployment to be secure you *must* purchase an **SSL certificate** and configure a SSL enabled reverse proxy in front of your copy of EmergentMUD. If you do not do this you **will** leak peoples' authentication secrets for their Google and Facebook accounts, and that is **really bad**.

![Service Architecture](https://bitbucket.org/repo/LBXMzk/images/96926331-em-docker-compose.png)

What I have done with [EmergentMUD's dev server](https://emergentmud.com) is to add an [nginx reverse proxy Docker container](https://hub.docker.com/r/jwilder/nginx-proxy/) to my `docker-compose.yaml` and configure it with my SSL certificate. Incoming connections are automatically upgraded to SSL at nginx, and through some sort of wicked sorcery the requests are dynamically routed to the MUD's container. The MUD doesn't need to know anything about SSL, and everybody is happy and safe. The data stores aren't accessible from the internet, which is great for their security. If you are interested in seeing a redacted copy of the `docker-compose.yaml` I use for EmergentMUD, just let me know. 

# Contributing
If you would like to contribute to the project, please feel free to submit a pull request. For the best chance of success getting your pull request merged, please do the following few things:

1. Check the tickets on [Taiga](https://tree.taiga.io/project/scionaltera-emergentmud/) to see if what you want to do is there already. If it isn't, check the [Roadmap](https://bitbucket.org/scionaltera/emergentmud/wiki/Product%20Roadmap) to see if it's something I'm planning to work on later. Discuss your proposed change with the dev team before doing the work. I can either assign the ticket to you or create a new ticket as necessary. If what you want to do isn't in line with the vision for EmergentMUD, you are still more than welcome to fork it and develop the code on your own.
1. Fork a copy of the project.
1. Match the coding style of existing code as best as possible.
1. Make sure the code you are contributing is covered by unit tests.
1. Document your work, or include updates to the existing documentation as necessary.
1. Include the license header in any new files that you create. Please note that contributing your code means you will give up ownership of it in the legal sense. I will of course still recognize and appreciate your contribution but I will not be able to pull your code back out if you change your mind later.
1. Finally, submit your pull request from your fork back to the project. I will work with you to get it reviewed and merged.

# Contact
So far the dev team consists of just me, Scion. I am not looking for partners or MUD staff at this time but I welcome discussion about the future direction of EmergentMUD and I welcome pull requests and forks. I'd love to know if you have used any of my code for your own project. The best motivation for me to continue work on the project is to know that other people are interested and making use of it.

The best ways to contact me about this project are to message me on [Telegram](http://telegram.me/scionaltera) or comments on the [blog](https://emergentmud.blogspot.com). You could also just hop onto the [MUD](https://www.emergentmud.com) and see if I'm hanging around there. If you'd prefer to email you can make an educated guess (it's something pretty obvious at emergentmud dot com) or send me a message through Bitbucket and we can go from there.

# License
EmergentMUD is licensed under the [GNU AFFERO GENERAL PUBLIC LICENSE](http://www.gnu.org/licenses/agpl.txt). This license ensures that EmergentMUD and all derivative works will always be free open source for everyone to enjoy, distribute and modify. Most importantly, the Affero license stipulates that you must be able to provide a copy of your source code to **anyone who plays your game**.