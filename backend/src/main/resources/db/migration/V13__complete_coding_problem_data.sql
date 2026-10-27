UPDATE coding_problems
SET description = 'Given an array of integers nums and an integer target, return the indices of the two numbers that add up to target. You may assume that each input has exactly one solution, and you may not use the same element twice. Return the answer in any order.',
    constraints = '- 2 <= nums.length <= 10^4\n- -10^9 <= nums[i] <= 10^9\n- -10^9 <= target <= 10^9\n- Exactly one valid answer exists.',
    input_format = 'The first line contains an integer array in bracket notation. The second line contains the target integer.',
    output_format = 'Print the two zero-based indices in bracket notation, for example [0,1].',
    examples = '[{"input":"[2,7,11,15]\\n9","output":"[0,1]","explanation":"nums[0] + nums[1] = 9."},{"input":"[3,2,4]\\n6","output":"[1,2]","explanation":"nums[1] + nums[2] = 6."}]',
    time_complexity_hint = 'O(n)',
    space_complexity_hint = 'O(n)'
WHERE slug = 'two-sum';

UPDATE coding_problems
SET description = 'Given the head of a singly linked list, reverse the list and return the reversed list. The input uses arrow notation and the output must use the same notation.',
    constraints = '- The number of nodes is between 0 and 5000.\n- -5000 <= Node.val <= 5000.',
    input_format = 'The first line contains a linked list in arrow notation, for example 1->2->3->null.',
    output_format = 'Print the reversed linked list in arrow notation, for example 3->2->1->null.',
    examples = '[{"input":"1->2->3->null","output":"3->2->1->null","explanation":"The links are reversed in place."}]',
    time_complexity_hint = 'O(n)',
    space_complexity_hint = 'O(1)'
WHERE slug = 'reverse-linked-list';

INSERT INTO test_cases (problem_id, input, expected_output, hidden, weight, description)
SELECT p.id, '[2,7,11,15]\n9', '[0,1]', FALSE, 1.0, 'Basic pair'
FROM coding_problems p
WHERE p.slug = 'two-sum'
  AND NOT EXISTS (SELECT 1 FROM test_cases t WHERE t.problem_id = p.id AND t.description = 'Basic pair');

INSERT INTO test_cases (problem_id, input, expected_output, hidden, weight, description)
SELECT p.id, '[3,2,4]\n6', '[1,2]', FALSE, 1.0, 'Pair is not at the beginning'
FROM coding_problems p
WHERE p.slug = 'two-sum'
  AND NOT EXISTS (SELECT 1 FROM test_cases t WHERE t.problem_id = p.id AND t.description = 'Pair is not at the beginning');

INSERT INTO test_cases (problem_id, input, expected_output, hidden, weight, description)
SELECT p.id, '[3,3]\n6', '[0,1]', TRUE, 1.0, 'Duplicate values'
FROM coding_problems p
WHERE p.slug = 'two-sum'
  AND NOT EXISTS (SELECT 1 FROM test_cases t WHERE t.problem_id = p.id AND t.description = 'Duplicate values');

INSERT INTO test_cases (problem_id, input, expected_output, hidden, weight, description)
SELECT p.id, '1->2->3->null', '3->2->1->null', FALSE, 1.0, 'Three-node list'
FROM coding_problems p
WHERE p.slug = 'reverse-linked-list'
  AND NOT EXISTS (SELECT 1 FROM test_cases t WHERE t.problem_id = p.id AND t.description = 'Three-node list');

INSERT INTO test_cases (problem_id, input, expected_output, hidden, weight, description)
SELECT p.id, '1->null', '1->null', TRUE, 1.0, 'Single-node list'
FROM coding_problems p
WHERE p.slug = 'reverse-linked-list'
  AND NOT EXISTS (SELECT 1 FROM test_cases t WHERE t.problem_id = p.id AND t.description = 'Single-node list');
