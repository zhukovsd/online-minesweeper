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
import com.zhukovsd.endlessfield.ChunkSize;
import com.zhukovsd.endlessfield.field.EndlessFieldChunk;
import com.zhukovsd.endlessfield.fielddatasource.EndlessFieldDataSource;
import com.zhukovsd.entrylockingconcurrenthashmap.EntryLockingConcurrentHashMap;

import java.util.Map;

/**
 * Created by ZhukovSD on 04.07.2016.
 */
public class MinesweeperFieldDataSource implements EndlessFieldDataSource<MinesweeperFieldCell> {
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
}
