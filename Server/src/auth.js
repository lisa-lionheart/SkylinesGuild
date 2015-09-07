var express = require('express')
    , passport = require('passport')
    , util = require('util')
    , crypto = require('crypto')
    , RedditStrategy = require('passport-reddit').Strategy;

var REDDIT_CONSUMER_KEY = "jRMQeUJNmlQqHQ";
var REDDIT_CONSUMER_SECRET = "fYvLEMfxGDWuVfpEP51qtt-HM60";


// Passport session setup.
//   To support persistent login sessions, Passport needs to be able to
//   serialize users into and deserialize users out of the session.  Typically,
//   this will be as simple as storing the user ID when serializing, and finding
//   the user by ID when deserializing.  However, since this example does not
//   have a database of user records, the complete Reddit profile is
//   serialized and deserialized.
passport.serializeUser(function(user, done) {
    done(null, user);
});

passport.deserializeUser(function(obj, done) {
    done(null, obj);
});




// Use the RedditStrategy within Passport.
//   Strategies in Passport require a `verify` function, which accept
//   credentials (in this case, an accessToken, refreshToken, and Reddit
//   profile), and invoke a callback with a user object.
passport.use(new RedditStrategy({
        clientID: REDDIT_CONSUMER_KEY,
        clientSecret: REDDIT_CONSUMER_SECRET,
        callbackURL: "http://127.0.0.1:3000/auth/reddit/callback"
    },
    function(accessToken, refreshToken, profile, done) {
        // asynchronous verification, for effect...
        process.nextTick(function () {

            // To keep the example simple, the user's Reddit profile is returned to
            // represent the logged-in user.  In a typical application, you would want
            // to associate the Reddit account with a user record in your database,
            // and return that user instead.
            return done(null, profile);
        });
    }
));



module.exports = function(app) {


    app.use(passport.initialize());
    app.use(passport.session());

    app.use(function(req,res,next){
        res.locals.req = req;
        res.locals.user = req.user;
        next();
    });

    app.get('/user', function(req,res) {
       res.json(req.user);
    });

    // GET /auth/reddit
    //   Use passport.authenticate() as route middleware to authenticate the
    //   request.  The first step in Reddit authentication will involve
    //   redirecting the user to reddit.com.  After authorization, Reddit
    //   will redirect the user back to this application at /auth/reddit/callback
    //
    //   Note that the 'state' option is a Reddit-specific requirement.
    app.get('/auth/reddit', function (req, res, next) {
        req.session.state = crypto.randomBytes(32).toString('hex');
        req.session.returnPath = req.query.return;
        passport.authenticate('reddit', {
            state: req.session.state,
        })(req, res, next);
    });


    // GET /auth/reddit/callback
    //   Use passport.authenticate() as route middleware to authenticate the
    //   request.  If authentication fails, the user will be redirected back to the
    //   login page.  Otherwise, the primary route function function will be called,
    //   which, in this example, will redirect the user to the home page.
    app.get('/auth/reddit/callback', function (req, res, next) {
        // Check for origin via state token
        if (req.query.state == req.session.state) {
            passport.authenticate('reddit', {
                successRedirect: req.session.returnPath || '/',
                failureRedirect: '/auth/reddit'
            })(req, res, next);
        }
        else {
            console.log('Session state: '+ req.session.state);
            next(new Error(403));
        }
    });

    app.get('/auth/logout', function (req, res) {
        req.logout();
        res.redirect('/');
    });

};