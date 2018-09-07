#!/bin/sh
release_core=$(curl --silent "https://api.github.com/repos/datacrow/core/releases/latest" | grep -Po '"tag_name": "\K.*?(?=")')
wget https://github.com/datacrow/core/releases/download/${release_core}/datacrow-core.jar -O ./lib/datacrow-core.jar
release_server=$(curl --silent "https://api.github.com/repos/datacrow/server/releases/latest" | grep -Po '"tag_name": "\K.*?(?=")')
wget https://github.com/datacrow/server/releases/download/${release_server}/datacrow-server.jar -O ./lib/datacrow-server.jar
