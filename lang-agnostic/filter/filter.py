import argparse
import json
import os
import os.path
import re

import filter_util

# load args
parser = argparse.ArgumentParser()

parser.add_argument('--combined', dest='combined')
parser.add_argument('--filters', dest='filters')
parser.add_argument('--filtered', dest='filtered')

args = parser.parse_args()

# get the list of test cases
files = [f for f in os.listdir(args.combined)
         if os.path.isfile(os.path.join(args.combined, f))]

for f in files:
    # load the test cases
    test_cases = json.load(open(os.path.join(args.combined, f)))
    filter_util.test_cases_are_valid(test_cases)

    # load the filters
    filters = json.load(open(os.path.join(args.filters, f)))
    filter_util.filters_are_valid(filters)

    lst_idx_2rm = []

    for rule in filters:
        idx = 0
        for test_case in test_cases:
            if not filter_util.keep(
                    test_case['Case'],
                    rule['rule'],
                    rule['action']):
                lst_idx_2rm.append(idx)
            idx += 1

    # sort and remove duplicate indices
    lst_idx_2rm = sorted(list(set(lst_idx_2rm)), reverse=True)

    # remove test cases referenced by indices
    for idx in lst_idx_2rm:
        del test_cases[idx]

    # back to json
    json.dump(test_cases, open(os.path.join(args.filtered, f), 'w'))
