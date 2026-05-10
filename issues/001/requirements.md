# SDK 37 リファクタリング要件定義

## 背景
- 現在の Android ビルド設定は `compileSdk 36` / `targetSdkVersion 36` で、Android SDK 37 への追従が未完了。
- ルート Gradle 設定、アプリ依存、Codemagic workflow が SDK 更新の影響を受ける可能性がある。
- 現在の作業ディレクトリでは別機能の改修が進行中のため、作業は分離した worktree で行う必要がある。

## 目的
- Android SDK 37 へ更新し、関連する Gradle・依存ライブラリ・CI 設定を整合させる。
- 本番アプリコードとビルド設定に存在する deprecated を可能な限り解消する。
- テストコードは原則変更せず、問題が顕在化した場合は原因調査と改修計画の策定までを行う。

## スコープ
1. Android SDK 37 への更新
   - `compileSdk` / `targetSdk` を 37 に更新する。
   - SDK 37 対応のために必要な AGP、Gradle、Kotlin、Compose、AndroidX、Firebase、周辺プラグインを見直す。
2. Codemagic 更新
   - `codemagic.yaml` の release / dev-release / e2e-test workflow を SDK 37 前提で整合させる。
3. deprecated 解消
   - 本番アプリコードとビルド設定を優先対象とする。
   - テストコードと外部ライブラリ由来の deprecated は対象外とし、必要なら別計画を作る。
4. 検証と切り分け
   - 既存のビルド・テストタスクで影響確認を行う。
   - テストエラー発生時は、テストコードを直ちに変更せず、原因分析と対応方針整理を行う。

## 非スコープ
- 新機能追加
- UI/UX 改修
- ドメイン仕様変更
- Firebase、DeployGate、AWS Device Farm の運用仕様変更
- 原因分析を伴わないテストコード修正

## 制約
1. 作業は `feature/schedule-search` から派生した別 worktree で行う。
2. テストコード修正は原則禁止とし、必要時は別改修計画として扱う。
3. `local.properties`、署名情報、`google-services.json` の機密値は扱わない。

## 受け入れ条件
1. Android ビルド設定が SDK 37 に更新されている。
2. SDK 37 対応に必要な依存・ツール更新内容と理由が説明できる。
3. Codemagic workflow の更新方針と変更点が整理されている。
4. 本番アプリコードとビルド設定の主要 deprecated が解消されている、または残件理由が整理されている。
5. テスト関連の問題は、原因と今後の改修方針が文書化されている。

## 対応方針
- deprecated はまず Gradle DSL と build script から優先解消する。
- 次に本番アプリコードの警告を解消する。
- 依存更新は SDK 37 対応に必要な最小範囲を基本とし、影響の大きい更新は理由を明示する。
