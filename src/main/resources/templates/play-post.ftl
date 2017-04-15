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
<form id="redirectForm" action="<@spring.url '/play'/>" method="post">
    <input name="essenceId" type="hidden" value="${essenceId}"/>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <noscript>
        <p>It seems your browser doesn't support Javascript! Websocket relies on
            Javascript being enabled. Please enable Javascript and reload this page!</p>
    </noscript>
</form>
<script type="text/javascript" src="/webjars/jquery/jquery.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        $("#redirectForm").submit();
    });
</script>
</body>
</html>