'use strict';

const AWS = require('aws-sdk');
const credentials = new AWS.SharedIniFileCredentials({
  profile: 'ray-test'
});
AWS.config.credentials = credentials;

const sqs = new AWS.SQS({region:'us-west-2'});

const queueManager = module.exports = {
  newTweet: (tweetId, createUserId,followedBy,messageContent,cb)=> {
    //send message to amazone sqs
    //https://sqs.us-west-2.amazonaws.com/135853609931/ray-sample-queue
    console.log('send message to SQS');
    let params = {
      DelaySeconds: 1,
      MessageAttributes: {
        'Type': {
          DataType: 'String',
          StringValue: 'NewTweet'
        },
        'CreateUserId': {
          DataType: 'String',
          StringValue: createUserId
        },
        'followedBy':{
          DataType:'String',
          StringValue: followedBy.join(',')
        }
      },
      MessageBody: messageContent,
      QueueUrl: 'https://sqs.us-west-2.amazonaws.com/135853609931/ray-sample-queue'
    };
    sqs.sendMessage(params, (err, data)=> {
      console.log('send message to SQS done with err'+err);
      if (err) console.log(err, err.stack); // an error occurred
      else     console.log(data);           // successful response
      if (cb) cb(err,data);
    });
  }
};
