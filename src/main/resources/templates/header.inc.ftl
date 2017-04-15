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
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <#if title??>
    <title>${title} - EmergentMUD</title>
    <#else>
    <title>EmergentMUD</title>
    </#if>
    <meta name="description" content="EmergentMUD is a free, retro text based game that runs in your browser using HTML5 and Websockets. It's a modern MUD with an old school feel."/>
    <meta name="viewport" content="width=device-width"/>
    <link rel="canonical" href="https://emergentmud.com${path!}"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url 'https://fonts.googleapis.com/css?family=Inconsolata'/>">
    <link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/font-awesome/css/font-awesome.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/css/bootstrap-social.css'/>"/>
    <#if links??>
    <#list links as link>
    <link rel="stylesheet" type="text/css" href="<@spring.url '${link}'/>"/>
    </#list>
    </#if>
</head>
