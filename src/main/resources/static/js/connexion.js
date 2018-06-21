
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
        fetch("http://192.168.86.105:8082/register/" + ID);
    // fetch("/register?id=" + ID);
    console.log(ID);
    if (ID == "Erreur lors de la saisie de l'identifiant ou du mot de passe"){
        document.getElementById("ID").className = "ConnexionHide";
        document.getElementById("Connexion").className = "Connexion";

    } else if (ID == "ID déjà saisie"){
        document.getElementById("Connexion").className = "ConnexionHide";
        document.getElementById("ID").className = "Connexion";
    } else {
        sleep(250);
        window.location.href = "http://192.168.86.105:8082/id";
    }
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

