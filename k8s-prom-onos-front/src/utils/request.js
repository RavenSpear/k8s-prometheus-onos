import axios from "axios";

const service = axios.create({
    // baseURL: 'http://127.0.0.1:8181/onos/p4virtex',
    // timeout: 5000
})

service.interceptors.request.use(
    config => {
        // 访问ONOS API需要携带Authorization信息，才不需要手动输入用户名密码
        config.headers.Authorization = "Basic a2FyYWY6a2FyYWY=";
        return config;
    },
    error =>{
        console.log(error);
        return Promise.reject();
    }
)

service.interceptors.response.use(
    response => {
        return response;
        // if (response.status === 200 || response.status === 204) {
        //     return response.data;
        // } else {
        //     Promise.reject();
        // }
    },
    error => {
        console.log(error);
        return Promise.reject();
    }
);

export default service;