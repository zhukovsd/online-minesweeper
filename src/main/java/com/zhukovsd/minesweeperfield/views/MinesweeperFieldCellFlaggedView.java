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
import com.zhukovsd.endlessfield.field.EndlessFieldCellView;
import com.zhukovsd.minesweeperfield.MinesweeperFieldCell;

/**
 * Created by ZhukovSD on 20.08.2016.
 */
public class MinesweeperFieldCellFlaggedView extends EndlessFieldCellView<MinesweeperFieldCell> {
    private boolean hasFlag = true;

    private MinesweeperFieldCellFlaggedView() {}

    public MinesweeperFieldCellFlaggedView(MinesweeperFieldCell viewSource) {}

    @Override
    public EndlessCellCloneFactory cloneFactory() {
        return source -> new MinesweeperFieldCellFlaggedView();
    }
}
