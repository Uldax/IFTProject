var jwt = require('jwt-simple');
var User = require('../models/users');
var request = require("request");
var Promise = require('promise');

var auth = {
    // Fire a query to your DB and check if the credentials are valid
    // must be promises
    validate: function(username, password) {
        return new Promise(function(resolve, reject) {
            if (username === '' || password === '') {
                reject("empty credentials");
            }
            User.find({
                'username': username,
                'password': password
            }, function(err, user) {
                if (err) {
                    reject(err);
                }
                if (user.length === 1) {
                    resolve(user[0]);
                } else {
                    reject("database error");
                }

            });
        });
    },

    validateGoogleToken: function(req, res) {
        var token = req.body.token;
        if (token === undefined || token === null) {
            res.status(401);
            res.json({
                "status": 401,
                "message": "invalid token"
            });
        } else {
            console.log("Token receive ");
            //To validate an ID token using the tokeninfo endpoint, make an HTTPS POST or GET request to the endpoint, and pass your ID token in the id_token (for google)
            var endpoint = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + token;
            request(endpoint, function(err, response, body) {
                if (!err && response.statusCode === 200) {
                    //Parse repsonse into JSON
                    var info = JSON.parse(body);
                    if (info.sub) {
                        //TODO add name
                        var user = {
                            id: info.sub,
                            role: "user",
                            username : info.name
                        };
                        res.json(genToken(user));
                    } else {
                        res.status(401);
                        res.json({
                            "status": 401,
                            "message": "Wrong toker"
                        });
                    }

                } else {
                    res.status(401);
                    res.json({
                        "status": 401,
                        "message": err
                    });
                }
            });
        }

    },
    //Call after token check to get user role and information
    validateUser: function(username) {
        return new Promise(function(resolve, reject) {
            User.find({
                'username': username
            }, function(err, user) {
                if (err) {
                    reject(err);
                }
                if (user.length === 1) {
                    resolve(user[0]);
                } else {
                    reject("multiple user");
                }
            });
        });
    },

    //http://localhost:8080/login
    login: function(req, res) {
        var username = req.body.username || '';
        var password = req.body.password || '';
        auth.validate(username, password)
            .then(function(user) {
                // If authentication is success, we will generate a token
                // and dispatch it to the client
                res.json(genToken(user));
            })
            .catch(function(errMessage) {
                res.status(401);
                res.json({
                    "status": 401,
                    "message": errMessage
                });
            });
    },
};

// private method
function expiresIn(numDays) {
    var dateObj = new Date();
    return dateObj.setDate(dateObj.getDate() + numDays);
}

function genToken(user) {
    var expires = expiresIn(7); // 7 days
    var token = jwt.encode({
        exp: expires
    }, require('../config/secret')());
    return {
        token: token,
        expires: expires,
        user: user
    };
}

module.exports = auth;