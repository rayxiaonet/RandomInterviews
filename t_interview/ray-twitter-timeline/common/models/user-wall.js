'use strict';

const wallManager = require('../../server/wallManager');
const loopback = require('loopback');

module.exports = function(userWall) {
  userWall.rebuild = function(id, cb) {
    console.log('override the delete with invalidate user wall action');

    userWall.expire( id, 1, null, (err,out)=>{
      wallManager.buildUserWall(id);
      return cb(null,{msg:'success'});
    });

  };

  //   .rebuild = function(userId,cb) {
  //   console.log('Start rebuild wall for userId %s',userId);
  //   wallManager.submitRebuildRequest(userId,cb);
  // };

  userWall.remoteMethod(
    'rebuild', {
      http: {
        path: '/:userId',
        verb: 'delete'
      },
      accepts: {arg: 'userId', type: 'string'},

      returns: {
        arg: 'status',
        type: 'string'
      }
    }
  );
};
