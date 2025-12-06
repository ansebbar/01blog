#!/bin/bash
set -e

echo "🚀 Setting up Angular CLI locally (no sudo)..."

# --- Step 1: Check Node.js ---
if ! command -v node &> /dev/null; then
    echo "Node.js not found. Installing Node.js (LTS) locally..."
    mkdir -p "$HOME/local"
    cd "$HOME/local"

    curl -fsSL https://nodejs.org/dist/latest-lts/ | grep -oE 'node-v[0-9]+\.[0-9]+\.[0-9]+-linux-x64.tar.xz' | head -n 1 | xargs -I {} wget https://nodejs.org/dist/latest-lts/{}
    tar -xf node-v*-linux-x64.tar.xz
    mv node-v*-linux-x64 node
    export PATH="$HOME/local/node/bin:$PATH"
    echo 'export PATH=$HOME/local/node/bin:$PATH' >> ~/.zshrc
    echo "✅ Node.js installed locally: $(node -v)"
else
    echo "Node.js already exists: $(node -v)"
fi

# --- Step 2: Local npm setup ---
NPM_GLOBAL_DIR="$HOME/.npm-global"
mkdir -p "$NPM_GLOBAL_DIR"
npm config set prefix "$NPM_GLOBAL_DIR"
export PATH="$NPM_GLOBAL_DIR/bin:$PATH"
echo 'export PATH=$HOME/.npm-global/bin:$PATH' >> ~/.zshrc

# --- Step 3: Install Angular CLI locally ---
echo "⬇️ Installing Angular CLI..."
npm install -g @angular/cli

echo "✅ Done! Angular version:"
ng version