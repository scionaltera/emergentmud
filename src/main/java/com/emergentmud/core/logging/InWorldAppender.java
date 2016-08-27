/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 BoneVM, LLC
 *
 * This file is part of EmergentMUD.
 *
 * EmergentMUD is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EmergentMUD is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.emergentmud.core.logging;

import ch.qos.logback.core.AppenderBase;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.util.EntityUtil;
import com.emergentmud.core.util.SpringContextSingleton;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class InWorldAppender<T> extends AppenderBase<T> {
    @Override
    protected void append(T eventObject) {
        ApplicationContext applicationContext = SpringContextSingleton.getInstance().getApplicationContext();

        if (applicationContext == null) {
            return;
        }

        EntityUtil entityUtil = (EntityUtil)applicationContext.getBean("entityUtil");
        EntityRepository entityRepository = (EntityRepository)applicationContext.getBean("entityRepository");

        if (entityUtil == null || entityRepository == null) {
            return;
        }

        GameOutput logMessage = new GameOutput(String.format("[magenta]%s'", eventObject))
                .append("")
                .append("> ");

        List<Entity> contents = entityRepository.findByAdminAndRoomIsNotNull(true);

        if (!contents.isEmpty()) {
            entityUtil.sendMessageToListeners(contents, logMessage);
        }
    }
}
