var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * ClassItem Model
 * ==========
 */
var ClassItem = new keystone.List('ClassItem',{
autokey: {
	from: 'name',
	path: 'key',
	unique: true
},
});
ClassItem.add({

	classID: {
		type: Types.Relationship,
		ref: 'Classes',
		initial: true
	},
	name: {
		type: String,
		required: true,
		index: true
	},
	pics: {
		type: Types.CloudinaryImages,
		initial: true,
		required: true,
		collapse: true
	},
	description: {
		type: Types.Textarea,
		initial: true,
		toolbarOptions: {
			hiddenButtons: 'H1,H6,Code'
		}
	},

});
ClassItem.relationship({
	ref: 'Post',
	path: 'Post',
	refPath: 'classies'
});
ClassItem.relationship({
	ref: 'News',
	path: 'News',
	refPath: 'classies'
});
ClassItem.relationship({
	ref: 'Org',
	path: 'Org',
	refPath: 'classItemId'
});


/**
 * Registration
 */
ClassItem.defaultColumns = 'name, pics,description';
ClassItem.register();
