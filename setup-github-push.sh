#!/bin/bash

# GitHub Push Setup Script
# This script helps you set up SSH authentication and push to GitHub

echo "🚀 GitHub Push Setup Script"
echo "================================"
echo ""

# Check if repository exists
REPO_DIR="/home/imanitim/CODE/OnlineQuotes/Quotes"
if [ ! -d "$REPO_DIR/.git" ]; then
    echo "❌ Error: Not a git repository"
    exit 1
fi

cd "$REPO_DIR"

echo "📍 Current directory: $(pwd)"
echo "📦 Repository: https://github.com/imani-prog/OnlineQuotesAPIs"
echo ""

# Check remote
echo "🔗 Checking remote configuration..."
REMOTE_URL=$(git remote get-url origin 2>/dev/null)
echo "   Remote URL: $REMOTE_URL"
echo ""

# Check SSH key
echo "🔑 Checking SSH key..."
if [ -f ~/.ssh/id_ed25519.pub ]; then
    echo "   ✅ SSH key found!"
    echo ""
    echo "📋 Your SSH Public Key:"
    echo "   ================================"
    cat ~/.ssh/id_ed25519.pub
    echo "   ================================"
    echo ""
else
    echo "   ❌ No SSH key found"
    echo "   Run: ssh-keygen -t ed25519 -C 'your_email@example.com'"
    exit 1
fi

# Test SSH connection
echo "🧪 Testing GitHub SSH connection..."
ssh -T git@github.com 2>&1 | grep -q "successfully authenticated"
if [ $? -eq 0 ]; then
    echo "   ✅ SSH authentication successful!"
    echo ""
    echo "🚀 Ready to push! Run:"
    echo "   git push -u origin main"
    echo ""
    read -p "Push now? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git push -u origin main
    fi
else
    echo "   ❌ SSH authentication failed"
    echo ""
    echo "📝 To fix this:"
    echo "   1. Go to: https://github.com/settings/ssh/new"
    echo "   2. Add the SSH key shown above"
    echo "   3. Run this script again"
    echo ""
    echo "   Or run manually:"
    echo "   ssh -T git@github.com"
fi

