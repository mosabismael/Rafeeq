var keystone = require('keystone'),
	ClassItem = keystone.list('ClassItem');

exports.getClassItems = function(req, res) {
	ClassItem.model.find(function(err, items) {

		if (err) return res.apiError('database error', err);

		res.apiResponse({
			Posts: items
		});

	})
}
