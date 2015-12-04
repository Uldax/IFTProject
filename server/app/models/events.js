var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var EventsSchema = new Schema({
    position: {
        lat: {
            type: Number,
            required: true
        },
        lng: {
            type: Number,
            required: true
        }
    },
    title: String,
    picture: {
        type: String,
        default: null
    },
    detail: {
        category: String, //Hocket,soccer , etc..
        type: String, //For fun, match , tournament
        maxParticipants: {
            type: Number,
            default: 0 //open for everyone
        },
        admin: [{
            type: String,
            ref: 'users'
        }],
        participants: [{
            type: String,
            ref: 'users'
        }],
        start: Date,
        updated: {
            type: Date,
            default: Date.now
        },
        status: String,
        createBy: {
            type: String,
            ref: 'users'
        }
    },
});

module.exports = mongoose.model('events', EventsSchema);