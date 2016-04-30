import argparse
import json
import os
import re

from os.path import join

# load args
parser = argparse.ArgumentParser()

parser.add_argument('--combined', dest='combined')
parser.add_argument('--templates', dest='templates')
parser.add_argument('--interpolated', dest='interpolated')

args = parser.parse_args()

# get the list of test cases
files = [
    f for f in os.listdir(
        args.combined) if os.path.isfile(
            join(
                args.combined,
                f))]

for f in files:
    # run a one-time regex to rule out the variable-free lines
    matches = [
        re.findall(
            r'\$(\d+)',
            line) for line in open(
            re.sub(
                '\.json$',
                '',
                join(
                    args.templates,
                    f)))]

    # load the combinations
    test_cases = json.load(open(join(args.combined, f)))

    # parse file and inject vals
    test_case_idx = -1
    for test_case in test_cases:
        test_case_idx += 1
        out = open(
            re.sub(
                '\.json$',
                '',
                join(
                    args.interpolated,
                    f)) +
            '_' +
            str(test_case_idx),
            'w')
        line_idx = -1
        for line in open(re.sub('\.json$', '', join(args.templates, f))):
            line_idx += 1
            if matches[line_idx]:
                for val_idx in matches[line_idx]:
                    line = line.replace(
                        '$' + val_idx,
                        test_case['Case'][
                            int(val_idx) - 1])
            out.write(line.strip() + '\n')
        out.close()
