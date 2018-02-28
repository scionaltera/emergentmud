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
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Pronoun {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    private UUID id;

    private String name;
    private String subject;
    private String object;
    private String possessive;
    private String possessivePronoun;
    private String reflexive;

    public Pronoun() {
        // this method intentionally left blank
    }

    public Pronoun(String name, String subject, String object, String possessive, String possessivePronoun, String reflexive) {
        this.name = name;
        this.subject = subject;
        this.object = object;
        this.possessive = possessive;
        this.possessivePronoun = possessivePronoun;
        this.reflexive = reflexive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPossessive() {
        return possessive;
    }

    public void setPossessive(String possessive) {
        this.possessive = possessive;
    }

    public String getPossessivePronoun() {
        return possessivePronoun;
    }

    public void setPossessivePronoun(String possessivePronoun) {
        this.possessivePronoun = possessivePronoun;
    }

    public String getReflexive() {
        return reflexive;
    }

    public void setReflexive(String reflexive) {
        this.reflexive = reflexive;
    }

    @Override
    public String toString() {
        return StringUtils.capitalize(getSubject()) + "/" + StringUtils.capitalize(getObject());
    }
}
