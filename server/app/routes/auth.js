var jwt = require('jwt-simple');
var User = require('../models/users');
var request = require("request");
var Promise = require('promise');

var auth = {
    // must be promises
    // TODO resolve juste email and role
    validate: function(email, password) {
        return new Promise(function(resolve, reject) {
            if (email === '' || password === '') {
                reject("empty credentials");
            }
            User.find({
                    'email': email,
                    'password': password
                })
                //Virtuals are NOT available for document queries or field selection.
                .select({
                    name: 1,
                    role: 1,
                    _id: 1,
                    type: 1,
                    picture : 1
                })
                .exec(function(err, user) {
                    if (err) {
                        console.log(err);
                        reject(err);
                    }
                    if (user.length === 1 && user[0].type == 'default') {
                        resolve(user[0]);
                    } else {
                        reject("this user doesn't exist");
                    }

                });
        });
    },

    validateGoogleToken: function(req, res) {
        var token = req.body.token;
        if (typeof req.body.token === 'undefined') {
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
                        //Response to client
                        var user = {
                            _id: info.sub,
                            role: "user",
                            email: info.email,
                            name: {
                                first: info.name,
                                last: info.family_name
                            },
                        };
                        var token = genToken(user);
                        handleNewUser(user);
                        res.json(token);
                    } else {
                        res.status(401);
                        res.json({
                            "status": 401,
                            "message": "Wrong token"
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
    validateUser: function(email) {
        return new Promise(function(resolve, reject) {
            User.find({
                'email': email
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
        var email = req.body.email || '';
        var password = req.body.password || '';
        auth.validate(email, password)
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
        exp: expires,
        user: user
    }, require('../config/secret')());
    return {
        token: token,
        expires: expires,
        user: user
    };
}

function handleNewUser(userData) {
    console.log("call to handleNewUser");
    Promise.resolve(User.count({
            'email': userData.email
        }).exec())
        .then(function(count) {
            console.log(count);
            if (count === 0) {
                var user = new User();
                user.email = userData.email;
                var name = {
                    first: userData.name.first,
                    last: userData.name.last,
                };
                user._id = userData._id;
                user.name = name;
                user.type = 'google';
                //warning here but prevent by type
                user.password = require('../config/secret')();
                user.role = userData.role;
                user.save(function(err) {
                    if (err) {
                        console.log(err.err);
                        return false;
                    } else {
                        return true;
                    }
                });
            }
        })
        .catch(function(err) {
            console.log('error:', err);
            res.send(err);
        });
    }

    module.exports = auth;