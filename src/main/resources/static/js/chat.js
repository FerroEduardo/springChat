let stompClient = null;

function setConnected(connected, username) {
    if (username === ""){
        return;
    }
    $("#send-message-text").prop("disabled", !connected);
    $("#inputUsername").prop("disabled", connected);
    $("#send-message-btn").prop("disabled", !connected);
    // $("#mensagens").html("");
    $("#userList").val('');
}
let subscription;
function connect(canalNome, username, csrfHeaderName, csrfToken) {
    if (username === ""){
        return;
    }
    canalNome = canalNome.trim();
    let socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    // stompClient.debug = function(str) {};
    stompClient.connect(
        { /* headers */
            [csrfHeaderName]: csrfToken,
            usuario: username,
            canal: canalNome,
        },
        function (frame) {
            setConnected(true, username);
            $("#form-login").hide();
            $("#main-content").show();
            console.log('Conectado: ' + frame);
            $("#chat-name").text('Chat: ' + canalNome);
            subscription = stompClient.subscribe('/topic/message/' + canalNome, function (mensagem) {
                showMessage(JSON.parse(mensagem.body), username);
            }, {id: canalNome});
            getUserList(canalNome);
        },
        function (errorMessage) {
            setDisconnected();
        }
    );
}

function disconnect(canalNome, username) {
    canalNome = canalNome.trim();
    if (stompClient !== null) {
        // stompClient.disconnect(function () {}, {usuario: $("#inputUsername").val(), canal: canalNome});
        subscription.unsubscribe(canalNome);
        // subscription = null;
    }
    setConnected(false, username);
    // $("#userList").html('<li class="list-group-item active">Usuários:</li>');
    console.log("Disconnected");
}

function sendMessage(canalNome, username) {
    canalNome = canalNome.trim();
    stompClient.send("/app/message/"+canalNome, {},
        JSON.stringify({
            'author': username,
            'message': $("#send-message-text").val(),
            'date': new Date(),
            'color': corAtual
        }));
    $("#send-message-text").val("");
}

function getUserList(canalNome) {
    canalNome = canalNome.trim();
    stompClient.send("/app/message/"+canalNome+"/userList", {},{});
}

async function showMessage(message, username) {
    // console.log(message)
    if (message.tipo.toString() === "CHATMESSAGE") {
        message = message.chatMessage;
        let date = new Date(message.date);
        let dateString = date.toLocaleDateString() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
        $("#messages").append(
            `<div class="message">
            <!--                        <img alt="" class="user-image" src="square.png">-->
            <div class="user-image" style="background-color: ${message.color}"></div>
            <div>
                <h5 class="message-username">
                    ${message.author.toString().replace(/</g, "&lt;").replace(/>/g, "&gt;")}
                </h5>
                <p class="message-text">
                    ${message.message.toString().replace(/</g, "&lt;").replace(/>/g, "&gt;")}
                </p>
                <p class="message-time">
                    ${dateString}
                </p>
            </div>
        </div>`
        );
    }
    else if (message.tipo.toString() === "USERSONLINE"){
        // await $("#userList").html('<li class="list-group-item active">Usuários:</li>');
        // console.log(message);
        removerTodosUsuarios();
        await message.usuarios.forEach((message) => {
            // Para não ter nome repetido
            if ($(".usuario:contains(" + message.nome.toString() + ")").length === 0) {
                $("#userList").append(
                    '<a class="dropdown-item usuario" href="#">' + message.nome.toString().replace(/</g, "&lt;").replace(/>/g, "&gt;") + '</a>'
                );
            }
        });
    }
    else if (message.tipo.toString() === "REMOVEUSER"){
        removerUsuario(message.usuario);
    }
}

function removerUsuario(username) {
    let usuarios = $(".dropdown-item, .usuario");
    usuarios.each((e) => {
        let indice = parseInt(e);
        if (usuarios[indice].text === username) {
            usuarios[indice].outerHTML = '';
        }
    });
}

function removerTodosUsuarios() {
    let usuarios = $(".dropdown-item, .usuario");
    usuarios.each((e) => {
        let indice = parseInt(e);
        usuarios[indice].outerHTML = '';
    });
    // Obter nome do canal
    // let canal = $('#chat-name').text().toString().replace('Chat: ', '');
    // console.log(canal);
    // getUserList(canal);
}

function adicionarCanalNaLista(nomeDoCanal, descDoCanal, corDoCanal) {
    nomeDoCanal = nomeDoCanal.trim();
    descDoCanal = descDoCanal.trim();
    corDoCanal = corDoCanal || '#E94560';
    let cssEfeitoContrasteNaLista = ' active-chat';
    let chatList = $('#chat-list');
    if ((chatList.children().length % 2) !== 0) {
        cssEfeitoContrasteNaLista = '';
    }
    chatList.append(
    `<div class="channel${cssEfeitoContrasteNaLista} chat-item">
        <div class="channel-image" style="background-color: ${corDoCanal}">
        
        </div>
        <div class="channel-info">
            <h5 class="channel-name chat-title">
                ${nomeDoCanal.toString().replace(/</g, "&lt;").replace(/>/g, "&gt;")}
                <span class="prevent remove-channel">
                    <svg width="1em" height="1em" viewBox="0 0 16 16" class="bi bi-x-circle-fill prevent" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                        <path class="prevent" fill-rule="evenodd" d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293 5.354 4.646z"/>
                    </svg>
                </span>
            </h5>
            <p class="channel-description">
                ${descDoCanal.toString().replace(/</g, "&lt;").replace(/>/g, "&gt;")}
            </p>
        </div>
    </div>`
    );
    let ultimoCanalInserido = chatList.children()[chatList.children().length - 1];
    let ultimoCanalInseridoBotaoApagar = $(ultimoCanalInserido).children('div:last-child');
    ultimoCanalInseridoBotaoApagar.find('.remove-channel').click(function () {
        removerCanalLocalStorage(nomeDoCanal);
    })
    // console.log(ultimoCanalInseridoBotaoApagar);
    // adicionarCanalLocalStorage(nomeDoCanal, descDoCanal);
}

function adicionarCanalLocalStorage(nomeDoCanal, descDoCanal, corDoCanal) {
    nomeDoCanal = nomeDoCanal.trim();
    descDoCanal = descDoCanal.trim();
    corDoCanal = corDoCanal || '#E94560';
    let canalJsonString = JSON.stringify({nome: nomeDoCanal, desc: descDoCanal, cor: corDoCanal});
    let userLocalStorage = localStorage.getItem(username);
    if (userLocalStorage === undefined || userLocalStorage === null) {
        let jsonString = JSON.stringify({'canais': JSON.stringify([canalJsonString])});
        localStorage.setItem(username, jsonString);
    } else {
        let localStorageJson = JSON.parse(userLocalStorage);
        if ('canais' in localStorageJson) {
            let canaisObjArray = JSON.parse(localStorageJson.canais);
            // console.log(canaisObjArray);
            canaisObjArray.push(canalJsonString);
            let canaisObjArrayJsonString = JSON.stringify(canaisObjArray);
            let jsonObj = {'canais': canaisObjArrayJsonString};
            let jsonObjMerge = $.extend(localStorageJson, jsonObj);
            let jsonString = JSON.stringify(jsonObjMerge);
            // console.log(jsonString);
            localStorage.setItem(username, jsonString);
        } else {
            let jsonObj = {'canais': JSON.stringify([canalJsonString])};
            let jsonObjMerge = $.extend(localStorageJson, jsonObj);
            let jsonString = JSON.stringify(jsonObjMerge);
            localStorage.setItem(username, jsonString);
        }
    }
}

function carregarCanaisLocalStorage() {
    let userLocalStorage = localStorage.getItem(username);
    if (userLocalStorage !== null) {
        let localStorageJson = JSON.parse(userLocalStorage);
        if ('canais' in localStorageJson) {
            let canaisObjArray = JSON.parse(localStorageJson.canais)
            // console.log(canaisObjArray);
            canaisObjArray.forEach(function (canal) {
                canal = JSON.parse(canal);
                // console.log(canal);
                adicionarCanalNaLista(canal.nome, canal.desc, canal.cor);
            });
        }
    }
}

function removerCanalLocalStorage(nomeDoCanal) {
    nomeDoCanal = nomeDoCanal.trim();
    let userLocalStorage = localStorage.getItem(username);
    if (userLocalStorage !== null || userLocalStorage !== undefined) {
        let localStorageJson = JSON.parse(userLocalStorage);
        if ('canais' in localStorageJson) {
            let canaisObjArray = JSON.parse(localStorageJson.canais);
            let newCanaisObjArray = canaisObjArray.filter(function (canal) {
                canal = JSON.parse(canal);
                return canal.nome !== nomeDoCanal;
            });
            // console.log(newCanaisObjArray)
            newCanaisObjArray = {canais: JSON.stringify(newCanaisObjArray)};
            let jsonObj = $.extend(localStorageJson, newCanaisObjArray);
            let jsonString = JSON.stringify(jsonObj);
            localStorage.setItem(username, jsonString);
        }
    }
    location.reload(); // Reload Page to Avoid Problems
}

function showToast(title, body, delay) {
    let toast =
        `
        <div class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-delay="${delay}">
            <div class="toast-header" >
                <img src="" class="rounded mr-2" alt="">
                <strong class="mr-auto">
                    ${title}
                </strong>
                <small class="text-muted">Agora</small>
                <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="toast-body">
                ${body}
            </div>
        </div>
        `;
    $('#toast-list').append(toast);
    $('.toast').toast('show');
    setTimeout(removeFirstToast, delay);
}

function removeFirstToast() {
    let toastList = $('#toast-list');
    let first = toastList.children()[0];
    if (first) {
        first.remove();
    }
}

function setUserColor(username, color) {
    let userLocalStorage = localStorage.getItem(username);
    if (userLocalStorage === undefined || userLocalStorage === null) {
        let jsonString;
        if (color === null || color === undefined) {
            let index = Math.floor(Math.random() * userColors.length - 1);
            jsonString = JSON.stringify({'color': userColors[index]});
            corAtual = userColors[index];
        } else {
            jsonString = JSON.stringify({'color': color.toString()});
            corAtual = color.toString();
        }
        localStorage.setItem(username, jsonString);
    } else {
        let localStorageJson = JSON.parse(userLocalStorage);
        let jsonNewColor;
        if (color === null || color === undefined) {
            let index = Math.floor(Math.random() * userColors.length - 1);
            jsonNewColor = {'color': userColors[index]};
            corAtual = userColors[index];
        } else {
            jsonNewColor = {'color': color.toString()};
            corAtual = color.toString();
        }
        let jsonObj = $.extend(localStorageJson, jsonNewColor);
        let jsonString = JSON.stringify(jsonObj);
        localStorage.setItem(username, jsonString);
    }
    defineUserColor(username);
}

function defineUserColor(username) {
    let userLocalStorage = localStorage.getItem(username);
    if (userLocalStorage !== null) {
        let localStorageJson = JSON.parse(userLocalStorage);
        if ('color' in localStorageJson) {
            corAtual = localStorageJson.color;
            $("#user-image-color").css('color', corAtual);
        } else {
            setUserColor(username);
        }
    } else {
        setUserColor(username);
    }
}

function searchChannel() {
    let input = $("#searchChannel");
    let filter = input.val().toUpperCase();
    $(".chat-item").each(function () {
        let element = $(this);
        let title = element.find('.chat-title').text().trim().toUpperCase();
        if (title.indexOf(filter) > -1) {
            element.css('display', '');
        } else {
            element.css('display', 'none');
        }
    });
}

function setDisconnected() {
    $("#actual-channel-name").text('Desconectado');
    $("#actual-channel-img").css('background-color', '#fa1616');
    $("#send-message-text").prop("disabled", true);
    $("#send-message-btn").prop("disabled", true);
    $("#emoji-btn").prop("disabled", true);
}

function toggleSideBar() {
    if (isMobile) {
        if ($("#left-side-parent").css('display') !== 'none') {
            $("#right-side-parent").fadeIn();
            $(".side-bar-btn").css('display','block');
            $("#left-side-parent").fadeOut();
        } else {
            $("#right-side-parent").fadeOut();
            $(".side-bar-btn").css('display','none');
            $("#left-side-parent").fadeIn();
        }
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $('#addChatBtn').click(() => {
        let nome = $('#addChatNome').val();
        let desc = $('#addChatDesc').val();
        let color = $('#addChatColor').val();
        if (nome.length <= 35 && nome.length >= 1) {
            adicionarCanalNaLista(nome, desc, color);
            adicionarCanalLocalStorage(nome, desc, color);
            $('#addChatPopup').modal('hide');
        } else {
            showToast('Erro', 'O nome do canal deve possuir até 35 caracteres', 7000);
        }
    });
    carregarCanaisLocalStorage();
    // setInterval(removerTodosUsuarios, 10000);
});

userColors = [
    '#ff0000', '#ff4d00', '#f638dc', '#ffd700',
    '#00fff5', '#acdbdf', '#a7d129', '#4ecca3',
    '#c70039', '#14ffec'
];
