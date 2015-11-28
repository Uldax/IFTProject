var Marker = require('../models/markers');
var defaultRadius = 100;

var marker = {
    //Todo : test
    getByRadius: function(req, res) {
        console.log("yeay you call me ");
        console.log(req.query);
        if (req.query.initialLong && req.query.initialLat) {
            var radius = req.query.radius || defaultRadius;
            Marker.find({
                    'position.lat': {
                        $gt: req.query.initialLat - radius,
                        $lt: req.query.initialLat + radius
                    },
                    'position.lng': {
                        $gt: req.query.initialLong - radius,
                        $lt: req.query.initialLat - radius
                    },
                })
                .exec(function(err, markers) {
                    if (err)  res.send(err);
                    res.json(markers);
                });
        } else {
            res.json({
                message: 'Wrong number of parameters!'
            });
        }
    },

    create: function(req, res) {
        console.log(req.body);
        if (req.body.position && req.body.eventId && req.body.createBy) {
            var marker = new Marker();
            marker.categ = req.body.categ;
            marker.position = req.body.position;
            marker.createBy = req.body.createBy;
            //Todo add upload
            if (req.body.picture !== null) {
                marker.picture = req.body.picture;
            }
            marker.save(function(err) {
                if (err) {
                    res.send(err);
                } else {
                    res.json({
                        message: 'Markers created!'
                    });
                }
            });
        } else {
            res.json({
                message: 'Wrong number of parameters!'
            });
        }
    }
};

module.exports = marker;