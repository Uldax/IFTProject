//Schema for autoIncrement id in user
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var CounterSchema = Schema({
    _id: { type : String , unique : true, required : true, dropDups: true },
    seq: { type: Number, default: 0 }
});
var Counter = mongoose.model('counter', CounterSchema);

var userIncrement = new Counter();
userIncrement._id ="userid";
userIncrement.seq = 0;
//Must do this in first use
userIncrement.save(function(err) {
    if(err) console.log(err.err);
});
module.exports = Counter;