var keystone = require('keystone');
var async = require('async');

exports = module.exports = function(req, res) {

	var view = new keystone.View(req, res);
	var locals = res.locals;

	// Init locals
	locals.section = 'blog';
	locals.filters = {
		category: req.params.category,
	};
	locals.data = {
		comments: [],
		posts: [],
	};

	view.on('init', function(next) {

		if (req.params.category) {
			keystone.list('Post').model.findOne({
				slug: locals.filters.category
			}).exec(function(err, result) {
				locals.data.category = result;
				next(err);
			});
		} else {
			next();
		}
	});

	// Load the comments
	view.on('init', function(next) {

		var q = keystone.list('PostComment').paginate({
						filters: {
							commentState: 'published',
						},
		}).sort('comments');
		if (locals.data.category) {
			q.where('comments').in([locals.data.category]);
		}

		q.exec(function(err, results) {
			locals.data.comments = results;
			next(err);
		});
	});

	locals.form = req.body;
	console.log('test')
	view.on('post', function(next) {
		async.series([
			function(cb) {
				var commentData = {
					author: locals.user.id,
					title: req.body.title,
					comments: req.body.category,
				};

				var PostComment = keystone.list('PostComment').model,
					newComment = new PostComment(commentData);

				newComment.save(function(err) {
					res.redirect('/post');

					return cb(err);
				});

			}

		], function(err) {

			if (err) return next();
			var onSuccess = function() {
				res.redirect('/post');
			}

			var onFail = function(e) {
				req.flash('error', 'There was a problem add post, please try again.');
				console.log('Yup error');

				return next();
			}
		});

	});
	// Render the view
	view.render('post');
};
