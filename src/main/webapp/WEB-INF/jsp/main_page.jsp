<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ru">
<head>

    <jsp:include page="common/imports.jsp"></jsp:include>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Title</title>

    <%--Page initialization scripts--%>
    <script>
        $(document).ready(function (event) {
            var recordIdInputField = $('#recordIdInputField');
            var openRecordButton = $('#openRecordButton');

            openRecordButton.on('click', function (event) {
                window.location.href = '/view/'+recordIdInputField.val();
            });

            recordIdInputField.on('keyup', function (event) {
                 if (recordIdInputField.val().length == 0) {
                    openRecordButton.attr('disabled', true);
                } else {
                    openRecordButton.attr('disabled', false);
                }
            })

            recordIdInputField.keypress(function (event) {
                if (event.keyCode == 13) {
                    recordIdInputField.attr('disabled', true);
                    openRecordButton.focus();
                    openRecordButton.click();
                }
            })

        })
    </script>

</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3">
            <div class="panel panel-default">
                <div class="panel-body">

                    <div class="form-group">
                        <spring:url value="/create" var="createRecord"></spring:url>
                        <a href="${createRecord}" class="btn btn-default" style="width: 100%">Создать</a>
                    </div>


                    <div class="form-group">
                        <div class="input-group">
                            <input type="text" class="form-control" id="recordIdInputField"
                                   placeholder="Введите идентификатор...">
                            <span class="input-group-btn">
                                    <input type="button" value="Открыть" class="btn btn-default" id="openRecordButton" disabled="true">
                            </span>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>


</body>
</html>
