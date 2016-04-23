import unittest

import filter_util


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

if __name__ == '__main__':
    unittest.main()
