var mongoose = require('mongoose');
var Schema = mongoose.Schema;

// ref for populate fonction
var EventsSchema = new Schema({
    category: String,
    admin: [{
        type: Schema.Types.ObjectId,
        ref: 'users'
    }],
    participants: [{
        type: Schema.Types.ObjectId,
        ref: 'users'
    }],
    eventDate: Date,
    updated: {
        type: Date,
        default: Date.now
    },
    status: String
});

module.exports = mongoose.model('events', EventsSchema);