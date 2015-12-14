var mongoose = require('mongoose');
var counter = require('./counter');
var Schema = mongoose.Schema;

var TeamsSchema = new Schema({
    _id: String,
    idEvent: String,
    participants: [{
            type: String,
            ref: 'users'
        }],
    name: String    
}, {
    toObject: {
        virtuals: true
    },
    toJSON: {
        virtuals: true
    }
});

//Auto-increment
TeamsSchema.pre('save', function(next) {
    var doc = this;
    counter.findByIdAndUpdate({
        _id: 'teamid'
    }, {
        $inc: {
            seq: 1
        }
    }, function(error, counter) {
        if (error)
            return next(error);
        if (typeof doc._id === 'undefined') {
            doc._id = counter.seq;
        }
        next();
    });
});

var Team = mongoose.model('teams', TeamsSchema);

var teamTest = new Team();
teamTest.idEvent = "566cc0d1ee0ee66c01000001";
teamTest.name = "Test Équipe";
teamTest.save(function(err) {
    if (err) console.log(err.err);
});

module.exports = Team;