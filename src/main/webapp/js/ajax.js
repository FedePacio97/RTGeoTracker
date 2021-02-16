const DEBUG_FLAG = false

var DEBUG_POSITIONS = {
    "Graziano_x": 180,
    "Graziano_y": 300,
    "Silvano_x": 280,
    "Silvano_y": 700,
    "Mariano_x": 580,
    "Mariano_y": 80
};

function sendNewPlayerPosition() {
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            if(JSON.parse(xhr.responseText).response === "BAD"){
                console.log("Server responding 'BAD' in position update");
                return;
            }
            PLAYERS_VERSION[CURRENT_PLAYER_USERNAME] = parseInt(JSON.parse(xhr.responseText).version);
        }
    };

    xhr.open("POST", "http://localhost:8080/RTGeoTracker_war_exploded/ServletTrial", true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(JSON.stringify({
        "opcode": "POS",
        "username": CURRENT_PLAYER_USERNAME,
        "state": {"x": CURRENT_PLAYER.position_x, "y": CURRENT_PLAYER.position_y},
        "version": PLAYERS_VERSION[CURRENT_PLAYER_USERNAME],
        "priority": 1
    }));
}

// requestForPlayerPositions() is periodically called to ask for other players' position
function requestForPlayerPositions() {
    if (DEBUG_FLAG) {
        var ret = {
            "players": [
                {"player": "graziano", "position_x": DEBUG_POSITIONS.Graziano_x, "position_y": DEBUG_POSITIONS.Graziano_y},
                {"player": "silvano", "position_x": DEBUG_POSITIONS.Silvano_x, "position_y": DEBUG_POSITIONS.Silvano_y},
                {"player": "mariano", "position_x": DEBUG_POSITIONS.Mariano_x, "position_y": DEBUG_POSITIONS.Mariano_y}
            ]
        };
        DEBUG_POSITIONS.Graziano_x += 5 * (Math.floor(Math.random() * 3) - 1)
        DEBUG_POSITIONS.Graziano_y += 5 * (Math.floor(Math.random() * 3) - 1)
        DEBUG_POSITIONS.Silvano_x += 5 * (Math.floor(Math.random() * 3) - 1)
        DEBUG_POSITIONS.Silvano_y += 5 * (Math.floor(Math.random() * 3) - 1)
        DEBUG_POSITIONS.Mariano_x += 5 * (Math.floor(Math.random() * 3) - 1)
        DEBUG_POSITIONS.Mariano_y += 5 * (Math.floor(Math.random() * 3) - 1)

        drawPlayers(ret.players);
        return;
    }

    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            if(JSON.parse(xhttp.responseText).response === "BAD"){
                console.log("Server responding 'BAD' in map request");
                return;
            }
            var players = JSON.parse(xhttp.responseText).players;
            var positions = [];

            //convert formats and update versions
            for (let i = 0; i < players.length; i++) {
                console.log("CURRENT_PLAYER_USERNAME " + CURRENT_PLAYER_USERNAME);
                console.log("players[i].username " + players[i].username);
                if(players[i].username === CURRENT_PLAYER_USERNAME){
                    console.log("Username equal!")
                    continue;
                }
                positions.push({"player":players[i].username, "position_x":parseInt(players[i].state.x), "position_y":parseInt(players[i].state.y)});
                PLAYERS_VERSION[players[i].username] = parseInt(players[i].version);
            }
            //console.log("Username " + positions[0].player);
            //console.log("x " + positions[0].position_x);
            //console.log("y " + positions[0].position_y);

            //console.log("x " + players[0].state.x);
            //console.log("y " + players[0].state.y);

            drawPlayers(positions);
        }
    };

    xhttp.open("PUT", "http://localhost:8080/RTGeoTracker_war_exploded/ServletTrial", true);

    var request = {
        "opcode": "MAP",
        "close_players": []
    }
    //TODO PLAYERS IN RADIUS
    for(var i = 0; i< LIST.length; i++){
        if((LIST[i].position_x - CURRENT_PLAYER.position_x)**2 + (LIST[i].position_y - CURRENT_PLAYER.position_y)**2 < CLOSE_PLAYERS_RADIUS**2){
            request.close_players.push({
                username: LIST[i].player,
                version: PLAYERS_VERSION[LIST[i].player]
            });
        }
    }
    xhttp.send(JSON.stringify(request));
}