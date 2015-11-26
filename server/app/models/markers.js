var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var MarkersSchema   = new Schema({
    position : { lat : Number, lng : Number },
    event : [Schema.Events.ObjectId],
});

module.exports = mongoose.model('markers', MarkersSchema);

