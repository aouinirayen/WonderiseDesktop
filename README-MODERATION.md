# Wonderwise Moderation System

## Overview
The Wonderwise application includes a comprehensive moderation system to ensure all content (experiences, comments, etc.) meets community guidelines. The system uses OpenAI's moderation API to automatically detect inappropriate content.

## Important: Content Visibility Rules

In the Wonderwise application, content visibility follows these rules:

1. **New content is hidden by default** until it passes moderation checks
2. Content is only shown in the front-end if:
   - It has been explicitly approved by a moderator, OR
   - It passed the automatic moderation check without being flagged
3. Content that fails moderation or generates API errors remains hidden until manually reviewed

This ensures that potentially inappropriate content is never displayed to users.

## Setting Up OpenAI API for Moderation

The moderation system uses OpenAI's moderation API with the following credentials:

- API Endpoint: `https://api.openai.com/v1/moderations`
- API Key: You must update the `API_KEY` in `ModerationService.java` with a valid key

### API Key Configuration

1. Open `ModerationService.java`
2. Replace `YOUR_OPENAI_API_KEY` with your actual OpenAI API key
3. Ensure your account has sufficient quota to handle moderation requests

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

### Handling API Errors

When the OpenAI API returns an error:
- Content is now automatically hidden (flagged for moderation) until manually reviewed
- This is a safety-first approach to prevent potentially inappropriate content from being displayed
- This behavior is different from earlier versions that would allow content through when API errors occurred

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

If the OpenAI API is unavailable or returns an error, the system is designed to automatically flag the content for manual review and prevent it from being displayed. This ensures that no potentially harmful content can be published when the moderation service is experiencing issues.

## Error Messages

If you see the error message "the moderation doesn't work", please check:

1. Your OpenAI API key is correctly configured
2. You haven't exceeded your API usage limits (check for "insufficient_quota" errors)
3. Your internet connection is working properly
4. The API endpoint is correct (OpenAI occasionally updates their endpoints)

### Common Issues:

1. **404 Error**: This usually means the API endpoint URL has changed. Check the OpenAI API documentation for the current endpoint and update the `API_ENDPOINT` constant in `ModerationService.java`.

2. **403 Error**: This typically indicates an authentication problem with your API key. Ensure your key is valid and has not expired.

3. **429 Error**: This means you've hit rate limits or exceeded your quota. Common causes:
   - "insufficient_quota": Your account has no remaining credits
   - "rate_limit_exceeded": You're making too many requests in a short period
   
   Solutions:
   - Upgrade your OpenAI plan
   - Add billing information to your account
   - Implement request throttling in your application

## Support

For any issues with the moderation system, please contact the development team or refer to the [OpenAI API documentation](https://platform.openai.com/docs/api-reference/moderations). 