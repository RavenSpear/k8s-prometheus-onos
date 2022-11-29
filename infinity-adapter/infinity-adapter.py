from flask import Flask, request
import requests
import re
app = Flask(__name__)


@app.route("/ratio", methods=["GET"])
def ratio():
    url = "http://223.3.94.112:8009/api/v1/nodes"
    nodelist = requests.get(url).json()['items']
    nodetypehash = {}
    for item in nodelist:
        nodetypehash[item['metadata']['name']
                     ] = item['metadata']['labels']['cluster']
    # print(nodetypehash)

    url = "http://223.3.94.112:8009/api/v1/namespaces/default/pods"
    podlist = requests.get(url).json()['items']

    taskName = request.args.get("taskName")
    print(taskName)
    statistic = {
        "cloud": 0,
        "edge": 0,
    }
    if taskName is None:
        taskarr = []
        url = "http://223.3.94.112:8009/apis/apps/v1/namespaces/default/deployments"
        tasklist = requests.get(url).json()['items']
        for item in tasklist:
            taskarr.append(item['metadata']['name'])
        print(taskarr)
        for item in podlist:
            taskname = re.match(
                "^(.*)-([a-z]|[0-9])+-([a-z]|[0-9])+$", item['metadata']['name']).group(1)
            if taskname in taskarr:
                # print(taskname)
                statistic[nodetypehash[item['spec']['nodeName']]] += 1
        # print(statistic)
    else:
        for item in podlist:
            taskname = re.match(
                "^(.*)-([a-z]|[0-9])+-([a-z]|[0-9])+$", item['metadata']['name']).group(1)
            if taskname == taskName:
                # print(taskname)
                statistic[nodetypehash[item['spec']['nodeName']]] += 1

    res = {
        "ratio": str(statistic['cloud']) + "/" + str(statistic['edge'])
    }
    return res


# if __name__ == '__main__':
#   app.run(host = '0.0.0.0')
