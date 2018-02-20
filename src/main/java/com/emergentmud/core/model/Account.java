/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2018 Peter Keeler
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

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
public class Account implements Capable {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    private UUID id;
    private String socialNetwork;
    private String socialNetworkId;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Capability> capabilities = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(String socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public String getSocialNetworkId() {
        return socialNetworkId;
    }

    public void setSocialNetworkId(String socialNetworkId) {
        this.socialNetworkId = socialNetworkId;
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
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        return getId().equals(account.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getSocialNetwork() + ":" + getSocialNetworkId();
    }
}
