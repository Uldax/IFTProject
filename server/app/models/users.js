var mongoose = require('mongoose');
var counter = require('./counter');
var Schema = mongoose.Schema;

var UsersSchema = new Schema({
    _id: String,
    email: {
        type: String,
        unique: true,
        required: true,
        dropDups: true
    }, //login
    password: String,
    picture: {
        type: String,
        default: null
    },
    type: {
        type: String,
        default: 'default'
    },
    name: {
        first: String,
        last: String
    },
    role: String
}, {
    toObject: {
        virtuals: true
    },
    toJSON: {
        virtuals: true
    }
});

//console.log('%s is insane', user.name.full);
UsersSchema.virtual('name.full')
    .get(function() {
        return this.name.first + ' ' + this.name.last;
    })
    .set(function(name) {
        var split = name.split(' ');
        this.name.first = split[0];
        this.name.last = split[1];
    });

//Auto-increment
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
        if (typeof doc._id === 'undefined') {
            doc._id = counter.seq;
        }
        next();
    });
});

var User = mongoose.model('users', UsersSchema);

var userTest = new User();
userTest.email = "user@api.fr";
userTest.password = "user";
userTest.role = "admin";
userTest.type = 'default';
userTest.name = {
    first: "super",
    last: "test"
};
userTest.save(function(err) {
    if (err) console.log(err.err);
});

var userTest2 = new User();
userTest2.email = "user2@api.fr";
userTest2.password = "user";
userTest2.role = "admin";
userTest2.type = 'default';
userTest2.name = {
    first: "super2",
    last: "test2"
};
userTest2.save(function(err) {
    if (err) console.log(err.err);
});

var userTest3 = new User();
userTest3.email = "user3@api.fr";
userTest3.password = "user";
userTest3.role = "admin";
userTest3.type = 'default';
userTest3.name = {
    first: "super3",
    last: "test3"
};
userTest3.save(function(err) {
    if (err) console.log(err.err);
});

var userTest4 = new User();
userTest4.email = "user4@api.fr";
userTest4.password = "user";
userTest4.role = "admin";
userTest4.type = 'default';
userTest4.name = {
    first: "super4",
    last: "test4"
};
userTest4.save(function(err) {
    if (err) console.log(err.err);
});

var userTest5 = new User();
userTest5.email = "user5@api.fr";
userTest5.password = "user";
userTest5.role = "admin";
userTest5.type = 'default';
userTest5.name = {
    first: "super5",
    last: "test5"
};
userTest5.save(function(err) {
    if (err) console.log(err.err);
});

var userTest6 = new User();
userTest6.email = "user6@api.fr";
userTest6.password = "user";
userTest6.role = "admin";
userTest6.type = 'default';
userTest6.name = {
    first: "super6",
    last: "test6"
};
userTest6.save(function(err) {
    if (err) console.log(err.err);
});

module.exports = User;