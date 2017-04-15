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
            <h1>The Explorer's Dream</h1>
            <p>EmergentMUD is a free, text based "Multi-User Domain" that you play in your browser. It's a modern MUD with an old school retro feel. Just like most other MUDs back in the 90s you play a character in a medieval fantasy setting. The modern aspect is that the entire game world is procedurally generated on the fly and is fully interactive. All parts of the world from the species of plants and animals, societies of sentient creatures, geography, weather, and even quests are created on demand as players explore. All the different game systems interact with one another to create fun and unexpected <em>emergent behavior</em>. This world is alive.</p>
            <p>Help an NPC gather resources to build his house and he'll build his house - not just continue asking everyone he sees for resources. Steal the gold from the King's vault and he won't be able to fund the war he's waging - having a direct effect on international politics. Burn down a village and maybe it will be rebuilt - but maybe it won't. Help someone in need and make an ally you can rely on later. Start a business and hire NPCs to work for you. Head out in a direction that isn't on the map yet and it will be created as you begin to walk through it - complete with new plants, animals, NPCs, religions, cultures and discoveries that the world has never seen before. Everything you do in this world has a real effect. You won't see any quest vendors and you won't experience the same "content" that everyone else has already devoured before you. You can forge your own path, create your own destiny, and leave your own mark upon the world in the process.</p>

            <h2>So that's the elevator pitch.</h2>
            <p>The code has been in active development for about a year now, and still going strong although there is still a very long way to go. Please drop in and take a look around, and pardon the dust. Let me know what you think. New things are being added on a regular basis.</p>
            <p>If you're a programmer, please check out the MUD's source code and see what you think. If you're a gamer, I'd love to hear your feedback. I talk a lot about the development process on the blog and you can track my work at the links below to see what features are currently being worked on. <strong>Thanks for visiting!</strong></p>

            <h1>Development Progress</h1>
            <p>The current release is called <em>Playable World</em>. It is focused on all the most basic necessities of a MUD.</p>
            <ul>
                <li>Application framework and architecture</li>
                <li>Production deployment with Docker</li>
                <li>Administrative commands and tools</li>
                <li>Basic room generation with biomes</li>
                <li>Communication, emotes and movement commands</li>
                <li>Help files</li>
            </ul>
            <p>The next release is called <em>People</em>. It will focus on developing some of the first gameplay features of the MUD.</p>
            <ul>
                <li>Character attributes (gender, strength, etc.)</li>
                <li>Animals and NPCs</li>
                <li>Plants and Trees</li>
                <li>Minerals, Metals and Other Natural Resources</li>
                <li>Items and Equipment</li>
                <li>Bodies of Water</li>
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