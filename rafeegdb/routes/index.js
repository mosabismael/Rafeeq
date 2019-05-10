/**
 * This file is where you define your application routes and controllers.
 *
 * Start by including the middleware you want to run for every request;
 * you can attach middleware to the pre('routes') and pre('render') events.
 *
 * For simplicity, the default setup for route controllers is for each to be
 * in its own file, and we import all the files in the /routes/views directory.
 *
 * Each of these files is a route controller, and is responsible for all the
 * processing that needs to happen for the route (e.g. loading data, handling
 * form submissions, rendering the view template, etc).
 *
 * Bind each route pattern your application should respond to in the function
 * that is exported from this module, following the examples below.
 *
 * See the Express application routing documentation for more information:
 * http://expressjs.com/api.html#app.VERB
 */

var keystone = require('keystone');
var middleware = require('./middleware');
var logger = require('morgan');
var auth = require('basic-auth');
var jwt = require('jsonwebtoken');

var importRoutes = keystone.importer(__dirname);
// Common Middleware
keystone.pre('routes', middleware.initLocals);
keystone.pre('render', middleware.flashMessages);

// Import Route Controllers
var routes = {
	views: importRoutes('./views'),
	api: importRoutes('./api')
};

// Setup Route Bindings
exports = module.exports = function(app) {

	app.use(logger('dev'));
	// app.use(bodyParser.json());

	// app.use('/api/v1', router);

	app.get('/', routes.views.index);
	app.all('/blog/:classis?', routes.views.blog);
	app.all('/news/:classis?', routes.views.news);
	app.all('/class/:classis?', routes.views.Org.Classes);
	app.get('/post/:category ?', routes.views.post);
	app.all('/contact', routes.views.contact);
	app.all('/User', routes.views.User);
	app.all('/Org/org', routes.views.User);
	app.all('/Org', routes.views.Org.orgProfile);
	app.all('/signin', routes.views.signin);
	app.all('/normal', routes.views.Normal.normalProfile);

	app.get('/api/classes', keystone.middleware.api, routes.api.classes.getClass);
	app.get('/api/classItem', keystone.middleware.api, routes.api.classitem.getClassItems);


  app.post('/api/comments', keystone.middleware.api, routes.api.post.creatComments);
  app.get('/api/comment', keystone.middleware.api, routes.api.post.getComments);
  app.get('/api/count', keystone.middleware.api, routes.api.post.getCount);


	// API post
	app.get('/api/posts', keystone.middleware.api, routes.api.post.getPosts);
	app.get('/api/posts/:id', keystone.middleware.api, routes.api.post.getPostById);
	app.post('/api/posts', keystone.middleware.api, routes.api.post.createPost);
	app.delete('/api/posts/:id', keystone.middleware.api, routes.api.post.deletePostById);
	app.post('/api/fileuploads', keystone.middleware.api, routes.api.post.uploadImage);
  	app.get('/api/fileuploads/', keystone.middleware.api, routes.api.post.getImage);

	app.post('/api/saves', keystone.middleware.api, routes.api.save.savePost);

	// API news
	app.get('/api/News', keystone.middleware.api, routes.api.news.getNewses);
	app.get('/api/News/:id', keystone.middleware.api, routes.api.news.getNewsById);
	app.post('/api/News', keystone.middleware.api, routes.api.news.createNews);
	app.delete('/api/News/:id', keystone.middleware.api, routes.api.news.deleteNewsById);

	// API User
	app.get('/api/normals/', keystone.middleware.api, routes.api.normals.getNormals);
	app.post('/api/normals', keystone.middleware.api, routes.api.normals.createNormals);
	app.delete('/api/normals/:id', keystone.middleware.api, routes.api.normals.deleteNormalsById);
	app.post('/api/authenticate', (req, res, next) => {

		const credentials = auth(req);

		if (!credentials) {

			res.status(400).json({
				message: 'Invalid Request !'
			});

		} else {
			routes.api.authenticate.login(credentials.name, credentials.pass)
				// login.loginUser(credentials.name, credentials.pass)

				.then(result => {

					// const token = jwt.sign(result, config.config.secret, {
					// 	expiresIn: 1440
					// });

					res.status(result.status).json({
						message: result.message,
						// token: token
					});

				})

				.catch(err => res.status(err.status).json({
					message: err.message
				}));
		}
	});
	app.get('/api/normals/:id', (req, res) => {

			routes.api.normals.getProfile(req.params.id)

				.then(result => res.json(result))

				.catch(err => res.status(err.status).json({
					message: err.message
				}));


	});
};
