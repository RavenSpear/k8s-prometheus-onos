onos_url := http://localhost:8181/onos
onos_curl := curl --fail -sSL --user onos:rocks --noproxy localhost
netcfg_file := ./netcfg.json

install_netcfg:
        ${onos_curl} -XPOST -H'Content-Type:application/json' ${onos_url}/v1/network/configuration -d@${netcfg_file}
