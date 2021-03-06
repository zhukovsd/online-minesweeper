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
import com.zhukovsd.endlessfield.field.EndlessFieldCellView;
import com.zhukovsd.endlessfield.field.EndlessFieldCellViewFactory;
import com.zhukovsd.minesweeperfield.views.MinesweeperFieldCellBlownView;
import com.zhukovsd.minesweeperfield.views.MinesweeperFieldCellClosedView;
import com.zhukovsd.minesweeperfield.views.MinesweeperFieldCellFlaggedView;
import com.zhukovsd.minesweeperfield.views.MinesweeperFieldCellOpenedView;

/**
 * Created by ZhukovSD on 26.06.2016.
 */
public class MinesweeperFieldCell extends EndlessFieldCell<MinesweeperFieldCell> {
    private boolean isOpen = false;
    private boolean hasMine;
    private boolean hasFlag = false;
    private boolean mineBlown = false;
    private int neighbourMinesCount = -1;

    public MinesweeperFieldCell(boolean hasMine) {
        this.hasMine = hasMine;
    }

    // clone constructor. should be called only if source is locked, otherwise transitional state may be cloned
    private MinesweeperFieldCell(EndlessFieldCellView source) {
        MinesweeperFieldCell casted = ((MinesweeperFieldCell) source);
        this.isOpen = casted.isOpen;
        this.hasMine = casted.hasMine;
        this.hasFlag = casted.hasFlag;
        this.mineBlown = casted.mineBlown;
        this.neighbourMinesCount = casted.neighbourMinesCount;
    }

    @Override
    public EndlessCellCloneFactory cloneFactory() {
        return MinesweeperFieldCell::new;
    }

    @Override
    public EndlessFieldCellViewFactory viewFactory() {
        return (cell) -> {
            if (this.hasFlag())
                return new MinesweeperFieldCellFlaggedView(this);
            else if (this.mineBlown()) {
                return new MinesweeperFieldCellBlownView(this);
            } else if (!this.isOpen())
                return new MinesweeperFieldCellClosedView(this);
            else
                return new MinesweeperFieldCellOpenedView(this);
        };
    }

    public boolean hasMine() {
        return hasMine;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public boolean mineBlown() {
        return mineBlown;
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

    public void setFlag() {
        if (!hasFlag && hasMine && !mineBlown) {
            hasFlag = true;
        }
    }

    public void blowMine() {
        if (!hasFlag && hasMine) {
            mineBlown = true;
        }
    }
}
