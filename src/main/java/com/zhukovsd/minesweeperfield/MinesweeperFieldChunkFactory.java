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

package com.zhukovsd.minesweeperfield;

import com.zhukovsd.endlessfield.CellPosition;
import com.zhukovsd.endlessfield.ChunkIdGenerator;
import com.zhukovsd.endlessfield.EndlessFieldArea;
import com.zhukovsd.endlessfield.field.EndlessField;
import com.zhukovsd.endlessfield.field.EndlessFieldChunk;
import com.zhukovsd.endlessfield.field.EndlessFieldChunkFactory;

import java.util.*;

/**
 * Created by ZhukovSD on 27.06.2016.
 */
public class MinesweeperFieldChunkFactory extends EndlessFieldChunkFactory<MinesweeperFieldCell> {
    private final int mineOdds;

    private static Random rand = new Random();

    public MinesweeperFieldChunkFactory(EndlessField<MinesweeperFieldCell> field, int mineOdds) {
        super(field);
        this.mineOdds = mineOdds;
    }

    @Override
    protected EndlessFieldChunk<MinesweeperFieldCell> generateChunk(Integer chunkId, Collection<Integer> lockedChunkIds) {
        EndlessFieldChunk<MinesweeperFieldCell> chunk = super.generateChunk(chunkId, lockedChunkIds);

        fillWithCellsAndPlaceMines(chunk, chunkId);
        calculateNeighbourMinesCountForInnerCells(chunk, chunkId);
        calculateNeighbourMinesCountForBorderCells(chunk, chunkId, lockedChunkIds);

        return chunk;
    }

    private void fillWithCellsAndPlaceMines(EndlessFieldChunk<MinesweeperFieldCell> chunk, int chunkId) {
        EndlessFieldArea chunkArea = ChunkIdGenerator.chunkAreaById(field, chunkId);

        for (CellPosition position : chunkArea) {
            chunk.put(position, new MinesweeperFieldCell(rand.nextInt(100) < mineOdds));
        }
    }

    private void calculateNeighbourMinesCountForInnerCells(EndlessFieldChunk<MinesweeperFieldCell> chunk, int chunkId) {
        EndlessFieldArea innerArea = ChunkIdGenerator.chunkAreaById(field, chunkId).narrowToCenter(1);

        for (CellPosition position : innerArea) {
            MinesweeperFieldCell cell = chunk.get(position);
            if (!cell.hasMine()) {
                EndlessFieldArea neighbourArea = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(1);

                int neighbourMinesCount = 0;
                for (CellPosition neighbourCellPosition : neighbourArea) {
                    if (!neighbourCellPosition.equals(position)) {
                        if (chunk.get(neighbourCellPosition).hasMine())
                            neighbourMinesCount++;
                    }
                }

                cell.setNeighbourMinesCount(neighbourMinesCount);
            }
        }
    }

    private void calculateNeighbourMinesCountForBorderCells(
            EndlessFieldChunk<MinesweeperFieldCell> chunk, Integer chunkId, Collection<Integer> lockedChunkIds
    ) {
        EndlessFieldArea chunkArea = ChunkIdGenerator.chunkAreaById(field, chunkId);
        EndlessFieldArea innerArea = ChunkIdGenerator.chunkAreaById(field, chunkId).narrowToCenter(1);

        // border cells lays in chunkArea, but not in innerArea
        int c = 0;
        for (CellPosition position : chunkArea) {
            if (!innerArea.contains(position)) {
                c++;
                MinesweeperFieldCell cell = chunk.get(position);
                if (!cell.hasMine()) {
                    EndlessFieldArea neighbourArea = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(1);

                    int neighbourMinesCount = 0;
                    for (CellPosition neighbourCellPosition : neighbourArea) {
                        if (!neighbourCellPosition.equals(position)) {
                            int neighbourCellChunkId = ChunkIdGenerator.generateID(field.chunkSize, neighbourCellPosition);

                            if (lockedChunkIds.contains(neighbourCellChunkId)) {
                                if (field.getCell(neighbourCellPosition).hasMine())
                                    neighbourMinesCount++;
                            } else if (neighbourCellChunkId == chunkId) {
                                if (chunk.get(neighbourCellPosition).hasMine())
                                    neighbourMinesCount++;
                            }
                        }
                    }

                    cell.setNeighbourMinesCount(neighbourMinesCount);
                }
            }
        }

//        System.out.println("c = " + c);
    }
}
