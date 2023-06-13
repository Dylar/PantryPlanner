#!/bin/bash

git fetch --all --tags
tags=($(git tag))
commits=()

tagsLength=${#tags[@]}
for ((i=0; i<${#tags[@]}; i++))
do
    tag=${tags[i]}

    if ((i == 0))
    then
        commit_messages=$(git log "$tag" --pretty=format:"\"%s\",")
        joined_commits="$(IFS=,; echo "${commit_messages[*]}")"
        joined_commits="${joined_commits::${#joined_commits}-1}"
        commits+=("{\n\"version\":\"$tag\",\n\"commits\":[\n$joined_commits\n]\n}")
    fi
    if ((i > 0))
    then
        prev_tag=${tags[i-1]}
        commit_messages=$(git log "$prev_tag".."$tag" --pretty=format:"\"%s\",")
        joined_commits="$(IFS=,; echo "${commit_messages[*]}")"
        joined_commits="${joined_commits::${#joined_commits}-1}"
        commits+=("{\n\"version\":\"$tag\",\n\"commits\":[\n$joined_commits\n]\n}")
    fi
    if((i == tagsLength-1))
    then
        commit_messages=$(git log "$tag"..HEAD --pretty=format:"\"%s\",")
        joined_commits="$(IFS=,; echo "${commit_messages[*]}")"
        joined_commits="${joined_commits::${#joined_commits}-1}"
        commits+=("{\n\"version\":\"New\",\n\"commits\":[\n$joined_commits\n]\n}")
    fi
done

result=()
first=true
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
mv releaseNotes.json ./app/src/main/assets