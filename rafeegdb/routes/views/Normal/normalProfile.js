var keystone = require('keystone'),
	async = require('async');
var User = keystone.list('User');
var Normal = keystone.list('Normal');

exports = module.exports = function(req, res) {
	var view = new keystone.View(req, res);
	var locals = res.locals;

	// Set locals
	locals.section = 'normal';
	locals.genders = Normal.fields.gender.ops;

	locals.filters = {
		User: req.params.username,

	};

	locals.User = [];

	var view = new keystone.View(req, res),
		locals = res.locals;

	locals.section = 'createaccount';
	locals.form = req.body;

	locals.data = {
		normals: []
	};

	view.on('init', function(next) {

		var q = keystone.list('Normal').model.find()

		q.exec(function(err, results) {
			locals.data.normals = results;
			next(err);
		});
	});

	// Set locals

	view.on('post', function(next) {

		async.series([
			function(cb) {
				var normalData = {
					userId: locals.user.id,
					fullName: req.body.name,
					phone: req.body.phone,
					email: locals.user.email,
					bio: req.body.bio,
					gender: req.body.gender
				};

				var Normal = keystone.list('Normal').model,
					newNormal = new Normal(normalData);

				newNormal.save(function(err) {
					res.redirect('/blog');

					return cb(err);
				});

			}

		], function(err) {

			if (err) return next();
			var onSuccess = function() {
				console.log('Yup');
				res.redirect('');
			}

			var onFail = function(e) {
				req.flash('error', 'There was a problem , please try again.');
				console.log('Yup');

				return next();
			}
		});
	});
	view.query('Normal', keystone.list('Normal').model.find().where('fullName').sort('fullName'));

	// Load the galleries by sortOrder
	view.render('normal');

};
