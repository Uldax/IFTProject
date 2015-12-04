var Events = require('../models/events');
var Const = require('../config/const');
var defaultRadius = 100;
var jwt = require('jwt-simple');



//Marker is the point we put in google map
var evenement = {
    //Todo : test
    getByRadius: function(req, res) {
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
                .exec(function(err, evt) {
                    returnResult(res, err, evt);
                });
        } else {
            res.json({
                message: 'Wrong number of parameters!'
            });
        }
    },

    //return bench of event matching criteria
    //find with title doesn't work : TODO : FIX IT
    find: function(req, res) {
        var criteria = createGetOneCriteria(req.query);
        //At lest one criteria
        if (Object.keys(criteria).length) {
            var query = Events.find().populate('detail.participants', 'name'); // only return the Persons name
            for (var i = 0; i < criteria.length; i++) {
                query.where(criteria[i].fieldName).equals(criteria[i].value);
            }
            query.exec(function(err, evt) {
                returnResult(res, err, evt);
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
                .exec(function(err, evt) {
                    returnResult(res, err, evt);
                });
        }
    },

    getAll: function(req, res) {
        Events.find().exec(function(err, evt) {
            returnResult(res, err, evt);
        });
    },

    addParticipant: function(req, res) {
        if (req.params.id && req.body.idParticipant) {
            Promise.resolve(Events.findById(req.params.id).exec())
                .then(function(evt) {
                    evt.detail.participants.push(req.body.idParticipant);
                    return evt.save(); // returns a promise
                })
                .then(function(newEvt) {
                    res.json({
                        message: 'User added to event!'
                    });
                })
                .catch(function(err) {
                    console.log('error:', err);
                    res.send(err);
                });
        } else {
            res.json({
                message: 'Wrong number of parameters!'
            });
        }
    },

    removeParticipant: function(req, res) {
        if (req.params.id && req.body.idParticipant) {
            Promise.resolve(Events.findById(req.params.id).exec())
                .then(function(evt) {
                    var index = evt.detail.participants.indexOf(req.body.idParticipant);
                    if (index > -1) {
                        evt.detail.participants.splice(index, 1);
                    }
                    return evt.save(); // returns a promise
                })
                .then(function(newEvt) {
                    res.json({
                        message: 'User removed from event'
                    });
                })
                .catch(function(err) {
                    console.log('error:', err);
                    res.send(err);
                });
        } else {
            res.json({
                message: 'Wrong number of parameters!'
            });
        }
    },

    create: function(req, res) {
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
    },

    //only administartor of event can add an admin
    addAdmin: function(req, res) {
        if (req.params.id && req.body.idAdmin) {
            checkPermission(req).then(function(evt) {
                evt.detail.admin.push(req.body.idAdmin);
                evt.save(function(err) {
                    if (err) res.send(err);
                    else {
                        res.json({
                            message: 'Event save with success!'
                        });
                    }
                });
            }).catch(function(err) {
                console.log(err);
                res.json({
                    message: 'not authorized'
                });
            });
        }
    },


    delEvent: function(req, res) {
         if (req.params.id ) {
            checkPermission(req).then(function(evt) {
                evt.remove(function(err) {
                    if (err) res.send(err);
                    else {
                        res.json({
                            message: 'Event removed with success!'
                        });
                    }
                });
            }).catch(function(err) {
                res.json({
                    message: 'not authorized'
                });
            });
        }
    }
};
//Private function
function returnResult(res, err, evt) {
    if (err) {
        console.log(err);
        res.send(err);
    } else {
        res.json(evt);
    }
}

function createEvenementObject(parameters, addCreator) {
    if (parameters.category && parameters.createBy && parameters.admin && parameters.start && parameters.maxParticipants) {
        //Create event object
        var newEvent = {
            category: parameters.category,
            admin: parameters.admin,
            start: parameters.start,
            createBy: parameters.createBy,
            status: Const.eventStatue.created,
            type: parameters.type || Const.eventType.fun,
            maxParticipants: parameters.maxParticipants
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
        var marker = new Events();
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
    var criteria = [];
    if (parameters.type) criteria.push(createFilterObject('detail.type', parameters.type));
    if (parameters.category) criteria.push(createFilterObject('detail.category', parameters.category));
    if (parameters.title) {
        //RegEx can failed if bad string in parameters
        try {
            var regex = new RegExp('^' + parameters.title + '$', "i");
            criteria.push(createFilterObject('detail.title', parameters.title));
        } catch (e) {
            console.log(e);
        }
    }
    console.log(criteria);
    return criteria;
}

function createFilterObject(name, filterValue) {
    var filter = {
        fieldName: name,
        value: filterValue
    };
    return filter;
}

function checkPermission(req) {
    return new Promise(function(resolve, reject) {
        token = (req.body && req.body.access_token) || (req.query && req.query.access_token) || req.headers['x-access-token'];
        Promise.resolve(Events.findById(req.params.id).select('detail.admin').exec())
            .then(function(evt) {
                var decoded = jwt.decode(token, require('../config/secret.js')());
                if ((evt.detail.admin.indexOf(decoded.user._id) > -1)) {
                    console.log('authorized');
                    resolve(evt);
                } else reject(false);
            }).catch(function(err) {
                console.log('error:', err);
                reject(err);
            });
    });
}
module.exports = evenement;