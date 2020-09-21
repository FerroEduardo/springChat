function openNav() {
    document.getElementById("sideNav").style.width = "250px";
}

function closeNav() {
    document.getElementById("sideNav").style.width = "0";
}

function checkIfUserExists(username, btnSubmit, btnCheck, element) {
    fetch(`/admin/userexists?username=${username}`).then(function (response) {
        let contentType = response.headers.get("content-type");
        if(contentType && contentType.indexOf("application/json") !== -1) {
            return response.json().then(function (json) {
                let userExists = json.exists === 'true';
                let userSame = json.same === 'true';
                if (response.status === 200) {
                    if (userSame) {
                        btnCheck.removeClass("btn-info").removeClass("btn-danger").removeClass("btn-success").addClass("btn-warning");
                        btnSubmit.prop("disabled", true);
                        element.prop("disabled", true);
                        return;
                    }
                    userExists ? btnCheck.removeClass("btn-info").removeClass("btn-danger").removeClass("btn-warning").addClass("btn-success") : btnCheck.removeClass("btn-info").removeClass("btn-success").removeClass("btn-warning").addClass("btn-danger");
                    btnSubmit.prop("disabled", !userExists);
                    element.prop("disabled", !userExists);
                }
            });
        }
    });
}

$(document).mouseup(function(e) {
    // FECHAR SIDENAV SE CLICAR FORA DO DIV
    let container = $("#sideNav");
    if (!container.is(e.target) && container.has(e.target).length === 0 && document.getElementById("sideNav").style.width !== "0") {
        closeNav();
    }
});