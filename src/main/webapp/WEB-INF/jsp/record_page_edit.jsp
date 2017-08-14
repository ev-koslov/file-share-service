<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ru">
<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <%--Common imports--%>
    <jsp:include page="common/imports.jsp"/>

    <spring:url var="css_share" value="/public/css/share.css"></spring:url>
    <link href="${css_share}" rel="stylesheet">

    <spring:url var="js_common" value="/public/js/common.js"></spring:url>
    <script src="${js_common}"></script>

    <spring:url var="js_share_edit" value="/public/js/share_edit.js"></spring:url>
    <script src="${js_share_edit}"></script>

    <title>Record</title>



    <script>

        <%--Page initialization--%>
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
            let fileProcessor = new FileProcessor(record, container);
            let uploadZone = new UploadZone($('#upload-zone'), document, fileProcessor);

            $.each(recordObj.files, function (i, fileObj) {
                let file = container.createFile(fileObj);
                file.setState.ready();

                file.setAction(function () {
                    fileProcessor.delete(file.id);
                    container.remove(file);
                });

                container.attach(file);
            });


            $('[data-manual-file-selection]').on('click', function () {
                fileProcessor.openFileSelectionDialog();
            })


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

            <%--&lt;%&ndash;Record info block&ndash;%&gt;--%>
            <%--<div id="record-info" style="display: inline-flex; width: 100%; align-items: baseline">--%>
                <%--<div>--%>
                    <%--&lt;%&ndash;Building record link&ndash;%&gt;--%>
                    <%--http://${header['host']}/get/<span data-record-link></span>--%>
                <%--</div>--%>

                <%--<div style="margin-left: auto">--%>
                    <%--<span>Expires in: </span><span data-record-expiry-time></span>--%>
                <%--</div>--%>

                <%--<div>--%>
                    <%--<button class="btn btn-default btn-sm" data-manual-file-selection><span class="glyphicon glyphicon-cloud-upload"></span> Добавить файлы</button>--%>
                <%--</div>--%>
            <%--</div>--%>


                <div id="record-info">


                    <div class="row">

                        <%--Record info block--%>
                        <div class="col-sm-6">

                            <%--Record view URL--%>
                            <div>
                                <span class="glyphicon glyphicon-link btn-link"></span>
                                ${header['host']}/get/<span data-record-link></span>
                            </div>


                            <%--Record expiration countdown--%>
                            <div style="margin-left: auto">
                                <span class="glyphicon glyphicon-time btn-link"></span> <span data-record-expiry-time></span>
                            </div>

                            
                        </div>


                    </div>


                </div>

        </div>

    </div>
</div>


<div class="container">

    <div class="panel panel-default">
        <div class="panel-body">

            <%--Record files--%>
            <div id="record-files">

                <%--This placeholder need to be shown if there are no files in a record--%>
                <div class="hidden" data-no-files-placeholder>

                    <div>
                        <b>Перетащите файлы сюда или
                            <button class="btn btn-default btn-sm" data-manual-file-selection>
                                <span class="glyphicon glyphicon-cloud-upload"></span>
                                нажмите для выбора файлов
                            </button>
                        </b>
                    </div>

                </div>


            </div>

        </div>
    </div>

</div>

<%--Record file template--%>
<div class="flex full-width file-container" id="file-template" data-file-template>

    <div class="flex full-width file-body">

        <div class="flex">

            <div>
                <span class="glyphicon glyphicon-file"></span>
                <span data-file-label-name=""></span>
            </div>

            <div style="margin-left: auto" class="hidden-xs">
                <span data-file-label-size=""></span>
            </div>

        </div>

        <div class="file-progress-bar-container">
            <div style="width: 0%;" data-file-progress-bar="" class="file-progress-bar"></div>
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

    <%--file action button--%>
    <div class="file-controls-container">
        <button class="btn btn-default btn-sm file-control" data-file-action-button="">
            <span class="glyphicon glyphicon-remove"></span>
        </button>
    </div>


    <%--END File template--%>
</div>

<%--Zone will be shown when Drag event occurs--%>
<div id="upload-zone" class="hidden upload-zone">
    <div class="upload-zone-backgound"></div>

    <div style="display: flex; height: 100%; align-items: center; justify-content: center">

        <div>

            <b>
                Отпустите кнопку для загрузки
                <span class="glyphicon glyphicon-cloud-fileMetadata"/>
            </b>

        </div>

    </div>

</div>

</body>

</html>
