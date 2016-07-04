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

/**
 * Created by ZhukovSD on 04.07.2016.
 */

var MinesweeperFieldActionType = {
    OPEN_CELL: 0,

    selectByMouseButton: function(mouseButton) {
        switch(mouseButton) {
            case MouseButton.LEFT: return MinesweeperFieldActionType.OPEN_CELL;
        }
    }
};

var MinesweeperMouseEventListener = function(fieldView, fieldViewTopLayerName) {
    MouseEventListener.call(this, fieldView, fieldViewTopLayerName);
};
MinesweeperMouseEventListener.prototype = Object.create(MouseEventListener.prototype);

MinesweeperMouseEventListener.prototype.cellClicked = function(mouseButton, cellPosition) {
    var action = MinesweeperFieldActionType.selectByMouseButton(mouseButton);

    if (action !== undefined) {
        this.fieldManager.sendMessage(
            new ActionMessage(cellPosition, action)
        );
    }
};