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

package com.zhukovsd.minesweeperfield.views;

import com.zhukovsd.endlessfield.field.EndlessCellCloneFactory;
import com.zhukovsd.endlessfield.field.EndlessFieldCell;
import com.zhukovsd.endlessfield.field.EndlessFieldCellView;
import com.zhukovsd.minesweeperfield.MinesweeperFieldCell;

/**
 * Created by ZhukovSD on 21.08.2016.
 */
public class MinesweeperFieldCellOpenedView extends EndlessFieldCellView<MinesweeperFieldCell> {
    private boolean isOpen = true;
    private int neighbourMinesCount;

    public MinesweeperFieldCellOpenedView(MinesweeperFieldCell viewSource) {
        neighbourMinesCount = viewSource.neighbourMinesCount();
    }

    // clone constructor. should be called only if source is locked, otherwise transitional state may be cloned
    private MinesweeperFieldCellOpenedView(EndlessFieldCellView source) {
        MinesweeperFieldCellOpenedView casted = ((MinesweeperFieldCellOpenedView) source);
        this.isOpen = casted.isOpen;
        this.neighbourMinesCount = casted.neighbourMinesCount;
    }

    @Override
    public EndlessCellCloneFactory cloneFactory() {
        return MinesweeperFieldCellOpenedView::new;
    }
}
