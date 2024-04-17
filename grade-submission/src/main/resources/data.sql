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