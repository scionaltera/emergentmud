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
<#assign title="Commands">
<#assign path="/public/commands">
<#assign links = [ "/css/index.css", "/css/commands.css" ]>
<#include "header.inc.ftl">
<#import "/spring.ftl" as spring>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12 text-center heading">
            <span>EmergentMUD</span>
        </div>
    </div>
    <div class="row">
        <div class="col-md-10 col-md-offset-1 description">
            <div class="row">
                <div class="col-md-12 command-heading">
                    <p>The following are the commands you can use while playing the game.</p>
                    <p>Commands are how you interact with the game. They will perform actions such as walking to the
                    next room, attacking a foe, picking up an object, or seeing who else is playing. Below is a list
                    of all the commands in the game, descriptions of what they are used for, and how to use them.</p>
                </div>
            </div>
            <#list metadataList as metadata>
                <#if commandMap[metadata.name].parameters?size gt 0>
                    <div class="row">
                    <div class="col-md-12 command">${metadata.name?upper_case}
                    <#list commandMap[metadata.name].parameters as parameter>
                        <#if parameter.required>
                        &lt;${parameter.name}&gt;
                        <#else>
                        [${parameter.name}]
                        </#if>
                    </#list>
                    <br/>${commandMap[metadata.name].description!}
                    </div>
                    </div>
                <#elseif commandMap[metadata.name].subCommands?size gt 0>
                    <div class="row">
                    <div class="col-md-12 command">${metadata.name?upper_case} &lt;sub-command&gt;
                    <ul>
                    <#list commandMap[metadata.name].subCommands as subCommand>
                        <li>${subCommand.name?upper_case}
                        <#list subCommand.parameters as parameter>
                            <#if parameter.required>
                                &lt;${parameter.name}&gt;
                            <#else>
                                [${parameter.name}]
                            </#if>
                        </#list>
                        <br/>
                        ${subCommand.description}
                        </li>
                    </#list>
                    </ul>
                    ${commandMap[metadata.name].description!}
                    </div>
                    </div>
                <#else>
                    <div class="row">
                    <div class="col-md-12 command">${metadata.name?upper_case}<br/>
                        ${commandMap[metadata.name].description!}
                    </div>
                    </div>
                </#if>
            </#list>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 text-center margin-bottom">
            [ <a href="<@spring.url '/'/>">Home</a> ]
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