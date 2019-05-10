var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
	Posts
	=====
 */

var Comment = new keystone.List('Comment', {
	label: 'Comments',
	map: {
		name: 'content'
	},

	autokey: {
		path: 'slug',
		from: 'content',
		unique: true
	},
});

Comment.add({
	author: {
		type: Types.Relationship,
		initial: true,
		ref: 'User',
		index: true
	},

	commentState: {
		type: Types.Select,
		options: ['published', 'draft', 'archived'],
		default: 'published',
		index: true
	},
	publishedOn: {
		type: Date,
		default: new Date().getTime(),
		noedit: true,
		index: true
	},

	author: {
		type: Types.Relationship,
		ref: 'Normal',
		index: true,
	},
});

Comment.add('Content', {
	content: {
		type: Types.Text,
		wysiwyg: true,
		height: 300
	},
	account: {
		type: String,
		index: true,
	},
});

Comment.relationship({
	ref: 'Post',
	path: 'Posts',
	refPath: 'comment'
});
Comment.schema.pre('save', function(next) {
	this.wasNew = this.isNew;
	if (!this.isModified('publishedOn') && this.isModified('commentState') && this.commentState === 'published') {
		this.publishedOn = new Date();
	}
	next();
});

Comment.schema.post('save', function() {
	if (!this.wasNew) return;
	if (this.author) {
		keystone.list('User').model.findById(this.author).exec(function(err, user) {
			if (user) {
				user.wasActive().save();
			}
		});
	}
});

Comment.track = true;
Comment.defaultColumns = 'content, author, post, publishedOn, commentState';
Comment.register();
