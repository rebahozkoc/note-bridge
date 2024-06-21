CREATE TABLE BaseUser (
    id SERIAL PRIMARY KEY,
    createDate TIMESTAMP NOT NULL,
    lastUpdate TIMESTAMP,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    picture VARCHAR(255),
    phoneNumber VARCHAR(15),
    password VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE Person(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    lastname VARCHAR(255),
    FOREIGN KEY(id) REFERENCES BaseUser(id)
);

CREATE TABLE Sponsor (
    id SERIAL PRIMARY KEY,
    companyName VARCHAR(255),
    websiteURL VARCHAR(255),
    FOREIGN KEY(id) REFERENCES BaseUser(id)
);

CREATE TABLE Instrument (
    name VARCHAR(255) PRIMARY KEY

);

CREATE TABLE PersonInstrument (
    personId INT REFERENCES Person(id),
    instrumentName VARCHAR(255) REFERENCES Instrument(name),
    yearsOfExperience FLOAT,
    PRIMARY KEY (personId, instrumentName)
);

CREATE TABLE EventType (
    name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE Post (
    id SERIAL PRIMARY KEY,
    createDate TIMESTAMP NOT NULL,
    lastUpdate TIMESTAMP,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    sponsoredBy INT REFERENCES Sponsor(id),
    sponsoredFrom TIMESTAMP,
    sponsoredUntil TIMESTAMP,
    eventType VARCHAR(255) REFERENCES EventType(name),
    personId INT NOT NULL,
    FOREIGN KEY(personId) REFERENCES Person(id) ON DELETE CASCADE
);

CREATE TABLE Picture (
    pictureURL VARCHAR(255) PRIMARY KEY,
    postId INT NOT NULL,
    FOREIGN KEY(postId) REFERENCES Post(id) ON DELETE CASCADE
);


CREATE TABLE Comment (
    id SERIAL PRIMARY KEY,
    createDate TIMESTAMP NOT NULL,
    lastUpdate TIMESTAMP,
    content TEXT,
    postId INT NOT NULL,
    personId INT NOT NULL,
    FOREIGN KEY(postId) REFERENCES Post(id) ON DELETE CASCADE,
    FOREIGN KEY (personId) REFERENCES Person(id) ON DELETE SET NULL
);


CREATE TABLE PersonLikesPost (
    personId INT REFERENCES Person(id),
    postId INT REFERENCES Post(id),
    PRIMARY KEY (personId, postId)
);


CREATE TABLE PrivateMessage (
    id SERIAL PRIMARY KEY,
    content TEXT,
    createDate TIMESTAMP NOT NULL,
    senderId INT NOT NULL,
    receiverId INT NOT NULL,
    FOREIGN KEY(senderId) REFERENCES BaseUser(id),
    FOREIGN KEY (receiverId) REFERENCES BaseUser(id)
);


CREATE TABLE Feedback (
    id SERIAL PRIMARY KEY,
    createDate TIMESTAMP NOT NULL,
    lastUpdate TIMESTAMP,
    email VARCHAR(255) NOT NULL,
    message TEXT NOT NULL
);