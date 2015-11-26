var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var EventsSchema   = new Schema({
    category : [String],
    admin : Schema.Users.ObjectId,
    title : String,
    participants : [Schema.Users.ObjectId],
    eventDate : Date,
    updated: { type: Date, default: Date.now },
});

module.exports = mongoose.model('events', EventsSchema);

