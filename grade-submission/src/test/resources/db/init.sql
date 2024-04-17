-- Create tables
CREATE TABLE IF NOT EXISTS student (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    birth_date DATE
);

CREATE TABLE IF NOT EXISTS course (
    id INT PRIMARY KEY,
    subject VARCHAR(255),
    code VARCHAR(255),
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS course_student (
    student_id INT,
    course_id INT,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (course_id) REFERENCES course(id)
);

CREATE TABLE IF NOT EXISTS grade (
    id INT PRIMARY KEY,
    score VARCHAR(10),
    course_id INT,
    student_id INT,
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (student_id) REFERENCES student(id)
);

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255)
);


-- Insert students
INSERT INTO student (id, name, birth_date) VALUES
(1, 'Harry Potter', '1980-07-31'),
(2, 'Ron Weasley', '1980-03-01'),
(3, 'Neville Longbottom', '1980-07-30');

-- Insert courses
INSERT INTO course (id, subject, code, description) VALUES
(1, 'Subject1', 'CODE1', 'Description1'),
(2, 'Subject2', 'CODE2', 'Description2'),
(3, 'Subject3', 'CODE3', 'Description3');

-- Enroll students
INSERT INTO course_student (student_id, course_id) VALUES
(1, 1),
(2, 2),
(3, 2);

-- Insert grades
INSERT INTO grade (id, score, course_id, student_id) VALUES
(1, 'A', 1, 1),
(2, 'B', 2, 2);

-- Insert users
INSERT INTO users (id, username, password) VALUES
(1, 'User', 'password123');