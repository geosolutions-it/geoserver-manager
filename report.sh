#!/bin/sh
if [ "$#" -ne 3 ]; then
 echo "
Generates git log report between two tags:
USAGE:
$0 {START_TAG} {STOP_TAG|HEAD} {MESSAGE DESCRIPTION}"
exit 1
fi

echo "What's new?
===========

$3

Where can I get it?
===================

* Home:
  https://github.com/geosolutions-it/geoserver-manager
* Download:
  https://github.com/geosolutions-it/geoserver-manager/releases
* Documentation:
  https://github.com/geosolutions-it/geoserver-manager/wiki

Who contributed to this release?
================================

* List authors of this release
"
git log $1..$2 --pretty="format:* %aN" | sort | uniq

echo "
========================
CHANGES IN THIS RELEASE:
========================
"
git log $1..$2 --no-merges --pretty=oneline --reverse --grep fix --grep improve --grep close --grep "#" --grep add --format='* %s' .

exit $?
