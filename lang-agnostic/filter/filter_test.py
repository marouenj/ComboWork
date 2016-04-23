import unittest

import filter_util


action_keep = "keep"
action_discard = "discard"


class FilterTest(unittest.TestCase):

    def test_matches(self):
        testcases = [
            # test case 1
            [[1, 2, 3], ["*", "*", "*"], 1],
            # test case 2
            [[1, 2, 3], ["*", 2, "*"], 1],
            # test case 3
            [[1, 2, 3], [1, 2, 4], 0],
            # test case 4
            [[1, 2, 3], [1, 2, 3, 4], 0],
            # test case 5
            [[1, 2, 3, 4], [1, 2, 3], 0]
        ]
        for v in testcases:
            actual = filter_util.matches(v[0], v[1])
            self.assertEqual(v[2], actual)

    def test_keep(self):
        testcases = [
            # test case 1
            [[1, 2, 3], ["*", "*", "*"], action_keep, 1],
            # test case 2
            [[1, 2, 3], ["*", "*", "*"], action_discard, 0],
            # test case 3
            [[1, 2, 3], ["*", 2, "*"], action_keep, 1],
            # test case 4
            [[1, 2, 3], ["*", 2, "*"], action_discard, 0],
            # test case 5
            [[1, 2, 3], [1, 2, 4], action_keep, 0],
            # test case 6
            [[1, 2, 3], [1, 2, 4], action_discard, 1],
            # test case 7
            [[1, 2, 3], [1, 2, 3, 4], action_keep, 0],
            # test case 8
            [[1, 2, 3], [1, 2, 3, 4], action_discard, 1],
            # test case 9
            [[1, 2, 3, 4], [1, 2, 3], action_keep, 0],
            # test case 10
            [[1, 2, 3, 4], [1, 2, 3], action_discard, 1]
        ]
        for v in testcases:
            actual = filter_util.keep(v[0], v[1], v[2])
            self.assertEqual(v[3], actual)

if __name__ == '__main__':
    unittest.main()
