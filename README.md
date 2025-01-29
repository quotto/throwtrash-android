# 今日のゴミ出し Androidアプリ

## 概要
ごみの収集日をカレンダー形式で表示、通知するアプリです。  
Alexaスキルとアカウントリンクして、Alexaからゴミ出し日を確認できます。

[Google Play](https://play.google.com/store/apps/details?id=net.my.throwtrash&hl=ja)

[Alexaスキル](https://www.amazon.co.jp/quo1987-%E4%BB%8A%E6%97%A5%E3%81%AE%E3%82%B4%E3%83%9F%E5%87%BA%E3%81%97/dp/B07BHTKYDQ)

## プロジェクト構成
モジュラモノリス+レイヤードアーキテクチャを意識して以下のようなプロジェクト構成にしています。

- `application`: Androidプラットフォーム依存のモジュール。DI設定や起動時の初期処理を実装。
- `module`: アプリの主要機能を提供するモジュール群。機能をモジュールとして捉えて、モジュールごとに以下のパッケージで構成する。
  - `dto`: ユースーケースが管理するDTO。`usecase`に入れても良いのだが、`service`でも利用するため切り出した。
  - `entity`: ドメインモデル。
  - `infra`: データアクセスのためのrepositoryやAPIクライアントの実装。
    - `data`: インフラ実装内で扱うデータモデル。`usecase`や`service`には公開しない。
    - `model`: 主にAPIクライアントで扱うリクエスト・レスポンスモデル。
  - `presentation`: ユーザインターフェースのためのViewModelやView。
    - `view`: ActivityやComposableの実装。
    - `view_model`: ViewModel。
  - `service`: モジュールの境界を超えて別のモジュールから利用するサービスの実装。原則として入出力はDTOを利用する。
  - `usecase`: ユースケースの実装。`presentation`との入出力にはDTOを利用する。
- `ui`: Material Designのコンポーネントやカラースキームを提供するモジュール。
