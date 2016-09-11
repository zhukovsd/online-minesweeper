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
import com.zhukovsd.minesweeperfield.MinesweeperField;
import com.zhukovsd.minesweeperfield.MinesweeperFieldCell;

import java.util.*;

/**
 * Created by ZhukovSD on 29.06.2016.
 */
public class MinesweeperFieldOpenCellActionBehavior implements EndlessFieldActionBehavior {
    @Override
    public Collection<Integer> getChunkIds(EndlessField<? extends EndlessFieldCell> field, CellPosition position) {
        // all 8 adjacent chunks
        EndlessFieldArea area = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(
                field.chunkSize.rowCount, field.chunkSize.columnCount
        );
        return ChunkIdGenerator.chunkIdsByArea(field.chunkSize, area);

        // max 5 chunks, top left bottom right
//        HashSet<Integer> result = new HashSet<>();
//        EndlessFieldArea area = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(
//                field.chunkSize.rowCount, 0
//        );
//        result.addAll(ChunkIdGenerator.chunkIdsByArea(field.chunkSize, area));
//
//        area = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(
//                0, field.chunkSize.columnCount
//        );
//        result.addAll(ChunkIdGenerator.chunkIdsByArea(field.chunkSize, area));
//
//        return result;
    }

    @Override
    public LinkedHashMap<CellPosition, ? extends EndlessFieldCell> perform(
            EndlessField<? extends EndlessFieldCell> field, CellPosition position
    ) {
        int c = 0;
        HashSet<CellPosition> positions = new HashSet<>();

        LinkedHashMap<CellPosition, EndlessFieldCell> result = new LinkedHashMap<>();
        MinesweeperField minesweeperField = (MinesweeperField) field;

        MinesweeperFieldCell actionCell = minesweeperField.getCell(position);

        if (actionCell.hasMine() && !actionCell.hasFlag() && !actionCell.mineBlown()) {
            synchronized (actionCell) {
                actionCell.blowMine();
            }

            result.put(position, actionCell);
        } else if (!actionCell.isOpen() && !actionCell.hasMine()) {
//            Map<CellPosition, MinesweeperFieldCell> entries = minesweeperField.getEntriesByChunkIds(getChunkIds(field, position));

            LinkedHashMap<CellPosition, MinesweeperFieldCell> cellsToOpen = new LinkedHashMap<>();

//            MinesweeperFieldCell actionCell = entries.get(position);
//            MinesweeperFieldCell actionCell = minesweeperField.getCell(position);
//            if (!actionCell.isOpen() && !actionCell.hasMine()) {
                cellsToOpen.put(position, actionCell);
//            }

            while (!cellsToOpen.isEmpty()) {
                Map.Entry<CellPosition, MinesweeperFieldCell> entry = cellsToOpen.entrySet().iterator().next();

                CellPosition openCellPosition = entry.getKey();
                MinesweeperFieldCell cell = cellsToOpen.remove(openCellPosition);

                synchronized (cell) {
                    cell.open();
                }
                result.put(openCellPosition, cell);

                if (cell.neighbourMinesCount() == 0) {
                    EndlessFieldArea neighboursArea = new EndlessFieldArea(field, openCellPosition, 1, 1).expandFromCenter(1);
                    for (CellPosition neighbourCellPosition : neighboursArea) {
                        if (!openCellPosition.equals(neighbourCellPosition)) {

                            //
                            c++;
                            positions.add(neighbourCellPosition);
                            //


//                            MinesweeperFieldCell neighbourCell = entries.get(neighbourCellPosition);
                            MinesweeperFieldCell neighbourCell = minesweeperField.getCell(neighbourCellPosition);
                            if (!neighbourCell.isOpen() && !neighbourCell.hasMine()) {
                                cellsToOpen.put(neighbourCellPosition, neighbourCell);
                            }
                        }
                    }
                }
            }

//            if (c > 0)
//                System.out.println(
//                        "c = " + c + ", set size = " + positions.size() + ", ratio = "
//                                + ((double) c) / ((double) positions.size())
//                );
        }

        return result;
    }
}
