var Marker = require('../models/markers');
var defaultRadius = 100;

var marker = {
    //Todo : test
    getByRadius: function(req, res) {
        console.log(req.query);
        if (req.query.lng && req.query.lat) {
            var radius = req.query.radius || defaultRadius;
            Marker.find({
                    'position.lat': {
                        $gt: req.query.lat - radius,
                        $lt: req.query.lat + radius
                    },
                    'position.lng': {
                        $gt: req.query.lng - radius,
                        $lt: req.query.lng + radius
                    },
                })
                .exec(function(err, markers) {
                    if (err) {
                        console.log(err.err);
                        res.send(err);
                    }
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
        var evt = createEvenementObject(req.body);
        var marker = createMarkerObject(req.body,evt);
        if (marker !== null && evt !== null) {
            marker
                .save(function(err, saved) {
                    if (err) res.send(err);
                    else {
                        console.log("yeay bro");
                        console.log(saved);
                        res.json({
                            message: 'All good!'
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


//Private function
function createEvenementObject(parameters, addCreator) {
    if (parameters.category && parameters.createBy && parameters.admin && parameters.eventDate) {
        //Create event object
        var newEvent = {
            category: parameters.category,
            admin: parameters.admin,
            eventDate: parameters.eventDate,
            createBy: parameters.createBy,
            status: "Created"
        };
        if (addCreator) {
            newEvent.participants = [parameters.createBy];
        }
        return newEvent;
    } else return null;
}

function createMarkerObject(parameters,evt) {
    if (parameters.lat && parameters.lng && parameters.title) {
        var position = {
            lat: parameters.lat,
            lng: parameters.lng
        };
        var marker = new Marker();
        marker.position = position;
        marker.evenement = evt;
        marker.title = parameters.title;
        //Todo add upload
        if (parameters.picture !== null) {
            marker.picture = parameters.picture;
        }
        return marker;
    } else return null;
}
module.exports = marker;