var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * News Model
 * ==========
 */
var News = new keystone.List('News', {

	map: {
		name: 'title'
	},
	singular: 'News',
	autokey: {
		plural: 'News',
		from: 'title',
		path: 'key'
	},
});
News.add({
	title: {
		type: String,
		initial: true,
		index: true
	},

date: {
			type: Date,
			default: new Date().getTime()
	},
	author: {
		type: Types.Relationship,
		ref: 'User',
		initial: true
	},
	location: {
		type: Types.Location,
		defaults: {
			country: 'sudan',
			state:'Khartoum'
		}
	},
	classies: {
		type: Types.Relationship,
		label: 'Class type',
		ref: 'ClassItem',
		index: true
	},

	content: {
		type: Types.Html,
		wysiwyg: true,
		height: 150
	},
	CommentID: {
		type: Types.Relationship,
		ref: 'Comment',
		index: true,
		initial: true,
		many: true
	},
	likesId: {
		type: Number,
		initial: true,
		index: true
	},

});
News.schema.virtual('content.full').get(function() {
	return this.content.extended || this.content.brief;
});


/**
 * Registration
 */
News.defaultColumns = 'content ,CommentID, location ,classies';
News.register();
