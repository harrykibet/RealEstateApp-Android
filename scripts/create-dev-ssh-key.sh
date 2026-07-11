#!/usr/bin/env bash

set -euo pipefail

KEY_NAME="id_ed25519_dev"
SSH_DIR="$HOME/.ssh"
KEY_PATH="$SSH_DIR/$KEY_NAME"
PUBLIC_KEY="$KEY_PATH.pub"
SSH_CONFIG="$SSH_DIR/config"

COMMENT="$(whoami)@$(hostname)-dev"

echo "======================================"
echo " SSH Development Key Generator"
echo " GitHub + GitLab compatible"
echo "======================================"
echo

# Ensure SSH directory exists
mkdir -p "$SSH_DIR"
chmod 700 "$SSH_DIR"

# Check existing key
if [[ -f "$KEY_PATH" ]]; then
    echo "Existing SSH key found:"
    echo "$KEY_PATH"
    echo

    read -rp "Overwrite existing key? (y/N): " ANSWER

    if [[ "$ANSWER" != "y" && "$ANSWER" != "Y" ]]; then
        echo "Cancelled."
        exit 0
    fi

    rm -f "$KEY_PATH" "$PUBLIC_KEY"
fi


echo "Generating Ed25519 SSH key..."

ssh-keygen \
    -t ed25519 \
    -C "$COMMENT" \
    -f "$KEY_PATH" \
    -N ""


chmod 600 "$KEY_PATH"
chmod 644 "$PUBLIC_KEY"


echo
echo "Starting ssh-agent..."

eval "$(ssh-agent -s)" >/dev/null

ssh-add "$KEY_PATH"


echo
echo "Configuring SSH identity..."

touch "$SSH_CONFIG"
chmod 600 "$SSH_CONFIG"


# Add config only if missing
if ! grep -q "IdentityFile ~/.ssh/$KEY_NAME" "$SSH_CONFIG"; then

cat >> "$SSH_CONFIG" <<EOF

# Development Git identity
Host github.com
    HostName github.com
    User git
    IdentityFile ~/.ssh/$KEY_NAME
    IdentitiesOnly yes

Host gitlab.com
    HostName gitlab.com
    User git
    IdentityFile ~/.ssh/$KEY_NAME
    IdentitiesOnly yes

EOF

fi


echo
echo "======================================"
echo " COPY THIS PUBLIC KEY TO:"
echo
echo " GitHub  -> Settings -> SSH Keys"
echo " GitLab  -> Preferences -> SSH Keys"
echo "======================================"
echo

cat "$PUBLIC_KEY"

echo
echo "======================================"
echo "Fingerprint:"
echo "======================================"

ssh-keygen -lf "$PUBLIC_KEY"

echo
echo "======================================"
echo "SSH key created successfully."
echo "Private key:"
echo "$KEY_PATH"
echo
echo "Public key:"
echo "$PUBLIC_KEY"
echo "======================================"