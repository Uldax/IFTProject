var mongoose = require('mongoose');
var counter = require('./counter');
var Schema = mongoose.Schema;

var UsersSchema = new Schema({
    _id: String,
    email:  { type : String , unique : true, required : true, dropDups: true },
    password: String,
    picture: {
        type: String,
        default: null
    },
    name: {
        first: String,
        last: String
    },
    role: String
});

//console.log('%s is insane', user.name.full);
UsersSchema.virtual('name.full').get(function() {
    return this.name.first + ' ' + this.name.last;
});

//Autoincrement
UsersSchema.pre('save', function(next) {
    var doc = this;
    counter.findByIdAndUpdate({
        _id: 'userid'
    }, {
        $inc: {
            seq: 1
        }
    }, function(error, counter) {
        if (error)
            return next(error);
        doc.testvalue = counter.seq;
        next();
    });
});

module.exports = mongoose.model('users', UsersSchema);