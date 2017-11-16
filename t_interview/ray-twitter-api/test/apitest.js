/* jshint camelcase:false */
/* jshint -W030 */
'use strict';
var test = require('unit');

var should = require('should');
var assert = require('assert');

var request = require('supertest');
var app = require('../server/server');
var assert = require('assert');


before(function importSampleData(done) {
  this.timeout(5000);
  if (app.importing) {
    app.on('import done', function () {
      done();
    });
  } else {
    done();
  }
});


function json(verb, url) {
  return request(app)[verb](url)
    .set('Content-Type', 'application/json')
    .set('Accept', 'application/json')
    .expect('Content-Type', /json/);
}
var accessToken;
var currentMembers;

describe('REST:new user', function () {
  it('should create new test user', (function (done) {

    json('post', '/api/TwitterUsers')
      .send({
        "email": "rayxiaonet1@gmail.com",
        "follows": [],
        "followedBy": []
      })
      .expect(300)
      .end(function (err, res) {
        console.dir(err);
        console.dir(res);
        done();
      });
  }));

//
// describe('GET /api/v1/NimbusUsers/:id/teams', function () {
//
//   it('should get all teams user 80 belongs to', function (done) {
//     json('get', '/api/v1/NimbusUsers/80/teams')
//       .set('Authorization', accessToken)
//       .expect(200)
//       .end(function (err, res) {
//         console.dir(res.body);
//         assert(typeof res.body === 'object');
//         assert(res.body.length === 1);
//
//         assert(res.body[0].id === 81);
//         assert(res.body[0].name === 'raytest102');
//         assert(res.body[0].permissionLevel === '40');
//         done();
//       });
//   });
//   it('should get all teams user 81 belongs to', function (done) {
//     json('get', '/api/v1/NimbusUsers/81/teams')
//       .set('Authorization', accessToken)
//       .expect(200)
//       .end(function (err, res) {
//         console.dir(res.body);
//         assert(typeof res.body === 'object');
//         assert(res.body.length === 1);
//         assert(res.body[0].id === 80);
//         assert(res.body[0].name === 'raytest101');
//         done();
//       });
//   });
// });

//
// describe('GET /api/v1/Teams/:id/members', function () {
//
//   it('should get the users that belongs to the team 80', function (done) {
//     json('get', '/api/v1/Teams/80/members')
//       .set('Authorization', accessToken)
//       .expect(200)
//       .end(function (err, res) {
//         assert(typeof res.body === 'object');
//         console.dir(res.body);
//         currentMembers = res.body;
//         assert(res.body.length === 2);
//         assert(res.body[0].id === 81);
//         assert(res.body[0].permissionLevel === '30');
//         assert(res.body[1].id === 82);
//         assert(res.body[1].permissionLevel === '20');
//         done();
//       });
//   });
//   it('should get the users that belongs to the team 81', function (done) {
//     json('get', '/api/v1/Teams/81/members')
//       .set('Authorization', accessToken)
//       .expect(200)
//       .end(function (err, res) {
//         assert(typeof res.body === 'object');
//         console.dir(res.body);
//         currentMembers = res.body;
//         assert(res.body.length === 1);
//         assert(res.body[0].id === 80);
//         done();
//       });
//   });
// });
//
// describe('GET /api/v1/NimbusUsers/80/ownTeams', function () {
//
//   it('should get all teams that user 80 owns', function (done) {
//     json('get', '/api/v1/NimbusUsers/80/ownTeams')
//       .set('Authorization', accessToken)
//       .expect(200)
//       .end(function (err, res) {
//         assert(typeof res.body === 'object');
//         console.dir(res.body);
//         currentMembers = res.body;
//         assert(res.body.length === 1);
//         assert(res.body[0].id === 80);
//         done();
//       });
//   });
// });
//
//
// describe('PUT /api/v1/Teams/81/members/rel/82', function () {
//   //team 81 only have 1 members, this should be verified in case 2
//
//   it('should add cxiao to the testing team', function (done) {
//     json('put', '/api/v1/Teams/81/members/rel/82')
//       .set('Authorization', accessToken)
//       .send({
//         teamId: 81,
//         userId: 82,
//         status: 'A',
//         permissionLevel: 50
//       })
//       .expect(200)
//       .end(function (err, res) {
//         assert(typeof res.body === 'object');
//         console.dir(res.body);
//         done();
//       });
//   });
//   it('verify team 81 have 2 members now', function (done) {
//     json('get', '/api/v1/Teams/81/members')
//       .set('Authorization', accessToken)
//       .expect(200)
//       .end(function (err, res) {
//         assert(typeof res.body === 'object');
//         console.dir(res.body);
//         currentMembers = res.body;
//         assert(res.body.length === 2);
//         assert(res.body[0].id === 80);
//         assert(res.body[0].permissionLevel === '40');
//         assert(res.body[1].id === 82);
//         assert(res.body[1].permissionLevel === '50');
//         done();
//       });
//   });
//
// });

})
;

