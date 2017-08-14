
    //class makes client-server file processing
const FileProcessor = function (record, filesContainer) {
        let verifyQueue = [],
            uploadQueue = [],
            verifyRunning = false,
            uploadRunning = false,
            uploadProcessor = this,
            $fileSelector = $("<input type='file' multiple/>");

        //setting onchange listener
        $fileSelector.change(function (event) {
            let files = $fileSelector.prop('files');
            $.each(files, function (i, file) {
                uploadProcessor.upload(file);
            })

            $fileSelector.val('');
        });

        this.openFileSelectionDialog = function () {
            $fileSelector.click();
        };

        //function takes data transfer file and perform its upload to server
        this.upload = function (dataTransferFile) {
            //instantiating file object
            var file = filesContainer.createFile({
                id: 0,
                originalName: dataTransferFile.name,
                mimeType: dataTransferFile.type,
                size: dataTransferFile.size,
                recordId: record.id,
            });

            //adding datatransfer file to new object as a field
            file.dataTransferFile = dataTransferFile;

            //setting file status to PENDING (verified, but not uploaded yed)
            file.setState.verifying();

            file.setAction(function () {
                cancelUpload(file);
            })

            //attaching file to container (showing it)
            filesContainer.attach(file);

            //starting upload procedure
            doVerify(file);
        };

        const doVerify = function (file) {
            //adding file verify queue
            verifyQueue.push(file);

            //if verification task is not running, starting it
            if (!verifyRunning) {
                runVerifier();
            }
        };

        const runVerifier = function () {
            //setting verifyrunning flag
            verifyRunning = true;

            //retrieving first file from verifyQueue
            let file = verifyQueue.shift();

            //if there is no file, return and stop processing
            if (file === undefined) {
                verifyRunning = false;
                return;
            }

            //sending query to server
            var ajax = $.ajax({
                type: "POST",
                processData: false,
                contentType: 'application/json',
                url: '/api/files.verify',
                data: JSON.stringify(file.info),
            });

            //if request successful we receive an metadataId
            ajax.done(function (metadataId) {
                //adding metadataId field to file
                file.metadataId = metadataId;

                file.setState.pending();

                doUpload(file);
            });

            ajax.fail(function (jqXHR, textStatus) {
                file.setState.error();
            });

            ajax.always(function () {
                //making next verify iteration using recursion
                runVerifier();
            });

        };

        const doUpload = function (file) {
            //adding file upload queue
            uploadQueue.push(file);

            //if verification task is not running, starting it
            if (!uploadRunning) {
                runUploader();
            }
        };

        const runUploader = function () {
            //setting uploadRunning flag
            uploadRunning = true;

            //retrieving first file from uploadQueue
            var file = uploadQueue.shift();

            //if there is no file, return and stop processing
            if (file === undefined) {
                uploadRunning = false;
                return;
            }

            //setting file status to UPLOAD_STARTED (system is preparing file to upload)
            file.setState.uploadStarted();

            //sending query to server


            var formData = new FormData();

            formData.append('metadataId', file.metadataId);
            formData.append('file', file.dataTransferFile);

            file.ajax = $.ajax({
                type: "POST",
                processData: false,
                contentType: false,
                url: '/api/files.upload',
                data: formData,

                //configuring upload watcher
                xhr: function () {
                    var xhr = $.ajaxSettings.xhr();

                    var prevPercentage = 0,
                        currentPercentage = 0,
                        prevBytesLoaded = 0,
                        currentBytesLoaded,
                        prevTime = Date.now(),
                        currentTime,
                        currentSpeed = 0;

                    //setting upload progress watcher
                    xhr.upload.onprogress = function (progressEvent) {
                        if (progressEvent.lengthComputable) {
                            //getting current values
                            currentBytesLoaded = progressEvent.loaded;
                            currentTime = Date.now();
                            //calculating current percentage
                            currentPercentage = Math.round(((currentBytesLoaded * 100) / progressEvent.total));

                            if ((currentPercentage > prevPercentage) || (currentTime - prevTime >= 1000)) {

                                //calculating current upload speed (in bytes/sec) using previous measurements.
                                currentSpeed = Math.round((prevBytesLoaded - currentBytesLoaded) / ((prevTime - currentTime) / 1000));

                                file.setState.uploadProgress(currentPercentage, currentBytesLoaded, currentSpeed);

                                //updating previous values
                                prevPercentage = currentPercentage;
                                prevBytesLoaded = currentBytesLoaded;
                                prevTime = currentTime;
                            }

                        }
                    }

                    return xhr;
                }

            });

            //server will return populated file object on request success
            file.ajax.done(function (result) {

                //updating file and setting status UPLOAD_COMPLETE (file was successfully stoired on server)
                file.update(result);
                file.setState.uploadCompleted();

//                    TODO: MAKE FILE DELETION
                file.setAction(function () {
                    uploadProcessor.delete(file.id);
                    filesContainer.remove(file);
                })
            });


            file.ajax.fail(function (jqXHR, textStatus) {
                if (textStatus !== 'abort') {

                    file.setState.error();

                    //TODO: add "retry upload" option
                    file.setAction(function () {
                        filesContainer.remove(file);
                    });

                }
            });

            file.ajax.always(function () {

                //remove upload aware fields from file object
                delete file.metadataId;
                delete file.dataTransferFile;
                delete file.ajax;

                //continue uploading
                runUploader();
            });

        };

        const cancelUpload = function (file) {

            //if file had not verified yet
            if (verifyQueue.indexOf(file) >= 0) {
                //removing it from queue
                verifyQueue.splice(verifyQueue.indexOf(file), 1);
            }

            //if file was verified, but not uploaded yed
            if (uploadQueue.indexOf(file) >= 0) {
                //removing it from queue
                uploadQueue.splice(uploadQueue.indexOf(file), 1);
            }

            //if file has an ajax field, assuming that upload was started already
            if (file.ajax !== undefined) {
                //aborting ajax
                file.ajax.abort();
            }

            file.setState.cancelled();

            file.setAction(function () {
                filesContainer.remove(file);
            })
        };

        //removes file from server
        this.delete = function () {

            var fileIds = '';

            $.each(arguments, function (i, id) {
                fileIds += (id+',');
            });

            var qString = 'recordId='+record.id+'&fileIds='+fileIds.slice(0, fileIds.length-1);

            ////TODO: add file deletion processing
            $.ajax({
                type: "POST",
                processData: false,
                url: '/api/files.delete',
                data: qString,
            }).done(function (updatedRecord) {
                record.update(updatedRecord);
            });
        };
    };

const UploadZone = function(uploadZoneHtmlTemplate, openUZwatchableHtmlElement, fileProcessor) {

    //shows drop zone
    const showZone = function () {
        uploadZoneHtmlTemplate.css('width', window.innerWidth);
        uploadZoneHtmlTemplate.css('height', window.innerHeight);

        uploadZoneHtmlTemplate.removeClass('hidden');
    }

    //hides drop zone
    const hideZone = function () {
        uploadZoneHtmlTemplate.addClass('hidden');
    }

    //processed files obtained from drag-drop
    const processFilesFromDrop = function (event) {
        event.preventDefault();
        var files = event.originalEvent.dataTransfer.files;

        $.each(files, function (i, dataTransferFile) {
            fileProcessor.upload(dataTransferFile);
        });
    }

           //set dropzone event handling
           $(uploadZoneHtmlTemplate).on({
               dragleave: function () {
                   hideZone();
               },
               drop: function (event) {
                   processFilesFromDrop(event);
                   hideZone();
               }
           });


    $(openUZwatchableHtmlElement).on({
        dragover: function (event) {
            showZone();
            return false;
        },
    });

}