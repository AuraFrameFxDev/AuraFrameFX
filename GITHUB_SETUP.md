# GitHub Setup Guide for AuraFrameFX

This guide will help you push your AuraFrameFX project to GitHub and set up Dependabot to automatically fix dependency issues.

## Prerequisites

- Git installed on your machine
- GitHub account
- Access to the AuraFrameFxDev organization

## Steps to Push to GitHub

1. **Open a terminal or command prompt in your project directory**:
   ```
   cd C:\Users\Wehtt\Desktop\AuraFrameFx-Complete\complete
   ```

2. **Initialize Git (if not already done)**:
   ```
   git init
   ```

3. **Add the remote repository**:
   ```
   git remote add origin https://github.com/AuraFrameFxDev/AuraFrameFx.git
   ```

4. **Add all files to Git**:
   ```
   git add .
   ```

5. **Commit the changes**:
   ```
   git commit -m "Initial commit of AuraFrameFX project"
   ```

6. **Push to GitHub**:
   ```
   git push -u origin main
   ```
   
   If you're overwriting an existing repository:
   ```
   git push -f origin main
   ```

## What We've Prepared

We've set up the following files to help with your GitHub integration:

1. **dependabot.yml**: Configures Dependabot to automatically check for dependency updates weekly
2. **android-ci.yml**: GitHub Actions workflow for continuous integration
3. **dependabot-auto-merge.yml**: Automatically merges minor dependency updates
4. **CONTRIBUTING.md**: Guidelines for contributors
5. **Updated .gitignore**: Prevents sensitive files from being committed

## After Pushing to GitHub

1. **Enable GitHub Actions**:
   - Go to your repository on GitHub
   - Navigate to "Actions" tab
   - Enable workflows

2. **Set up Branch Protection**:
   - Go to "Settings" > "Branches"
   - Add a rule for the main branch
   - Require pull request reviews before merging
   - Require status checks to pass before merging

3. **Check Dependabot**:
   - Dependabot should automatically create pull requests for any outdated dependencies
   - These will help fix the dependency issues you're experiencing

## Troubleshooting

If you encounter any issues with dependencies after pushing to GitHub:

1. Check the Dependabot pull requests for updates
2. Manually update problematic dependencies in your build.gradle.kts file
3. Consider adding custom repositories if needed

For any other issues, refer to the GitHub documentation or contact the repository administrator.
