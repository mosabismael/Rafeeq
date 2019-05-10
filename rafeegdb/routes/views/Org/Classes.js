var keystone = require('keystone');

exports = module.exports = function(req, res) {

	var view = new keystone.View(req, res);
	var locals = res.locals;

	// locals.section is used to set the currently selected
	// item in the header navigation.
	locals.section = 'class';
	locals.filters = {
		classis: req.params.classis,
	};
	var view = new keystone.View(req, res),
		locals = res.locals;
	locals.data = {
		classes: [],
		classItems: []
	};
	view.on('init', function(next) {

		var q = keystone.list('Classes').paginate({

			})
			.sort('-publishedDate')
			.populate('author classies');

		if (locals.data.classis) {
			q.where('classItems').in([locals.data.classis]);
		}

		q.exec(function(err, results) {
			locals.data.classes = results;
			next(err);
		});
	});
	view.on('init', function(next) {

		var q = keystone.list('ClassItem').model.find().select('name')

		q.exec(function(err, results) {
			locals.data.classItems = results;
			next(err);
		});


	});
	// Render the view
	view.render('class');
};
