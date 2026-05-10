# SDK 37 リファクタリング計画

## 概要
- issue ID: `001`
- 実装ブランチ: `chore/sdk37-upgrade`
- worktree: `/Volumes/extend/project/throwtrash-Android-sdk37-upgrade`
- planning link: `/Volumes/extend/project/throwtrash-Android-sdk37-upgrade/planning -> /Volumes/extend/project/throwtrash-Android/issues/001`

## 現状把握
- `app/build.gradle` は `compileSdk 36` / `targetSdkVersion 36` を使用している。
- build script には旧 Android Gradle DSL が残っている。
- `build.gradle` は AGP `9.0.0` / Kotlin `2.3.0` / Compose plugin `2.3.0` を参照している。
- `codemagic.yaml` は Java 17 前提で release / dev-release / e2e-test workflow を持つ。

## 調査結果
- SDK 37 を利用するには AGP `9.1.1` 以上と Gradle `9.3.1` 以上が必要なため、ビルド基盤更新が必須。
- `com.deploygate` Gradle plugin は AGP 9 系の variant API 変更と非互換で、タスク一覧取得時点で構成失敗を引き起こしたため除去対象。
- `org.jetbrains.kotlin.android` / `kotlin-kapt` を外した built-in Kotlin への移行は、現状の kapt / Dagger 構成では別作業規模になる。
- `downloadAndroidUtilTestLibs` の出力先が `app/build/tmp` だと Gradle 9.3.1 の task validation に抵触するため、専用ディレクトリへ分離が必要。

## 実施結果
- AGP を `9.1.1`、Gradle wrapper を `9.3.1` に更新し、`compileSdk` / `targetSdk` を `37` に更新した。
- AGP 9 と非互換な DeployGate Gradle plugin を除去し、DeployGate 連携は既存の Codemagic API upload に集約した。
- `codemagic.yaml` の AndroidTest ビルドを `assembleDevDebugAndroidTest` に修正し、APK 未生成時に即 fail するチェックを追加した。
- Device Farm 補助 APK の格納先を `app/build/devicefarm/androidTestUtil` に分離し、Gradle validation error を解消した。
- Jackson の `setSerializationInclusion`、Compose の `ParagraphIntrinsics` / `LocalClipboardManager` / `menuAnchor()` など、本番コード側の主要 deprecated を置換した。
- `./gradlew testDevDebugUnitTest assembleDevDebug assembleDevDebugAndroidTest app:downloadAndroidUtilTestLibs --warning-mode all --continue` は成功した。

## 残課題
- built-in Kotlin への移行は未対応で、`android.builtInKotlin=false` / `android.newDsl=false` と `org.jetbrains.kotlin.android` 利用に関する deprecation は残る。
- テストコード起因の warning（androidTest 側の Jackson deprecation など）は、方針どおり今回未対応。
- Device Farm 実行そのものはローカル未検証のため、Codemagic 上での実挙動確認は別途必要。

## 実施順
1. worktree と planning 連携を作成する。
2. SDK 37 対応に必要な Gradle・依存・CI 変更点を棚卸しする。
3. ビルド設定と依存を更新する。
4. 本番アプリコードと build script の deprecated を優先解消する。
5. Codemagic workflow を更新する。
6. 既存ビルド・テストを実行し、問題があれば原因を分類して追加改修計画を起票する。

## タスク進捗
| タスク | 担当 | 状態 | 完了概要 |
| --- | --- | --- | --- |
| 要件定義・計画作成 | manager | 完了 | `requirements.md` / `plan.md` / task ファイルを作成・更新 |
| worktree 準備 | manager | 完了 | `/Volumes/extend/project/throwtrash-Android-sdk37-upgrade` を作成し `planning` を接続 |
| SDK 37 互換性監査 | developer / devops | 完了 | AGP `9.1.1`、Gradle `9.3.1` 必須、DeployGate plugin 非互換を確認 |
| ビルド設定更新 | developer | 完了 | `compileSdk` / `targetSdk` を `37` に更新し、関連 build 設定を修正 |
| Codemagic 更新 | devops | 完了 | AndroidTest タスクを flavor 対応し、APK 未検出時の fail-fast を追加 |
| deprecated 解消 | developer | 完了 | 本番コード側の Jackson / Compose / Clipboard など主要 warning を置換 |
| レビュー | reviewer | 完了 | 重大な問題なし、継続監視事項のみ整理 |
| 検証 | developer | 完了 | `testDevDebugUnitTest`、`assembleDevDebug`、`assembleDevDebugAndroidTest`、`app:downloadAndroidUtilTestLibs`、`lintDevDebug` を通過 |

## エージェント構成
- `manager`: 要件管理、計画管理、進行管理、結果統合
- `developer`: SDK 37 更新、依存更新、deprecated 解消、ローカル検証
- `reviewer`: 変更差分レビュー、回帰リスク・セキュリティ観点の確認
- `devops`: Codemagic workflow の更新方針確認、CI 影響分析

## 成果物
1. SDK 37 対応済みの Android ビルド設定
2. 更新済みの `codemagic.yaml`
3. deprecated 解消の差分
4. 必要に応じた追跡用の改修計画

## リスク
1. SDK 37 対応で一部依存が追従不足の可能性がある。
2. Device Farm や orchestrator 周りは CI 環境依存のため、ローカルのみでは完全検証できない。
3. テストコードを触らない制約により、互換性問題は即時解決ではなく切り分け中心になる可能性がある。
4. built-in Kotlin への移行は annotation processing の置き換えを伴う可能性があり、今回スコープで完了しない恐れがある。
5. built-in Kotlin を残しているため、AGP 10 までに kapt / Dagger を含む移行方針の整理が必要。

## 完了条件
1. 実装用 worktree が準備されている。
2. 要件定義と計画が文書化されている。
3. SDK 37 対応の build / unit test / androidTest APK 生成 / Device Farm 補助 APK 取得がローカルで通る。
