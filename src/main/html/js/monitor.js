var url="http://192.168.42.105:8001/gpiomofo/monitor";
var chambers = 6;
var panicActive = {};

function onLoad() {

    for ( var i=0; i<chambers; i++ ) {
        panicActive[i] = false;
    }

    setInterval(function() {
        getMonitorStates();
    }, 500);

    setInterval(function() {
        flashActiveMonitors();
    }, 500);

//    $("#resetBtn").click(function() {
//        $.ajax({
//            url: url+"/reset",
//            type: "POST",
//        });
//    });

    offline();
}

var flashOn = false;

function flashActiveMonitors() {
    for ( var i=0; i<chambers; i++ ) {
        if ( panicActive[i] ) {
            console.log("sweaty knickers");
            if ( flashOn ) {
                $("#chamber"+i)
                .addClass("flash");
            } else {
                $("#chamber"+i)
                .removeClass("flash");
            }
        }
    }
    flashOn = !flashOn;
}

function getMonitorStates() {

    $.ajax({
        url: url,
        type: "GET",
        success: function(response) {
            updateMonitors(response);
        },
        error: function(response) {
            offline();
        }
    });

}

function updateMonitors(result) {

    activeMonitors = result.split(",");

    for ( var i=0; i<chambers; i++ ) {
        if ( activeMonitors.indexOf(""+i)==-1 ) {
            panicActive[i] = false;
            $("#chamber"+i)
                .removeClass("offline")
                .removeClass("monitorOn")
                .removeClass("flash")
                .addClass("monitorOff");

        } else {
            panicActive[i] = true;
            $("#chamber"+i)
                .removeClass("offline")
                .removeClass("monitorOff")
                .addClass("monitorOn");
        }
    }

}

function offline() {
    for ( var i=0; i<chambers; i++ ) {
        panicActive[i] = false;
        $("#chamber"+i)
            .removeClass("monitorOn")
            .removeClass("monitorOff")
            .addClass("offline");
    }
}