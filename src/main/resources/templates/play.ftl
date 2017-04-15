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
<#assign title="Play">
<#assign path="/play">
<#assign links = [ "/css/color.css", "/css/play.css" ]>
<#include "header.inc.ftl">
<body>

<div>
    <div id="output-box">
        <ul id="output-list">
            <noscript>
                <li style="color: #ff0000">It seems your browser doesn't support Javascript! Websocket relies on
                    Javascript being enabled. Please enable Javascript and reload this page!</li>
            </noscript>
        </ul>
    </div>
    <div id="input-box">
        <form id="user-input-form">
            <input type="text" id="user-input" autocomplete="off" autofocus />
        </form>
    </div>
</div>

<script type="text/javascript">
    var breadcrumb = "${breadcrumb}";
</script>

<#include "scripts.inc.ftl">
<script type="text/javascript" src="//cdn.jsdelivr.net/sockjs/1.0.3/sockjs.min.js"></script>
<script type="text/javascript" src="<@spring.url '/js/stomp.min.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/js/client.js'/>"></script>

</body>
</html>