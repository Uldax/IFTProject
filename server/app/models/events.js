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
        },
        name : String
    },
    title: {
        unique: true,
        required: true,
        dropDups: true,
        type : String
    },
    picture: {
        type: String,
        default: null
    },
    detail: {
        category: String, //Hocket,soccer , etc..
        //thx : http://stackoverflow.com/questions/14754889/mongoose-3-4-0-returns-object-object-instead-of-actual-values-nodejs
        type: { type: String }, //For fun, match , tournament
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
        teams: [{
            type: String,
            ref: 'teams'
        }],
        start: Date,
        updated: {
            type: Date,
            default: Date.now
        },
        status: {
            type: String,
            default: "created"
        },
        createBy: {
            type: String,
            ref: 'users'
        }
    },
});



module.exports = mongoose.model('events', EventsSchema);