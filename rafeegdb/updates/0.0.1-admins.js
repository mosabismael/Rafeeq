// /**
//  * This script automatically creates a default Admin user when an
//  * empty database is used for the first time. You can use this
//  * technique to insert data into any List you have defined.
//  *
//  * Alternatively, you can export a custom function for the update:
//  * module.exports = function(done) { ... }
//  */
//
// exports.create = {
// 	User: [{
// 		'name.first': 'Admin',
// 		'name.last': 'User',
// 		'email': 'RafeegDB@gmail.com',
// 		'password': 'admin',
// 		'isAdmin': true
// 	}, ],
// };
var keystone = require('keystone');
var User = keystone.list('User');

module.exports = function (done) {
	new User.model({
		name: {
			first: 'Admin',
			last: 'User'
		},
		email: 'RafeegDB@gmail.com',
		password: 'admin',
		isAdmin: true,
		isProtected: true,
	})
	.save(done);
};
