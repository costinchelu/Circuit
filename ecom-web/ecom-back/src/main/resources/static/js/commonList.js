function clearFilter() {
    window.location = moduleURL;
}

function showDeleteConfirmModal(link, entityName) {
    let entityId = link.attr("entityId");

    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text(" Sunteți sigur(ă) că doriți ștergerea intrării " + entityName + " ID " + entityId + "?");
    $("#confirmModal").modal();
}