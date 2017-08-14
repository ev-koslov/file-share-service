//DataFormatter class does value formatting
const DataFormatter = function (library) {
    this.formatSize = function (sizeInBytes) {
        var divider = 1,
            suffix,
            fixed = 0;

        if (sizeInBytes <= 1024) {
            suffix = library.bytes;
        }

        if (sizeInBytes > 1024) {
            divider *= 1024;
            suffix = library.kilobytes;
            fixed = 2;
        }

        if (sizeInBytes > 1048576) {
            divider *= 1024;
            suffix = library.megabytes;
        }

        if (sizeInBytes > 1073741824) {
            divider *= 1024;
            suffix = library.gigabytes;
        }

        var formattedSize = Number(sizeInBytes / divider).toFixed(fixed) + ' ' + suffix;

        return formattedSize;
    },

        this.formatDate = function (timeUtcLong) {

        },

//            TODO: FIX time conversion
        this.formatEstimatedTime = function (timeUtcLong) {
            var temp = timeUtcLong - Date.now();

            //converting value to seconds
            temp = Math.floor(temp / 1000);
            var seconds = temp % 60;

            //converting value to minutes
            temp = Math.floor(temp / 60);
            var minutes = temp % 60;

            //converting value to hours
            temp = Math.floor(temp / 60);
            var hours = temp % 24;

            //converting value to days
            temp = Math.floor(temp / 24);
            var days = temp;

            var formatted = [];

            (days > 0)
                ? (days % 10 == 1)
                    ? formatted.push(days + ' ' + library.day + ' ')
                    : formatted.push(days + ' ' + library.days + ' ')

                : undefined;

            (hours > 9) ? formatted.push(hours) : formatted.push('0' + hours);
            formatted.push(':');
            (minutes > 9) ? formatted.push(minutes) : formatted.push('0' + minutes);
            formatted.push(':');
            (seconds > 9) ? formatted.push(seconds) : formatted.push('0' + seconds);

            return formatted.join('');
        }
};


//      JS representation of  record
const Record = function (recordObj, htmlTemplate, dataFormatter) {
    this.id, this.expiryTime;

    //timerObject
    let expiryTimeUpdater = null;

    let gui = {
        $id: $(htmlTemplate).find('[data-record-link]'),
        $expiryTime: $(htmlTemplate).find('[data-record-expiry-time]'),

        setId: function (id) {
            this.$id.text(id);
        },
        setExpiry: function (expiryTime) {
            this.$expiryTime.text(expiryTime);
        },
    };

    this.update = function (source) {
        this.id = source.id;
        this.expiryTime = source.expiryTime;

        gui.setId(this.id);
        gui.setExpiry(dataFormatter.formatEstimatedTime(source.expiryTime));

        //enabling time countdown
        if (expiryTimeUpdater != null) {
            clearInterval(expiryTimeUpdater);
        }

        expiryTimeUpdater = setInterval(function () {
            gui.setExpiry(dataFormatter.formatEstimatedTime(source.expiryTime));
        }, 1000);
    }

    this.update(recordObj);
}


//      JS representation of record file
const File = function (fileObj, htmlTemplate, dataFormatter) {

    //file info variables
    this.id, this.originalName, this.mimeType, this.size, this.downloadLink, this.recordId, this.info;

    //all of objects and methods used to reflect file changes to HTML. All of vars with prepending $ are html nodes
    let gui = {
        $name: $(htmlTemplate).find('[data-file-label-name]'),
        $size: $(htmlTemplate).find('[data-file-label-size]'),

        setName: function (name) {
            this.$name.text(name);
        },
        setSize: function (size) {
            this.$size.text(dataFormatter.formatSize(size));
        },

        progressBar: {
            $node: $(htmlTemplate).find('[data-file-progress-bar]'),
            reset: function () {
                this.$node.prop('class', 'file-progress-bar');
                this.$node.css('width', '0');
            },
            set: function (percentage, cssClassesString) {
                this.$node.prop('class', 'file-progress-bar ' + cssClassesString);
                this.$node.css('width', percentage + '%');
            },
        },

        statuses: {
            verifying: {
                $node: $(htmlTemplate).find('[data-file-status-verifying]'),
                show: function () {
                    this.$node.removeClass('hidden');
                },
                hide: function () {
                    this.$node.addClass('hidden');
                },
            },

            pending: {
                $node: $(htmlTemplate).find('[data-file-status-pending]'),
                show: function () {
                    this.$node.removeClass('hidden');
                },
                hide: function () {
                    this.$node.addClass('hidden');
                },
            },

            uploading: {
                $node: $(htmlTemplate).find('[data-file-status-uploading]'),
                $percentage: $(htmlTemplate).find('[data-file-upload-percentage]'),
                $uploaded: $(htmlTemplate).find('[data-file-upload-bytes]'),
                $speed: $(htmlTemplate).find('[data-file-upload-speed]'),
                setPercentage: function (percentage) {
                    this.$percentage.text(percentage + "%");
                },
                setUploaded: function (uploaded) {
                    this.$uploaded.text(dataFormatter.formatSize(uploaded));
                },
                setSpeed: function (uploadSpeed) {
                    this.$speed.text(dataFormatter.formatSize(uploadSpeed));
                },
                show: function () {
                    this.$node.removeClass('hidden');
                },
                hide: function () {
                    this.$node.addClass('hidden');
                },
            },

            uploaded: {
                $node: $(htmlTemplate).find('[data-file-status-uploaded]'),
                show: function () {
                    this.$node.removeClass('hidden');
                },
                hide: function () {
                    this.$node.addClass('hidden');
                },
            },

            cancelled: {
                $node: $(htmlTemplate).find('[data-file-status-cancelled]'),
                show: function () {
                    this.$node.removeClass('hidden');
                },
                hide: function () {
                    this.$node.addClass('hidden');
                },
            },

            error: {
                $node: $(htmlTemplate).find('[data-file-status-error]'),
                $message: $(htmlTemplate).find('[data-file-upload-error-message]'),
                setMessage: function (message) {
                    this.$message.text(message);
                },
                show: function () {
                    this.$node.removeClass('hidden');
                },
                hide: function () {
                    this.$node.addClass('hidden');
                },
            },

            hideAll: function () {
                //hides all message blocks
                this.verifying.hide();
                this.pending.hide();
                this.uploading.hide();
                this.uploaded.hide();
                this.cancelled.hide();
                this.error.hide();
            },
        },

        button: {
            $node: $(htmlTemplate).find('[data-file-action-button]'),
            set: function (func) {
                this.$node.off('click');
                this.$node.on('click', func);
                this.$node.removeClass('hidden');
            },
            disable: function () {
                this.$node.off('click');
                this.$node.addClass('hidden');
            }
        },
    };

    this.setState = {
        ready: function () {
            gui.progressBar.set(100, 'progress-bar-success');
            gui.statuses.hideAll();
        },

        verifying: function () {
            gui.statuses.hideAll();
            gui.statuses.verifying.show();
        },

        pending: function () {
            gui.progressBar.reset();
            gui.statuses.hideAll();
            gui.statuses.pending.show();
        },

        uploadStarted: function () {
            gui.progressBar.set(0, 'progress-bar-info');
            gui.statuses.hideAll();

            gui.statuses.uploading.setPercentage(0);
            gui.statuses.uploading.setUploaded(0);
            gui.statuses.uploading.setSpeed(0);

            gui.statuses.uploading.show();
        },

        uploadProgress: function (percentage, uploaded, speed) {

            //updating upload info
            gui.progressBar.set(percentage, 'progress-bar-info');

            gui.statuses.uploading.setPercentage(percentage);
            gui.statuses.uploading.setUploaded(uploaded);
            gui.statuses.uploading.setSpeed(speed);
        },

        uploadCompleted: function () {
            gui.progressBar.set(100, 'progress-bar-success');
            gui.statuses.hideAll();
            gui.statuses.uploaded.show();
        },

        cancelled: function () {
            gui.progressBar.set(100, 'progress-bar-warning');
            gui.statuses.hideAll();
            gui.statuses.cancelled.show();
        },

        error: function (message) {
            gui.progressBar.set(100, 'progress-bar-danger');
            gui.statuses.hideAll();

            if (message != undefined) {
                gui.statuses.error.setMessage(message);
            }

            gui.statuses.error.show();
        },
    };

//          set action to button
    this.setAction = function (action) {
        if (action != undefined && action != null) {
            gui.button.set(action);
        } else {
            gui.button.disable();
        }
    };

    //update data from object
    this.update = function (source) {
        this.id = source.id;
        this.originalName = source.originalName;
        this.mimeType = source.mimeType;
        this.size = source.size;
        this.downloadLink = source.downloadLink;
        this.recordId = source.recordId;
        this.info = source;

        gui.setName(this.originalName);
        gui.setSize(this.size);
    };

    //attach object gui to given parent DOM
    this.show = function (parent) {
        $(parent).append(htmlTemplate);
    }

    //hides and detaches gui component from DOM
    this.hide = function () {
        $(htmlTemplate).remove();
    }

    //performing object initialization

    //hiding all text statuses
    gui.statuses.hideAll();

    this.update(fileObj);
}


//        JS representation of files container
const FilesContainer = function (containerHtmlTemplate, fileHtmlTemplate, dataFormatter) {
    var $noFilesPlaceholder = $(containerHtmlTemplate).find('[data-no-files-placeholder]');
    //array of attached files
    var files = [];

    //function shows placeholder if there are no files in a record
    var showPlaceholderIfNeeded = function () {
        if ($(containerHtmlTemplate).find('[data-file-template]').length == 0) {
            $noFilesPlaceholder.removeClass('hidden');
        } else {
            $noFilesPlaceholder.addClass('hidden');
        }
    }

    showPlaceholderIfNeeded();

    //adding observer to listen RecordFiles container DOM tree changes
    new MutationObserver(function (mutations) {
        mutations.forEach(function (mutation) {
            showPlaceholderIfNeeded();
        });
    }).observe($(containerHtmlTemplate).get(0), {childList: true});


    //find file by its id
    this.getById = function (fileId) {
        let file = files.find(function (elem) {
            return elem.id = fileId;
        })
        return file;
    }

    this.createFile = function (fileObj) {
        return new File(fileObj, $(fileHtmlTemplate).clone(), dataFormatter);
    }

    this.attach = function (file) {
        files.push(file);
        file.show(containerHtmlTemplate);
    }

    this.remove = function (file) {
        files.splice(files.indexOf(file), 1);
        file.hide();
    }
}