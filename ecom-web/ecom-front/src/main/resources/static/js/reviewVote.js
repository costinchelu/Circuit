$(document).ready(function () {
    $(".linkVoteReview").on("click", function (e) {
        e.preventDefault();
        voteReview($(this));
    });
});

function voteReview(currentLink) {
    requestURL = currentLink.attr("href");

    $.ajax({
        type: "POST",
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (voteResult) {
        console.log(voteResult);

        if (voteResult.successful) {
            $("#modalDialog").on("hide.bs.modal", function (e) {
                updateVoteCountAndIcons(currentLink, voteResult);
            });
        }

        showModalDialog("Votează recenzia", voteResult.message);

    }).fail(function () {
        showErrorModal("Eroare la votarea recenziei.");
    });
}

function updateVoteCountAndIcons(currentLink, voteResult) {
    reviewId = currentLink.attr("reviewId");
    voteUpLink = $("#linkVoteUp-" + reviewId);
    voteDownLink = $("#linkVoteDown-" + reviewId);

    $("#voteCount-" + reviewId).text(voteResult.voteCount + " voturi");

    message = voteResult.message;

    if (message.includes("Ai votat ( up")) {
        highlightVoteUpIcon(currentLink, voteDownLink);
    } else if (message.includes("Ai anulat votul ( down")) {
        highlightVoteDownIcon(currentLink, voteUpLink);
    } else if (message.includes("unvoted down")) {
        unhighlightVoteDownIcon(voteDownLink);
    } else if (message.includes("unvoted up")) {
        unhighlightVoteDownIcon(voteUpLink);
    }
}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {
    voteUpLink.removeClass("far").addClass("fas");
    voteUpLink.attr("title", "Anulează votul acestei recenzii");
    voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
    voteDownLink.removeClass("far").addClass("fas");
    voteDownLink.attr("title", "Anulează votul acestei recenzii");
    voteUpLink.removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink) {
    voteDownLink.attr("title", "Votează 👎 această recenzie");
    voteDownLink.removeClass("fas").addClass("far");
}

function unhighlightVoteUpIcon(voteUpLink) {
    voteUpLink.attr("title", "Votează 👍 această recenzie");
    voteUpLink.removeClass("fas").addClass("far");
}