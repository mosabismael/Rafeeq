var keystone = require('keystone');
var async = require('async');
var Post = keystone.list('Post');
var class_res = keystone.list('ClassItem');
var fs = require('fs');
exports = module.exports = function(req, res) {

	var view = new keystone.View(req, res);
	var locals = res.locals;
	// Init locals
	locals.section = 'blog';
	locals.filters = {
		classis: req.params.classis,
		User: req.params.username,
	};
	locals.data = {
		posts: [],
		classies: [],
	};
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

				keystone.list('Post').model.count().where('classies').in([classis.id]).exec(function(err, count) {
					classis.postCount = count;
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
	// Load the posts
	view.on('init', function(next) {

		var q = keystone.list('Post').paginate({
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
			locals.data.posts = results;
			next(err);
		});
	});

	// create the posts
	view.on('post', {
		action: 'blog'
	}, function(next) {

		async.series([
			function(cb) {

				var postData = {
					author: locals.user.id,
					title: req.body.title,
					classies: req.body.classis,
					file: req.files.photo,
					// file:{
					// 	filename:req.files.photo.filename,
					// 	size:req.files.photo.size,
					// 	mimetype:req.files.photo.mimetype,
					// 	path:req.files.photo.path,
					// 	originalname:req.files.photo.name,
					// 	url:req.files.photo.url,
					// },
					date: new Date()
				};

				console.log(req.files);

				var Post = keystone.list('Post').model,
					newPost = new Post(postData);

				newPost.save(function(err) {
					res.redirect('/blog');
					return cb(err);
				});
			}
		], function(err) {
			if (err) return next();
			var onSuccess = function() {
				console.log('Yup sess');
				res.redirect('/blog');
			}
			var onFail = function(e) {
				req.flash('error', 'There was a problem add post, please try again.');
				console.log('Yup error');
				return next();
			}
		});

	});

	view.render('blog');
};
