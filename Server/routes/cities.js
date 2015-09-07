

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
    if (req.isAuthenticated()) { return next(); }
    res.send(403,'Not logged in');
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

module.exports = router;
