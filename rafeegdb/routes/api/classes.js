var keystone = require('keystone'),
	Classes = keystone.list('Classes');

exports.getClass = function(req, res) {
	Classes.model.find(function(err, items) {

		if (err) return res.apiError('database error', err);

		res.apiResponse({
			Posts: items
		});

	}).sort('-publishedDate');
}
