var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * OrgLinks Model
 * ==========
 */
var OrgLinks = new keystone.List('OrgLinks', {

	map: {
		name: 'Facebook'
	},
	singular: 'Links',
	label: 'Links',
	autokey: {
		plural: 'OrgLinks',
		from: 'Facebook',
		path: 'key',
		unique: true
	},
});
OrgLinks.add({

	Facebook: {
		type: String,
		initial: true,
		label: "Facebook",
		index: true,
		unique: true
	},
	linkedin: {
		type: String,
		initial: true,
		label: "linkedin",
		index: true,
		unique: true
	},
	instagram: {
		type: String,
		initial: true,
		label: "instagram",
		index: true,
		unique: true
	},
	twitter: {
		type: String,
		initial: true,
		label: "twitter",
		index: true,
		unique: true
	},
});

OrgLinks.relationship({
	ref: 'Org',
	path: 'Org',
	refPath: 'orgLinksID'
});



/**
 * Registration
 */
OrgLinks.defaultColumns = 'Facebook,linkedin,instagram,twitter';
OrgLinks.register();
