import unittest
from eval import Eval
#simple unittest for Eval class

class TestEval(unittest.TestCase):
    def testEval1(self):
        lines = ['offset = 4 + random + 1', 'location = 1 + origin + offset', 'origin = 3 + 5', 'random = 2',
                 'tick=tock+133', '   tock=   random+5+   7+ location','']
        result = Eval().solve(lines)
        print(result)
        assert (result['origin'] == 8)
        assert (result['random'] == 2)
        assert (result['location'] == 16)
        assert (result['offset'] == 7)
        assert (result['tick'] == 163)
        assert (result['tock'] == 30)

    def testEval2(self):
        lines = ['offset = 55 + random + 1', 'location = 1 + origin + offset+33+toe', 'origin = toe + 5+1+1+1+111110', 'random = 2',
                 'tick=tock+133', '   tock=   random+5+   7+ location','toe=0','lol=999999999999999999999']
        result = Eval().solve(lines)
        print(result)
        assert (result['origin'] == 111118)
        assert (result['random'] == 2)
        assert (result['location'] == 111210)
        assert (result['offset'] == 58)
        assert (result['tick'] == 111357)
        assert (result['tock'] == 111224)