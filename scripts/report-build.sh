#!/usr/bin/env bash

# Trace what's being executed (useful for debug)
# set -o xtrace
# Exit on error
set -o errexit
# Exit on unset variables
set +o nounset
# Catch the error code of the program who crashed in piping commands
set -o pipefail

# Report builds to labs.xwiki.com
if [[ -z "$TRAVIS" ]]
then
	curl -X POST "https://labs.xwiki.com/xwiki/bin/view/BuildCounters/?xpage=plain&project=eesc"
else
	curl -X POST "https://labs.xwiki.com/xwiki/bin/view/BuildCounters/?xpage=plain&project=eesc&travis_ci=1"
fi

exit 0
