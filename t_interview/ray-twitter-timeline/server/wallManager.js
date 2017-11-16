'use strict';

const app = require('./server');

const AWS = require('aws-sdk');
const credentials = new AWS.SharedIniFileCredentials({
  profile: 'ray-test'
});
AWS.config.credentials = credentials;

const sqs = new AWS.SQS({region: 'us-west-2'});
const wallManager = module.exports = {

  buildUserWall: function (userId, cb) {
    console.log('Start build userwall for userId %s', userId);
    var err, out;
    return cb(err, out);
  },
  handleNewTweets: (count, cb)=> {
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
      VisibilityTimeout: 5,
      WaitTimeSeconds: 5
    };
    sqs.receiveMessage(params, function (err, data) {
      if (err) {
        console.log(err, err.stack);
        return cb(err, data);
      } // an error occurred
      else {
        //app
        console.dir(data);           // successful response
        if (data && data.Messages && data.Messages.length) {
          data.Messages.map((m)=> {
            // m.MessageId
            // m.ReceiptHandle
            console.dir(m.MessageAttributes);
            console.dir(m.Body);
            let followedBy = [];
            if (m.MessageAttributes.followedBy && m.MessageAttributes.followedBy.StringValue) {
              followedBy = m.MessageAttributes.followedBy.StringValue.split(',');
            }
            followedBy.map((followUserId)=> {
              app.models.UserWall.get(followUserId, null, (err, followUserData)=> {
                console.dir(err);
                console.log('put new wall data for %s', followUserId);
                console.log('old data:');
                console.dir(followUserData);
                if (!followUserData) {
                  followUserData = {
                    'tweets': []
                  };
                }
                followUserData.updated = Date.now();
                if (!Array.isArray(followUserData.tweets)) {
                  followUserData.tweets = [];
                }
                followUserData.tweets.push({
                  'content':m.Body,
                  'on':Date(),
                  'from':'somebody'
                });
                console.log('new data:');
                console.dir(followUserData);
                app.models.UserWall.set(followUserId, followUserData, null, (err, out)=> {
                });
              });
            });
            let deleteparams = {
              QueueUrl: params.QueueUrl,
              ReceiptHandle: m.ReceiptHandle
            };
            sqs.deleteMessage(deleteparams, (err, data)=> {
              if (err) console.log(err, err.stack); // an error occurred
              else     console.log(data);           // successful response
            });


          });
          return cb(null, {'msg': 'done'});
        }
      }


    });


  }

};
