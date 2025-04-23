APK_DIR="./app/build/outputs/apk/debug"
APK_ORIGINAL="$APK_DIR/app-debug.apk"
OUTPUT_DIR="./output"
OUTPUT_APK="$OUTPUT_DIR/삑.apk"

echo "Cleaning old APKs in output directory..."
rm -f "$OUTPUT_DIR"/*.apk

echo "Cleaning project..."
./gradlew clean

echo "Building APK..."
./gradlew assembleDebug

if [ ! -f "$APK_ORIGINAL" ]; then
    echo "APK build failed!"
    exit 1
fi


echo "Moving APK to output folder..."
mkdir -p "$OUTPUT_DIR"
mv "$APK_ORIGINAL" "$OUTPUT_APK"

echo "Done! APK available at: $OUTPUT_APK"
