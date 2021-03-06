var express = require('express'),
router = express.Router(),
auth = require('./auth.js'),
events = require('./events.js'),
//markers = require('./markers.js'),
users = require('./users.js');
teams = require('./teams.js');

// Test route to make sure everything is working (accessed at GET http://localhost:8080/api)
router.get('/', function(req, res) {
    res.json({ message: 'hooray! welcome to our api!' });
});


// Routes that can be accessed by any one
router.post('/login', auth.login);
router.post('/tokensignin', auth.validateGoogleToken);


//Routes that can be accessed only by autheticated users
router.get('/api/events', events.getByRadius); //first call from main activity : ?lat=10.2&lng=23&radius=Z
router.get('/api/events/all', events.getAll); //debug purpose
router.get('/api/events/find', events.find); //?type=X&category=Y&title=Z
router.get('/api/events/user/:id', events.findForUser);
router.get('/api/events/:id', events.getOne);
router.post('/api/events/create', events.create);
router.delete('/api/events/del/:id', events.delEvent);
router.post('/api/events/:id/addParticipant', events.addParticipant);
router.post('/api/events/:id/closeEvent', events.closeEvent);
router.post('/api/events/:id/removeParticipant', events.removeParticipant);
router.post('/api/events/:id/addAdmin',events.addAdmin);
router.post('/api/events/:id/createTeam',events.createTeam);//with a body attribut name
router.post('/api/events/:id/shuffleParticipants',events.shuffleParticipants);
//todo : remove admin


//Routes that can be accessed only by authenticated & authorized users
router.get('/api/users', users.getAll); //debug purpose
//todo : add name and mail search
router.get('/api/users', users.get); //?id=X,Y,Z
router.post('/api/user', users.create);

router.put('/api/admin/user/:id', users.update);
router.delete('/api/admin/user/:id', users.delete);

//ByPassCheck for devellopment only
router.delete('/api/admin/events/del/:id', events.delEventAdmin);


//Routes that can be accessed only by authenticated & authorized users
router.get('/api/teams', teams.getAll); //debug purpose
//todo : add name and mail search
router.get('/api/teams', teams.get); //?id=X,Y,Z
router.post('/api/team', teams.create);

router.put('/api/admin/team/:id', teams.update);
router.delete('/api/admin/team/:id', teams.delete);

module.exports = router;
