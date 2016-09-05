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
        <table class="table">
            <tr>
                <th>Name</th>
                <th>Actions</th>
            </tr>
        <#list essences as essence>
            <tr>
                <td>${essence.name}</td>
                <td>
                    <#if (essence.entity.x)?? || (essence.entity.y)?? || (essence.entity.z)??>
                    <a class="btn btn-warning" role="button" href="<@spring.url '/play/${essence.id}'/>">
                        <i class="fa fa-play"></i> Reconnect
                    </a>
                    <#else>
                    <a class="btn btn-success" role="button" href="<@spring.url '/play/${essence.id}'/>">
                        <i class="fa fa-play"></i> Play
                    </a>
                    </#if>
                </td>
            </tr>
        </#list>
        </table>
    </div>

    <div class="row">
        <div class="col-md-12 text-center">
            <a class="btn btn-danger" role="button" href="<@spring.url '/logout'/>">
                <i class="fa fa-power-off"></i> Log Out
            </a>
            <a class="btn btn-primary" role="button" href="<@spring.url '/new-essence'/>">
                <i class="fa fa-heart"></i> Create Essence
            </a>
        </div>
    </div>
</div>

<script type="text/javascript" src="/webjars/jquery/jquery.min.js"></script>
<script type="text/javascript" src="/webjars/bootstrap/js/bootstrap.min.js"></script>
<script src="//static.getclicky.com/js" type="text/javascript"></script>
<script type="text/javascript">try{ clicky.init(100986651); }catch(e){}</script>
<noscript><p><img alt="Clicky" width="1" height="1" src="//in.getclicky.com/100986651ns.gif" /></p></noscript>
</body>
</html>