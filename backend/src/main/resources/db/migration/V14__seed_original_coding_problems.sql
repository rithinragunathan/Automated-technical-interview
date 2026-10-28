INSERT INTO coding_problems (
    title, slug, description, difficulty, topic, tags, constraints,
    input_format, output_format, examples, time_complexity_hint,
    space_complexity_hint, starter_code_java, active
)
SELECT seed.title, seed.slug, seed.description, seed.difficulty, seed.topic, seed.tags,
       seed.constraints, seed.input_format, seed.output_format, seed.examples,
       seed.time_complexity_hint, seed.space_complexity_hint, seed.starter_code_java, TRUE
FROM (
    SELECT
        'Largest Fixed-Size Window' AS title,
        'largest-fixed-size-window' AS slug,
        'Given a list of integers and a window length k, return the largest sum among all contiguous windows of exactly k elements.' AS description,
        'EASY' AS difficulty,
        'Arrays' AS topic,
        'array,sliding-window' AS tags,
        '- 1 <= k <= values.length\n- Values may be negative.' AS constraints,
        'The first line contains comma-separated integers in brackets. The second line contains k.' AS input_format,
        'Print the largest window sum as an integer.' AS output_format,
        '[{"input":"[2,1,5,1,3,2]\\n3","output":"9","explanation":"The window [5,1,3] has the largest sum."}]' AS examples,
        'O(n)' AS time_complexity_hint,
        'O(1)' AS space_complexity_hint,
        'import java.util.*;\n\nclass Solution {\n    public long largestWindowSum(int[] values, int k) {\n        return 0;\n    }\n}' AS starter_code_java
    UNION ALL SELECT
        'Merge Ordered Sequences', 'merge-ordered-sequences',
        'Combine two already sorted integer sequences into one sorted sequence. Keep every input value, including duplicates.',
        'EASY', 'Arrays', 'array,two-pointers',
        '- Both input sequences are sorted in non-decreasing order.',
        'The first two lines contain comma-separated integer arrays in brackets.',
        'Print the merged sequence in bracket notation.',
        '[{"input":"[1,4,8]\\n[2,3,9]","output":"[1,2,3,4,8,9]","explanation":"Values from both sequences are emitted in order."}]',
        'O(n + m)', 'O(n + m)',
        'import java.util.*;\n\nclass Solution {\n    public int[] mergeOrdered(int[] first, int[] second) {\n        return new int[0];\n    }\n}'
    UNION ALL SELECT
        'Circular Right Shift', 'circular-right-shift',
        'Shift every value in an integer sequence to the right by k positions. Values shifted past the end wrap around to the beginning.',
        'EASY', 'Arrays', 'array,rotation',
        '- 0 <= k\n- An empty sequence remains empty.',
        'The first line contains a comma-separated integer array in brackets. The second line contains k.',
        'Print the shifted sequence in bracket notation.',
        '[{"input":"[4,7,9,1]\\n2","output":"[9,1,4,7]","explanation":"The final two values wrap to the front."}]',
        'O(n)', 'O(n)',
        'class Solution {\n    public int[] shiftRight(int[] values, int k) {\n        return values;\n    }\n}'
    UNION ALL SELECT
        'First Unique Signal', 'first-unique-signal',
        'Given a string, return the zero-based index of its first character that occurs exactly once. Return -1 when every character repeats.',
        'EASY', 'Strings', 'string,frequency-count',
        '- The input contains lowercase English letters.',
        'The input is one line containing a non-empty lowercase string.',
        'Print the index of the first unique character, or -1.',
        '[{"input":"swiss","output":"1","explanation":"The character w occurs once and is the first such character."}]',
        'O(n)', 'O(1)',
        'class Solution {\n    public int firstUniqueIndex(String value) {\n        return -1;\n    }\n}'
    UNION ALL SELECT
        'Longest Uphill Run', 'longest-uphill-run',
        'Find the length of the longest contiguous part of a sequence in which every value is strictly greater than the value immediately before it. A single value is a run of length one.',
        'EASY', 'Arrays', 'array,sequences',
        '- The sequence contains at least one integer.',
        'The input is one comma-separated integer array in brackets.',
        'Print the length of the longest strictly increasing contiguous run.',
        '[{"input":"[2,4,3,5,7,1]","output":"3","explanation":"The run [3,5,7] has length three."}]',
        'O(n)', 'O(1)',
        'class Solution {\n    public int longestUphillRun(int[] values) {\n        return 0;\n    }\n}'
    UNION ALL SELECT
        'Balanced Bracket Sequence', 'balanced-bracket-sequence',
        'Determine whether a string containing only round, square, and curly brackets is balanced. Each closing bracket must match the most recent unmatched opening bracket.',
        'MEDIUM', 'Stacks', 'string,stack',
        '- The string contains only (), [], and {} characters.',
        'The input is one line containing a bracket sequence. The sequence may be empty.',
        'Print true if the sequence is balanced; otherwise print false.',
        '[{"input":"{[()]}","output":"true","explanation":"Every opening bracket is closed in the correct order."}]',
        'O(n)', 'O(n)',
        'class Solution {\n    public boolean isBalanced(String value) {\n        return false;\n    }\n}'
    UNION ALL SELECT
        'Best Forward Gain', 'best-forward-gain',
        'Given a sequence of daily measurements, find the largest positive difference between a later measurement and an earlier one. Return zero if no positive gain is possible.',
        'EASY', 'Arrays', 'array,one-pass',
        '- The sequence contains at least one integer.',
        'The input is one comma-separated integer array in brackets.',
        'Print the largest later-minus-earlier difference, or zero.',
        '[{"input":"[8,3,6,2,9,4]","output":"7","explanation":"The gain from 2 to 9 is seven."}]',
        'O(n)', 'O(1)',
        'class Solution {\n    public int bestForwardGain(int[] values) {\n        return 0;\n    }\n}'
    UNION ALL SELECT
        'Count Exact Tokens', 'count-exact-tokens',
        'Count how many whitespace-separated tokens in a line exactly match a target token. Matching is case-sensitive.',
        'EASY', 'Strings', 'string,parsing',
        '- The target token contains no whitespace.',
        'The first line contains the text. The second line contains the target token.',
        'Print the number of exact token matches.',
        '[{"input":"red blue redwood red\\nred","output":"2","explanation":"Only the two tokens equal to red are counted."}]',
        'O(n)', 'O(1)',
        'class Solution {\n    public int countExactTokens(String text, String token) {\n        return 0;\n    }\n}'
    UNION ALL SELECT
        'Grid Diagonal Gap', 'grid-diagonal-gap',
        'For a square integer grid, calculate the absolute difference between the sum of its top-left-to-bottom-right diagonal and the sum of its top-right-to-bottom-left diagonal.',
        'EASY', 'Matrices', 'array,matrix',
        '- The grid has at least one row and the same number of columns as rows.',
        'The first line contains n. The next n lines each contain n comma-separated integers.',
        'Print the absolute difference between the two diagonal sums.',
        '[{"input":"3\\n1,2,3\\n4,5,6\\n7,8,9","output":"0","explanation":"Both diagonal sums are 15."}]',
        'O(n)', 'O(1)',
        'class Solution {\n    public int diagonalGap(int[][] grid) {\n        return 0;\n    }\n}'
    UNION ALL SELECT
        'Collapse Neighbor Repeats', 'collapse-neighbor-repeats',
        'Remove repeated values that appear next to one another, keeping the first value in each consecutive group. Preserve the order of the remaining values.',
        'EASY', 'Arrays', 'array,sequence',
        '- The sequence may be empty.',
        'The input is one comma-separated integer array in brackets.',
        'Print the resulting sequence in bracket notation.',
        '[{"input":"[2,2,5,5,5,1,2,2]","output":"[2,5,1,2]","explanation":"Only adjacent duplicate values are collapsed."}]',
        'O(n)', 'O(n)',
        'class Solution {\n    public int[] collapseNeighbors(int[] values) {\n        return values;\n    }\}'
) AS seed
WHERE NOT EXISTS (
    SELECT 1 FROM coding_problems existing WHERE existing.slug = seed.slug
);

INSERT INTO test_cases (problem_id, input, expected_output, hidden, weight, description)
SELECT problem.id, seed.input, seed.expected_output, seed.hidden, 1.0, seed.description
FROM (
    SELECT 'largest-fixed-size-window' AS slug, '[2,1,5,1,3,2]\n3' AS input, '9' AS expected_output, FALSE AS hidden, 'Mixed values' AS description
    UNION ALL SELECT 'largest-fixed-size-window', '[-4,-2,-7]\n2', '-6', TRUE, 'All negative values'
    UNION ALL SELECT 'merge-ordered-sequences', '[1,4,8]\n[2,3,9]', '[1,2,3,4,8,9]', FALSE, 'Interleaved sequences'
    UNION ALL SELECT 'merge-ordered-sequences', '[1,1]\n[1,2]', '[1,1,1,2]', TRUE, 'Repeated values'
    UNION ALL SELECT 'circular-right-shift', '[4,7,9,1]\n2', '[9,1,4,7]', FALSE, 'Shift by two'
    UNION ALL SELECT 'circular-right-shift', '[5,6,7]\n5', '[6,7,5]', TRUE, 'Shift exceeds length'
    UNION ALL SELECT 'first-unique-signal', 'swiss', '1', FALSE, 'Unique character near beginning'
    UNION ALL SELECT 'first-unique-signal', 'aabbcc', '-1', TRUE, 'No unique character'
    UNION ALL SELECT 'longest-uphill-run', '[2,4,3,5,7,1]', '3', FALSE, 'Increasing middle run'
    UNION ALL SELECT 'longest-uphill-run', '[9,7,5,3]', '1', TRUE, 'Strictly decreasing values'
    UNION ALL SELECT 'balanced-bracket-sequence', '{[()]}', 'true', FALSE, 'Nested balanced brackets'
    UNION ALL SELECT 'balanced-bracket-sequence', '([)]', 'false', TRUE, 'Incorrect closing order'
    UNION ALL SELECT 'best-forward-gain', '[8,3,6,2,9,4]', '7', FALSE, 'Positive gain'
    UNION ALL SELECT 'best-forward-gain', '[9,7,4,2]', '0', TRUE, 'No positive gain'
    UNION ALL SELECT 'count-exact-tokens', 'red blue redwood red\nred', '2', FALSE, 'Exact token matches'
    UNION ALL SELECT 'count-exact-tokens', 'One one ONE\none', '1', TRUE, 'Case-sensitive match'
    UNION ALL SELECT 'grid-diagonal-gap', '3\n1,2,3\n4,5,6\n7,8,9', '0', FALSE, 'Equal diagonal sums'
    UNION ALL SELECT 'grid-diagonal-gap', '2\n1,2\n3,8', '6', TRUE, 'Different diagonal sums'
    UNION ALL SELECT 'collapse-neighbor-repeats', '[2,2,5,5,5,1,2,2]', '[2,5,1,2]', FALSE, 'Multiple repeated groups'
    UNION ALL SELECT 'collapse-neighbor-repeats', '[4]', '[4]', TRUE, 'Single value'
) AS seed
JOIN coding_problems problem ON problem.slug = seed.slug
WHERE NOT EXISTS (
    SELECT 1
    FROM test_cases existing
    WHERE existing.problem_id = problem.id
      AND existing.description = seed.description
);
