#!/usr/bin/env bash
#ls -la
#chmod +x updateVersionVars.sh
source ./updateVersionVars.sh

## Client ID, Client Secret, and Redirect URI obtained from the Google Developer Console
#REDIRECT_URI="https://developers.google.com/oauthplayground"
#
## Get authorization code from the user interaction
#echo "Please visit the following URL, authenticate and provide the authorization code:"
#echo "https://accounts.google.com/o/oauth2/v2/auth?client_id=$CLIENT_ID&redirect_uri=$REDIRECT_URI&response_type=code&scope=your-required-scopes&access_type=offline&prompt=consent"
#
## Read the authorization code from the user
#read -p "Enter the authorization code: " AUTH_CODE
#
## Exchange authorization code for access and refresh tokens
#TOKEN_RESPONSE=$(curl -s --request POST \
#  --url 'https://oauth2.googleapis.com/token' \
#  --header 'Content-Type: application/x-www-form-urlencoded' \
#  --data-urlencode "code=$AUTH_CODE" \
#  --data-urlencode "client_id=$CLIENT_ID" \
#  --data-urlencode "client_secret=$CLIENT_SECRET" \
#  --data-urlencode "redirect_uri=$REDIRECT_URI" \
#  --data-urlencode "grant_type=authorization_code")
#
## Extract tokens from the response
#ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.access_token')
#REFRESH_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.refresh_token')
#
## Output tokens
#echo "Access Token: $ACCESS_TOKEN"
#echo "Refresh Token: $REFRESH_TOKEN"

# Fetch the access token
TOKEN_RESPONSE=$(curl -s https://www.googleapis.com/oauth2/v4/token \
  -d "client_id=$CLIENT_ID" \
  -d "client_secret=$CLIENT_SECRET" \
  -d "refresh_token=$REFRESH_TOKEN" \
  -d "grant_type=refresh_token")

# Extract access_token from the JSON response
ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r .access_token)

# Fetch the current Firebase Remote Config
current_config=$(curl -s -H "Authorization: Bearer $ACCESS_TOKEN" "https://firebaseremoteconfig.googleapis.com/v1/projects/$FIRE_PROJECT/remoteConfig")

# Update the specific parameters within the fetched config
updated_config=$(echo "$current_config" | \
  jq '.parameters["app_version_'$ENV'"].defaultValue.value = "'$NEW_VERSION'"' | \
  jq '.parameters["app_download_url_'$ENV'"].defaultValue.value = "'"$NEW_DOWNLOAD_URL"'"')

# Use the updated config to update Firebase Remote Config
response=$(curl -X PUT \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -H "If-Match: *" \
  -d "$updated_config" \
  "https://firebaseremoteconfig.googleapis.com/v1/projects/$FIRE_PROJECT/remoteConfig")

#echo "$response"
# Check for an error in the response
if echo "$response" | jq -e '.error' > /dev/null; then
  # If there is an error field, print it and exit
  echo "Error in response:"
  echo "Message: $(echo "$response" | jq '.error.message')"
  echo "Status: $(echo "$response" | jq '.error.status')"
  echo "Code: $(echo "$response" | jq '.error.code')"
  exit 1
fi
