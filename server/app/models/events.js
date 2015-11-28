var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var EventsSchema   = new Schema({
    category : String,
    admin : [Schema.Types.ObjectId],
    createBy : Schema.Types.ObjectId,
    participants : [Schema.Types.ObjectId],
    eventDate : Date,
    updated: { type: Date, default: Date.now },
});

module.exports = mongoose.model('events', EventsSchema);

