var Events = require('../models/events');
var defaultRadius = 100;

//Marker is the point we put in google map
var evenement = {
    //Todo : test
    getByRadius: function(req, res) {
        console.log(req.query);
        if (req.query.lng && req.query.lat) {
            var radius = req.query.radius || defaultRadius;
            Events.find({
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

    //return bench of event matching criteria
    find: function(req, res) {
        var criteria = createGetOneCriteria(req.query);
        //At lest one criteria
        if (Object.keys(criteria).length) {
            Events
                .find(criteria)
                //.populate('detail.participants', 'name') // only return the Persons name
                .exec(function(err, evt) {
                    if (err) res.send(err);
                    else {
                        console.log(saved);
                        res.json(evt);
                    }
                });
        } else {
            res.json({
                message: 'No criteria found'
            });
        }
    },

    //return one event with populate
    getOne: function(req, res) {
        if (req.params.id) {
            Events.findById(req.params.id)
                .populate('detail.participants', 'name') // only return the Persons name
                .populate('detail.createBy', 'name')
                .exec(callback);
        }

        function callback(err, currentEvent) {
            if (err) {
                res.send(err);
            }
            res.json(currentEvent);
        }
    },

    getAll: function(req, res) {
        Events.find().exec(function(err, evts) {
            if (err) {
                console.log(err.err);
                res.send(err);
            }
            else {
                res.json(evts);
            }
        });
    },

    //Todo test
    addParticipant: function(req, res) {
        if (req.params.id && req.body.idParticipant) {
            Events.findById(req.params.idEvent, function(err, evt) {
                if (err) {
                    res.send(err);
                } else {
                    evt.detail.participants.push(req.body.idParticipant);
                    evt.save(function(err) {
                        if (err) {
                            res.send(err);
                        }
                        res.json({
                            message: 'User added to event!'
                        });
                    });
                }
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
        var marker = createMarkerObject(req.body, evt);
        if (marker !== null && evt !== null) {
            marker
                .save(function(err, saved) {
                    if (err) res.send(err);
                    else {
                        console.log(saved);
                        res.json({
                            message: 'Event save with success!'
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

function createMarkerObject(parameters, evt) {
    if (parameters.lat && parameters.lng && parameters.title) {
        var position = {
            lat: parameters.lat,
            lng: parameters.lng
        };
        var marker = new Marker();
        marker.position = position;
        marker.detail = evt;
        marker.title = parameters.title;
        //Todo add upload
        if (parameters.picture !== null) {
            marker.picture = parameters.picture;
        }
        return marker;
    } else return null;
}

//create criteria for findOne
function createGetOneCriteria(parameters) {
    var criteria = {};
    if (parameters.type) criteria.detail.type = parameters.type;
    if (parameters.category) criteria.detail.category = parameters.category;
    if (parameters.title) criteria.title = parameters.title;
    console.log(criteria);
    return criteria;
}
module.exports = evenement;