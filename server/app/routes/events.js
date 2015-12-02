//Don't use for the moment

var Event = require('../models/events');
var markerFunction = require('./markers');
var ObjectId = require('mongodb').ObjectID;
//No border effect purpose
var events = {
    //Todo : test
    getByCategs: function(req, res) {
        if (req.params.categ) {
            Event.find({
                category: req.params.categ
            }, function(err, events) {
                if (err) {
                    res.send(err);
                } else {
                    res.json(events);
                }
            });
        } else {
            res.json({
                message: 'Wrong categorie'
            });
        }
    },

    //Todo test
    addParticipant: function(req, res) {
        if (req.params.id && req.body.idParticipant) {
            Event.findById(req.params.idEvent, function(err, currentEvent) {
                if (err) {
                    res.send(err);
                } else {
                    currentEvent.idParticipant.push(req.body.idParticipant);
                    currentEvent.save(function(err) {
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

    getOne: function(req, res) {
        var populateQuery = [{
            path: 'participants',
            select: 'name'
        }, {
            path: 'admin',
            select: 'name'
        }];
        Event.findById(req.params.id)
            .populate(populateQuery)
            .exec(callback);

        function callback(err, currentEvent) {
            if (err) {
                res.send(err);
            }
            res.json(currentEvent);
        }
    },

    //TodoTest
    //Create event then create a marker
    create: function(req, res) {
        console.log("call to create Event");
        console.log(req.body);
        var marker = markerFunction.createMarkerObject(req.body);
        var evenement = createEvenementObject(req.body);
        if (marker !== null && evenement !== null) {
            evenement
                .save(function(err,evt) {
                    if (err) res.send(err);
                    else {
                        marker.eventId = evt.id;
                        marker.save(function(error) {
                            if (error) res.send(error);
                            else {
                                console.log("yeay bro");
                                res.json({
                                    message: 'All good!'
                                });
                            }
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
        var newEvent = new Event();
        newEvent.category = parameters.category;
        newEvent.admin = ObjectId(parameters.admin);
        if (addCreator) {
            newEvent.participants = [ObjectId(parameters.createBy)];
        }
        newEvent.eventDate = parameters.eventDate;
        newEvent.createBy = ObjectId(parameters.createBy);
        newEvent.status = "Created";
        return newEvent;
    } else return null;
}

module.exports = events;