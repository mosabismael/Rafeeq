var keystone = require('keystone');
var async = require('async');
var message = keystone.list('Botmessage');

exports = module.exports = function(req, res) {

	var view = new keystone.View(req, res);
	var locals = res.locals;

	// Init locals
	locals.section = 'message';
	locals.filters = {
		category: req.params.category,

	};
	locals.data = {
		messages: [],
		classes: [],
	};

	// Load all classes
	view.on('init', function(next) {

		keystone.list('Botmessage').model.find().sort('name').exec(function(err, results) {

			if (err || !results.length) {
				return next(err);
			}

			locals.data.classes = results;

			// Load the counts for each class
			async.each(locals.data.classes, function(category, next) {

				keystone.list('Botmessage').model.count().where('userId').in([category.id]).exec(function(err, count) {
					category.messageCount = count;
					next(err);
				});

			}, function(err) {
				next(err);
			});
		});
	});

	// Load the current class filter
	view.on('init', function(next) {

		if (req.params.class) {
			keystone.list('Botmessage').model.findOne({
				key: locals.filters.class
			}).exec(function(err, result) {
				locals.data.class = result;
				next(err);
			});
		} else {
			next();
		}
	});

	// Load the messages
	view.on('init', function(next) {

		var q = keystone.list('Botmessage').paginate({

				filters: {
					userId: locals.user.id,
				},
			})
			.sort('date')
			.populate('author classes');

		if (locals.data.class) {
			q.where('classes').in([locals.data.class]);
		}

		q.exec(function(err, results) {
			locals.data.messages = results;
			next(err);
		});
	});
	// Load messages on the category
	view.on('init', function(next) {
		message.model.find()
			.where('userId', locals.user.id)
			.sort('date')
			.exec(function(err, messages) {
				if (err) return res.err(err);
				if (!messages) return res.notfound('category messages not found');
				locals.messages = messages;
				next();
			});
	});

	// Create a message
	view.on('post', {
		action: 'message.create'
	}, function(next) {

		var newmessage = new message.model({
			userId: locals.user.id,

		});

		var updater = newmessage.getUpdateHandler(req);

		updater.process(req.body, {
			fields: 'text_1,text_2,image',
			flashErrors: true,
			logErrors: true,
		}, function(err) {
			if (err) {
				validationErrors = err.errors;
			} else {
				req.flash('success', 'Your message was added.');
				return res.redirect('/blog/message/' + locals.category.key + '#message-id-' + newmessage.id);
			}
			next();
		});

	});

	// Delete a message
	view.on('get', {
		remove: 'message'
	}, function(next) {

		if (!req.user) {
			req.flash('error', 'You must be signed in to delete a message.');
			return next();
		}

		message.model.findOne({
				_id: req.query.message,
			})
			.exec(function(err, message) {
				if (err) {
					if (err.name === 'CastError') {
						req.flash('error', 'The message ' + req.query.message + ' could not be found.');
						return next();
					}
					return res.err(err);
				}
				if (!message) {
					req.flash('error', 'The message ' + req.query.message + ' could not be found.');
					return next();
				}
				if (message.author != req.user.id) {
					req.flash('error', 'Sorry, you must be the author of a message to delete it.');
					return next();
				}
				message.messagestate = 'archived';
				message.save(function(err) {
					if (err) return res.err(err);
					req.flash('success', 'Your message has been deleted.');
					return res.redirect('/blog/message/' + locals.message.key);
				});
			});
	});

	// Render the view
	view.render('message');
};
