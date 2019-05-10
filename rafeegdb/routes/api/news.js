var keystone = require('keystone'),
	News = keystone.list('News');

/**
5 * List Newses
6 */
exports.getNewses = function(req, res) {
	News.model.find(function(err, items) {

		if (err) return res.apiError('database error', err);

		res.apiResponse({
			Newses: items
		});

	});
}

/**
20 * Get News by ID
21 */
exports.getNewsById = function(req, res) {
	News.model.findById(req.params.id).exec(function(err, item) {

		if (err) return res.apiError('database error', err);
		if (!item) return res.apiError('not found');
		res.apiResponse({
			News: item
		});

	});
}


/**
37 * Create a News
38 */
exports.createNews = function(req, res) {

	var item = new News.model(),
		data = req.body;

	item.getUpdateHandler(req).process(data, function(err) {

		if (err) return res.apiError('error', err);

		res.apiResponse({
			News: item
		});

	});
}

/**
56 * Update News by ID
57 */
exports.updateNewsById = function(req, res) {
	News.model.findById(req.params.id).exec(function(err, item) {

		if (err) return res.apiError('database error', err);
		if (!item) return res.apiError('not found');

		var data = req.body;

		item.getUpdateHandler(req).process(data, function(err) {

			if (err) return res.apiError('create error', err);

			res.apiResponse({
				News: item
			});


		});

	});
}

/**
80 * Delete News by ID
81 */
exports.deleteNewsById = function(req, res) {
	News.model.findById(req.params.id).exec(function(err, item) {

		if (err) return res.apiError('database error', err);
		if (!item) return res.apiError('not found');

		item.remove(function(err) {
			if (err) return res.apiError('database error',
				err);

			return res.apiResponse({
				success: true
			});
		});

	});
}
