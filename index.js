'use-strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/subjects/{subjectName}/questionids/{questionId}')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const subjectName = context.params.subjectName;
      const questionId = context.params.questionId;
      //console.log(subjectName+" | "+questionId);


       const payload = {notification: {
           title: 'New question',
           body: 'New question has been added to '+ subjectName +' section'
           ,
           click_action : "app.moallem.TARGETNOTIFcation",
           sound: "default",
           icon : "default"
         },
         data :{
           subjectkey : subjectName,
           questionid : questionId,
           storageDataID : snapshot.child('storageDataID').val(),
           gotoSession : "true"
          }

       };

    return admin.messaging().sendToTopic(subjectName,payload)
      .then(function(response){
           return console.log('Notification sent successfully:',response);
      })
      .catch(function(error){
           return console.log('Notification sent failed:',error);
      });



    });



    exports.sendNotificationToStudentOnAccept = functions.database.ref('/subjects/{subjectName}/questionids/{questionId}')
        .onWrite((snapshot, context) => {

          const isReplyedBefore = snapshot.before.child("isReplyed").val();
          const isReplyed = snapshot.after.child("isReplyed").val();
          const userId = snapshot.after.child("studentId").val();


          const payloadMoallem = {notification: {
              title: 'Moallem App',
              body: 'Your question has been accepted',
              click_action : "com.moallem.stu.studentNotifaication",
              sound: "default",
              icon : "default"
            },

            data :{
               teacherPic : snapshot.after.child("teacherPic").val(),
               teachername : snapshot.after.child("teacherName").val(),
               teacherId : snapshot.after.child("teacherId").val(),
               questionType : snapshot.after.child("questionType").val(),
               nodeKey : String(snapshot.after.child("nodeKey").val()),
               isFinished : String(snapshot.after.child("isFinished").val()),
               isStudentReachedZeroMins : String(snapshot.after.child("isStudentReachedZeroMins").val()),
               storageDataID : snapshot.after.child("storageDataID").val(),
             }

          };


            return admin.database().ref('usersinfo/'+userId)
            .once('value')
              .then(snap1 => {
          const token = snap1.child("tokenId").val();
          console.log(token);

          if (!isReplyedBefore && isReplyed) {
            //console.log("Notification  sent");
            return admin.messaging().sendToTopic(token, payloadMoallem).then(function(response){
                 return console.log('Notification sent successfully:',response);
            })
            .catch(function(error){
                 return console.log('Notification  failed:',error);
            });


        }
          else {
              console.log("Notification NOT sent ");
              return false;

          }

        })
        .catch(error => {
          console.log('Error sending message:', error);
          return false;
        });

        //var token = snap.child("tokenId").val();


/*
           const payload = {notification: {
               title: 'New question',
               body: 'New question has been added to '+ subjectName +' section'
               ,
               click_action : "app.moallem.TARGETNOTIFcation",
               sound: "default",
               icon : "default"
             },
             data :{
               subjectkey : subjectName,
               questionid : questionId,
               storageDataID : snapshot.child('storageDataID').val(),
               gotoSession : "true"
              }

           };


        return admin.messaging().sendToTopic(subjectName,payload)
          .then(function(response){
               return console.log('Notification sent successfully:',response);
          })
          .catch(function(error){
               return console.log('Notification sent failed:',error);
          });

*/


        });
