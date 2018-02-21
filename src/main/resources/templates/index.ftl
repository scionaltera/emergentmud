<#--
EmergentMUD - A modern MUD with a procedurally generated world.
Copyright (C) 2016-2018 Peter Keeler

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
            <h1>The Explorer's Dream</h1>
            <p>EmergentMUD is a free, open source, text based "Multi-User Domain" that you play in your browser. It's a modern MUD with an old school retro feel. Just like most other MUDs back in the 90s you play a character in a medieval fantasy setting. The modern aspect is that the entire game world is procedurally generated on the fly and is fully interactive. All parts of the world from the species of plants and animals, societies of sentient creatures, geography, weather, and even quests are created on demand as players explore. All the different game systems interact with one another to create fun and unexpected <em>emergent behavior</em>. This world is alive.</p>
            <p>Help an NPC gather resources to build his house and he'll build his house - not just continue asking everyone he sees for resources. Steal the gold from the King's vault and he won't be able to fund the war he's waging - having a direct effect on international politics. Burn down a village and maybe it will be rebuilt - but maybe it won't. Help someone in need and make an ally you can rely on later. Start a business and hire NPCs to work for you. Head out in a direction that isn't on the map yet and it will be created as you begin to walk through it - complete with new plants, animals, NPCs, religions, cultures and discoveries that the world has never seen before. Everything you do in this world has a real effect. You won't see any quest vendors and you won't experience the same "content" that everyone else has already devoured before you. You can forge your own path, create your own destiny, and leave your own mark upon the world in the process.</p>

            <h2>So that's the elevator pitch.</h2>
            <p>The code has been in active development for about two years now and still going strong, although there is still a very long way to go. Please drop in and take a look around. New things are being added on a <a href="https://github.com/scionaltera/emergentmud/wiki/Product-Roadmap" target="_blank">regular basis</a>.</p>
            <p>I'm not looking for MUD staff at this time since the game isn't in what I'd call a "playable" state. There are often <a href="https://github.com/scionaltera/emergentmud/issues" target="_blank">issues</a> on GitHub labeled "good first issue" or "help wanted". Pull requests against those issues are always welcome as are new issues from visitors to suggest features, ask questions or provide feedback. I am hoping to start building a community around this project as it progresses, and ultimately to build the game I described in the paragraphs above. It's a game I think a lot of people would like to play; myself included.</p>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 text-center margin-bottom">
            [ <a href="<@spring.url '/public/commands'/>">Commands</a> ]
            [ <a href="<@spring.url '/public/emotes'/>">Emotes</a> ]
            [ <a href="<@spring.url 'https://github.com/scionaltera/emergentmud/wiki'/>" target="_blank">Wiki</a> ]
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