var Marker = require('../models/markers');

var marker = {
    //Todo
    getByRadius: function(req, res) {
        res.json(Marker);
    },

    //Todo
    getByCategs: function(req, res) {
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
            user.save(function(err) {
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