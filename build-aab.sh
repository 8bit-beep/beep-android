#!/bin/bash

AAB_PATH="app/build/outputs/bundle/release/app-release.aab"

if [ -f "$AAB_PATH" ]; then
    echo "기존 aab파일 삭제중"
    rm "$AAB_PATH"
fi

echo "빌드 시작"
./gradlew bundleRelease

if [ -f "$AAB_PATH" ]; then
    echo "빌드 완료: $AAB_PATH"
else
    echo "빌드 실패"
fi
