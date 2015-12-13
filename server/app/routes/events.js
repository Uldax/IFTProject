var Events = require('../models/events');
var Team = require('../models/teams');
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

    findForUser: function(req, res) {
        var idUser = req.params.id;
        Events.find({
                'detail.participants': idUser
            })
            .populate('detail.participants', 'name')
            .populate('detail.createBy', 'name')
            .exec(function(err, evt) {
                returnResult(res, err, evt);
            });
    },

    //return one event with populate
    getOne: function(req, res) {
        if (req.params.id) {
            Events.findById(req.params.id)
                .populate('detail.teams')
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
    
    createTeam: function(req, res) { 
        
        console.log("createTeam: start");
        
        //Params validation
        if (req.params.id && req.body.name) {
            
            console.log("createTeam: retreived the params correctly");
            
            Promise.resolve(Events.findById(req.params.id).exec())
                .then(function(evt) {  
                    var team = new Team();
                    team.idEvent = req.params.id;
                    team.name = req.body.name
                    team.save(function(err, obj) {
                        if (err) {
                            console.log("createTeams:" + err.err);
                                return false;
                        } else {
                            evt.detail.teams.push(obj._id);
                            evt.save();                                 
                            console.log("createTeams: a new team has been created with the id: " + obj._id);
                            return true;
                        }
                    });
                return evt.save(); // returns a promise     
                }) 
                .then(function(newEvt) {
                    res.json({
                        message: 'A new Team was added to the event!'
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
    
      shuffleParticipants: function(req, res) {
        
          //Getting the current Events informations as evt
          Promise.resolve(Events.findById(req.params.id).exec())
                .then(function(evt) {       
                
                    var nbTeam = evt.detail.teams.length;
                        
                    console.log("shuffleParticipants: current number of team for this event " + nbTeam);

                    if (nbTeam > 1) {

                        console.log("shuffleParticipants: start");
                        
                        
                        var promiseArray = [];
                        //We clean the current participantList in each teams                        
                        for(var i=0; i < nbTeam; i++){
                                console.log("shuffleParticipants: we add a promise into the promiseArray");
                                promiseArray.push(generatePromiseForTeam(evt,i));
                        } 
                                 
                        Promise.all(promiseArray)
                        .then(function(team){
                            console.log("shuffleParticipants-emptyTeam-loop: start");
                            for(var i=0; i < team.length;i++){
                                console.log("shuffleParticipants: emptied the team:" + team[i].name);
                                emptyTeam(team[i]);                                
                            }
                            return team;
                        })
                        .then(function(team){
                            
                            //We shuffle the participants into n teams
                            var randomizedArray = shuffle(evt.detail.participants);
                            
                            //The counter that we will use to distribute the participants into all the teams
                            var counter = 0; 
                             
                            console.log("shuffleParticipants-repartition-loop: start");
                            //Repartition loop
                            for (var p = 0; p < randomizedArray.length; p++) {
                                                                
                                console.log("shuffleParticipants: we added the participant:" + p + " to the team: " + team[counter].name);
                                
                                //We add the current participant into the team[counter]
                                addParticipantIntoTeam(randomizedArray[p],team[counter])

                                //if the counter == nbTeams - 1 then we reset it, otherwise we increment it.
                                if(counter === nbTeam - 1){
                                    counter = 0;
                                }
                                else{
                                    counter++;
                                }                            
                            }//ENDFOR REPARTITION LOOP
                        }).then(function(team) {
                            res.json({
                                message: 'shuffleParticipants was a success!'
                            });
                        }) 
                        .catch(function(err) {
                            console.log('shuffleParticipants-promiseall: error:' + err);
                            res.send(err);
                        });
                    }
                    else{
                        res.json({
                            message: 'You need at least 2 teams!'
                        });
                    }       
                })
                .catch(function(err) {
                        console.log('shuffleParticipants error: ' + err);
                        res.send(err);
                });
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
        var evt = createEvenementObject(req.body, true);
        var marker = createMarkerObject(req.body, evt);
        console.log("create event");
        if (marker !== null && evt !== null) {
            marker
                .save(function(err, saved) {
                    if (err) {
                        console.log(err);
                        res.send(err);
                    } else {
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
        if (req.params.id) {
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

function addParticipantIntoTeam(participantId,team){                                  
        team.participants.push(participantId);
        console.log("addParticipantIntoTeam: participant was added to the team: " + team.name);        
        return team.save(); // returns a promise   
   
}
function generatePromiseForTeam(evt,index){
   return new Promise(function(resolve,reject){       
       resolve(Team.findById(evt.detail.teams[index]).exec());
   });
}
function emptyTeam(team){ 
        team.participants = [];
        console.log('emptyTeam: participants list for the team: ' + team.name + "has been wiped out");                                
        return team.save(); // returns a promise
    
}
function shuffle(array) {
  var currentIndex = array.length, temporaryValue, randomIndex ;

  // While there remain elements to shuffle...
  while (0 !== currentIndex) {

    // Pick a remaining element...
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;

    // And swap it with the current element.
    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
}
function handleNewTeam(teamData) {
    console.log("call to handleNewTeam");
    var id = 0; 
    var team = new Team();
    team.idEvent = teamData.idEvent;
    
    team.save(function(err, obj) {
        
       
        if (err) {
            console.log("handleNewTeam:" + err.err);
            return false;
        } else {
             console.log("handleNewTeam: succes");
             console.log("handleNewTeam: a new team has been creadted with the id: " + obj._id);
             id = obj._id;
             return true;
        }
    });
    return id;
        
    }

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
            status: Const.eventStatus.created,
            type: parameters.type || Const.eventType.fun,
            maxParticipants: parameters.maxParticipants
        };
        if (addCreator) {
            newEvent.participants = [parameters.createBy];
        }
        return newEvent;
    } else {
        console.log("createEvenementObject: wrong number of parameters");
        return null;
    }
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
    } else {
        console.log("createMarkerObject: wrong number of parameters");
        return null;
    }
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