var Bear = require('../models/bear');

var bear = {
    getAll: function(req, res) {
        Bear.find(function(err, bears) {
            if (err) {
                res.send(err);
            }
            res.json(bears);
        });
    },

    getOne: function(req, res) {
        Bear.findById(req.params.id, function(err, bear) {
            if (err) {
                res.send(err);
            }
            res.json(bear);
        });
    },

    create: function(req, res) {
        var bear = new Bear();
        bear.name = req.body.name;
        bear.save(function(err) {
            if (err) {
                res.send(err);
            } else {
                res.json({
                    message: 'Bear created!'
                });
            }
        });
    },

    update: function(req, res) {
        Bear.findById(req.params.id, function(err, bear) {
            if (err) {
                res.send(err);
            }
            bear.name = req.body.name; // update the bears info
            // save the bear
            bear.save(function(err) {
                if (err) {
                    res.send(err);
                }
                res.json({
                    message: 'Bear updated!'
                });
            });
        });
    },

    delete: function(req, res) {
        Bear.remove({
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

module.exports = bear;