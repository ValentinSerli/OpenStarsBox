
function validForm(){
    var adresse = "http://192.168.86.119:8080";
    var mail = document.getElementById("mail");
    var password = document.getElementById("password");

    if (mail.value.trim() === "")
    {
        mail.focus();
        document.getElementById("ErreurMail").className = "ErreurMail";
        document.getElementById("ErreurPassword").className = "ConnexionHide";
        return;
    }

    if (password.value.trim() === ""){
        password.focus();
        document.getElementById("ErreurPassword").className = "ErreurPassword";
        document.getElementById("ErreurMail").className = "ConnexionHide";
        return;
    }

    if (mail.value.trim() !== "" && password.value.trim() !== "")
    {
        fetch(adresse + "/login", {
            method: "POST",
            body: JSON.stringify({
                email: document.getElementById("mail").value,
                password: document.getElementById("password").value,
            }),
            headers: {
                'Access-Control-Allow-Origin': adresse,
                'Content-Type': 'application/json'
            }
        }).then((response) => {
            console.log("token : " + response.headers.get("token"));
            var token = response.headers.get("token");
            if (response.status === 403){
                document.getElementById("Connexion").className = "Connexion";
                return;
            }
            fetch(adresse + "/identification/check", {
                method: "POST",
                body: JSON.stringify({
                    mail:document.getElementById("mail").value,
                    password: document.getElementById("password").value,
                }),
                headers:{
                    'Access-Control-Allow-Origin': adresse,
                    'Content-Type': 'application/json',
                    'token': token
                }
            }).then((response) => {
                if (response.status === 404 || response.status === 500){
                    console.log("Erreur serveur")
                }
                return response.text();
                console.log(response.text());
            }).then(ID => {
                fetch("/register/" + ID);
                console.log(ID);
                fetch("/token/" + token);
                if (response.status === 404 || response.status === 500){
                    console.log("Erreur serveur")
                } else {
                    sleep(350);
                    window.location.href = "/id";
                }

                })
            })
    }
}

function sleep(milliseconds) {
    var start = new Date().getTime();
    for (var i = 0; i < 1e7; i++) {
        if ((new Date().getTime() - start) > milliseconds){
            break;
        }
    }
}

function getUserIP(onNewIP) {
    var myPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
    var pc = new myPeerConnection({
            iceServers: []
        }),
        noop = function() {},
        localIPs = {},
        ipRegex = /([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/g,
        key;

    function iterateIP(ip) {
        if (!localIPs[ip]) onNewIP(ip);
        localIPs[ip] = true;
    }

    pc.createDataChannel("");

    pc.createOffer().then(function(sdp) {
        sdp.sdp.split('\n').forEach(function(line) {
            if (line.indexOf('candidate') < 0) return;
            line.match(ipRegex).forEach(iterateIP);
        });

        pc.setLocalDescription(sdp, noop, noop);
    }).catch(function(reason) {

    });

    pc.onicecandidate = function(ice) {
        if (!ice || !ice.candidate || !ice.candidate.candidate || !ice.candidate.candidate.match(ipRegex)) return;
        ice.candidate.candidate.match(ipRegex).forEach(iterateIP);
    };
}