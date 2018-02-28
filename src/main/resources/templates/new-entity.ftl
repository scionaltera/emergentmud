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
<#assign title="Create Character">
<#assign path="/essence">
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
        <form id="new-entity-form" class="form-horizontal">
            <div class="form-group">
                <div class="col-md-4 col-md-push-4">
                    <label for="name">First Name</label>
                    <input type="text" class="form-control" name="name" id="name" value="${entityName!}" placeholder="First Name" autofocus>
                </div>
            </div>
            <div class="form-group">
                <div class="col-md-4 col-md-push-4">
                    <#list genders as gender>
                        <label class="radio-inline">
                            <input type="radio" name="gender" id="gender${gender?counter}" value="${gender.name}"> ${gender}
                        </label>
                    </#list>
                </div>
            </div>
            <#if errorName??>
            <div class="form-group">
                <div class="col-md-4 col-md-push-4">
                    <span style="color: red;">${errorName}</span>
                </div>
            </div>
            </#if>
            <div class="form-group">
                <div class="col-md-4 col-md-push-4">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <a role="button" class="btn btn-danger" href="<@spring.url '/'/>">
                        <i class="fa fa-times"></i> Cancel
                    </a>
                    <button role="button" class="btn btn-primary" formmethod="post" formaction="<@spring.url '/entity'/>">
                        <i class="fa fa-heart"></i> Save Character
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<#include "scripts.inc.ftl">
</body>
</html>