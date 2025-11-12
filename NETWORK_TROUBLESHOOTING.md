# 🔧 Git Push Network Issue - Troubleshooting Guide

## 🐛 The Problem

```bash
ssh: Could not resolve hostname github.com: Temporary failure in name resolution
fatal: Could not read from remote repository.
```

This is a **network/DNS issue**, not a Git or repository problem.

---

## 🔍 Diagnosis

### What This Means

Your computer cannot resolve `github.com` to an IP address. This could be:
1. 🌐 Internet connection issue
2. 🔌 DNS server problem
3. 🛡️ Firewall blocking DNS
4. ⏰ Temporary network glitch
5. 🔐 VPN or proxy interference

### Quick Test

```bash
# Test 1: Can you reach GitHub?
ping github.com

# Test 2: Can you resolve DNS?
nslookup github.com

# Test 3: Check your internet
ping 8.8.8.8
```

---

## ✅ Solutions (Try in Order)

### Solution 1: Check Your Internet Connection (Most Common)

```bash
# Test internet connectivity
ping -c 3 8.8.8.8

# If this fails, check:
# - WiFi is connected
# - Ethernet cable is plugged in
# - Router is on
```

**Fix:** Reconnect to your network

---

### Solution 2: Restart Network Manager (Quick Fix)

```bash
# Restart NetworkManager (Fedora)
sudo systemctl restart NetworkManager

# Wait a few seconds, then try again
git push origin main
```

---

### Solution 3: Change DNS Server

Your DNS server might be down or slow.

```bash
# Check current DNS
cat /etc/resolv.conf

# Option A: Use Google DNS (Temporary)
sudo bash -c 'echo "nameserver 8.8.8.8" > /etc/resolv.conf'
sudo bash -c 'echo "nameserver 8.8.4.4" >> /etc/resolv.conf'

# Option B: Use Cloudflare DNS (Temporary)
sudo bash -c 'echo "nameserver 1.1.1.1" > /etc/resolv.conf'
sudo bash -c 'echo "nameserver 1.0.0.1" >> /etc/resolv.conf'

# Try git push again
git push origin main
```

**Note:** This is temporary. To make it permanent:

```bash
# Edit NetworkManager config
sudo nano /etc/NetworkManager/NetworkManager.conf

# Add under [main]:
[main]
dns=none

# Create resolv.conf
sudo nano /etc/resolv.conf

# Add:
nameserver 8.8.8.8
nameserver 8.8.4.4

# Restart NetworkManager
sudo systemctl restart NetworkManager
```

---

### Solution 4: Flush DNS Cache

```bash
# Clear DNS cache (Fedora/systemd)
sudo systemd-resolve --flush-caches

# Or restart systemd-resolved
sudo systemctl restart systemd-resolved

# Try again
git push origin main
```

---

### Solution 5: Check Firewall

```bash
# Check if firewall is blocking
sudo firewall-cmd --list-all

# Temporarily disable to test
sudo systemctl stop firewalld

# Try git push
git push origin main

# Re-enable firewall
sudo systemctl start firewalld
```

If this works, add Git to allowed services:
```bash
sudo firewall-cmd --permanent --add-service=git
sudo firewall-cmd --reload
```

---

### Solution 6: Use HTTPS Instead of SSH (Workaround)

If SSH is having issues, switch to HTTPS:

```bash
# Check current remote
git remote -v

# If using SSH (git@github.com:...)
# Switch to HTTPS
git remote set-url origin https://github.com/imani-prog/OnlineQuotesAPIs.git

# Try again
git push origin main
```

You'll be prompted for username/password or personal access token.

---

### Solution 7: Add GitHub to /etc/hosts (Last Resort)

If DNS keeps failing, manually add GitHub's IP:

```bash
# Get GitHub's IP
nslookup github.com 8.8.8.8

# Add to hosts file
sudo nano /etc/hosts

# Add this line (use actual IP from nslookup):
140.82.121.4 github.com

# Save and try again
git push origin main
```

---

## 🔍 Diagnostic Commands

Run these to gather information:

```bash
# 1. Check network status
nmcli device status

# 2. Check DNS resolution
dig github.com
nslookup github.com

# 3. Check routing
ip route show

# 4. Check if DNS works with Google
nslookup google.com 8.8.8.8

# 5. Check git remote
git remote -v

# 6. Test SSH to GitHub
ssh -T git@github.com
```

---

## 🎯 Quick Fix (Most Reliable)

**Just use HTTPS instead:**

```bash
# 1. Switch to HTTPS
git remote set-url origin https://github.com/imani-prog/OnlineQuotesAPIs.git

# 2. Push
git push origin main

# 3. Enter credentials when prompted
# Username: imani-prog
# Password: your-personal-access-token
```

**Get Personal Access Token:**
1. GitHub → Settings → Developer settings
2. Personal access tokens → Tokens (classic)
3. Generate new token
4. Select scopes: `repo`, `workflow`
5. Copy token and use as password

---

## 🔄 If It Happens Repeatedly

### Permanent Solution: Configure NetworkManager DNS

```bash
# Create NetworkManager DNS config
sudo nano /etc/NetworkManager/conf.d/dns.conf

# Add:
[main]
dns=default
systemd-resolved=false

# Create custom resolv.conf
sudo nano /etc/NetworkManager/conf.d/resolv.conf

# Add:
[main]
rc-manager=file

# Set DNS servers
sudo nano /etc/resolv.conf

# Add:
nameserver 8.8.8.8
nameserver 8.8.4.4
nameserver 1.1.1.1

# Make it immutable (prevent changes)
sudo chattr +i /etc/resolv.conf

# Restart NetworkManager
sudo systemctl restart NetworkManager

# Test
ping github.com
git push origin main
```

---

## 🌐 Check Your Network Setup

```bash
# View current DNS
cat /etc/resolv.conf

# View network connections
nmcli connection show

# View active connection details
nmcli connection show "Your-WiFi-Name"

# Check if VPN is active
ip link show
```

---

## ⚡ Quick Workaround While You Investigate

Use GitHub CLI instead:

```bash
# Install gh (if not installed)
sudo dnf install gh

# Login
gh auth login

# Push using gh
gh repo sync

# Or create PR
gh pr create
```

---

## 🎯 Recommended Solution

**Most reliable approach:**

1. **Switch to HTTPS** (avoids SSH DNS issues)
   ```bash
   git remote set-url origin https://github.com/imani-prog/OnlineQuotesAPIs.git
   ```

2. **Use Google DNS** (more reliable than ISP DNS)
   ```bash
   sudo nano /etc/resolv.conf
   # Add:
   nameserver 8.8.8.8
   nameserver 8.8.4.4
   ```

3. **Test and push**
   ```bash
   ping github.com
   git push origin main
   ```

---

## 📞 Still Not Working?

### Check These:

1. **Is your internet working?**
   ```bash
   ping google.com
   ```

2. **Can you access GitHub in browser?**
   Open: https://github.com

3. **Is this a workplace/school network?**
   - Might be blocking SSH (port 22)
   - Use HTTPS instead

4. **Is there a proxy?**
   ```bash
   env | grep -i proxy
   ```

5. **Try mobile hotspot**
   - Rule out local network issues

---

## ✅ Summary

**The Issue:** DNS cannot resolve `github.com`

**Quick Fix:** 
```bash
# Option 1: Switch to HTTPS
git remote set-url origin https://github.com/imani-prog/OnlineQuotesAPIs.git

# Option 2: Use Google DNS
sudo bash -c 'echo "nameserver 8.8.8.8" > /etc/resolv.conf'

# Then push
git push origin main
```

**This is NOT related to:**
- Your code
- Git configuration
- GitHub repository
- SSH keys

**This IS related to:**
- Network connection
- DNS resolution
- Internet service

---

**Created:** November 13, 2025  
**Issue:** Git push DNS resolution failure  
**Status:** Network/DNS issue (not Git issue)  
**Recommended:** Use HTTPS remote + Google DNS

