function getRandomFloat(min, max) {
    return Math.random() * (max - min) + min;
}

const deviceName = "pressure-sensor";

// DataSender sends async value to MQTT broker every 15 seconds
schedule('*/15 * * * * *', ()=>{
    let body = {
        "name": deviceName,
        "cmd": "pressurevalue",
        "pressurevalue": getRandomFloat(1000,1200).toFixed(1)
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
