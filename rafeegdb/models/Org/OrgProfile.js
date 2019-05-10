var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Org Model
 * ==========
 */
var Org = new keystone.List('Org', {

	map: {
		name: 'name'
	},
	singular: 'Org',
	label: 'Org',
	autokey: {
		plural: 'Org',
		from: 'name',
		path: 'key',
		unique: true
	},
});
Org.add({
	userId: {
		type: Types.Relationship,
		ref: 'User',
		initial: true
	},
	name: {
		type: String,
		required: true,
		initial: true,
		label: 'Name',
		index: true
	},
	picturesID: {
		type: Types.Relationship,
		ref: 'Pictures',
		initial: true,
		many: true,
		default:'null'
	},
	location: {
		type: Types.Location,
		defaults: {
			country: 'sudan',
			state:'kartoum'
		}
	},
	description: {
		type: Types.Textarea,
		initial: true,
		toolbarOptions: {
			hiddenButtons: 'H1,H6,Code'
		}
	},
	orgLinksID: {
		type: Types.Relationship,
		ref: 'OrgLinks',
		initial: true,
		default:'null'

	},
	phone: {
		type: Types.Number,
		label: 'Phone number',
		required: true,
		initial: true,
		index: true,
		unique: true
	},
	classItemId: {
		type: Types.Relationship,
		ref: 'ClassItem',
		initial: true,
		many: true,
		default:'null'

	},

});




/**
 * Relationships
 */
Org.relationship({
	ref: 'Post',
	path: 'posts',
	refPath: 'author'
});


/**
 * Registration
 */
Org.defaultColumns = 'name';
Org.register();
