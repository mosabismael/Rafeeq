var keystone = require('keystone');
var Types = keystone.Field.Types;
var fs = require('fs');
/**
 * FileUpload Model
 * ==========
 */
var myStorage = new keystone.Storage({
	adapter: keystone.Storage.Adapters.FS,
	fs: {
		path: keystone.expandPath('./public/uploads/files/'), // required; path where the files should be stored
		publicPath: '/uploads/files/', // path where files will be served
	},
	schema: {
		size: true,
		mimetype: true,
		path: true,
		originalname: true,
		url: true,
	},
});
var FileUpload = new keystone.List('FileUpload', {

		map: {
			name: 'file'
		},
	singular: 'FileUpload',
	autokey: {
		path: 'slug',
		plural: 'FileUploads',
		from: 'post',
		unique: true
	}
});

FileUpload.add({


	file: {
		type: Types.File,
		storage: myStorage,

	},
});

FileUpload.relationship({
	ref: 'Post',
	path: 'Posts',
	refPath: 'Image'
});
FileUpload.defaultColumns = 'post';
FileUpload.register();
