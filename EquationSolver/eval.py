from collections import deque
# This Eval class contains the equation evaluation core logic
#
# The idea for my solution is:
# 1, read inputs
# 2, put all equations into a deque
#    2.1, if the line is already an simple assignment, put it into the variables dictionary rather than deque
# 3, looping through the deque, take first equation line, try solved it will known variables .
#    if there are still unknown variable, put back to the bottom of deque
#    if all variables solved, lhs will be a new var name, rhs will be a simple numeric value, add it to the variables dictionary
# repeat 3 until all done

class Eval(object):
    def solve(self,lines):
        solved_vars = {}
        to_eval = deque([])
        for line in lines:
            words = line.split('=')
            if len(words) < 2:
                print('wrong line:' % words)
            else:
                # check if right part is expression or simple number value
                rhs = words[1].split('+')
                if len(rhs) > 1:
                    # contains '+', means an expression:
                    to_eval.append(
                        (words[0].strip(), map(lambda x: int(x) if x and x.strip().isdigit() else x.strip(), rhs)))
                else:
                    solved_vars[words[0].strip()] = int(words[1])

        # looping through the toEval stack using existing vars, until all expression in toEval deque has been solved
        i = 0
        while len(to_eval) > 0:
            exp = to_eval.popleft()

            # try solve lhs if not solved yet:
            if not isinstance(exp[0], int):
                if exp[0] in solved_vars:
                    exp[0] = solved_vars[exp[0]]

            # try solve rhs if not solved yet:
            newRhs = []
            if not isinstance(exp[1], int):
                numSum = 0
                for val in exp[1]:
                    if isinstance(val, int):
                        numSum += int(val)
                    else:
                        if val in solved_vars:
                            numSum += solved_vars[val]
                        else:
                            newRhs.append(val)
                newRhs.append(numSum)
            if len(newRhs) == 1:
                # new rhs only have 1 element, means it must be a number value that assign to lhs

                solved_vars[exp[0]] = newRhs[0]
            else:
                # more than 1 elements, means a combination of number value and unsolved variables, add back to the deque
                to_eval.append((exp[0], newRhs))
        return solved_vars




