var keystone = require('keystone'),
	async = require('async');
var User = keystone.list('User');

exports = module.exports = function(req, res) {
	var view = new keystone.View(req, res);
	var locals = res.locals;

	// Set locals
	locals.section = 'User';

	locals.filters = {
		User: req.params.username,

	};

	locals.User = [];

	view.on('init', function(next) {
		keystone.list('User').model.find().where('username').sort('sortOrder').exec(function(err, results) {

			if (err || !results.length) {
				return next(err);
			}
			locals.User = results;

		});
	});

	if (req.user) {
		return res.redirect('/blog/post');
	}

	var view = new keystone.View(req, res),
		locals = res.locals;

	locals.section = 'createaccount';
	locals.form = req.body;

	// Set locals

	view.on('post', function(next) {

		async.series([

			function(cb) {

				if (!req.body.username || !req.body.email || !req.body.password) {
					req.flash('error', 'Please enter a username, your name, email and password.');
					return cb(true);
				}

				return cb();

			},

			function(cb) {

				keystone.list('User').model.findOne({
					username: req.body.username
				}, function(err, user) {

					if (err || user) {
						req.flash('error', 'User already exists with that Username.');
						return cb(true);
					}

					return cb();

				});

			},



			function(cb) {

				var userData = {

					username: req.body.username,
					email: req.body.email,
					password: req.body.password
				};

				var User = keystone.list('User').model,
					newUser = new User(userData);

				newUser.save(function(err) {
					return cb(err);
				});

			}

		], function(err) {

			if (err) return next();
			var onSuccess = function() {
				console.log('Yup');
				res.redirect('/Org/org');
			}

			var onFail = function(e) {
				req.flash('error', 'There was a problem signing you up, please try again.');
				console.log('Yup');

				return next();

			}

			keystone.session.signin({
				email: req.body.email,
				password: req.body.password
			}, req, res, onSuccess, onFail);

		});

	});
	view.query('User', keystone.list('User').model.find().where('username').sort('username'));

	// Load the galleries by sortOrder
	view.render('User');

};
