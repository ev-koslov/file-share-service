//        WebSocket informer
var WebSocketInformer = function (webSocketURL, subscribeURL, filesContainer) {
    let watchingFiles = [],
        websocket = new SockJS(webSocketURL),
        client = Stomp.over(websocket);

    client.connect({}, function (frame) {
        client.subscribe(subscribeURL, function (message) {
            resolveAction(JSON.parse(message.body));
        });
    });

    const getWatchFile = function (metadataId) {
        return watchingFiles.find(function (elem) {
            return elem.metadataId === metadataId;
        });
    }


    const createWatchFile = function(fileInfo) {
        let file = filesContainer.createFile(fileInfo);
        file.metadataId = fileInfo.metadataId;
        watchingFiles.push(file);

        file.setAction(null);

        filesContainer.attach(file);

        return file
    }

    var resolveAction = function (message) {
        var attachment = message.object;
        switch (message.action) {
            case 'UPLOAD_PENDING': {

                let file = createWatchFile(attachment);

                file.setState.pending();

                break;
            }

            case  'UPLOAD_STARTED': {

                let file = getWatchFile(attachment.metadataId);

                if (file === undefined){
                    file = createWatchFile(attachment);
                }

                file.setState.uploadStarted();

                break;
            }

            case  'UPLOAD_PROGRESS': {

                let file = getWatchFile(attachment.metadataId);

                if (file === undefined){
                    file = createWatchFile(attachment);
                }

                file.setState.uploadProgress(attachment.percentage, attachment.uploaded, attachment.uploadSpeed);

                break;
            }

            case 'UPLOAD_COMPLETE': {

                let file = getWatchFile(attachment.metadataId);

                if (file === undefined){
                    file = createWatchFile(attachment);
                }

                watchingFiles.splice(watchingFiles.indexOf(file), 1);

                delete file.metadataId;

                file.setState.uploadCompleted();

                file.setAction(function () {
                    window.location = attachment.downloadLink;
                });

                break;
            }

            case 'FILE_REMOVED': {
                let file;

                if (attachment.metadataId != undefined) {
                    file = getWatchFile(attachment.metadataId);

                    if (file !== undefined) {
                        watchingFiles.splice(watchingFiles.indexOf(file), 1);
                    }
                }


                if (attachment.id !== 0){
                    filesContainer.remove(filesContainer.getById(attachment.id));
                } else {
                    filesContainer.remove(file);
                }

                break;
            }
        }
    }

}