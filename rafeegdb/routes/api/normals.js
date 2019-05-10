var keystone = require('keystone'),
	Normal = keystone.list('Normal');

/**
5 * List Normals
6 */
exports.getNormals = function(req, res) {
	Normal.model.find(function(err, items) {

		if (err) return res.apiError('database error', err);

		res.apiResponse({
			Normals: items
		});

	});
}

/**
20 * Get Normal by Email
21 */

exports.getProfile = email =>

	new Promise((resolve, reject) => {

		Normal.model.find({
				email: email
			}, {
				fullName: 1,
				email: 1,
				phone: 1,
				bio: 1,
				_id: 1
			})

			.then(users => resolve(users[0]))

			.catch(err => reject({
				status: 500,
				message: 'Internal Server Error !'
			}))

	});
/**
37 * Create a Normal
38 */
exports.createNormals = function(req, res) {

	var item = new Normal.model(),
		data = req.body;

	item.getUpdateHandler(req).process(data, function(err) {

		if (err) return res.apiError('error', err);

		res.apiResponse({
			Normal: item
		});

	});
}

/**
56 * Update Normal by ID
57 */
exports.updateNormalById = function(req, res) {
	Normal.model.findById(req.params.id).exec(function(err, item) {

		if (err) return res.apiError('database error', err);
		if (!item) return res.apiError('not found');

		var data = req.body;

		item.getUpdateHandler(req).process(data, function(err) {

			if (err) return res.apiError('create error', err);

			res.apiResponse({
				Normal: item
			});


		});

	});
}

/**
80 * Delete Normal by ID
81 */
exports.deleteNormalsById = function(req, res) {
	Normal.model.findById(req.params.id).exec(function(err, item) {

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
