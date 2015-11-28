var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var MarkersSchema = new Schema({
    position: {
        lat: Number,
        lng: Number
    },
    eventId: [{
        type: Schema.Types.ObjectId,
        ref: 'events'
    }],
    title: String,
    picture: {
        type: String,
        default: null
    },
});

module.exports = mongoose.model('markers', MarkersSchema);