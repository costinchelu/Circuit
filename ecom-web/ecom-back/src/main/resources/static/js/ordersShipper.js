var iconNames = {
    'RIDICATA': 'fa-people-carry',
    'LIVRARE': 'fa-shipping-fast',
    'LIVRATA': 'fa-box-open',
    'RETURNATA': 'fa-undo'
};

var confirmText;
var confirmModalDialog;
var yesButton;
var noButton;

$(document).ready(function () {
    confirmText = $("#confirmText");
    confirmModalDialog = $("#confirmModal");
    yesButton = $("#yesButton");
    noButton = $("#noButton");

    $(".linkUpdateStatus").on("click", function (e) {
        e.preventDefault();
        link = $(this);
        showUpdateConfirmModal(link);
    });

    addEventHandlerForYesButton();
});

function addEventHandlerForYesButton() {
    yesButton.click(function (e) {
        e.preventDefault();
        sendRequestToUpdateOrderStatus($(this));
    });
}

function sendRequestToUpdateOrderStatus(button) {
    requestURL = button.attr("href");

    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        showMessageModal("Comanda a fost actualizată.");
        updateStatusIconColor(response.orderId, response.status);

        console.log(response);
    }).fail(function (err) {
        showMessageModal("Eroare la actualizarea stării comenzii.");
    })
}

function updateStatusIconColor(orderId, status) {
    link = $("#link" + status + orderId);
    link.replaceWith("<i class='fas " + iconNames[status] + " fa-2x icon-green'></i>");
}

function showUpdateConfirmModal(link) {
    noButton.text("Nu");
    yesButton.show();

    orderId = link.attr("orderId");
    status = link.attr("status");
    yesButton.attr("href", link.attr("href"));
    confirmText.text("Ești sigur(ă) că dorești schimbarea stării comenzii ID #" + orderId + " la " + status + "?");

    confirmModalDialog.modal();
}

function showMessageModal(message) {
    noButton.text("Închide");
    yesButton.hide();
    confirmText.text(message);
}