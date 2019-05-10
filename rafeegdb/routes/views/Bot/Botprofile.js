var keystone = require('keystone');

exports = module.exports = function(req, res) {

	var view = new keystone.View(req, res);
	var locals = res.locals;

	// Set locals
	locals.section = 'Botprofile';
	locals.filters = {
		profile: req.params.profile,
		classID: req.params.classes,
	};
	locals.data = {
		profiles: [],
	};

	// Load the current profile
	view.on('init', function(next) {

		var q = keystone.list('Botprofile').model.findOne({
			country: 'Khartoum',
			slug: locals.filters.profile,
		}).populate('author categories');

		q.exec(function(err, result) {
			locals.data.profile = result;
			next(err);
		});

	});

	// Load other profiles
	view.on('init', function(next) {

		var q = keystone.list('Botprofile').model.find().where('country', 'Khartoum').where('classID', locals.classID).sort('location').populate('author').limit('4');

		q.exec(function(err, results) {
			locals.data.profiles = results;
			next(err);
		});

	});

	// Render the view
	view.render('Botprofile');
};
