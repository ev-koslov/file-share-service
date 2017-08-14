<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ru">
<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <%--Common imports--%>
    <jsp:include page="common/imports.jsp"></jsp:include>

    <spring:url var="css_share" value="/public/css/share.css"></spring:url>
    <link href="${css_share}" rel="stylesheet">

    <spring:url var="js_common" value="/public/js/common.js"></spring:url>
    <script src="${js_common}"></script>

    <spring:url var="js_share_view" value="/public/js/share_view.js"></spring:url>
    <script src="${js_share_view}"></script>

    <spring:url var="sockjs_js" value="/webjars/sockjs-client/1.1.2/sockjs.min.js"></spring:url>
    <script src="${sockjs_js}"></script>

    <spring:url value="/webjars/stomp-websocket/2.3.3-1/stomp.min.js" var="stomp_js"></spring:url>
    <script src="${stomp_js}"></script>


    <title>Record</title>



    <script>

        $(document).ready(function () {


            //Setting document drag events
            $(document).on({
                dragenter: function (event) {
                    return false;
                },
                dragover: function () {
                    return false;
                },
                dragleave: function (event) {
                    return false;
                },
                drop: function () {
                    return false;
                }
            });

            const recordObj = JSON.parse('${recordJSON}');

            let dataFormatter = new DataFormatter({});
            let $fileTemplate = $('#file-template');

            $fileTemplate.removeAttr('id');
            $fileTemplate.remove();

            let record = new Record(recordObj, $('#record-info'), dataFormatter);

            let container = new FilesContainer($('#record-files'), $fileTemplate, dataFormatter);

            $.each(recordObj.files, function (i, fileObj) {
                let file = container.createFile(fileObj);
                file.setState.ready();

                file.setAction(function () {
                    window.location = file.downloadLink;
                });

                container.attach(file);
            });


            if (window.WebSocket) {
                let informer = new WebSocketInformer('/informer', '/topic/'+record.id+'/', container);
            }

            //          Showing rendered page
            $('body').removeClass('hidden');


        });


    </script>
</head>
<body class="hidden">

<%--<nav class="navbar navbar-default">--%>
    <%--<div class="container">--%>
        <%--<div class="navbar-header">--%>
            <%--<a class="navbar-brand" href="#">WebSiteName</a>--%>
        <%--</div>--%>
        <%--<ul class="nav navbar-nav">--%>
            <%--<li class="active"><a href="#">Home</a></li>--%>
            <%--<li><a href="#">Page 1</a></li>--%>
            <%--<li><a href="#">Page 2</a></li>--%>
            <%--<li><a href="#">Page 3</a></li>--%>
        <%--</ul>--%>
    <%--</div>--%>
<%--</nav>--%>


<div class="container">
    <div class="panel panel-default">
        <div class="panel-body">

            <%--Record info block--%>
            <div id="record-info" style="display: inline-flex; width: 100%; align-items: baseline">
                <div data-record-link></div>
                <div style="margin-left: auto"><span>Expires in: </span><span data-record-expiry-time></span></div>
            </div>

        </div>
    </div>
</div>


<div class="container">

    <div class="panel panel-default">
        <div class="panel-body">

            <%--Record content--%>
            <div id="record-files">

                <%--This placeholder need to be shown if there are no files in a record--%>
                <div data-no-files-placeholder>

                    <div>
                        <b>В данной записи пока нет файлов.</b>
                    </div>

                </div>

            </div>

        </div>
    </div>

</div>

<div class="flex full-width file-container" data-file-id="" id="file-template" data-file-template>

    <div class="flex full-width file-body">

        <div class="flex">

            <div>
                <span class="glyphicon glyphicon-file"></span>
                <span data-file-label-name></span>
            </div>

            <div style="margin-left: auto" class="hidden-xs">
                <span data-file-label-size></span>
            </div>

        </div>

        <div class="file-progress-bar-container">
            <div style="width: 0%;" data-file-progress-bar class="file-progress-bar"></div>
        </div>


        <%--file verification status--%>
        <div class="flex file-status status-default" data-file-status-verifying="">
            <div>
                Проверка файла.
            </div>
        </div>


        <%--file pending status--%>
        <div class="flex file-status status-default" data-file-status-pending="">
            <div>
                Ожидание загрузки.
            </div>
        </div>


        <%--file uploading status--%>
        <div class="flex file-status status-info" data-file-status-uploading="">
            <div>
                Загружено
                <span data-file-upload-percentage="">50%</span>
                (<span data-file-upload-bytes=""></span> из
                <span data-file-label-size=""></span>) скорость
                <span data-file-upload-speed=""></span>/сек.
            </div>
        </div>

        <%--file upload complete status--%>
        <div class="flex file-status status-success" data-file-status-uploaded="">
            <div>
                Успешно загружено.
            </div>
        </div>


        <%--file upload cancelled status--%>
        <div class="flex file-status status-warning" data-file-status-cancelled="">
            <div>
                Загрузка отменена.
            </div>
        </div>


        <%--file upload error status--%>
        <div class="flex file-status status-danger" data-file-status-error="">
            <div>
                Ошибка загрузки: <span data-file-upload-error-message=""></span>
            </div>
        </div>

    </div>

    <div class="file-controls-container">
        <button class="btn btn-default btn-sm file-control" data-file-action-button>
            <span class="glyphicon glyphicon-cloud-download"></span>
        </button>
    </div>

</div>

</body>
</html>
