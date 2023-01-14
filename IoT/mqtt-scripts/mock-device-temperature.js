var count = 0
function dataLoop(){
    let data = [14.8,12.3,13.6,11.7,12.8,12.0,13.6,14.7,12.3,13.6,11.7,13.2];
    return data[count++ % 12];
}

const deviceName = "temperature-sensor";
// 23:00	02:00	05:00	08:00	11:00	14:00	17:00	20:00
// 14.8	    12.3    13.6	11.7	8.6	    6.8	    3.1	    2
// DataSender sends async value to MQTT broker every 15 min
schedule('* */15 * * * *', ()=>{
    let body = {
        "name": deviceName,
        "cmd": "temperaturevalue",
        "temperaturevalue": dataLoop()
    };
    publish( 'DataTopic', JSON.stringify(body));
});

// CommandHandler receives commands and sends response to MQTT broker
// 1. Receive the reading request, then return the response
// 2. Receive the set request, then change the device value
subscribe( "CommandTopic" , (topic, val) => {
    var data = val;
    if (data.method == "set") {
        switch(data.cmd) {
        }
    }else{
        switch(data.cmd) {
            case "ping":
              data.ping = "pong";
              break;
            case "temperaturevalue":
              data.temperaturevalue = 12.123;
              break;
          }
    }
    publish( "ResponseTopic", JSON.stringify(data));
});
