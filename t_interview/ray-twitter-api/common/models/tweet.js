'use strict';
const queueManager = require('../../server/queueManager');


module.exports = function(Tweet) {

  Tweet.observe('after save',  (ctx, next)=> {
    console.log('Post new tweet event ');
    if (ctx.instance) {
      console.log('New tweet content is %s#%s', ctx.Model.modelName, ctx.instance.id);
      Tweet.findById(ctx.instance.id,(err,out)=>{

        if (err) {
          console.err(err);
        }else{
          Tweet.app.models.TwitterUser.findById(out.userId,(err,twitUser)=>{
            if(err){

              console.err('twitter user not found %s'+userId);
            }else {
              console.dir(twitUser);
              //call the timeline service to rebuild the userwall
              queueManager.newTweet(ctx.instance.id,out.userId,twitUser.followedBy,out.message,(err,out)=>{});
              //call notification service to push notifications
            }


          })

        }

      });
    } else {
      //skip this path, it's for massive updates
      console.log('Updated %s matching %j',
        ctx.Model.pluralModelName,
        ctx.where);
    }
    next();
  });

};
