#!/bin/sh

BRANCH="$(git branch --show-current)"

INTERNALS="micro-ui-internals"

echo "Building internal packages..."

cd $INTERNALS

echo "Installing internals dependencies..."
yarn install --frozen-lockfile

echo "Building internals..."
yarn build

echo "Cleaning internals node_modules to save space..."
find . -name "node_modules" -type d -prune -exec rm -rf '{}' +

cd ..

echo "Internals build complete"