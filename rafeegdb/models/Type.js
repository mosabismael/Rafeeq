var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Type Model
 * ==========
 */
var Type = new keystone.List('Type');

Type.add({
	name: {
		type: String,
		required: true,
		index: true
	},
});

// Provide access to Keystone


/**
 * Relationships
 */
Type.relationship({
	ref: 'User',
	path: 'type',
	refPath: 'type'
});


/**
 * Registration
 */
Type.defaultColumns = 'name';
Type.register();
