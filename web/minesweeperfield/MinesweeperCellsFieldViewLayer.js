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

var MinesweeperCellsFieldViewLayer = function(fieldView, canvasId) {
    CellsFieldViewLayer.call(this, fieldView, canvasId);
};

MinesweeperCellsFieldViewLayer.prototype = Object.create(CellsFieldViewLayer.prototype);

var inheritedDrawCell = MinesweeperCellsFieldViewLayer.prototype.drawCell;
MinesweeperCellsFieldViewLayer.prototype.drawCell = function(rect, cell, clear) {
    var c = this.imageData.renderContext;

    if (cell != null) {
        if (clear)
            c.clearRect(rect.x, rect.y, rect.width, rect.height);

        c.save();        
        if (!cell.isOpen) {
            c.beginPath();
            c.rect(rect.x, rect.y, rect.width, rect.height);
            c.fillStyle = "#E8E8E8";
            c.fill();
        } else {
            c.strokeStyle = "black";
            c.font = "12pt Arial";
            c.lineWidth = 1;
            c.textAlign = "center";
            c.textBaseline="middle";

            // todo proper alignment
            var cellSize = this.fieldView.drawSettings.cellSize;
            c.fillText(cell.neighbourMinesCount, rect.x + cellSize.width / 2, rect.y + cellSize.height / 2);
        }
        c.restore();        

        // draw cell borders in the inherited draw proc
        inheritedDrawCell.call(this, rect, cell, false);
    }
};