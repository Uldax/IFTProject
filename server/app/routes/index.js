var express = require('express'),
router = express.Router(),
auth = require('./auth.js'),
events = require('./events.js'),
markers = require('./markers.js'),
users = require('./users.js');

// Test route to make sure everything is working (accessed at GET http://localhost:8080/api)
router.get('/', function(req, res) {
    res.json({ message: 'hooray! welcome to our api!' });
});


// Routes that can be accessed by any one
router.post('/login', auth.login);
router.post('/tokensignin', auth.validateGoogleToken);

//Routes that can be accessed only by autheticated users
router.get('/api/markers', markers.getByRadius);
router.post('/api/markers/', markers.create);

router.get('/api/events/:id', events.getOne);
router.get('/api/events/categ/:categ', events.getByCategs);
router.post('/api/events/', events.create);
router.post('/api/events/:id/addUsers', events.addParticipant);
//todo remove user


//Routes that can be accessed only by authenticated & authorized users
router.get('/api/admin/users', users.getAll);
//&id =blablbla;blalb
router.get('/api/users/', users.get);
router.post('/api/user/', users.create);
router.put('/api/admin/user/:id', users.update);
router.delete('/api/admin/user/:id', users.delete);

module.exports = router;
