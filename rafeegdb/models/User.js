var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * User Model
 * ==========
 */
var User = new keystone.List('User', {
	map: {
		name: 'username'
	},
	singular: 'Users',
	autokey: {
		plural: 'Users',
		from: 'username',
		path: 'key'
	},
});
User.add({
	username: {
		type: String,
		label: 'User name',
		initial: true,
		filters: {
			isAdmin: true
		},
		index: true
	},
	email: {
		type: Types.Email,
		initial: true,
		required: true,
		unique: true,
		index: true
	},
	password: {
		type: Types.Password,
		initial: true,
		required: true
	},
	type: {
		type: Types.Relationship,
		ref: 'Type',
		initial: true
	},
}, 'Permissions', {
	isAdmin: {
		type: Boolean,
		label: 'Can access Keystone',
		index: true,
		default: true
	},
});

// Provide access to Keystone
User.schema.virtual('canAccessKeystone').get(function() {
	return this.isAdmin;
});


/**
 * Relationships
 */
User.relationship({
	ref: 'Post',
	path: 'posts',
	refPath: 'author'
});

User.relationship({
	ref: 'Normal',
	path: 'Normal',
	refPath: 'userId'
});

User.relationship({
	ref: 'Org',
	path: 'Org',
	refPath: 'userId'
});

User.relationship({
	ref: 'News',
	path: 'News',
	refPath: 'author'
});

User.relationship({
	ref: 'Comment',
	path: 'Comment',
	refPath: 'author'
});

User.relationship({
	ref: 'Botmessage',
	path: 'botmessage',
	refPath: 'userId'
});

/**
 * Registration
 */
User.defaultColumns = 'username, email, isAdmin';
User.register();
