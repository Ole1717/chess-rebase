#!/system/bin/sh
# CHESS REBASE launcher — запускает уже установленный APK.
PKG="com.chessrebase.app"
if command -v monkey >/dev/null 2>&1; then
  monkey -p "$PKG" -c android.intent.category.LAUNCHER 1
else
  am start -n "$PKG/.MainActivity"
fi
