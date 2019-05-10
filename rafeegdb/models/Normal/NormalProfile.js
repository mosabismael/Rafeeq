var keystone = require('keystone');
var Types = keystone.Field.Types;
const bcrypt = require('bcryptjs');


/**
 * Normal Model
 * ==========
 */
var Normal = new keystone.List('Normal', {

	singular: 'Profile',
	autokey: {
		path: 'slug',
		plural: 'Profiles',
		from: 'phone',
		unique: true
	}

});

Normal.add({
	fullName: {
		type: String,
		label: 'Full Name',
		initial: true,
		required: true,
		index: true
	},
	password: {
		type: String,
		initial: true
		},
	userId: {
		type: Types.Relationship,
		label: 'Normal',
		ref: 'User',
		index: true,
		initial: true
	},
	phone: {
		type: Types.Number,
		label: 'Phone number',
		initial: true,
		index: true,
		unique: true
	},
	bio: {
		type: Types.Textarea,
		initial: true,
		toolbarOptions: {
			hiddenButtons: 'H1,H6,Code'
		}
	},
	location: {
		type: Types.Location,
		defaults: {
			country: 'sudan',
			state:'khartoum',
		}
	},
	email: {
		type: Types.Email,
		initial: true,
		required: true,
		unique: true,
		index: true
	},
	// Geo:{ type: Types.GeoPoint},
	gender: {
		type: Types.Select,
		options: [{
				value: 'male',
				label: 'Male'
			},
			{
				value: 'female',
				label: 'Female'
			},
		]
	},
	avatar: {
		type: Types.CloudinaryImages,
		initial: true,
		collapse: true
	}

});


Normal.relationship({
	ref: 'Post',
	path: 'posts',
	refPath: 'author'
});

Normal.relationship({
	ref: 'Save',
	path: 'Saves',
	refPath: 'author'
});
Normal.relationship({
	ref: 'FileUpload',
	path: 'FileUploads',
	refPath: 'author'
});
Normal.schema.pre('save', function (next) {
	const salt = bcrypt.genSaltSync(10);
	const hash = bcrypt.hashSync(this.password, salt);
    this.password = hash;

  return next();
});
/**
 * Registration
 */
Normal.defaultColumns = 'fullName, phone';
Normal.register();
