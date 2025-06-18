CREATE EXTENSION pgcrypto;

CREATE TABLE summarizations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
  userInput TEXT NOT NULL,
  summary TEXT NOT NULL
);

CREATE UNIQUE INDEX summarizations_userinput_uidx ON summarizations (userInput);
