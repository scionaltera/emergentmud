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

package com.emergentmud.core.command;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public abstract class BaseCommunicationCommandTest {
    @Mock
    protected SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    protected EntityRepository entityRepository;

    @Mock
    protected GameOutput output;

    @Mock
    protected Room room;

    @Mock
    protected Entity entity;

    @Mock
    protected Entity stu;

    @Captor
    protected ArgumentCaptor<MessageHeaders> headerCaptor;

    protected List<Entity> roomContents;

    protected List<Entity> generateRoomContents() {
        List<Entity> contents = new ArrayList<>();

        stu = mock(Entity.class);

        when(stu.getId()).thenReturn("stuId");
        when(stu.getName()).thenReturn("Stu");
        when(stu.getStompSessionId()).thenReturn("stuSimpSessionId");
        when(stu.getStompUsername()).thenReturn("stuSimpUsername");

        contents.add(stu);

        return contents;
    }
}
