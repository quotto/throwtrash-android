# AGENTS.md

このドキュメントは、このリポジトリで作業する AI エージェント向けのガイドラインです。
過去のガイドラインは参照せず、現在のコードベースを前提に新規作成しています。

## プロジェクト概要

- アプリ名: 今日のゴミ出し Android アプリ
- 目的: ごみ収集日をカレンダー表示し、通知する。Alexa スキルとのアカウントリンクにも対応する。
- 技術スタック: Android、Kotlin、Java 17、Gradle、Jetpack Compose、Android View/ViewBinding、Dagger、Firebase Crashlytics、JUnit 5、Espresso
- アーキテクチャ: モジュラモノリスを意識したレイヤード構成

## 主要ディレクトリ

- `app/src/main/java/net/mythrowaway/app/application`: Android アプリケーション初期化、DI コンポーネント
- `app/src/main/java/net/mythrowaway/app/module`: 機能単位のモジュール
- `app/src/main/java/net/mythrowaway/app/module/*/entity`: ドメインモデル
- `app/src/main/java/net/mythrowaway/app/module/*/dto`: usecase や service の入出力 DTO
- `app/src/main/java/net/mythrowaway/app/module/*/usecase`: ユースケース
- `app/src/main/java/net/mythrowaway/app/module/*/service`: モジュール境界を越えて使う処理
- `app/src/main/java/net/mythrowaway/app/module/*/infra`: Repository、API、SharedPreferences などの実装
- `app/src/main/java/net/mythrowaway/app/module/*/presentation`: Activity、Composable、ViewModel などの UI 層
- `app/src/main/java/net/mythrowaway/app/ui`: アプリ共通の UI テーマ
- `app/src/test`: JVM 単体テスト
- `app/src/androidTest`: Instrumented test、UI テスト
- `app/src/dev`、`app/src/prod`: product flavor ごとのリソース

## 基本方針

- 応答は日本語で行う。
- 作業前に対象ファイルと周辺実装を読む。README、Gradle 設定、既存テスト、同じモジュール内の実装を優先する。
- 既存の設計、命名、パッケージ構成、テストの書き方に合わせる。
- ユーザーの未コミット変更を勝手に戻さない。作業中に見つけた無関係な差分は触らない。
- 機密情報、署名情報、API トークン、`local.properties` の値を出力・変更・コミットしない。
- Android SDK、Gradle、依存関係の更新は必要な場合だけ行い、影響範囲を説明する。
- 外部仕様やライブラリの最新情報が必要な場合は、公式ドキュメントを確認してから判断する。

## 実装ルール

- ドメインロジックは `entity` または `usecase` に寄せ、UI 層へ業務ルールを漏らさない。
- `presentation` から `infra` 実装へ直接依存させず、既存のインターフェースや DI 構成に合わせる。
- モジュール間連携は原則として `service` と DTO を使う。
- 永続化や API のデータモデルは `infra/data`、`infra/model` に閉じ込め、ドメインモデルと混在させない。
- Dagger の追加・変更時は `application/di` 配下の既存 Component/Module 構成に合わせる。
- Compose 画面は既存テーマと Material コンポーネントに合わせる。既存の View ベース画面を不要に置き換えない。
- リソース追加時は flavor、night mode、既存命名との整合性を確認する。
- コメントは、意図や制約がコードだけでは読み取りにくい場合にだけ書く。

## テスト方針

- ドメイン、DTO mapper、usecase、infra の変更には JVM 単体テストを優先して追加・更新する。
- UI 操作や Android フレームワーク依存が主対象の場合は `androidTest` を追加・更新する。
- 既存のテスト配置に合わせ、対象クラスと近いパッケージへ置く。
- 正常系だけでなく、境界値、空データ、例外、永続化済みデータとの互換性を必要に応じて確認する。
- SharedPreferences、API、日時計算、通知、アカウントリンク周辺は回帰リスクが高いため、既存テストを読んでから変更する。

## よく使う検証コマンド

- 単体テスト: `./gradlew testDebugUnitTest`
- Instrumented test: `./gradlew connectedDebugAndroidTest`
- ビルド確認: `./gradlew assembleDebug`
- カバレッジ: `./gradlew jacocoTestReport`
- Android test utility 依存取得: `./gradlew downloadAndroidUtilTestLibs`

必要な検証は変更範囲に応じて選ぶ。エミュレータや実機が必要なテストを実行できない場合は、その理由と未検証範囲を報告する。

## 作業フロー

1. 要求を読み、対象機能と影響範囲を特定する。
2. `rg` や `rg --files` で関連ファイル、既存テスト、同種実装を探す。
3. 実装方針を決め、必要なら短くユーザーに共有する。
4. 変更は小さく保ち、既存パターンに沿って実装する。
5. 変更範囲に応じてテストを追加・更新する。
6. 実行可能な Gradle タスクで検証する。
7. 変更内容、検証結果、残ったリスクを簡潔に報告する。

## Git とレビュー

- ユーザーから明示されない限り、コミットや push は行わない。
- コミットを依頼された場合は、差分を確認し、自分の変更範囲だけを含める。
- リモートへの push や PR 作成は、ユーザーの明示的な指示がある場合だけ行う。
- レビュー依頼では、変更概要よりも不具合、回帰リスク、テスト不足を優先して指摘する。

## 注意領域

- `app/src/google-services.json`、`local.properties`、署名設定、DeployGate、Firebase、Crashlytics 周辺は慎重に扱う。
- `applicationId`、`namespace`、flavor、versionCode/versionName はリリース影響がある。
- `SharedPreferences` のキーや保存形式は既存ユーザーのデータ移行に関わる。
- 通知、Alarm、日時計算、月次・週次スケジュールはタイムゾーンや端末設定の影響を受ける。
- Alexa アカウントリンクと API 通信は外部サービス連携のため、エラー処理と互換性を重視する。
