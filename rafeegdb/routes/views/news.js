var keystone = require('keystone');
var async = require('async');
var News = keystone.list('News');
var class_res = keystone.list('ClassItem');

exports = module.exports = function(req, res) {

	var view = new keystone.View(req, res);
	var locals = res.locals;

	// Init locals
	locals.section = 'news';
	locals.filters = {
		classis: req.params.classis,
		User: req.params.username,
	};
	locals.data = {
		newes: [],
		classies: [],
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

	var view = new keystone.View(req, res),
		locals = res.locals;

	// locals.section = 'createaccount';
	locals.form = req.body;


	// Load all classies
	view.on('init', function(next) {

		keystone.list('ClassItem').model.find().sort('name').exec(function(err, results) {

			if (err || !results.length) {
				return next(err);
			}

			locals.data.classies = results;

			// Load the counts for each classis
			async.each(locals.data.classies, function(classis, next) {

				keystone.list('News').model.count().where('classies').in([classis.id]).exec(function(err, count) {
					classis.newsCount = count;
					next(err);
				});

			}, function(err) {
				next(err);
			});
		});
	});

	// Load the current classis filter
	view.on('init', function(next) {

		if (req.params.classis) {
			keystone.list('ClassItem').model.findOne({
				key: locals.filters.classis
			}).exec(function(err, result) {
				locals.data.classis = result;
				next(err);
			});
		} else {
			next();
		}
	});
	// Load the newes
	view.on('init', function(next) {

		var q = keystone.list('News').paginate({
				page: req.query.page || 1,
				perPage: 10,
				maxPages: 10,

			})
			.sort('-publishedDate')
			.populate('author classies');

		if (locals.data.classis) {
			q.where('classies').in([locals.data.classis]);
		}

		q.exec(function(err, results) {
			locals.data.newes = results;
			next(err);
		});
	});

	view.on('post', function(next) {
		async.series([
			function(cb) {
				var newsData = {
					author: locals.user.id,
					content: req.body.content,
					classies: req.body.classis,
				};
				console.log('22222');
				var News = keystone.list('News').model,
					newNews = new News(newsData);

				newNews.save(function(err) {
					res.redirect('/news');

					return cb(err);
				});

			}

		], function(err) {

			if (err) return next();
			var onSuccess = function() {
				console.log('Yup sess');
				res.redirect('/news');
			}

			var onFail = function(e) {
				req.flash('error', 'There was a problem add News, please try again.');
				console.log('Yup error');

				return next();

			}


		});

	});
	// Load the newes

	// Render the view
	view.render('news');
};
