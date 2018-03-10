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

package com.emergentmud.core.service.maze;

import com.emergentmud.core.model.Coordinate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.Random;

/*
 * Prim's Algorithm
 */
@Component
public class RandomCellSelectionStrategy implements CellSelectionStrategy {
    private Random random;

    @Inject
    public RandomCellSelectionStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Coordinate selectCell(LinkedList<Coordinate> queue) {
        return queue.get(random.nextInt(queue.size()));
    }
}
