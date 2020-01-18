import fileinput
import os
import re

version = os.environ["TRAVIS_TAG"]

for line in fileinput.input("docs/index.md", inplace=True):
    print(re.sub("(<version>).*(</version>)", f"\g<1>{version}\g<2>",
                 line.rstrip(), flags=re.DOTALL))
