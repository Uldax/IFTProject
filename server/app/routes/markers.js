var Marker = require('../models/markers');
var ObjectId = require('mongodb').ObjectID;
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
                    if (err) res.send(err);
                    res.json(markers);
                });
        } else {
            res.json({
                message: 'Wrong number of parameters!'
            });
        }
    },

    //Must not be used
    create: function(req, res) {
        console.log(req.body);
        var marker = createMarkerObject(req.body,true);
        if (marker !== null) {
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
    },

    createMarkerObject: function(parameters) {
        if (parameters.lat && parameters.lng  && parameters.title) {
            var position = {
                lat: parameters.lat,
                lng: parameters.lng
            };
            var marker = new Marker();
            marker.position = parameters.position;
            marker.createBy = parameters.title;
            //Todo add upload
            if (parameters.picture !== null) {
                marker.picture = parameters.picture;
            }
        } else return null;
    }
};

module.exports = marker;