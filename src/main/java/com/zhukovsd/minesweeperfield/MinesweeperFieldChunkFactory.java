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

import com.google.common.collect.HashBiMap;
import com.zhukovsd.endlessfield.CellPosition;
import com.zhukovsd.endlessfield.ChunkIdGenerator;
import com.zhukovsd.endlessfield.EndlessFieldArea;
import com.zhukovsd.endlessfield.field.EndlessField;
import com.zhukovsd.endlessfield.field.EndlessFieldCell;
import com.zhukovsd.endlessfield.field.EndlessFieldChunk;
import com.zhukovsd.endlessfield.field.EndlessFieldChunkFactory;
import com.zhukovsd.graphs.Edge;
import com.zhukovsd.graphs.Graph;
import com.zhukovsd.graphs.Vertex;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ZhukovSD on 27.06.2016.
 */
public class MinesweeperFieldChunkFactory extends EndlessFieldChunkFactory<MinesweeperFieldCell> {
    static class CellVertex extends Vertex<CellVertex> {}
    enum Side {
        LEFT, TOP, RIGHT, BOTTOM;
    }

    public static AtomicInteger count = new AtomicInteger(), tryCount = new AtomicInteger();

    private final int mineOdds;

    private static Random rand = new Random();

    public MinesweeperFieldChunkFactory(EndlessField<MinesweeperFieldCell> field, int mineOdds) {
        super(field);
        this.mineOdds = mineOdds;
    }

    @Override
    public EndlessFieldChunk<MinesweeperFieldCell> generateChunk(Integer chunkId, Collection<Integer> lockedChunkIds) {
        boolean isChunkValid = false;
        EndlessFieldChunk<MinesweeperFieldCell> chunk = null;

        count.incrementAndGet();

        while (!isChunkValid) {
            tryCount.incrementAndGet();

            chunk = super.generateChunk(chunkId, lockedChunkIds);

            fillWithCellsAndPlaceMines(chunk, chunkId);
            calculateNeighbourMinesDisregardingRelatedChunks(chunk, chunkId);

            // TODO: 30.06.2016 remove debug
            //<editor-fold desc="print chunk with id 0">
//            if (chunkId == 80001) {
//                CellPosition origin = ChunkIdGenerator.chunkOrigin(field.chunkSize, chunkId);
//
//                StringBuilder sb = new StringBuilder();
//                String rowDelimiter = "";
//                for (int i = 0; i < field.chunkSize.rowCount; i++) {
//                    int row = origin.row + i;
//
//                    sb.append(rowDelimiter);
//
//                    String cellDelimiter = "";
//                    for (int j = 0; j < field.chunkSize.columnCount; j++) {
//                        int column = origin.column + j;
//
//                        sb.append(cellDelimiter);
//
//                        MinesweeperFieldCell cell = chunk.get(new CellPosition(row, column));
//                        if (cell.isOpen())
//                            sb.append('X');
//                        else if (cell.hasMine())
//                            sb.append('*');
//                        else {
//                            int count = cell.neighbourMinesCount();
//                            if (count >= 0)
//                                sb.append(count);
//                            else
//                                sb.append('-');
//                        }
//
//                        cellDelimiter = " ";
//                    }
//
//                    rowDelimiter = "\n";
//                }
//
//                System.out.println(sb.toString());
//            }
            //</editor-fold>

            isChunkValid = validateChunk(chunkId, chunk);
        }

        calculateNeighbourMinesCountForBorderCells(chunk, chunkId, lockedChunkIds);

        return chunk;
    }

    private void fillWithCellsAndPlaceMines(EndlessFieldChunk<MinesweeperFieldCell> chunk, int chunkId) {
        EndlessFieldArea chunkArea = ChunkIdGenerator.chunkAreaById(field, chunkId);

        for (CellPosition position : chunkArea) {
            boolean hasMine;
//            if (chunkId != 80001) {
                hasMine = rand.nextInt(100) < mineOdds;
//            } else {
//                CellPosition p1 = new CellPosition(10, 7),
//                        p2 = new CellPosition(10, 8),
//                        p3 = new CellPosition(11, 6),
//                        p4 = new CellPosition(14, 9);
//
//                hasMine = position.equals(p1) || position.equals(p2) || position.equals(p3) || position.equals(p4);
//            }

            chunk.put(position, new MinesweeperFieldCell(hasMine));
        }
    }

    private void calculateNeighbourMinesDisregardingRelatedChunks(EndlessFieldChunk<MinesweeperFieldCell> chunk, int chunkId) {
        EndlessFieldArea area = ChunkIdGenerator.chunkAreaById(field, chunkId);

        for (CellPosition position : area) {
            MinesweeperFieldCell cell = chunk.get(position);
            if (!cell.hasMine()) {
                EndlessFieldArea neighbourArea = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(1);

                int neighbourMinesCount = 0;
                for (CellPosition neighbourCellPosition : neighbourArea) {
                    if (!neighbourCellPosition.equals(position)) {
                        MinesweeperFieldCell neighbourCell = chunk.get(neighbourCellPosition);
                        if ((neighbourCell != null) && neighbourCell.hasMine())
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

        HashMap<CellPosition, EndlessFieldCell> modifiedEntries = new HashMap<>();

        // border cells lays in chunkArea, but not in innerArea
        for (CellPosition position : chunkArea) {
            if (!innerArea.contains(position)) {
                MinesweeperFieldCell cell = chunk.get(position);

                EndlessFieldArea neighbourArea = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(1);

                if (!cell.hasMine()) {
                    // if cell has no mine, calculate it's neighbour mines count
                    int neighbourMinesCount = 0;
                    for (CellPosition neighbourCellPosition : neighbourArea) {
                        if (!neighbourCellPosition.equals(position)) {
                            int neighbourCellChunkId = ChunkIdGenerator.chunkIdByPosition(field.chunkSize, neighbourCellPosition);

                            if (lockedChunkIds.contains(neighbourCellChunkId)) {
                                if (field.getCell(neighbourCellPosition).hasMine())
                                    neighbourMinesCount++;
                            } else if (neighbourCellChunkId == chunkId) {
                                if (chunk.get(neighbourCellPosition).hasMine())
                                    neighbourMinesCount++;
                            }
                        }
                    }

                    // TODO: 30.06.2016 synchronized?
                    cell.setNeighbourMinesCount(neighbourMinesCount);
                } else {
                    // if cell has mine, update neighbour mines counts for adjacent cells of locked chunks
                    for (CellPosition neighbourCellPosition : neighbourArea) {
                        int neighbourCellChunkId = ChunkIdGenerator.chunkIdByPosition(field.chunkSize, neighbourCellPosition);

                        if (lockedChunkIds.contains(neighbourCellChunkId)) {
                            MinesweeperFieldCell neighbourCell = field.getCell(neighbourCellPosition);

                            // TODO: 30.06.2016 synchronized?
                            neighbourCell.setNeighbourMinesCount(neighbourCell.neighbourMinesCount() + 1);
                            modifiedEntries.put(neighbourCellPosition, neighbourCell);
                        }
                    }
                }
            }

            if (modifiedEntries.size() > 0) {
//                System.out.println(modifiedEntries.size() + " cells were updated");
                field.updateEntries(modifiedEntries);
            }
        }
    }

    private boolean validateChunk(Integer chunkId, EndlessFieldChunk<MinesweeperFieldCell> chunk) {
//        if (chunkId == 80001)
//            System.out.println("hi");

        boolean result = true;

        Graph<CellVertex> graph = new Graph<>();
//        HashMap<CellPosition, CellVertex> vertexMap = new HashMap<>(field.chunkSize.cellCount());
        HashBiMap<CellPosition, CellVertex> vertexMap = HashBiMap.create(field.chunkSize.cellCount());

        EndlessFieldArea area = ChunkIdGenerator.chunkAreaById(field, chunkId);
        // create vertexes for each chunk cell
        for (CellPosition position : area) {
            CellVertex vertex = new CellVertex();

            graph.addVertex(vertex);
            vertexMap.put(position, vertex);
        }

        // connect vertexes which has no mines and 0 adjacent mines
        for (CellPosition position : area) {
            MinesweeperFieldCell cell = chunk.get(position);

            if (!cell.hasMine() && (cell.neighbourMinesCount() == 0)) {
                EndlessFieldArea neighbourArea = new EndlessFieldArea(field, position, 1, 1).expandFromCenter(1);

                for (CellPosition neighbourPosition : neighbourArea) {
                    if (!position.equals(neighbourPosition)) {
                        MinesweeperFieldCell neighbourCell = chunk.get(neighbourPosition);

                        if ((neighbourCell != null) && !neighbourCell.hasMine() && (neighbourCell.neighbourMinesCount() == 0)) {
                            CellVertex vertex = vertexMap.get(position);
                            CellVertex neighbourVertex = vertexMap.get(neighbourPosition);

                            graph.connectToEachOther(vertex, neighbourVertex);
                        }
                    }
                }
            }
        }

        CellPosition chunkOrigin = ChunkIdGenerator.chunkOrigin(field.chunkSize, chunkId);
        int bottomRow = chunkOrigin.row + field.chunkSize.rowCount - 1;
        int rightColumn = chunkOrigin.column + field.chunkSize.columnCount - 1;

        HashSet<CellPosition> borderPositions = new HashSet<>();
        // add top and bottom rows
        for (int i = 0; i < field.chunkSize.columnCount; i++) {
            borderPositions.add(new CellPosition(chunkOrigin.row, chunkOrigin.column + i));
            borderPositions.add(new CellPosition(bottomRow, chunkOrigin.column + i));
        }

        // add left and right columns
        for (int i = 0; i < field.chunkSize.rowCount; i++) {
            borderPositions.add(new CellPosition(chunkOrigin.row + i, chunkOrigin.column));
            borderPositions.add(new CellPosition(chunkOrigin.row + i, rightColumn));
        }

        for (Side side : Side.values()) {
            EndlessFieldArea originArea;
            if (side == Side.TOP) {
                originArea = new EndlessFieldArea(
                        field, new CellPosition(chunkOrigin.row, chunkOrigin.column + 1), 1, field.chunkSize.columnCount - 2
                );
            } else if (side == Side.LEFT) {
                originArea = new EndlessFieldArea(
                        field, new CellPosition(chunkOrigin.row + 1, chunkOrigin.column), field.chunkSize.rowCount - 2, 1
                );
            } else if (side == Side.BOTTOM) {
                originArea = new EndlessFieldArea(
                        field, new CellPosition(bottomRow, chunkOrigin.column + 1), 1, field.chunkSize.columnCount - 2
                );
            } else {
                originArea = new EndlessFieldArea(
                        field, new CellPosition(chunkOrigin.row + 1, rightColumn), field.chunkSize.rowCount - 2, 1
                );
            }

            ArrayList<CellPosition> originPositions = new ArrayList<>(), destinationPositions = new ArrayList<>();
            for (CellPosition position : originArea) {
                originPositions.add(position);
            }

            destinationPositions.addAll(borderPositions);
            destinationPositions.removeAll(originPositions);

//            System.out.println(originPositions.toString());
//            System.out.println(destinationPositions.toString());

            result = checkConnectivity(vertexMap, originPositions, destinationPositions);
            if (!result)
                break;
        }

//        ArrayList<CellPosition> originPositions = new ArrayList<>(field.chunkSize.columnCount * 2),
//                destinationPositions = new ArrayList<>(field.chunkSize.columnCount * 2);
//
//        // check top-bottom rows connectivity
//        for (int i = 0; i < field.chunkSize.columnCount; i++) {
//            originPositions.add(new CellPosition(chunkOrigin.row, chunkOrigin.column + i));
//            destinationPositions.add(new CellPosition(bottomRow, chunkOrigin.column + i));
//        }
//
////        for (int i = 1; i < field.chunkSize.rowCount - 1; i++) {
////            if (i <= field.chunkSize.rowCount / 2) {
////                originPositions.add(new CellPosition(chunkOrigin.row + i, chunkOrigin.column));
////                originPositions.add(new CellPosition(chunkOrigin.row + i, rightColumn));
////            } else {
////                destinationPositions.add(new CellPosition(chunkOrigin.row + i, chunkOrigin.column));
////                destinationPositions.add(new CellPosition(chunkOrigin.row + i, rightColumn));
////            }
////        }
//
//        result = checkConnectivity(vertexMap, originPositions, destinationPositions);
//
//        originPositions.clear();
//        destinationPositions.clear();
//
//        if (result) {
//            for (int i = 0; i < field.chunkSize.rowCount; i++) {
//                originPositions.add(new CellPosition(chunkOrigin.row + i, chunkOrigin.column));
//                destinationPositions.add(new CellPosition(chunkOrigin.row + i, rightColumn));
//            }
//
////            for (int i = 1; i < field.chunkSize.columnCount - 1; i++) {
////                if (i <= field.chunkSize.columnCount / 2) {
////                    originPositions.add(new CellPosition(chunkOrigin.row, chunkOrigin.column + i));
////                    originPositions.add(new CellPosition(bottomRow, chunkOrigin.column + i));
////                } else {
////                    destinationPositions.add(new CellPosition(chunkOrigin.row, chunkOrigin.column + i));
////                    destinationPositions.add(new CellPosition(bottomRow, chunkOrigin.column + i));
////                }
////            }
//
//            result = checkConnectivity(vertexMap, originPositions, destinationPositions);
//        }

//        System.out.println("chunk " + chunkId + " is " + (result ? "valid" : "invalid"));

        return result;
    }

    private boolean checkConnectivity(
            HashBiMap<CellPosition, CellVertex> vertexMap, ArrayList<CellPosition> originPositions,
            ArrayList<CellPosition> destinationPositions)
    {
        boolean result = true;

        ArrayList<HashSet<CellPosition>> connectedSets = new ArrayList<>(field.chunkSize.columnCount);

        Iterator<CellPosition> iterator = originPositions.iterator();
        while (iterator.hasNext() && result) {
            CellPosition position = iterator.next();

            boolean alreadyConnected = false;
            for (HashSet<CellPosition> set : connectedSets) {
                if (set.contains(position)) {
                    alreadyConnected = true;
                    break;
                }
            }

            if (!alreadyConnected) {
                ArrayList<CellVertex> list = new ArrayList<>();
                HashSet<CellPosition> connectedPositions = new HashSet<>();

                connectedPositions.add(position);

                list.add(vertexMap.get(position));

                while (!list.isEmpty()) {
                    CellVertex vertex = list.remove(0);

                    for (Edge<CellVertex> edge : vertex.edgeList) {
                        CellVertex connectedVertex = edge.destination;

                        CellPosition connectedPosition = vertexMap.inverse().get(connectedVertex);
                        if (!connectedPositions.contains(connectedPosition)) {
                            connectedPositions.add(connectedPosition);
                            list.add(connectedVertex);
                        }
                    }
                }

                connectedSets.add(connectedPositions);

                // check if connected set contains bottom any of row cells
                for (CellPosition destinationPosition : destinationPositions) {
                    if (connectedPositions.contains(destinationPosition)) {
                        result = false;
                        break;
                    }
                }

//                System.out.println(connectedPositions.toString());
            } else {
//                System.out.println("already connected");
            }
        }

        return result;
    }
}
