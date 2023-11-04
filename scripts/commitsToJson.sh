#!/bin/bash

commit_format='%s'

# Function to process commits and create JSON objects
process_commits() {
  local tag="$1"
  shift
  local commit_messages=("$@")

  if [ ${#commit_messages[@]} -eq 0 ]; then
    echo "{ \"version\": \"$tag\", \"commits\": [] }"
    return
  fi

  # Sanitizing and joining commit messages
  local sanitized_commits=()
  for commit in "${commit_messages[@]}"; do
    sanitized_commit=$(echo "$commit" | sed 's/"/\\"/g' | tr -d '\n')
    sanitized_commits+=("\"$sanitized_commit\"")
  done
  local joined_commits=$(
    IFS=,
    echo "[${sanitized_commits[*]}]"
  )

  # Creating a JSON object for the tag
  echo "{ \"version\": \"$tag\", \"commits\": $joined_commits }"
}

git fetch --all --tags
tags=($(git tag -l --sort=-v:refname))

commits_json=()

if git log "${tags[0]}"..HEAD --pretty=format:"%s" | grep -q '.*'; then
  # Get commits from the latest tag to HEAD
  IFS=$'\n' read -r -d '' -a commits < <(git log "${tags[0]}"..HEAD --pretty=format:"$commit_format" && printf '\0')
  commits_json+=("$(process_commits "NEW" "${commits[@]}")")
fi

for ((i = 0; i < ${#tags[@]}; i++)); do
  tag=${tags[i]}
  next_tag=${tags[i + 1]}

  if ((i < ${#tags[@]} - 1)); then
    # Get commits between this tag and the next
    IFS=$'\n' read -r -d '' -a commits < <(git log "$next_tag".."$tag" --pretty=format:"$commit_format" && printf '\0')
  else
    # Get commits from the first tag to the initial commit
    IFS=$'\n' read -r -d '' -a commits < <(git log "$tag" --pretty=format:"$commit_format" && printf '\0')
  fi

  commits_json+=("$(process_commits "$tag" "${commits[@]}")")
done

# Reverse the array to get the oldest tags first
printf -v joined '%s,' "${commits_json[@]}"
joined="[${joined%,}]"

echo "$joined" >releaseNotes.json
mv releaseNotes.json ../app/src/main/assets
