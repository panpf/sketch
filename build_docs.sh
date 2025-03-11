#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

cp README.md docs/index.md
cp README.zh.md docs/index.zh.md
cp CHANGELOG.md docs/CHANGELOG.md
cp CHANGELOG.zh.md docs/CHANGELOG.zh.md

perl -pi -e 's|\(README.zh.md\)|\(index.zh.md\)|g' docs/index.md
perl -pi -e 's|\(LICENSE.txt\)|\(../LICENSE.txt\)|g' docs/index.md
perl -pi -e 's|\(docs/|\(|g' docs/index.md
perl -pi -e 's|]: docs/|]: |g' docs/index.md
perl -pi -e 's|]: sketch-|]: ../sketch-|g' docs/index.md

perl -pi -e 's|\(README.md\)|\(index.md\)|g' docs/index.zh.md
perl -pi -e 's|\(LICENSE.txt\)|\(../LICENSE.txt\)|g' docs/index.zh.md
perl -pi -e 's|\(docs/|\(|g' docs/index.zh.md
perl -pi -e 's|]: docs/|]: |g' docs/index.zh.md
perl -pi -e 's|]: sketch-|]: ../sketch-|g' docs/index.zh.md

perl -pi -e 's|\(docs/|\(|g' docs/CHANGELOG.md
perl -pi -e 's|\(docs/|\(|g' docs/CHANGELOG.zh.md

find docs -type f -name "*.md" -exec perl -pi -e 's|]: ../|]: https://github.com/panpf/sketch/blob/main/|g' {} +
find docs -type f -name "*.md" -exec perl -pi -e 's|\(../|\(https://github.com/panpf/sketch/blob/main/|g' {} +

mkdocs build