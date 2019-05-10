var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Save Model
 * ==========
 */
var Save = new keystone.List('Save');
Save.add({
	author: {
		type: Types.Relationship,
		ref: 'Normal',
		index: true,
	},
	post: {
		type: Types.Relationship,
		ref: 'Post',
    many: false
	},


});


Save.defaultColumns = 'author,post';
Save.register();
