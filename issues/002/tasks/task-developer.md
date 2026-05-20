# Task: developer

## ステータス
- [x] 完了

## 役割
- ビルド補助設定の整理、不要資産の影響確認

## 作業内容
1. 現行の `assembleDevDebug` / `assembleDevDebugAndroidTest` 成果物パスが Firebase Test Lab 実行にそのまま使えることを確認する。
2. `app/build.gradle` の `ANDROIDX_TEST_ORCHESTRATOR`、`clearPackageData`、`downloadAndroidUtilTestLibs` が Firebase Test Lab 移行後にどう扱うべきかを整理する。
3. `.devicefarm/testspec.yaml` や Device Farm 専用補助処理を削除する場合の副作用を確認し、必要最小限の build script 変更だけを行う。

## 制約
- テストコードやテストケースは変更しない。
- CI 置き換えのために必要な最小限の build script 変更に留める。

## 完了条件
- Firebase Test Lab 実行に必要な APK 生成条件が明確になっている。
- Device Farm 専用補助設定のうち残すもの / 削除するものの判断根拠が整理されている。
- 必要な場合のみ `app/build.gradle` などへ最小限の差分が入っている。

## 完了内容
- `assembleDevDebug` と `assembleDevDebugAndroidTest` の成果物パスを Codemagic 側で固定できることを確認した。
- `ANDROIDX_TEST_ORCHESTRATOR` と `clearPackageData` は維持し、Firebase Test Lab 側では `--use-orchestrator` と `--environment-variables clearPackageData=true` で引き継ぐ方針にした。
- Device Farm 専用だった `downloadAndroidUtilTestLibs` タスクを削除した。
- `.devicefarm/testspec.yaml` を削除し、ローカルビルド確認では影響がないことを確認した。

## 期待成果物
- `app/build.gradle`
- 関連補助ファイルの整理方針
