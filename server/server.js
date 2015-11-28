// BASE SETUP
// =============================================================================

var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var logger = require('morgan');
var fs = require('fs');
var FileStreamRotator = require('file-stream-rotator');

//Connect to database
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost/testBear'); // connect to local database

// configure app to use bodyParser()
// this will let us get the data from a POST
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());

//logger
var logDirectory = __dirname + '/log';
// ensure log directory exists
fs.existsSync(logDirectory) || fs.mkdirSync(logDirectory);
// create a rotating write stream
var accessLogStream = FileStreamRotator.getStream({
  filename: logDirectory + '/access-%DATE%.log',
  frequency: 'daily',
  verbose: false
});

// setup the logger
app.use(logger('combined', {stream: accessLogStream}));


//Set CORS headers
app.all('/*', function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*"); // restrict it to the required domain
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
    // Set custom headers for CORS
    res.header('Access-Control-Allow-Headers', 'Content-type,Accept,X-Access-Token');
    // When performing a cross domain request, you will recieve
    // a preflighted request first. This is to check if our the app is safe.
    if (req.method == 'OPTIONS') {
        res.status(200).end();
    } else {
        next();
    }
});

//ROUTING
// =============================================================================

// Auth Middleware - This will check if the token is valid
// Only the requests that start with /api/* will be checked for the token.
app.all('/api/*', require('./app/middlewares/validateRequest'));
app.use('/', require('./app/routes'));

// If no route is matched by now, it must be a 404
app.use(function(req, res, next) {
    res.status(404)        // HTTP status 404: NotFound
   .send('URL Not found');
});

// START THE SERVER
// =============================================================================
app.set('port', process.env.PORT || 8080);
//console.log('stack', app._router.stack);
var server = app.listen(app.get('port'), function() {
    console.log('Magic happens on port ' + server.address().port);
});