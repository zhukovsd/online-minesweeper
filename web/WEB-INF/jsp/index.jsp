<%--
  ~     This file is part of online-minesweeper.
  ~
  ~     online-minesweeper is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     (at your option) any later version.
  ~
  ~     online-minesweeper is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with online-minesweeper.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>

    <link href="${pageContext.request.contextPath}/style.css" rel="stylesheet" type="text/css" />

    <script src="${pageContext.request.contextPath}/js/FieldManager.js"></script>
    <script src="${pageContext.request.contextPath}/js/FieldView.js"></script>
    <script src="${pageContext.request.contextPath}/js/FieldViewLayerImageData.js"></script>
    <script src="${pageContext.request.contextPath}/js/AbstractFieldViewLayer.js"></script>
    <script src="${pageContext.request.contextPath}/js/CellsFieldViewLayer.js"></script>
    <script src="${pageContext.request.contextPath}/js/PlayersLabelsFieldViewLayer.js"></script>
    <script src="${pageContext.request.contextPath}/js/MouseEventListener.js"></script>

    <script src="${pageContext.request.contextPath}/js/ChunkIdGenerator.js"></script>
    <script src="${pageContext.request.contextPath}/js/Camera.js"></script>
    <script src="${pageContext.request.contextPath}/js/CameraPosition.js"></script>
    <script src="${pageContext.request.contextPath}/js/Scope.js"></script>
    <script src="${pageContext.request.contextPath}/js/ChunksScope.js"></script>
    <script src="${pageContext.request.contextPath}/js/AddressBarManager.js"></script>
    <script src="${pageContext.request.contextPath}/js/ActionMessage.js"></script>
    <script src="${pageContext.request.contextPath}/js/SimpleBiMap.js"></script>

    <%--<script src="${pageContext.request.contextPath}/js/SimpleField/SimpleFieldManager.js"></script>--%>
    <script src="${pageContext.request.contextPath}/minesweeperfield/MinesweeperMouseEventListener.js"></script>
    <script src="${pageContext.request.contextPath}/minesweeperfield/MinesweeperCellsFieldViewLayer.js"></script>

    <script>
        var contextPath = "${pageContext.request.contextPath}";

        var fieldManager = new FieldManager(contextPath);
        var fieldView = new FieldView(fieldManager, 'field-canvas-container', new DrawSettings(30, 30));
        fieldView.addLayer('cells-layer', new MinesweeperCellsFieldViewLayer(fieldView, 'field-cells-layer-canvas'));
        fieldView.addLayer('players-labels-layer', new PlayersLabelsFieldViewLayer(fieldView, 'field-players-labels-layer-canvas'));

        var mouseEventListener = new MinesweeperMouseEventListener(fieldView, 'players-labels-layer');
        var uriManager = new AddressBarManager(contextPath + '/game/');

	    fieldView.getLayer('players-labels-layer').mouseListener = mouseEventListener;

        fieldManager.onStateChange = function() {
            switch (fieldManager.state) {
                case (FieldManagerState.CONNECTED): {
                    if (localStorage["cameraPosition"] !== undefined) {
                        var storedPosition = JSON.parse(localStorage["cameraPosition"]);
                    }

                    var uriChunkId = uriManager.getChunkId();

                    var cameraPosition;
                    if (uriChunkId == null) {
                        // if no chunk id specified in the URI (/path/game/<chunkId>)
                        cameraPosition = new CameraPosition(fieldManager.initialChunkId, 0, 0);
                    } else {
                        // if stored chunk id differs from uri chunk id, set camera to left-top corner of uri id
                        if ((storedPosition === undefined) || (uriChunkId != storedPosition.originChunkId)) {
                            cameraPosition = new CameraPosition(uriChunkId, 0, 0);
                        } else {
                            cameraPosition = new CameraPosition(
                                    storedPosition.originChunkId, storedPosition.shift.x, storedPosition.shift.y
                            );
                        }
                    }

                    fieldView.camera.setPosition(cameraPosition);
                    fieldView.updateExpandedScopeChunkIds();
                }

//                case (FieldManagerState.LOADED): {
//                     fieldView.paint();
//                }
            }
        };

        var cellsLayer = fieldView.getLayer('cells-layer');
        var labelsLayer = fieldView.getLayer('players-labels-layer');
        fieldManager.onChunksReceived = function(chunkIds) {
            fieldView.forEachLayer(function(layer) {
                layer.renderByChunkIds(chunkIds);
                layer.display();
            });
        };

        fieldManager.OnActionMessageReceived = function (positions) {
            cellsLayer.renderByPositions(positions);
            cellsLayer.display();

            labelsLayer.refresh();
        };

        fieldView.camera.onPositionChanged = function(position) {
            uriManager.setChunkId(position.originChunkId);
            localStorage["cameraPosition"] = JSON.stringify(position);
        };

        // TODO websocket close/error events
    </script>
</head>
<body>
    <div class="unselectable" id="field-canvas-container">
        <canvas id="field-cells-layer-canvas"></canvas>
        <canvas id="field-players-labels-layer-canvas"></canvas>
    </div>

    <div style="position: absolute; left: 20px; top: 20px; width: 600px; height: 200px; background-color: rgba(240, 255, 255, 0.8); z-index: 100;">
        <h3>Hi! Your session Id = <%= request.getSession().getId() %></h3>
        <div>canvas size = <span id="canvas-size"></span></div>
        <div>camera position = <span id="camera-position"></span></div>
        <div>camera scope = <span id="camera-scope"></span></div>
        <div>chunks scope = <span id="chunks-scope"></span></div>
        <input type="button" value="requestChunks()" onclick="fieldManager.requestChunks();">
        <input type="button" value="draw" onclick="fieldView.drawCellsByChunkIds([0, 1]);">
        <input type="text" name="chunk" id="chunk_id_text" value="0">
    </div>
</body>
</html>