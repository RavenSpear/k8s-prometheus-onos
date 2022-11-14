from flask import Flask,request
import requests
app = Flask(__name__)

@app.route("/data",methods=["GET"])
def data():
    nodeName = request.args.get("nodeName")
    deviceName = request.args.get("deviceName")
    limit = request.args.get("limit")
    url = "http://223.3.94.112:30080/{}/data/api/v2/reading/device/name/{}?limit={}".format(nodeName,deviceName,limit)
    r = requests.get(url).json()
    result = ""
    for i in range(len(r["readings"])):
        r["readings"][i]["origin"] = int(str(r["readings"][i]["origin"])[:-6])
    return r

# if __name__ == '__main__':
#   app.run(host = '0.0.0.0')
