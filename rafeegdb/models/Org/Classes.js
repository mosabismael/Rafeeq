var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Classes Model
 * ==========
 */
 var myStorage = new keystone.Storage({
 	adapter: keystone.Storage.Adapters.FS,
 	fs: {
 		path: keystone.expandPath('./public/uploads/files'), // required; path where the files should be stored
 		publicPath: '/public/uploads/files', // path where files will be served
 	},
 	schema: {
 		size: true,
 		mimetype: true,
 		path: true,
 		originalname: true,
 		url: true,
 	},
 });
var Classes = new keystone.List('Classes', {

	autokey: {
		from: 'name',
		path: 'key',
		unique: true
	},
});

Classes.add({
	title: {
		type: String,
		label: 'Name',
		initial: true,
		index: true
	},

	Thumbnail: {
		type: Types.File,
		storage: myStorage,

	},

});
Classes.relationship({
	ref: 'ClassItem',
	path: 'ClassItem',
	refPath: 'classID'
});

Classes.relationship({
	ref: 'Botprofile',
	path: 'Botprofile',
	refPath: 'classID'
});



Classes.defaultColumns = 'name';

Classes.register();
