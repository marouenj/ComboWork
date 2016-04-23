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
