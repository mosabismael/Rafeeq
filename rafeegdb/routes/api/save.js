var keystone = require('keystone'),
	Save = keystone.list('Save');

/**
5 * List Saves
6 */

exports.savePost = function(req, res) {

	var item = new Save.model(),
		data = req.body;

	item.getUpdateHandler(req).process(data, function(err) {

		if (err) return res.apiError('error', err);

		res.apiResponse({
			Save: item
		});

	});
}
