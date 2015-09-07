var net = require('net');


function ClientConnection(socket) {
    console.log('Client connected');

    this.owningUser = null;
    this.clientKey = null;
    this.socket = socket;

    socket.on('data', this.onDataRecieved.bind(this));
    socket.on('close', this.onDisconnect.bind(this));

    socket.on('error', this.onError.bind(this));
}

ClientConnection.authedConnections = {};


ClientConnection.findConnectionByKey = function(key) {
    return ClientConnection.authedConnections[key];
};

ClientConnection.listen = function() {
    var server = net.createServer(function(c) {
        new ClientConnection(c);
    });

    server.listen(8124, function() {
        console.log('server bound');
    });
};

ClientConnection.prototype.onDisconnect = function() {
    console.log('client disconnected');
};

ClientConnection.prototype.sendMessage = function(method,args){

    var data = method + ':' + args.join(',') + ',' + Date.now() + '\r\n';
    this.socket.write(data);
};

ClientConnection.prototype.onError = function(err) {
    console.log(err);
};


ClientConnection.prototype.onDataRecieved = function(data) {

    var validMethods = ['auth', 'ping'];

    try {

        console.log('RECV:' + data.toString());

        data = data.toString().split(':');
        var method = data[0];
        var args = data[1].split(',');

        if(validMethods.indexOf(method) != -1) {
            this[method].apply(this,args);
        } else {
            console.log('Unknown method: ' + method)
        }

    } catch(e) {
        console.log(e);
    }
};

// Methods

ClientConnection.prototype.auth = function(clientKey) {

    this.clientKey = clientKey;
    ClientConnection.authedConnections[clientKey] = this;
    console.log('Client authenticating as ' + clientKey)
};

ClientConnection.prototype.adopt = function(owningUser) {

    this.owningUser = owningUser.name;
    this.sendMessage('auth_success', [owningUser.name]);
};

ClientConnection.prototype.ping = function(cityName) {
    //Update the keep alive
};

module.exports = ClientConnection;

