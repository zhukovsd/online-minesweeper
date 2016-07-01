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

package com.zhukovsd.experiments;

import com.google.common.base.Strings;
import com.zhukovsd.endlessfield.*;
import com.zhukovsd.endlessfield.field.EndlessFieldCell;
import com.zhukovsd.endlessfield.field.EndlessFieldChunk;
import com.zhukovsd.endlessfield.fielddatasource.EndlessFieldDataSource;
import com.zhukovsd.entrylockingconcurrenthashmap.EntryLockingConcurrentHashMap;
import com.zhukovsd.minesweeperfield.MinesweeperField;
import com.zhukovsd.minesweeperfield.MinesweeperFieldCell;
import com.zhukovsd.minesweeperfield.MinesweeperFieldChunkFactory;
import com.zhukovsd.minesweeperfield.actions.MinesweeperFieldAction;

import java.util.*;

/**
 * Created by ZhukovSD on 28.06.2016.
 */
public class FieldGenerationConsistencyExperiment {
    private static int cellCount;
    private static HashMap<Integer, Integer> mineCounts = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            run();

            if ((i > 0) && (i % 1 == 0))
                System.out.format(
                        "#%s, count = %s, try count = %s, ratio = %s\n",
                        i, MinesweeperFieldChunkFactory.count,
                        MinesweeperFieldChunkFactory.tryCount,
                        ((double) MinesweeperFieldChunkFactory.tryCount.get()) / ((double) MinesweeperFieldChunkFactory.count.get())
                );
        }

        Optional<Integer> overallMineCount = mineCounts.values().stream().reduce(Integer::sum);
        System.out.println(
                "overall mine odds = " + ((double) overallMineCount.get()) / cellCount * 100 + "%"
        );

        System.out.println("mine counts by column:");
        mineCounts.keySet().stream().sorted().forEach(
                key -> System.out.println(key + " -> " + mineCounts.get(key).toString())
        );
    }

    private static void run() throws InterruptedException {
        MinesweeperField field = new MinesweeperField(
                15000, new ChunkSize(50, 50),
                new EndlessFieldSizeConstraints(3, 3),
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
                    public void modifyEntries(Map entries) {

                    }

                    @Override
                    public void storeChunk(EntryLockingConcurrentHashMap chunkMap, int chunkId, EndlessFieldChunk chunk) throws InterruptedException {

                    }
                }
                ,null
        );

        List<List<Integer>> lockOrder = Arrays.asList(
//                Arrays.asList(0, 1, 2),
//                Arrays.asList(40000, 40001, 40002),
//                Arrays.asList(80000, 80001, 80002)
                Collections.singletonList(0),
                Collections.singletonList(1),
                Collections.singletonList(2),
                Collections.singletonList(40000),
                Collections.singletonList(40001),
                Collections.singletonList(40002),
                Collections.singletonList(80000),
                Collections.singletonList(80001),
                Collections.singletonList(80002)
        );

        int maxChunkColumn = 0, maxChunkRow = 0;
        Set<Integer> chunkSet = new HashSet<>();

        for (List<Integer> chunkIds : lockOrder) {
//            System.out.println("locking chunks " + chunkIds.toString());

            field.lockChunksByIds(chunkIds);
            try {
                for (Integer chunkId : chunkIds) {
                    maxChunkColumn = Math.max(maxChunkColumn, chunkId / ChunkIdGenerator.idFactor);
                    maxChunkRow = Math.max(maxChunkRow, chunkId % ChunkIdGenerator.idFactor);

                    chunkSet.add(chunkId);
                }
            } finally {
                field.unlockChunks();
            }
        }

        //

        field.lockChunksByIds(chunkSet);
        try {
            Map<CellPosition, MinesweeperFieldCell> entries = field.getEntriesByChunkIds(chunkSet);

//            printField(field, maxChunkColumn, maxChunkRow, entries);
            checkFieldConsistency(field, maxChunkColumn, maxChunkRow, entries);
        } finally {
            field.unlockChunks();
        }

        try {
            openFieldCells(field, maxChunkColumn, maxChunkRow);
        } catch (Exception e ){
            System.out.println("cells opening error - " + e.getMessage());

            field.lockChunksByIds(chunkSet);
            try {
                Map<CellPosition, MinesweeperFieldCell> entries = field.getEntriesByChunkIds(chunkSet);
                printField(field, maxChunkColumn, maxChunkRow, entries);
            } finally {
                field.unlockChunks();
            }
        }

        field.lockChunksByIds(chunkSet);
        try {
            Map<CellPosition, MinesweeperFieldCell> entries = field.getEntriesByChunkIds(chunkSet);
//            printField(field, maxChunkColumn, maxChunkRow, entries);
            countMines(field, maxChunkColumn, maxChunkRow, entries);
        } finally {
            field.unlockChunks();
        }
    }

    private static void printField(
            MinesweeperField field, int maxChunkColumn, int maxChunkRow, Map<CellPosition, MinesweeperFieldCell> entries
    ) {
        StringBuilder sb = new StringBuilder();
        String rowDelimiter = "";
        for (int row = 0; row < field.chunkSize.rowCount * (maxChunkRow + 1); row++) {
            sb.append(rowDelimiter);

            String cellDelimiter = "";
            for (int column = 0; column < field.chunkSize.columnCount * (maxChunkColumn + 1); column++) {
                sb.append(cellDelimiter);

                MinesweeperFieldCell cell = entries.get(new CellPosition(row, column));
                if (cell.isOpen())
                    sb.append('X');
                else if (cell.hasMine())
                    sb.append('*');
                else {
                    int count = cell.neighbourMinesCount();
                    if (count >= 0)
                        sb.append(count);
                    else
                        sb.append('-');
                }

                if ((column + 1) % field.chunkSize.columnCount == 0)
                    cellDelimiter = "|";
                else
                    cellDelimiter = " ";
            }

            if ((row + 1) % field.chunkSize.rowCount == 0)
                rowDelimiter = "\n" + Strings.repeat("--", field.chunkSize.columnCount * (maxChunkColumn + 1) - 1) + "-\n";
            else
                rowDelimiter = "\n";
        }

        System.out.println(sb.toString());
    }

    private static void checkFieldConsistency(
            MinesweeperField field, int maxChunkColumn, int maxChunkRow, Map<CellPosition, MinesweeperFieldCell> entries
    ) {
        for (int row = 0; row < field.chunkSize.rowCount * (maxChunkRow + 1); row++) {
            for (int column = 0; column < field.chunkSize.columnCount * (maxChunkColumn + 1); column++) {
                CellPosition position = new CellPosition(row, column);
                MinesweeperFieldCell cell = entries.get(position);

                if (!cell.hasMine()) {
                    EndlessFieldArea neighbourArea = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(1);

                    int neighbourMinesCount = 0;
                    for (CellPosition neighbourPosition : neighbourArea) {
                        if (!neighbourPosition.equals(position)) {
                            if (entries.get(neighbourPosition).hasMine())
                                neighbourMinesCount++;
                        }
                    }

                    if (cell.neighbourMinesCount() != neighbourMinesCount) {
                        System.out.println("incorrect neighbour mines count at " + position);
                        break;
                    }
                }
            }
        }

//        System.out.println("chunks are correct!");
    }

    private static void openFieldCells(MinesweeperField field, int maxChunkColumn, int maxChunkRow) throws InterruptedException {
        MinesweeperFieldAction action = MinesweeperFieldAction.OPEN_CELL;

        for (int i = 0; i < field.chunkSize.rowCount * (maxChunkRow + 1); i++) {
            for (int j = 0; j < field.chunkSize.columnCount * (maxChunkColumn + 1); j++) {
                CellPosition position = new CellPosition(i, j);

                Collection<Integer> chunkIds = action.getChunkIds(field, position);
                field.lockChunksByIds(chunkIds);
                try {
                    LinkedHashMap<CellPosition, ? extends EndlessFieldCell> map = action.perform(field, position);
//                        System.out.println(map.size() + " cells opened");
                } finally {
                    field.unlockChunks();
                }
            }
        }
    }

    private static void countMines(
            MinesweeperField field, int maxChunkColumn, int maxChunkRow, Map<CellPosition, MinesweeperFieldCell> entries
    ) {
        for (int i = 0; i < field.chunkSize.rowCount * (maxChunkRow + 1); i++) {
            for (int j = 0; j < field.chunkSize.columnCount * (maxChunkColumn + 1); j++) {
                cellCount++;
                CellPosition position = new CellPosition(i, j);

                if (entries.get(position).hasMine()) {
                    Integer index = j % field.chunkSize.columnCount;

                    if (!mineCounts.containsKey(index))
                        mineCounts.put(index, 1);
                    else
                        mineCounts.put(index, mineCounts.get(index) + 1);
                }
            }
        }
    }
}
