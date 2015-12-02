var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var MarkersSchema = new Schema({
    position: {
        lat:  { type : Number , required : true},
        lng: { type : Number , required : true}
    },
    evenement: {
        category: String,
        admin: [{
            type:String,
            ref: 'users'
        }],
        participants: [{
            type: String,
            ref: 'users'
        }],
        eventDate: Date,
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
    title: String,
    picture: {
        type: String,
        default: null
    },
});

module.exports = mongoose.model('markers', MarkersSchema);