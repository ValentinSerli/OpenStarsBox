
function validForm(){
    fetch("http://192.168.86.105:8080/identification/check", {
        method: "POST",
        body: JSON.stringify({
            mail:document.getElementById("mail").value,
            password: document.getElementById("password").value,
        }),
        headers:{
            'Access-Control-Allow-Origin': 'http://192.168.86.105:8080',
            'Content-Type': 'application/json'
        }
    }).then((response) => {
        return response.text();
        console.log(response.text());
    }).then(ID => {
        getUserIP(function(ip){
            fetch("http://" + ip + ":8082/register/" + ID);
            console.log(ID);
            if (ID == "Erreur lors de la saisie de l'identifiant ou du mot de passe"){
                document.getElementById("ID").className = "ConnexionHide";
                document.getElementById("Connexion").className = "Connexion";

            } else if (ID == "ID déjà saisie"){
                document.getElementById("Connexion").className = "ConnexionHide";
                document.getElementById("ID").className = "Connexion";
            } else {
                sleep(250);
                window.location.href = "http://" + ip + ":8082/id";
            }
        })
    })
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