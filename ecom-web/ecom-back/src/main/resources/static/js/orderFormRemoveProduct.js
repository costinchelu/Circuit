$(document).ready(function() {
    $("#productList").on("click", ".linkRemove", function(e) {
        e.preventDefault();

        if (doesOrderHaveOnlyOneProduct()) {
            showWarningModal("Produsul nu poate fi șters. Comanda trebuie să conțină cel puțin un produs..");
        } else {
            removeProduct($(this));
            updateOrderAmounts();
        }
    });
});

function removeProduct(link) {
    rowNumber = link.attr("rowNumber");
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();

    $(".divCount").each(function(index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

function doesOrderHaveOnlyOneProduct() {
    productCount = $(".hiddenProductId").length;
    return productCount === 1;
}