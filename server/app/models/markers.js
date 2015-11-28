var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var MarkersSchema   = new Schema({
    position : { lat : Number, lng : Number },
    eventId : [Schema.Types.ObjectId],
    title : String,
    picture: { type: String, default: null},
});

module.exports = mongoose.model('markers', MarkersSchema);

