action_keep = "keep"
action_discard = "discard"


def matches(case, rule):
    """Match a test case against a rule
    return '1' if there's a match, 0 otherwise
    """
    if len(case) != len(rule):
        return 0

    match = 1
    for i in range(len(case)):
        if rule[i] != "*" and case[i] != rule[i]:
            match = 0
            break
    return match


def keep(case, rule, action):
    """Decide to keep or discard the test case
    Decision test depends on the result of 'matches' as well as the action (keep or discard)
    """
    keep = 0
    if action == action_keep:
        keep = 1 if matches(case, rule) else 0
    elif action == action_discard:
        keep = 0 if matches(case, rule) else 1
    else:
        keep = -1
    return keep


def test_cases_are_valid(vals):
    return 1


def filters_are_valid(vals):
    return 1
