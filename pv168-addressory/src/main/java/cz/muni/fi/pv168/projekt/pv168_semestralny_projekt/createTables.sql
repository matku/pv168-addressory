CREATE TABLE contact (
id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
first_name VARCHAR(20),
last_name VARCHAR(20),
address VARCHAR(50));

CREATE TABLE numbers
(contact_id INTEGER,
type VARCHAR(10),
number VARCHAR(15),
FOREIGN KEY (contact_id) REFERENCES contact(id));

CREATE TABLE groups
(id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
type VARCHAR(10),
note VARCHAR(255));

CREATE TABLE entry
(contact_id INTEGER,
group_id INTEGER,
PRIMARY KEY (contact_id, group_id),
FOREIGN KEY (contact_id) REFERENCES contact(id),
FOREIGN KEY (group_id) REFERENCES groups(id));