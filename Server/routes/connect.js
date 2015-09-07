var express = require('express');
var router = express.Router();

var ClientConnection = require('../src/clientconnection');


router.get('/:clientSecret', function(req, res, next) {

  if(!req.isAuthenticated()) {
     return res.redirect('/auth/reddit?return=/connect/' + req.params.clientSecret);
  }

  var connection = ClientConnection.findConnectionByKey(req.params.clientSecret);
  if(!connection) {
    res.status(500).send('Invalid Key')
  } else {
    req.session.clientSecret = req.params.clientSecret;
    connection.adopt(req.user);
    res.redirect('/');
  }

});

module.exports = router;
