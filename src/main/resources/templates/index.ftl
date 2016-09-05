<#--
EmergentMUD - A modern MUD with a procedurally generated world.
Copyright (C) 2016 BoneVM, LLC

This file is part of EmergentMUD.

EmergentMUD is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

EmergentMUD is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>EmergentMUD</title>
    <meta name="description" content=""/>
    <meta name="viewport" content="width=device-width"/>
    <base href="/"/>
    <link rel='stylesheet' type='text/css' href="<@spring.url 'https://fonts.googleapis.com/css?family=Inconsolata:400,700'/>">
    <link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/font-awesome/css/font-awesome.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/bootstrap-social.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/index.css'/>">
</head>
<body>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12 text-center heading">
            <span>EmergentMUD</span>
        </div>
    </div>
    <div class="row">
        <div class="col-md-4 col-md-offset-4 text-center">
    <#list networks as network>
            <a class="btn btn-social btn-${network.id} social-btn" role="button" href="<@spring.url '/social/${network.id}'/>">
                <i class="fa fa-${network.id}"></i> Sign in with ${network.displayName}
            </a>
    </#list>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 description">
            <p>EmergentMUD is a text based game that runs in your browser using HTML5 and Websockets. It will be a modern game with an old school feel. Just like most other MUDs back in the 90s, you will play a character in a medieval fantasy setting where the world has a rich range of features and ways you can interact both with the environment and with other players. The modern part is that the entire world is procedurally generated and fully interactive. It's gigantic, and most of it has never been visited by any human players (or the developers) yet. You will be able to get immersed in this world in ways that you never could on traditional MUDs.</p>
            <p>EmergentMUD is a brand new project and the groundwork is still in its very first stages of development. This is the actively running server for demonstration purposes. If you are interested in seeing what the current state of the MUD looks like, please drop in and take a look around. Please be aware that the version is fixed at v0.1.0-SNAPSHOT for a reason: while I make my best effort to keep it up and running, there are no guarantees at this point that it will be available or fast, that anything will work properly, or that it will be fun to play. It is likely to be rebooted often and the database may be wiped at any time.</p>
            <p>That being said, I do welcome visitors and would love for people to drop in and look around.</p>
            <p>-- Scion</p>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 text-center links">
            [ <a href="http://blog.emergentmud.com" target="_blank">Development Blog</a> ]
            [ <a href="https://bitbucket.org/scionaltera/emergentmud" target="_blank">Source Repository</a> ]
            [ <a href="https://tree.taiga.io/project/scionaltera-emergentmud/" target="_blank">Issue Tracker</a> ]
            [ <a href="https://hub.docker.com/r/scionaltera/emergentmud/" target="_blank">Docker Hub</a> ]
        </div>
    </div>
</div>

<script type="text/javascript" src="/webjars/jquery/jquery.min.js"></script>
<script type="text/javascript" src="/webjars/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>