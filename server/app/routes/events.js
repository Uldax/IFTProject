var Event = require('../models/events');
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
            res.json(user);
        }
    },

    //TodoTest
    //Create event then create a marker
    create: function(req, res) {
        console.log(req.body);
        if (req.body.category && req.body.admin && req.body.title) {
            var newEvent = new Event();
            newEvent.categ = req.body.categ;
            newEvent.position = req.body.position;
            newEvent.createBy = req.body.createBy;
            //Todo add upload
            if (req.body.picture !== null) {
                newEvent.picture = req.body.picture;
            }
            newEvent.save(function(err) {
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

module.exports = events;