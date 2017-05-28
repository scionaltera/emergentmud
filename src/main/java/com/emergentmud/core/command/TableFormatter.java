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

package com.emergentmud.core.command;

import com.emergentmud.core.model.stomp.GameOutput;

import java.util.ArrayList;
import java.util.List;

public class TableFormatter {
    private String label;
    private List<String> headers;
    private List<Row> rows = new ArrayList<>();
    private String object;
    private String objectPlural;

    public TableFormatter(String label, List<String> headers, String object, String objectPlural) {
        this.label = label;
        this.headers = headers;
        this.object = object;
        this.objectPlural = objectPlural;
    }

    public void addRow(List<String> values) {
        rows.add(new Row(values));
    }

    public void toTable(GameOutput output, String color) {
        StringBuilder buf = new StringBuilder();

        output.append(String.format("[d%s][ [%s]%s [d%s]]", color, color, label, color));

        buf.append("<table class=\"table\">");
        buf.append("<tr>");

        for (String header : headers) {
            buf.append(String.format("<th>[d%s]%s</th>", color, header));
        }

        buf.append("</tr>");

        for (Row row : rows) {
            buf.append("<tr>");

            for (String value : row.values) {
                buf.append(String.format("<td>[%s]%s</td>", color, value));
            }

            buf.append("</tr>");
        }

        output.append(buf.toString());
        output.append(String.format("[d%s]%d %s listed.",
                color,
                rows.size(),
                rows.size() == 1 ? object : objectPlural));
    }

    private static class Row {
        private List<String> values;

        Row(List<String> values) {
            this.values = values;
        }
    }
}
