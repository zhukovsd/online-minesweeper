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

import com.zhukovsd.endlessfield.ChunkSize;
import com.zhukovsd.endlessfield.EndlessFieldSizeConstraints;
import com.zhukovsd.endlessfield.field.EndlessFieldChunk;
import com.zhukovsd.endlessfield.field.EndlessFieldChunkFactory;
import com.zhukovsd.endlessfield.fielddatasource.EndlessFieldDataSource;
import com.zhukovsd.entrylockingconcurrenthashmap.EntryLockingConcurrentHashMap;
import com.zhukovsd.minesweeperfield.MinesweeperField;
import com.zhukovsd.minesweeperfield.MinesweeperFieldCell;
import com.zhukovsd.minesweeperfield.MinesweeperFieldChunkFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhukovSD on 30.06.2016.
 */
public class FieldGenerationPerformanceExperiment {
    public static void main(String[] args) {
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
        );

        MinesweeperFieldChunkFactory factory = new MinesweeperFieldChunkFactory(field, 15);
        int count = 1000;

        long time = System.nanoTime();
        for (int i = 0; i < count; i++) {
            factory.generateChunk(0, Collections.emptySet());
        }
        time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - time);

        System.out.format(
                "%s ms, count = %s, try count = %s\n", time, MinesweeperFieldChunkFactory.count,
                MinesweeperFieldChunkFactory.tryCount
        );
    }
}
