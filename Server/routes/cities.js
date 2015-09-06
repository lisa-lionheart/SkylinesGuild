

var express = require('express');
var router = express.Router();

var cities = [
    {
        id: 0,
        name: 'Steamboat springs',
        pop: 1000
    },
    {
        id:1,
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

module.exports = router;
