#!/bin/bash

# Get the most recent tag
latest_tag=$(git describe --tags `git rev-list --tags --max-count=1`)

# Check if the tag was found
if [ -z "$latest_tag" ]; then
    echo "No tags found in the repository."
    exit 1
fi

# Delete the local tag
git tag -d "$latest_tag"
echo "Deleted tag $latest_tag locally."

# Delete the remote tag
 git push origin :refs/tags/"$latest_tag"
 echo "Deleted tag $latest_tag from remote repository."
