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

import com.zhukovsd.endlessfield.field.EndlessCellCloneFactory;
import com.zhukovsd.endlessfield.field.EndlessFieldCell;

/**
 * Created by ZhukovSD on 26.06.2016.
 */
public class MinesweeperFieldCell extends EndlessFieldCell {
    private boolean isOpen = false;
    // TODO: 01.07.2016 private
    public boolean hasMine;
    private int neighbourMinesCount = -1;

    public MinesweeperFieldCell(boolean hasMine) {
        this.hasMine = hasMine;
    }

    // clone constructor. should be called only if source is locked, otherwise transitional state may be cloned
    private MinesweeperFieldCell(EndlessFieldCell source) {
        super(source);
    }

    @Override
    public EndlessCellCloneFactory getFactory() {
        return MinesweeperFieldCell::new;
    }

    public boolean hasMine() {
        return hasMine;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int neighbourMinesCount() {
        return neighbourMinesCount;
    }

    public void setNeighbourMinesCount(int count) {
        neighbourMinesCount = count;
    }

    public void open() {
        if (!hasMine())
            isOpen = true;
    }
}
