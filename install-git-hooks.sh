#!/bin/bash

# Install git hooks
mkdir -p .git/hooks

echo "Installing git hooks..."
cp .githooks/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit

echo "Git hooks installed successfully!"
