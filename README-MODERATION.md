# Wonderwise Moderation System

## Overview
The Wonderwise application includes a comprehensive moderation system to ensure all content (experiences, comments, etc.) meets community guidelines. The system uses OpenAI's moderation API to automatically detect inappropriate content.

## Setting Up OpenAI API for Moderation

The moderation system uses OpenAI's moderation API with the following credentials:

- API Endpoint: `https://api.openai.com/v1/moderations`
- API Key: Already configured

## How the Moderation System Works

The moderation system automatically screens content for:
- Hate speech and threatening content
- Harassment
- Self-harm content
- Sexual content (especially involving minors)
- Violence and graphic content

When potentially inappropriate content is detected:
1. The content is added to a moderation queue
2. Administrators can review the flagged content
3. Content can be approved or rejected through the moderation dashboard

## Moderation Levels

The system implements different moderation thresholds:
- High-risk content (sexual content involving minors, hate speech with threats, high violence scores) is automatically rejected
- Moderate-risk content is flagged for review but may still be displayed depending on settings
- Low-risk content passes through without being flagged

## Troubleshooting

If you encounter issues with the moderation system:

1. Check the console logs for detailed debugging information
2. Verify the OpenAI API key is valid and has access to the moderation endpoint
3. Make sure your network allows connections to the OpenAI API

## Features of the Moderation System

The moderation system provides:

- Automatic content screening for both experiences and comments
- Multi-level filtering with different thresholds for high-risk vs. moderate-risk content
- A moderation queue for admin review
- A modern, user-friendly moderation dashboard interface

## Moderation Dashboard

The moderation dashboard allows administrators to:

- View statistics about moderated content
- Review flagged content in a card-based layout
- Filter content by status, type, and text
- Approve or reject flagged content
- Preview detailed information about flagged items

## API Fallback

If the OpenAI API is unavailable or returns an error, the system is designed to let content through but still log it for potential review. This ensures the application continues to function even if the moderation service experiences issues.

## Error Messages

If you see the error message "the moderation doesn't work", please check:

1. Your OpenAI API key is correctly configured
2. You haven't exceeded your API usage limits
3. Your internet connection is working properly
4. The API endpoint is correct (OpenAI occasionally updates their endpoints)

### Common Issues:

1. **404 Error**: This usually means the API endpoint URL has changed. Check the OpenAI API documentation for the current endpoint and update the `API_ENDPOINT` constant in `ModerationService.java`.

2. **403 Error**: This typically indicates an authentication problem with your API key. Ensure your key is valid and has not expired.

3. **429 Error**: This means you've hit rate limits. Consider implementing backoff strategies or upgrading your OpenAI plan.

## Support

For any issues with the moderation system, please contact the development team or refer to the [OpenAI API documentation](https://platform.openai.com/docs/api-reference/moderations). 