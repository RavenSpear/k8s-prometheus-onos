var count = 0
function dataLoop(){
    let data = [1027.3,1026.4,1025.1,1023.8,1022.3,1019.8,1022.8,1025.1,1025.2,	1026.8,1027.5,1028.2];
    return data[count++ % 12];
}
const deviceName = "pressure-sensor";

// DataSender sends async value to MQTT broker every 15 min
schedule('* */15 * * * *', ()=>{
    let body = {
        "name": deviceName,
        "cmd": "pressurevalue",
        "pressurevalue": dataLoop()
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
            case "pressurevalue":
              data.pressurevalue = 1200.123;
              break;
          }
    }
    publish( "ResponseTopic", JSON.stringify(data));
});
