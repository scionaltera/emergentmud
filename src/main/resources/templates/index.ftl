<#--
EmergentMUD - A modern MUD with a procedurally generated world.
Copyright (C) 2016-2017 Peter Keeler

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
<#assign links = [ "/css/index.css" ]>
<#include "header.inc.ftl">
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
            <a class="btn btn-social btn-${network.id} margin-bottom" role="button" href="<@spring.url '/social/${network.id}'/>">
                <i class="fa fa-${network.id}"></i> Sign in with ${network.displayName}
            </a>
    </#list>
        </div>
    </div>
    <div class="row">
        <div class="col-md-10 col-md-offset-1 description">
            <p>EmergentMUD is a free, retro text based game that runs in your browser using HTML5 and Websockets. It's a modern MUD with an old school feel. Just like most other MUDs back in the 90s, you play a character in a medieval fantasy setting. The MUD has a rich set of ways you can interact both with the environment and with other players. The modern part is that the entire game is procedurally generated and fully interactive. It's gigantic, and much of it has never been visited by any human players yet. You will be able to get immersed in this world in ways that you never could on traditional MUDs.</p>
            <p>The codebase has been in active development for about a year now. If you are interested in seeing what the current state of the MUD looks like, please drop in and take a look around. While I make my best effort to keep the MUD up and running, there are no guarantees at this point that it will be available or fast, that anything will work properly, or that it will be fun to play. It is likely to be rebooted often and the database may be wiped at any time.</p>
            <p>If you're a programmer, please check out the MUD's source code and see what you think. If you're a gamer, I'd love to hear your feedback. I talk a lot about the development process on the blog and you can track my work on Bitbucket and Taiga to see what features are currently being worked on. <strong>Thanks for visiting!</strong></p>
            <p>Our current release is called <em>Playable World</em>. It is focused on all the most basic necessities of a MUD.</p>
            <ul>
                <li>Application framework and architecture</li>
                <li>Production deployment with Docker</li>
                <li>Administrative commands and tools</li>
                <li>Basic room generation</li>
                <li>Communication, emotes and movement commands</li>
                <li>Help files</li>
            </ul>
            <p>Our next release is called <em>The Environment</em>. It will focus on developing the natural world inside the MUD.</p>
            <ul>
                <li>Rivers, lakes and oceans</li>
                <li>Calendars</li>
                <li>Day/night cycle</li>
                <li>Seasons</li>
                <li>Weather</li>
                <li>Plants</li>
                <li>Animals</li>
            </ul>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 text-center margin-bottom">
            [ <a href="<@spring.url '/public/commands'/>">Commands</a> ]
            [ <a href="<@spring.url '/public/emotes'/>">Emotes</a> ]
        </div>
    </div>
    <div class="row">
        <#include "external-links.inc.ftl">
    </div>
</div>

<#assign addThis = 1>
<#include "scripts.inc.ftl">
</body>
</html>