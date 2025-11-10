#!/bin/bash

# Quick script to test GitHub SSH and push
# Run this AFTER adding your SSH key to GitHub

echo "🧪 Testing GitHub SSH Authentication..."
echo ""

# Test SSH connection
ssh -T git@github.com 2>&1 | head -3

echo ""
echo "================================"

# Check the result
if ssh -T git@github.com 2>&1 | grep -q "successfully authenticated"; then
    echo "✅ SUCCESS! SSH is working!"
    echo ""
    echo "🚀 Pushing to GitHub..."
    cd /home/imanitim/CODE/OnlineQuotes/Quotes
    git push -u origin main
else
    echo "❌ SSH authentication failed"
    echo ""
    echo "Please make sure you added this key to GitHub:"
    echo "https://github.com/settings/ssh/new"
    echo ""
    cat ~/.ssh/id_ed25519.pub
fi

