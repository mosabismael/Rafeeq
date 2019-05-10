var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Pictures Model
 * ==========
 */
var Pictures = new keystone.List('Pictures', {

	map: {
		name: 'title'
	},
	singular: 'Pictures',
	autokey: {
		plural: 'Pictures',
		from: 'title',
		path: 'key'
	},
});

Pictures.add({
	title: {
		type: String,
		initial: true,
		label: 'Title',
		default:'Title'
		},
	name: {
		type: Types.CloudinaryImages,
		initial: true,
		label: 'pics',
		collapse: true
	},



});

Pictures.relationship({
	ref: 'Org',
	path: 'Org',
	refPath: 'picturesID'
});


/**
 * Registration
 */
Pictures.defaultColumns = 'name';
Pictures.register();
