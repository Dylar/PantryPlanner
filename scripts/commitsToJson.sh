#!/bin/bash

commit_format="\"%s\","

git fetch --all --tags
tags=($(git tag -l --sort=-v:refname))
commits=()

tagsLength=${#tags[@]}-1
for ((i=0; i<${#tags[@]}; i++))
do
    tag=${tags[i]}

    if ((i == 0))
    then # last commit to last tag
        echo "NEW - last commit to last tag ($tag)"
        commit_messages=$(git log "$tag"..HEAD --pretty=format:"$commit_format")
        joined_commits="$(IFS=,; echo "${commit_messages[*]}")"
        joined_commits="${joined_commits::${#joined_commits}-1}"
        commits+=("{\n\"version\":\"New\",\n\"commits\":[\n$joined_commits\n]\n}")
    fi
    if ((i < tagsLength))
    then # commits between tags
        next_tag=${tags[i+1]}
        echo "commits between tags ($tag to $next_tag)"
        commit_messages=$(git log "$next_tag".."$tag" --pretty=format:"$commit_format")
        joined_commits="$(IFS=,; echo "${commit_messages[*]}")"
        joined_commits="${joined_commits::${#joined_commits}-1}"
        commits+=("{\n\"version\":\"$tag\",\n\"commits\":[\n$joined_commits\n]\n}")
    fi
    if((i == tagsLength))
    then # first tag to first commit
          echo "first tag to first commit"
          commit_messages=$(git log "$tag" --pretty=format:"$commit_format")
          joined_commits="$(IFS=,; echo "${commit_messages[*]}")"
          joined_commits="${joined_commits::${#joined_commits}-1}"
          commits+=("{\n\"version\":\"$tag\",\n\"commits\":[\n$joined_commits\n]\n}")
    fi
done

result=()
first=true # comma between commits (but no trailing comma)
for ((i=${#commits[@]}-1; i>=0; i--)); do
    if [[ $first == true ]]; then
      result=("${commits[$i]}")
      first=false
    else
      result+=(",${commits[$i]}")
    fi
done

all="[$(printf "%s\n" "${result[@]}")]"
echo "$all" > releaseNotes.json
#mv releaseNotes.json "$BITRISE_SOURCE_DIR"/app/src/main/assets
mv releaseNotes.json ./app/src/main/assets