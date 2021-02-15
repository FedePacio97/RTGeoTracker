const AJAX_POLLING_PERIOD_MS = 500
const PLAYER_PAWN_SIZE = 50
var MAP = null;
var LIST = null;

var FOLLOWED_PLAYER = null;
var CURRENT_PLAYER_USERNAME = null;
var CURRENT_PLAYER = null;
var PLAYERS_VERSION = [];

function main() {
    document.addEventListener('keyup', function (e) {
        handleKey(e.key);
    })
    setInterval(requestForPlayerPositions, AJAX_POLLING_PERIOD_MS)

    MAP = document.getElementById('map_div');
    MAP.onmouseup = function (e) {
        if (e.target.getAttribute('id') == 'map_div') {
            clearPlayersTableSelected();
            FOLLOWED_PLAYER = null;
            updatePlayerFollower();
            updatePlayersTableSelected();
        }
    }
    CURRENT_PLAYER_USERNAME = "pippo";
    CURRENT_PLAYER["player"] = CURRENT_PLAYER_USERNAME;
    CURRENT_PLAYER["position_x"] = 500;
    CURRENT_PLAYER["position_y"] = 500
    buildCurrentPlayer();
}

function handleKey(key) {
    switch (key) {
        case 'w':
            CURRENT_PLAYER.y_position -= 1;
            break;
        case 'a':
            CURRENT_PLAYER.x_position -= 1;
            break;
        case 's':
            CURRENT_PLAYER.y_position += 1;
            break;
        case 'd':
            CURRENT_PLAYER.x_position += 1;
            break;
        default:
            return;
    }
    sendNewPlayerPosition();
}

function buildCurrentPlayer(){
    var playerPawn = document.getElementById(CURRENT_PLAYER_USERNAME + '_pawn_div');
    playerPawn.setAttribute('id', CURRENT_PLAYER_USERNAME + '_pawn_div' + '_pawn_div');
    playerPawn.setAttribute('class', "pawn player");

    //listen to mouseover and mouseleave for explaining div
    playerPawn.onmouseover = function (e) {
        buildExplainingDivForPlayer(CURRENT_PLAYER);
    }

    playerPawn.onmouseleave = function (e) {
        var explainingDiv = document.getElementById(CURRENT_PLAYER_USERNAME + '_explaining_div');
        if (explainingDiv != null) {
            explainingDiv.remove();
        }
    }

    //listen to mouseup for selecting the followed player
    playerPawn.onmouseup = function (e) {
        if (FOLLOWED_PLAYER == null || FOLLOWED_PLAYER.player != CURRENT_PLAYER_USERNAME) {
            for(i = 0; i<LIST.length; i++){
                if(CURRENT_PLAYER_USERNAME == LIST[i].player){
                    FOLLOWED_PLAYER = LIST[i];
                    break;
                }
            }

        } else {
            FOLLOWED_PLAYER = null;
        }
        updatePlayerFollower();
        updatePlayersTableSelected();
    }
}

function drawPlayer(player) {
    var playerPawn = document.createElement('div');
    player.player + '_pawn_div'

    // if player doesn't exists
    if (playerPawn == null) {
        // create div for pawn
        playerPawn = document.createElement('div');
        playerPawn.setAttribute('id', player.player + '_pawn_div');
        playerPawn.setAttribute('class', "pawn");

        //listen to mouseover and mouseleave for explaining div
        playerPawn.onmouseover = function (e) {
            buildExplainingDivForPlayer(player);
        }

        playerPawn.onmouseleave = function (e) {
            var explainingDiv = document.getElementById(player.player + '_explaining_div');
            if (explainingDiv != null) {
                explainingDiv.remove();
            }
        }

        //listen to mouseup for selecting the followed player
        playerPawn.onmouseup = function (e) {
            if (FOLLOWED_PLAYER == null || FOLLOWED_PLAYER.player != player.player) {
                for(i = 0; i<LIST.length; i++){
                    if(player.player == LIST[i].player){
                        FOLLOWED_PLAYER = LIST[i];
                        break;
                    }
                }
                
            } else {
                FOLLOWED_PLAYER = null;
            }
            updatePlayerFollower();
            updatePlayersTableSelected();
        }
    }

    //update followed player position
    if (FOLLOWED_PLAYER != null && FOLLOWED_PLAYER.player == player.player) {
        FOLLOWED_PLAYER = player;
        updatePlayerFollower();
    }

    //change pawn position and style
    var playerPawnStyle = '' +
        'height: ' + PLAYER_PAWN_SIZE + 'px;' +
        'width: ' + PLAYER_PAWN_SIZE + 'px;' +
        'top: ' + player.position_y + 'px;' +
        'left: ' + player.position_x + 'px;' +
        'margin-top: ' + (-PLAYER_PAWN_SIZE) + 'px;' +
        'margin-left: ' + (-PLAYER_PAWN_SIZE / 2) + 'px;' +
        '';
    playerPawn.setAttribute('style', playerPawnStyle);

    MAP.appendChild(playerPawn);

}

function drawPlayers(players) {
    //draw each player
    for (i in players) {
        drawPlayer(players[i]);
        insertPlayersTable(players[i]);
    }

    playersToRemove = [];

    if (LIST != null) {
        for (i = 0; i < LIST.length; i++) {
            found = false;
            for (j = 0; j < players.length; j++){
                if(LIST[i].player == players[j].player){
                    found = true;
                }
            }
            if(!found){
                playersToRemove.push(LIST[i]);
            }   
        }
    }

    removePlayersFromTable(playersToRemove);

    LIST = players;

    updatePlayerFollower();
    updatePlayersTableSelected();
}