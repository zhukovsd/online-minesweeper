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

    <script src="${pageContext.request.contextPath}/js/FieldManager.js"></script>

    <script>
        var contextPath = "${pageContext.request.contextPath}";
        var fieldManager = new FieldManager(contextPath);

        alert("hi guy");
    </script>
</head>
<body>
    <h1>Hi there</h1>
</body>
</html>