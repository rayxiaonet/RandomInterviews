'use strict';
const wallManager = require('../../server/wallManager');

module.exports = function (Message) {
  Message.reload = function (count, cb) {

    wallManager.handleNewTweets(count, (err, out)=> {
      console.log('finish read message from SQS');
      return cb(err,out);
    });

  };


};
