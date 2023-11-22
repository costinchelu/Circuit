$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        window.location = moduleURL;
        // for Thymeleaf:
        // window.location = "[[@{/users}]]";
    });

    $("#fileImage").change(function () {
        let fileSize = this.files[0].size;
        if (!checkFileSize(this)) {
            return;
        }
        showImageThumbnail(this)
    });
});

function showImageThumbnail(fileInput) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    }
    reader.readAsDataURL(file);
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal('show');
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}

function checkFileSize(fileInput) {
    let fileSize = fileInput.files[0].size;

    if (fileSize > MAX_FILE_SIZE) {
        fileInput.setCustomValidity("Imaginea trebuie să fie mai mică decât " + MAX_FILE_SIZE + " bytes!");
        fileInput.reportValidity();
        return false;
    } else {
        fileInput.setCustomValidity("");
        return true;
    }
}