var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Botprofile Model
 * ==========
 */
var Botprofile = new keystone.List('Botprofile');

Botprofile.add({
	name: {
		type: String,
		label: 'Avtar name',
		required: true,
		index: true
	},
	description: {
		type: Types.Textarea,
		initial: true,
		toolbarOptions: {
			hiddenButtons: 'H1,H6,Code'
		}
	},
	location: {
		type: Types.Location,
		defaults: {
			country: 'sudan'
		}
	},
	country: {
		type: Types.Select,
		initial: true,
		options: 'Khartoum, kordfan,Alneel Alabyad,Bortsudan,Halfa,Dongola,Atbara',
		default: 'Khartoum',
		index: true
	},
	classID: {
		type: Types.Relationship,
		ref: 'Classes',
		initial: true
	},
	avatar: {
		type: Types.CloudinaryImages,
		initial: true,
		collapse: true
	},

});




/**
 * Relationships
 */
//Botprofile.relationship({ ref: 'Post', path: 'posts', refPath: 'author' });


/**
 * Registration
 */
Botprofile.defaultColumns = 'name, email, isAdmin';
Botprofile.register();
