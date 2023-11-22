dropdownCategories = $("#category");
dropdownBrands = $("#brand");

$(document).ready(function() {

    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function() {
        dropdownCategories.empty();
        getCategories();
    });

    getCategoriesForNewForm();
});

function getCategoriesForNewForm() {
    catIdField = $("#categoryId");
    editMode = false;

    if (catIdField.length) {
        editMode = true;
    }

    if (!editMode) getCategories();
}

function getCategories() {
    let brandId = dropdownBrands.val();
    let url = brandModuleURL + "/" + brandId + "/categories";

    $.get(url, function (responseJson) {
        $.each(responseJson, function (index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
        });
    });
}

function checkUnique(form) {
    let productId = $("#id").val();
    let productName = $("#name").val();
    let csrfValue = $("input[name='_csrf']").val();
    let params = {id: productId, name: productName, _csrf: csrfValue};

    $.post(checkUniqueUrl, params, function(response) {
        if (response === "OK") {
            form.submit();
        } else if (response === "Duplicate") {
            showWarningModal("Există alt produs cu denumirea " + productName);
        } else {
            showErrorModal("Răspuns eronat primit de la server");
        }

    }).fail(function() {
        showErrorModal("Nu s-a efectuat conexiunea cu serverul");
    });

    return false;
}