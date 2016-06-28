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
import com.zhukovsd.endlessfield.ChunkSize;
import com.zhukovsd.endlessfield.EndlessFieldArea;
import com.zhukovsd.endlessfield.field.EndlessField;
import com.zhukovsd.endlessfield.field.EndlessFieldChunk;
import com.zhukovsd.endlessfield.field.EndlessFieldChunkFactory;
import com.zhukovsd.endlessfield.fielddatasource.EndlessFieldDataSource;
import com.zhukovsd.entrylockingconcurrenthashmap.EntryLockingConcurrentHashMap;

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
        calculateNeighbourMinesCountInInnerCells(chunk, chunkId);

        StringBuilder sb = new StringBuilder();
        String rowDelimiter = "";
        for (int row = 0; row < field.chunkSize.rowCount; row++) {
            sb.append(rowDelimiter);

            String cellDelimiter = "";
            for (int column = 0; column < field.chunkSize.columnCount; column++) {
                sb.append(cellDelimiter);

                if (chunk.get(new CellPosition(row, column)).hasMine())
                    sb.append('*');
                else {
                    int count = chunk.get(new CellPosition(row, column)).neighbourMinesCount();
                    if (count >= 0)
                        sb.append(count);
                    else
                        sb.append('-');
                }

                cellDelimiter = " ";
            }

            rowDelimiter = "\n";
        }
        System.out.println(sb.toString());

        return chunk;
    }

    private void fillWithCellsAndPlaceMines(EndlessFieldChunk<MinesweeperFieldCell> chunk, int chunkId) {
        EndlessFieldArea chunkArea = ChunkIdGenerator.chunkAreaById(chunkId, field.chunkSize);

        for (CellPosition position : chunkArea) {
            chunk.put(position, new MinesweeperFieldCell(rand.nextInt(100) < mineOdds));
        }
    }

    private void calculateNeighbourMinesCountInInnerCells(EndlessFieldChunk<MinesweeperFieldCell> chunk, int chunkId) {
        EndlessFieldArea innerArea = ChunkIdGenerator.chunkAreaById(chunkId, field.chunkSize).narrowToCenter(1);

        for (CellPosition position : innerArea) {
            MinesweeperFieldCell cell = chunk.get(position);
            if (!cell.hasMine()) {
                EndlessFieldArea neighbourArea = new EndlessFieldArea(
                        new CellPosition(position.row - 1, position.column - 1), 3, 3
                );

                int neighbourMinesCount = 0;
                for (CellPosition neighbourPosition : neighbourArea) {
                    if (!neighbourPosition.equals(position)) {
                        if (chunk.get(neighbourPosition).hasMine())
                            neighbourMinesCount++;
                    }
                }

                cell.setNeighbourMinesCount(neighbourMinesCount);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MinesweeperField field = new MinesweeperField(
                15000,
                new ChunkSize(50, 50),
                new EndlessFieldDataSource<MinesweeperFieldCell>() {
                    @Override
                    public boolean containsChunk(Integer chunkId) {
                        return false;
                    }

                    @Override
                    public EndlessFieldChunk<MinesweeperFieldCell> getChunk(Integer chunkId, ChunkSize chunkSize) {
                        return null;
                    }

                    @Override
                    public void storeChunk(EntryLockingConcurrentHashMap<Integer, EndlessFieldChunk<MinesweeperFieldCell>> chunkMap, int chunkId, EndlessFieldChunk<MinesweeperFieldCell> chunk) throws InterruptedException {

                    }

                    @Override
                    public void modifyEntries(Map<CellPosition, MinesweeperFieldCell> entries) {

                    }
                },
                null
        );

        field.lockChunksByIds(Collections.singleton(0));
        field.unlockChunks();
    }
}
