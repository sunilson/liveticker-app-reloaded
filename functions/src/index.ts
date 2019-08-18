import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
import * as short from 'shorthash'
import * as uuid from 'uuid/v4'

admin.initializeApp()

export const addShareLink = functions.firestore.document("livetickers/{livetickerId}").onCreate((change, context) => {
    return Promise.all([
        change.ref.update({ "sharingUrl": short.unique(change.ref.id) }),
        admin.firestore().collection("editUrls").doc(change.ref.id).create({ "editUrl": uuid(), "authorId": change.data()["authorId"] })
    ])
})