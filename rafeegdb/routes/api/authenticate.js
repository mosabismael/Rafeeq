'use strict';

var keystone = require('keystone');
const bcrypt = require('bcryptjs');
var user = keystone.list('Normal');

exports.login = (email, password) =>

	new Promise((resolve, reject) => {
		user.model.find({
				email: email
			})
			.then((normals) => {

				if (normals.length == 0) {

					reject({
						status: 404,
						message: 'User Not Found !'
					});

				} else {

					return normals[0];

				}
			})

			.then((user) => {

				const hashed_password = user.password;

				if (bcrypt.compareSync(password, hashed_password)) {

					resolve({
						status: 200,
						message: email
					});


				} else {

					reject({
						status: 401,
						message: 'Invalid Credentials !'
					});

				}
			}).catch(err => reject({
				status: 500,
				message: 'Internal Server Error !'
			}));
	});
