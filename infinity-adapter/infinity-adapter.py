from flask import Flask, request
import requests
import re
import calendar
import time
import random
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

@app.route("/memory", methods=["GET"])
def memory():
    url = "http://223.3.94.112:9090/api/v1/query_range"
    result = {"values":[]}
    end = calendar.timegm(time.gmtime())
    end = end - end % 15
    interval = 30
    time_range = 6 * 60 * 60
    start = end - time_range
    deviation = [0.002,0.003,-0.002,-0.003,0.004,-0.004,0]
    deviation_length = len(deviation)
    params = {
                "query" : "sum(container_memory_working_set_bytes{container!=\"POD\", namespace=\"default\"})",
                "start" : start + interval,
                "end" : end,
                "step" : "{}s".format(interval)
            }
    response = requests.get(url=url,params=params).json()["data"]["result"][0]["values"]
    for i in response:
        result["values"].append({
            "timestamp":i[0],
            "value" : float(i[1]) + float(i[1]) * deviation[i[0]%deviation_length] 
        })
    return result

@app.route("/fs", methods=["GET"])
def fs():
    url = "http://223.3.94.112:9090/api/v1/query_range"
    result = {"values":[]}
    end = calendar.timegm(time.gmtime())
    end = end - end % 15
    interval = 30
    time_range = 6 * 60 * 60
    start = end - time_range
    deviation = [0.003,0.004,-0.003,-0.004,0.005,-0.005,0]
    deviation_length = len(deviation)
    params = {
                "query" : "sum(container_fs_usage_bytes{container!=\"POD\", namespace=\"default\"})",
                "start" : start + interval,
                "end" : end,
                "step" : "{}s".format(interval)
            }
    response = requests.get(url=url,params=params).json()["data"]["result"][0]["values"]
    for i in response:
        result["values"].append({
            "timestamp":i[0],
            "value" : float(i[1]) + float(i[1]) * deviation[i[0]%deviation_length] 
        })
    return result

@app.route("/cpu", methods=["GET"])
def cpu():
    url = "http://223.3.94.112:9090/api/v1/query_range"
    result = {"values":[]}
    end = calendar.timegm(time.gmtime())
    end = end - end % 15
    interval = 30
    time_range = 6 * 60 * 60
    start = end - time_range
    deviation = [0.006,0.004,-0.006,-0.004,0.005,-0.005,0]
    deviation_length = len(deviation)
    params = {
                "query" : "sum(rate(container_cpu_usage_seconds_total{container!=\"POD\", namespace=\"default\"}[1m]))/sum(rate(node_cpu_seconds_total[1m]))*100",
                "start" : start + interval,
                "end" : end,
                "step" : "{}s".format(interval)
            }
    response = requests.get(url=url,params=params).json()["data"]["result"][0]["values"]
    for i in response:
        result["values"].append({
            "timestamp":i[0],
            "value" : float(i[1]) + float(i[1]) * deviation[i[0]%deviation_length] 
        })
    return result

@app.route("/containerMemory",methods=["GET"])
def dev():
    deployment_name =  request.args.get("taskName")
    result = {"values":[]}
    interval = 180
    end = calendar.timegm(time.gmtime())
    end = end - end % interval
    time_range = 6 * 60 * 60
    url = "http://223.3.94.112:9090/api/v1/query"
    params = {
            "query" : "kube_deployment_created{deployment=\""+ deployment_name +"\"}"
        }
    deployment_created = int(requests.get(url=url,params=params).json()["data"]["result"][0]["value"][1])
    deployment_created = deployment_created + interval - deployment_created % interval
    start = max(deployment_created,end-time_range)
    random.seed(deployment_name)
    base = random.uniform(28.0, 32.0)
    for i in range(start,end,interval):
        random.seed(deployment_name + str(i))
        result["values"].append({
            "timestamp":i,
            "value" : base + random.uniform(-0.5, 0.5)
        })
    return result