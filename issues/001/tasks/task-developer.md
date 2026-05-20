# Task: developer

## ステータス
- 状態: 完了

## 役割
- SDK 37 更新、依存更新、deprecated 解消、検証

## 作業内容
1. AGP、Gradle、Kotlin、Compose、AndroidX、Firebase、周辺依存の互換性を調査する。
2. `app/build.gradle` と関連設定を SDK 37 向けに更新する。
3. 本番アプリコードと build script の deprecated を優先解消する。
4. 既存の build / unit test を実行し、失敗時は原因を分類する。

## 制約
- テストコードは原則変更しない。
- 失敗時は改修より先に原因分析を行う。

## 完了内容
- AGP `9.1.1`、Gradle wrapper `9.3.1`、`compileSdk` / `targetSdk` `37` への更新を実施した。
- AGP 9 と非互換だった DeployGate Gradle plugin を除去した。
- `downloadAndroidUtilTestLibs` の出力先を `app/build/devicefarm/androidTestUtil` に変更し、Gradle 9.3.1 の task validation error を解消した。
- Jackson の `setSerializationInclusion`、Compose の `ParagraphIntrinsics`、`LocalClipboardManager`、`menuAnchor()` など、本番コード側の主要 deprecated を置換した。
- `testDevDebugUnitTest`、`assembleDevDebug`、`assembleDevDebugAndroidTest`、`app:downloadAndroidUtilTestLibs`、`lintDevDebug` を成功させた。

## 成果物
- `build.gradle`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradle.properties`
- `app/build.gradle`
- `app/src/main/java/...` 配下の deprecated 解消差分

## 残課題
- built-in Kotlin への移行は未対応で、`android.builtInKotlin=false` / `android.newDsl=false` と `org.jetbrains.kotlin.android` 利用に関する deprecation が残る。
- テストコード起因の warning は今回の方針どおり未対応。

## 完了条件
- SDK 37 対応差分が揃い、発生した問題の切り分け結果が説明できる。
