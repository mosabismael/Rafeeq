var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Botmessage Model
 * ==========
 */
var Botmessage = new keystone.List('Botmessage', {

	map: {
		name: 'userId'
	},
	singular: 'Botmessage',
	autokey: {
		plural: 'Botmessage',
		from: 'userId',
		path: 'key'
	},
});

Botmessage.add({
	userId: {
		type: Types.Relationship,
		ref: 'User',
		initial: true
	},
	text_1: {
		type: Types.Html,
		label: 'User message',
		wysiwyg: true,
		height: 150
	},
	text_2: {
		type: Types.Html,
		label: 'Bot message',
		wysiwyg: true,
		height: 150
	},
	image: {
		type: Types.CloudinaryImage
	},

date: {
			type: Types.Date,
			default: new Date().getTime()
	}


});



/**
 * Registration
 */
Botmessage.defaultColumns = 'userId,text_1,text_2,date';
Botmessage.register();
