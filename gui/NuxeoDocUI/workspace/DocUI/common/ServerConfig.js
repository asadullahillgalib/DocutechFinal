//<LOCAL>
//location.host is used for machine independency of url as it will auto complete 
//machine url:port where this app is runnin for local test on your machine make it localhost:port\
var url = 'localhost:8080/';
//var url = location.host;

//<Server urls for nSync>
var httpServer = 'http://' + url + '/docutech/jsonRequest';
//var httpServer = 'http://vntdaclsappu031:8080/nuxeo';