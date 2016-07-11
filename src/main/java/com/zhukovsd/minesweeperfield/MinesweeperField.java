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

import com.zhukovsd.endlessfield.ChunkIdGenerator;
import com.zhukovsd.endlessfield.ChunkSize;
import com.zhukovsd.endlessfield.EndlessFieldArea;
import com.zhukovsd.endlessfield.EndlessFieldSizeConstraints;
import com.zhukovsd.endlessfield.field.EndlessField;
import com.zhukovsd.endlessfield.field.EndlessFieldActionInvoker;
import com.zhukovsd.endlessfield.field.EndlessFieldChunkFactory;
import com.zhukovsd.endlessfield.fielddatasource.EndlessFieldDataSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ZhukovSD on 26.06.2016.
 */
public class MinesweeperField extends EndlessField<MinesweeperFieldCell> {
    public MinesweeperField(
            int stripes, ChunkSize chunkSize, EndlessFieldSizeConstraints sizeConstraints,
            EndlessFieldDataSource<MinesweeperFieldCell> dataSource
    ) {
        super(stripes, chunkSize, sizeConstraints, dataSource);
    }

    @Override
    protected EndlessFieldActionInvoker createActionInvoker() {
        return new MinesweeperFieldActionInvoker(this);
    }

    @Override
    protected EndlessFieldChunkFactory<MinesweeperFieldCell> createChunkFactory() {
        return new MinesweeperFieldChunkFactory(this, 15);
    }

    @Override
    protected Set<Integer> relatedChunks(Integer chunkId) {
        HashSet<Integer> result = new HashSet<>();

        EndlessFieldArea area = ChunkIdGenerator.chunkAreaById(this, chunkId);
        area = area.expandFromCenter(1);
        result.addAll(ChunkIdGenerator.chunkIdsByArea(chunkSize, area));
        result.remove(chunkId);

        return result;
    }
}
