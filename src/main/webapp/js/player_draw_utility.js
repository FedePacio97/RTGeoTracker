function buildExplainingDivForPlayer(player) {
    if (document.getElementById(player.player + '_explaining_div') == null) {
        var explainingDiv = document.createElement('div');
        explainingDiv.setAttribute('class', 'explaining');
        explainingDiv.setAttribute('id', player.player + '_explaining_div');

        //adding title label
        var explainingDivTitle = document.createElement('h1');
        explainingDivTitle.innerHTML = player.player;
        explainingDiv.appendChild(explainingDivTitle);

        //adding position label
        var explainingDivTitle = document.createElement('p');
        explainingDivTitle.innerHTML = "(x=" + player.position_x + ", y=" + player.position_y + ")";
        explainingDiv.appendChild(explainingDivTitle);

        var explainingDivStyle = '' +
            'top: ' + (player.position_y - PLAYER_PAWN_SIZE) + 'px;' +
            'left: ' + (parseInt(player.position_x) + PLAYER_PAWN_SIZE / 2) + 'px;' +
            '';
        explainingDiv.setAttribute('style', explainingDivStyle);

        MAP.appendChild(explainingDiv);
    }
}

function updatePlayerFollower() {
    if (FOLLOWED_PLAYER == null) {
        var follower = document.getElementById('follower');
        if (follower != null) {
            follower.remove();
        }

        var followerText = document.getElementById('follower_text');
        if (followerText != null) {
            followerText.remove();
        }
        return;
    }

    //follower circle
    var follower = document.getElementById('follower');

    if (follower == null) {
        var follower = document.createElement('div');
        follower.setAttribute('id', 'follower');

        MAP.appendChild(follower);
    }

    var followerStyle = '' +
        'top: ' + (FOLLOWED_PLAYER.position_y) + 'px;' +
        'left: ' + (FOLLOWED_PLAYER.position_x) + 'px;' +
        '';
    follower.setAttribute('style', followerStyle);


    //follower text
    var followerText = document.getElementById('follower_text');

    if (followerText == null) {
        followerText = document.createElement('p');
        followerText.setAttribute('id', 'follower_text');
        MAP.appendChild(followerText);
    }

    followerText.innerHTML = FOLLOWED_PLAYER.player;

    var followerTextStyle = '' +
        'top: ' + parseInt(FOLLOWED_PLAYER.position_y) + 'px;' +
        'left: ' + (FOLLOWED_PLAYER.position_x) + 'px;' +
        '';
    followerText.setAttribute('style', followerTextStyle);

}

function insertPlayersTable(player) {
    //add player to player's table

    var playersTableDiv = document.getElementById(player.player + '_players_table_elem');

    if (playersTableDiv != null) {
        //update position
        var positionText = document.getElementById(player.player + '_table_position');
        positionText.innerHTML = "(x=" + player.position_x + ", y=" + player.position_y + ")";
        return;
    }

    playersTableDiv = document.createElement('div');
    playersTableDiv.setAttribute('class', 'players_table_elem');
    playersTableDiv.setAttribute('id', player.player + '_players_table_elem');

    //adding title label
    var playersTableTitle = document.createElement('h1');
    playersTableTitle.innerHTML = player.player;
    playersTableDiv.appendChild(playersTableTitle);

    //adding position label
    var playersTablePosition = document.createElement('p');
    playersTablePosition.setAttribute('id', player.player + '_table_position');
    playersTablePosition.innerHTML = "(x=" + player.position_x + ", y=" + player.position_y + ")";
    playersTableDiv.appendChild(playersTablePosition);

    playersTableDiv.onmouseup = function (e) { 
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


    document.getElementById('players_table').appendChild(playersTableDiv);
}

function clearPlayersTableSelected() {
    var tableElems = document.getElementsByClassName('players_table_elem');
    for (i = 0; i < tableElems.length; i++) {
        tableElems[i].setAttribute('class', tableElems[i].getAttribute('class').replace(" clicked_table_div", ""));
    }
}

function updatePlayersTableSelected() {
    var tableElems = document.getElementsByClassName('players_table_elem');
    for (i = 0; i < tableElems.length; i++) {
        tableElems[i].setAttribute('class', tableElems[i].getAttribute('class').replace(" clicked_table_div", ""));
        if (FOLLOWED_PLAYER != null && tableElems[i].getAttribute('id') == (FOLLOWED_PLAYER.player + '_players_table_elem')) {
            tableElems[i].setAttribute('class', tableElems[i].getAttribute('class') + " clicked_table_div");
        }
    }
}

function removePlayersFromTable(players) {
    var tableElems = document.getElementsByClassName('players_table_elem');
    for (i = 0; i < players.length; i++) {
        for (j = 0; j < tableElems.length; j++) {
            if (tableElems[j].getAttribute('id') == (players[i].player + '_players_table_elem')) {
                tableElems[j].remove();
            }
        }
    }
}