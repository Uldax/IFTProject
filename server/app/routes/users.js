var User = require('../models/users');
var user = {
    getAll: function(req, res) {
        User.find(function(err, users) {
            if (err) {
                console.log(err);
                res.json({
                    error: err.err
                });

            }
            res.json(users);
        });
    },

    //Get one or multiple user
    //Todo fix it !
    get: function(req, res) {
        //req.query.id => get &id =
        //check if multiple user asked
        if (!(req.query.id.indexOf(',') === -1)) {
            console.log(req.query.id);
            var ids = req.query.id.split(',');
            console.log(ids);
            User.find({
                '_id': {
                    $in: ids
                }
            }, function(err, users) {
                if (err) {
                    console.log(err);
                    res.json({
                        error: err.err
                    });
                }
                res.json(users);
            });
        } else {
            User.findById(req.query.id, function(err, user) {
                if (err) {
                    console.log(err);
                    res.json({
                        error: err.err
                    });
                }
                res.json(user);
            });
        }
    },

    create: function(req, res) {
        var user = new User();
        user.email = req.body.email;
        var name = {
            first: req.body.first,
            last: req.body.last,
        };
        user.name = name;
        user.password = req.body.password;
        user.role = req.body.role;
        user.save(function(err) {
            if (err) {
                console.log(err);
                res.json({
                    error: err.err
                });
            } else {
                res.json({
                    message: 'User created!'
                });
            }
        });
    },

    update: function(req, res) {
        var id = req.params.id;
        User.findById(req.params.id, function(err, user) {
            if (err) {
                console.log(err);
                res.json({
                    error: err.err
                });
            }
            //TODO
            user.name = req.body.name;
            user.save(function(err) {
                if (err) {
                    console.log(err);
                    res.json({
                        error: err.err
                    });
                }
                res.json({
                    message: 'User updated!'
                });
            });
        });
    },

    delete: function(req, res) {
        User.remove({
            _id: req.params.id
        }, function(err, bear) {
            if (err) {
                console.log(err);
                res.json({
                    error: err.err
                });
            }
            res.json({
                message: 'Successfully deleted'
            });
        });
    }
};
//Private methode :
//Add user if not eixiste

module.exports = user;