var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var UserSchema   = new Schema({
    username : String,
    password : String,
    name : String,
    role : String
});

module.exports = mongoose.model('users', UserSchema);

