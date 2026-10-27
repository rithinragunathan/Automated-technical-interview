INSERT INTO questions (title, content, question_type, difficulty, topic, tags, expected_answer, active) VALUES
('Explain Polymorphism in Java', 'What is polymorphism in Java and how is it implemented?', 'TECHNICAL', 'MEDIUM', 'Java OOP', 'java,oop,polymorphism', 'Polymorphism is the ability of an object to take on many forms. Implemented via method overloading (compile-time) and method overriding (run-time).', TRUE),
('Explain Hash Table time complexity', 'What is the time complexity of get and put operations in a HashMap and why?', 'TECHNICAL', 'EASY', 'Data Structures', 'hashmap,data structures', 'O(1) average case, O(n) worst case if there are many collisions. Uses a hash function to compute an index.', TRUE);

INSERT INTO coding_problems (title, slug, description, difficulty, topic, tags, active) VALUES
('Two Sum', 'two-sum', 'Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.', 'EASY', 'Arrays', 'array,hash-table', TRUE),
('Reverse Linked List', 'reverse-linked-list', 'Given the head of a singly linked list, reverse the list, and return the reversed list.', 'EASY', 'Linked Lists', 'linked-list,recursion', TRUE);

INSERT INTO test_cases (problem_id, input, expected_output, hidden, weight, description)
SELECT id, '[2,7,11,15]\n9', '[0,1]', FALSE, 1.0, 'Basic pair' FROM coding_problems WHERE slug = 'two-sum'
UNION ALL
SELECT id, '[3,2,4]\n6', '[1,2]', FALSE, 1.0, 'Pair is not at the beginning' FROM coding_problems WHERE slug = 'two-sum'
UNION ALL
SELECT id, '1->2->3->null', '3->2->1->null', FALSE, 1.0, 'Three-node list' FROM coding_problems WHERE slug = 'reverse-linked-list';
