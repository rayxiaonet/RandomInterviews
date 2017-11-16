'use strict';

const AWS = require('aws-sdk');
const credentials = new AWS.SharedIniFileCredentials({
  profile: 'ray-test'
});
AWS.config.credentials = credentials;

const sqs = new AWS.SQS({region:'us-west-2'});
const wallManager = module.exports =  {

  buildUserWall: function(userId,cb) {
    console.log('Start build userwall for userId %s',userId);
    var err,out;
    return cb(err,out);
  },
  handleNewTweets: (cb)=> {
    //receive message to amazone sqs
    //https://sqs.us-west-2.amazonaws.com/135853609931/ray-sample-queue

    let params = {
      AttributeNames: [
        'All'
      ],
      MaxNumberOfMessages: 10,
      MessageAttributeNames: [
        'All'
      ],
      QueueUrl: 'https://sqs.us-west-2.amazonaws.com/135853609931/ray-sample-queue',
      VisibilityTimeout: 123,
      WaitTimeSeconds: 123
    };
    sqs.receiveMessage(params, function(err, data) {
      if (err) console.log(err, err.stack); // an error occurred
      else     console.log(data);           // successful response
    });
  }

};
