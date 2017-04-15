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
        <div class="col-md-10 col-md-offset-1 description">
            <span class="heading2">${httpStatus}: ${message}</span>
            <p>Alas, you cannot go that way. Maybe one of these links will take you where you want to go?</p>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 text-center margin-bottom">
            [ <a href="<@spring.url '/'/>">Home</a> ]
            [ <a href="<@spring.url '/public/commands'/>">Commands</a> ]
            [ <a href="<@spring.url '/public/emotes'/>">Emotes</a> ]
        </div>
    </div>
    <div class="row">
    <#include "external-links.inc.ftl">
    </div>
</div>

<#include "scripts.inc.ftl">
</body>
</html>