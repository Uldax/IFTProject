var User = require('../models/user');
var user = {
    getAll: function(req, res) {
        User.find(function(err, users) {
            if (err) {
                res.send(err);
            }
            res.json(users);
        });
    },

    getOne: function(req, res) {
        User.findById(req.params.id, function(err, user) {
            if (err) {
                res.send(err);
            }
            res.json(user);
        });
    },

    create: function(req, res) {
        var user = new User();
        user.username = req.body.username;
        user.name = req.body.name;
        user.password = req.body.password;
        user.role = req.body.role;
        user.save(function(err) {
            if (err) {
                res.send(err);
            } else {
                res.json({
                    message: 'User created!'
                });
            }
        });
    },

    update: function(req, res) {
        var updateuser = req.body;
        var id = req.params.id;
        User.findById(req.params.id, function(err, user) {
            if (err) {
                res.send(err);
            }
            //TODO
            user.name = req.body.name;
            user.save(function(err) {
                if (err) {
                    res.send(err);
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
                res.send(err);
            }
            res.json({
                message: 'Successfully deleted'
            });
        });
    }
};

module.exports = user;