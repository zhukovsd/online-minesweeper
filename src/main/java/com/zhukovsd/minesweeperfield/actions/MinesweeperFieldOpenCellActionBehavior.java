/*
 * This file is part of online-minesweeper.
 *
 * online-minesweeper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * online-minesweeper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with online-minesweeper.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.zhukovsd.minesweeperfield.actions;

import com.zhukovsd.endlessfield.CellPosition;
import com.zhukovsd.endlessfield.ChunkIdGenerator;
import com.zhukovsd.endlessfield.EndlessFieldArea;
import com.zhukovsd.endlessfield.field.EndlessField;
import com.zhukovsd.endlessfield.field.EndlessFieldActionBehavior;
import com.zhukovsd.endlessfield.field.EndlessFieldCell;
import com.zhukovsd.minesweeperfield.MinesweeperFieldCell;

import java.util.*;

/**
 * Created by ZhukovSD on 29.06.2016.
 */
public class MinesweeperFieldOpenCellActionBehavior implements EndlessFieldActionBehavior {
    @Override
    public Collection<Integer> getChunkIds(EndlessField<? extends EndlessFieldCell> field, CellPosition position) {
//        return null;
        // TODO: 29.06.2016 add rows and columns in expandFromCenter() method
        EndlessFieldArea area = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(field.chunkSize.rowCount);
        return ChunkIdGenerator.chunkIdsByArea(field.chunkSize, area);
    }

    @Override
    public LinkedHashMap<CellPosition, ? extends EndlessFieldCell> perform(
            EndlessField<? extends EndlessFieldCell> field, CellPosition position
    ) {
        Map<CellPosition, ? extends EndlessFieldCell> entries = field.getEntriesByChunkIds(getChunkIds(field, position));

//        checkedMap.putAll(field.getEntriesByChunkIds(getChunkIds(field, position)));

        return null;
    }
}
