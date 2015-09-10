
var fs = require('fs');
var express = require('express');
var router = express.Router();
var ClientConnection = require('../src/clientconnection');

var cities = [
    {
        cityId: 0,
        name: 'Steamboat springs',
        pop: 1000
    },
    {
        cityId:1,
        name: 'Cardiff',
        pop: 10
    }

];

// Simple route middleware to ensure user is authenticated.
//   Use this route middleware on any resource that needs to be protected.  If
//   the request is authenticated (typically via a persistent login session),
//   the request will proceed.  Otherwise, the user will be redirected to the
//   login page.
function ensureAuthenticated(req, res, next) {

    var clientSecret = req.header('X-ClientSecret');
    if(clientSecret) {

        var client = ClientConnection.findConnectionByKey(clientSecret);

        if(client) {
            req.client = client;
            next();
        }else {
            console.log('Invalid client secret');
            req.status(400).write('Invlaid client secret');
        }
    }

    if (req.isAuthenticated()) { return next(); }
    res.status(403).write('Not logged in');
}

router.use(ensureAuthenticated);

router.get('/all', function(req, res, next) {
    res.json(cities);
});

router.get('/:id', function(req, res, next) {
    res.json(cities[req.params.id]);
});

router.get('/:id/play', function(req,res){

    var connection = ClientConnection.findConnectionByKey(req.session.clientSecret);
    if(!connection) {
        return res.status(400).send('Client not connected');
    }

    var url = "http://127.0.0.1:3000/data/NewSave.crp";
    connection.sendMessage("load_game", [req.params.id, url]);

    res.status(200);
});

router.post('/:id', function(req,res) {

    //console.log('Saving city' , req.data.cityName);

  //  var saveData = new Buffer(req.data.saveData, 'base64');
//    var thumbnail = new Buffer(req.data.thumbnail, 'base64');

    var dir = 'public/data/' + req.params;

    try { fs.mkdirSync(dir); } catch(e) {}
    try { fs.mkdirSync(dir + '/saves'); } catch(e) {}


    var chunks = [];

    req.on('data', function (chunk) {
        console.log('Recived ' + chunk.length);
        chunks.push(chunk);
    });


    // the end event tells you that you have entire body
    req.on('end', function () {

        var data = Buffer.concat(chunks);
        var offset = 0;

        function readString() {
            var len = data.readInt8(offset);
            offset += 1;
            var str = data.slice(offset,offset+len).toString();
            offset += len;
            return str;
        }

        function readFile() {
            var len = data.readUInt32LE();
            offset += 2;
            var data = data.slice(offset,offset+len);
            offset += len;
            return data;
        }

        var cityName = readString();
        var thumbnail = readFile();
        var saveData = readFile();

        console.log('Saving ' + cityName);

        fs.writeSync(dir + '/thumbnail.png', thumbnail);
        fs.writeSync(dir + '/saves/' + req.data.checksum, saveData);

        res.end();
    });

    res.status(200);
});

module.exports = router;
