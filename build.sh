#!/bin/bash
set -eu

cd bundles
sh deploy.sh
mvn clean install -e
