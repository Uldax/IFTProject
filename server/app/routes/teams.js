var Team = require('../models/teams');
var team = {
    getAll: function(req, res) {
        Team.find(function(err, teams) {
            if (err) {
                res.send(err);
            }
            res.json(teams);
        });
    },

    //Get one or multiple team
    get: function(req, res) {
        //req.query.id => get &id =
        //check if multiple user asked
        if( !( req.query.id.indexOf(',') === -1))
        {
          console.log(req.query.id);
          var ids = req.query.id.split(',');
          console.log(ids);
          User.find({
              '_id': { $in: ids}
          }, function(err, teams){
                if (err) {
                    res.send(err);
                }
                res.json(teams);
          });
        }
        else {
            User.findById(req.query.id, function(err, team) {
                if (err) {
                    res.send(err);
                }
                res.json(team);
            });
        }
    },

    create: function(req, res) {
        var team = new Team();
        team.idEvent = req.body.idEvent;        
        team.name = req.body.name;
        
        team.save(function(err) {
            if (err) {
                res.send(err);
            } else {
                res.json({
                    message: 'Team created!'
                });
            }
        });
    },

    update: function(req, res) {
        var id = req.params.id;
        Team.findById(req.params.id, function(err, team) {
            if (err) {
                res.send(err);
            }
            //TODO
            team.name = req.body.name;
            team.save(function(err) {
                if (err) {
                    res.send(err);
                }
                res.json({
                    message: 'Team updated!'
                });
            });
        });
    },

    delete: function(req, res) {
        Team.remove({
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
//Private methode :
//Add user if not eixiste

module.exports = team;