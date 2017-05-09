/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2017 Peter Keeler
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

package com.emergentmud.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document
public class Entity implements Capable {
    @Id
    private String id;
    private String name;

    @DBRef
    private Account account;

    @DBRef
    private Set<Capability> capabilities = new HashSet<>();

    private Long creationDate;
    private Long lastLoginDate;
    private String stompUsername;
    private String stompSessionId;
    private String remoteAddr;
    private String userAgent;

    @DBRef
    private Room room;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Long lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getStompUsername() {
        return stompUsername;
    }

    public void setStompUsername(String stompUsername) {
        this.stompUsername = stompUsername;
    }

    public String getStompSessionId() {
        return stompSessionId;
    }

    public void setStompSessionId(String stompSessionId) {
        this.stompSessionId = stompSessionId;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public void addCapabilities(Capability ... capability) {
        capabilities.addAll(Arrays.asList(capability));
    }

    @Override
    public void addCapabilities(Collection<Capability> capabilities) {
        this.capabilities.addAll(capabilities);
    }

    @Override
    public void removeCapabilities(Capability ... capability) {
        capabilities.removeAll(Arrays.asList(capability));
    }

    @Override
    public void removeCapabilities(Collection<Capability> capabilities) {
        this.capabilities.removeAll(capabilities);
    }

    @Override
    public List<Capability> getCapabilities() {
        return new ArrayList<>(capabilities);
    }

    @Override
    public boolean isCapable(Capability capability) {
        return capabilities.contains(capability);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;

        Entity entity = (Entity) o;

        return getId() != null && getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
