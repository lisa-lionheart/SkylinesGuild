var net = require('net');






function ClientConnection(socket) {
    console.log('Client connected');

    this.owningUser = null;
    this.clientKey = null;
    this.socket = socket;

    socket.on('data', this.onDataRecieved.bind(this));
    socket.on('end', this.onDisconnect.bind(this));

}

ClientConnection.allConnections = [];


ClientConnection.findConnectionByKey = function(key) {

    for(var i = 0; i < ClientConnection.allConnections.length; i++) {
        if(ClientConnection.allConnections[i].clientKey = key) {
            return ClientConnection.allConnections[i];
        }
    }

    return null;
};

ClientConnection.listen = function() {
    var server = net.createServer(function(c) {
        ClientConnection.allConnections.push(new ClientConnection(c));
    });

    server.listen(8124, function() {
        console.log('server bound');
    });
};

ClientConnection.prototype.onDisconnect = function() {
    console.log('client disconnected');
};

ClientConnection.prototype.sendMessage = function(method,args){

    var data = method + ':' + args.join(',');
    this.socket.send(data);
};

ClientConnection.prototype.onDataRecieved = function(data) {

    var validMethods = ['auth', 'ping'];

    try {

        data = data.toString().split(':');
        var method = data[0];
        var args = data[1].split(',');

        if(validMethods.indexOf(method) != -1) {
            this[method].call(this,args);
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

    console.log('Client authenticating as ' + clientKey)
};

ClientConnection.prototype.adopt = function(owningUser) {

    this.owningUser = owningUser.name;

    this.sendMessage('auth_success', [owingUser.name]);

};

ClientConnection.prototype.ping = function(cityName) {
    //Update the keep alive
};

module.exports = ClientConnection;

