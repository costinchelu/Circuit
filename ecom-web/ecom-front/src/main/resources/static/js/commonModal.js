function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}

function showErrorModal(message) {
    showModalDialog("Eroare", message);
}

function showWarningModal(message) {
    showModalDialog("Atenție", message);
}