var keystone = require('keystone'),
async = require('async');
var User = keystone.list('User');
var Org = keystone.list('Org');

exports = module.exports = function(req, res) {
	var view = new keystone.View(req, res);
	var locals = res.locals;

	// Set locals
	locals.section = 'Org';

	locals.filters = {
		User: req.params.username,

	};

	locals.User = [];

	var view = new keystone.View(req, res),
		locals = res.locals;

	locals.section = 'createaccount';
	locals.form = req.body;

	locals.data = {
	 Orgs: [],
	 links:[],
	 pictures:[],
	 items:[]

	 };

	 view.on('init', function(next) {

	 var q = keystone.list('OrgLinks').model.find()

	 q.exec(function(err, results) {
	 locals.data.links = results;
	 next(err);
	 });
	 });
	 view.on('init', function(next) {

	 var q = keystone.list('Pictures').model.find()

	 q.exec(function(err, results) {
	 locals.data.pictures = results;
	 next(err);
	 });
	 });

	 view.on('init', function(next) {

	var q = keystone.list('ClassItem').model.find()

	q.exec(function(err, results) {
	locals.data.items = results;
	next(err);
	});
	});
	// Set locals

	view.on('post', function(next) {

		async.series([
			function(cb) {

				var OrgData = {
					userId: locals.user.id,
					name: req.body.name,
					picturesID: req.body.picture,
					description: req.body.description,
					orgLinksID:req.body.link,
					phone:req.body.phone,
					classItemId:req.body.item
				};

				var Org = keystone.list('Org').model,
					newOrg = new Org(OrgData);

				newOrg.save(function(err) {
					res.redirect('/blog');

					return cb(err);
				});

			}

		], function(err) {

			if (err) return next();
			var onSuccess = function() {
				console.log('Yup');
				res.redirect('');
			}

			var onFail = function(e) {
				req.flash('error', 'There was a problem , please try again.');
				console.log('Yup');

				return next();
			}
		});
	});
	view.query('Org', keystone.list('Org').model.find().where('fullName').sort('fullName'));

	// Load the galleries by sortOrder
	view.render('Org');

};
