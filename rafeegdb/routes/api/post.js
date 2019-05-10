var keystone = require('keystone'),
	Post = keystone.list('Post');
	Comment = keystone.list('Comment');
	FileUpload = keystone.list('FileUpload');
	const moment = require('moment');



/**
5 * List Posts
6 */
exports.getPosts = function(req, res) {
	Post.model.find(function(err, items) {

		if (err) return res.apiError('database error', err);
		const dateTime = moment('2018-12-14');
		console.log(dateTime.fromNow());

		res.apiResponse({
			Posts: items
		});

	}).sort('-publishedDate');
}

/**
20 * Get Post by ID
21 */
exports.getPostById = function(req, res) {
	Post.model.findById(req.params.id).exec(function(err, item) {

		if (err) return res.apiError('database error', err);
		if (!item) return res.apiError('not found');
		res.apiResponse({
			Post: item
		});

	});
}


/**
37 * Create a Post
38 */
exports.createPost = function(req, res) {
	var item = new Post.model(),

		data = req.body;

	item.getUpdateHandler(req).process(data, function(err) {

		if (err) return res.apiError('error', err);

		res.apiResponse({
			Post: item
		});

	});
}

exports.uploadImage = function(req, res) {

  var item = new FileUpload.model(),
  data = req.body;

  item.getUpdateHandler(req).process(data, function(err) {

    if (err) return res.apiError('error', err);

    res.apiResponse({
      FileUpload: item,
    });
// console.log(FileUpload);
  });
}

exports.getImage = function(req, res) {
	FileUpload.model.find(function(err, items) {

		if (err) return res.apiError('database error', err);

		res.apiResponse({
			FileUpload: items
		});

	}).sort('-publishedDate');
}
/**
56 * Update Post by ID
57 */
exports.updatePostById = function(req, res) {
	Post.model.findById(req.params.id).exec(function(err, item) {

		if (err) return res.apiError('database error', err);
		if (!item) return res.apiError('not found');

		var data = req.body;

		item.getUpdateHandler(req).process(data, function(err) {

			if (err) return res.apiError('create error', err);

			res.apiResponse({
				Post: item
			});


		});

	});
}

/**
80 * Delete Post by ID
81 */
exports.deletePostById = function(req, res) {
	Post.model.findById(req.params.id).exec(function(err, item) {

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

exports.creatComments = function(req, res) {

	var item = new Comment.model(),
		data = req.body;

	item.getUpdateHandler(req).process(data, function(err) {

		if (err) return res.apiError('error', err);

		res.apiResponse({
			Comment: item
		});

	});
}
exports.getComments = function(req, res) {
	Comment.model.find(function(err, items) {

		if (err) return res.apiError('database error', err);

		res.apiResponse({
			Comment: items
		});

	}).sort('-publishedDate');
}


exports.getCount = function(req, res) {
	Comment.model.count().find(function(err, items) {

		if (err) return res.apiError('database error', err);

		res.apiResponse({
			Comment: items.length
		});

	}).sort('-publishedDate');
}
