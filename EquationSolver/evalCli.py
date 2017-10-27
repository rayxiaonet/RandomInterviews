#!/usr/bin/python
# cli wrapper for the core code
import sys
from eval import Eval

if len(sys.argv) <= 1:
    print('Please specify input file to parse')
    sys.exit()

inputFile = open(sys.argv[1], 'r')

# read input file
results = Eval().solve(inputFile.read().splitlines())

# push all expression to toEval deque, variable assignments to vars dictionary

inputFile.close()
for key in results:
    print('{0} = {1}'.format(key, results[key]))