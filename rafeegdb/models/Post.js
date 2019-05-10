var keystone = require('keystone');
var Types = keystone.Field.Types;
var fs = require('fs');
/**
 * Post Model
 * ==========
 */

var Post = new keystone.List('Post', {

	filters: {
		isAdmin: true
	},
	singular: 'Post',
	autokey: {
		path: 'slug',
		plural: 'Posts',
		from: 'title',
		unique: true
	}
});

Post.add({

	state: {
		type: Types.Select,
		options: 'draft, published, archived',
		default: 'published',
		index: true
	},
	date: {
		type: String,
		label: 'Date',
		index: true

	},
	account: {
		type: String,
		index: true,
	},
	author: {
		type: Types.Relationship,
		ref: 'Normal',
		index: true,
	},
	Image: {
		type: Types.Relationship,
		ref: 'FileUpload',
		many: true
	},
	location: {
		type: String,

	},
  classTitle: {
    type: String,

  },
	classies: {
		type: Types.Relationship,
		label: 'Class type',
		ref: 'ClassItem',
		index: true
	},
	publishedDate: {
		type: Types.Date,
		index: true,
		dependsOn: {
			state: 'published'
		}
	},

		title: {
		type: Types.Text,
		wysiwyg: true,
		height: 150
	},

	likesId: {
		type: Number,
		initial: true,
		index: true
	},
	comment: {
		type: Types.Relationship,
		ref: 'Comment',
		many: true
	},
	categories: {
		type: Types.Relationship,
		ref: 'PostCategory',
		many: true
	},


});


Post.relationship({
	ref: 'Save',
	path: 'Saves',
	refPath: 'post'
});
Post.schema.virtual('content.full').get(function() {
	return this.content.extended || this.content.brief;
});

Post.defaultColumns = 'content, state, author';
Post.register();
