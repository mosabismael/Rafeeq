var keystone = require('keystone');

 exports = module.exports = function(req, res) {
 var view = new keystone.View(req, res),
 locals = res.locals;

 // Set locals
 locals.filters = {
 keywords: req.query.keywords
 };
 locals.data = {
 posts: [],
 keywords: "",
 };

 // Load the current product
 view.on('init', function(next) {
console.log('search keywords=' + locals.filters.keywords);
locals.data.keywords = locals.filters.keywords;

 //search the full-text index
 keystone.list('Post').model.find(
 { $text : { $search : locals.filters.keywords } },
 { score : { $meta: "textScore" } }).sort({ score : { $meta : 'textScore' } }).limit(20).exec(function(error, results) {
 if(error) console.log(error);

 locals.data.posts = results
 next();
 });
 });

 // Render the view
 view.render('search');

 };
