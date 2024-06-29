# Notebridge Java Web Application

## Notebridge is a web application that allows musicians to connect with each other. It is a social platform where musicians can share their events, ideas and chat with each other.

**Trello**

- https://trello.com/b/wOJEnjiq/note-bridge-1
- https://trello.com/invite/b/wOJEnjiq/ATTI86b8a11560499279f55389802e1d8852EF9423E7/note-bridge-1

## Contributors

Rebah Ozkoc
Anil Sen
Ilgin Simay Ozcan
Enescu Alexandru-Cristian
Simeon Nikolov
Adham Ehab Magdy Selim

## Deployed application

Previder link:
https://notebridge1.paas.hosted-by-previder.com/notebridge/
***
## To test the application on local, follow the steps below:
in src/main/resources/ create a file called app.properties and add the following lines:

- USER=dab_di23242b_134
- PASSWORD=DZk0UGLbRstkxNwW
- PROD_URL=jdbc:postgresql://bronto.ewi.utwente.nl:5432/dab_di23242b_134?currentSchema=notebridge
- TEST_URL=jdbc:postgresql://bronto.ewi.utwente.nl:5432/dab_di23242b_134?currentSchema=notebridgetest
- PERSISTENCE_FOLDER_PATH =[choose a path for the folder where the images will be stored]
```

## Database

- Database name: dab_di23242b_134
- Deployed app uses the schema: notebridge 

### Posts

The Posts feature allows users to share their musical journey, from jam sessions to selling instruments, and engage with
the community. Users can interact with these posts by commenting, liking, and sharing, creating a vibrant and dynamic
music network.

Main Features:

- Create and Share Posts: Users can create posts to share various types of content, such as:
    - Jam Sessions: Share live or recorded jam sessions to showcase your musical talent.
    - Music Events: Announce upcoming gigs, concerts, or music festivals.
    - Music Discussion: Offer discussion places on ranging topics.
    - Find Musicians: Connect with other musicians to form bands or collaborate on projects.
    - Sell Instruments: Advertise musical instruments for sale or trade.
- Engage with Posts: Users can interact with posts by:

    - Liking: Show appreciation for the content by liking the post.
    - Commenting: Leave comments to start discussions or provide feedback.
    - Sharing: Share posts with other users or on social media platforms to reach a broader audience.
    - Show Interest: Users can press the "I'm Interested" button to indicate their interest for a specific post.
- Sponsor Posts: Sponsors can promote posts to increase their visibility. This feature helps musicians reach a larger
  audience and gain more recognition for their work.

### Credentials for testing:

- Person User


- Sponsor User

A Person or Sponsor account can be created through the Sign up form.

### Messenger

The Messenger requires users to be logged-in into their accounts. After log-in it will show up in the navigation bar.

The main features are:

- Send Messages.
- Add new Contacts.
- Share your Events to your contacts.
- Automatic update of received messages.
- For chats which aren't the selected chat you can see the number of received messages which haven't been seen yet.

To check everything we need 3 users and one post.

#### Credentials:

First User

- Username: messenger1
- Password: Simplepass1234!

Second User

- Username: messenger2
- Password: Simplepass1234!

Third User

- Username: messenger3
- Password: Simplepass1234!

## Designed by UT students.


