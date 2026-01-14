# maxine's blog

> 🌐 **言語**: [English](README.md) | [日本語](README_ja.md)

Spring Boot 3.2.0 と Java 17 で構築されたパーソナルブログプロジェクト。

## 概要

これは maxine のパーソナルブログで、プログラミング、執筆、学習についての考えを共有しています。このプロジェクトは、Linear や Apple などのモダンな Web アプリケーションにインスピレーションを得たミニマリストデザインを特徴としています。

## 機能

- **モダンな技術スタック**: Spring Boot 3.2.0、Java 17、MySQL
- **ミニマリスト UI**: Tailwind CSS を使用したクリーンでモダンなデザイン
- **ブログ管理**: ブログ、カテゴリ、タグを管理するためのフル機能の管理パネル
- **Markdown サポート**: Markdown サポートによるリッチテキスト編集
- **レスポンシブデザイン**: モバイルフレンドリーなインターフェース
- **画像アップロード**: ローカル画像アップロードのサポート
- **スマートな特集画像**: 提供されていない場合、コンテンツから最初の画像を自動抽出
- **公開ヒートマップ**: ブログ公開活動の視覚的な統計

## 技術スタック

- **バックエンド**: Spring Boot 3.2.0、Spring Data JPA、Hibernate 6
- **フロントエンド**: Thymeleaf、Tailwind CSS、JavaScript、ECharts
- **データベース**: MySQL 8+
- **ビルドツール**: Maven

## はじめに

### 前提条件

- Java 17 以上
- Maven 3.6+
- MySQL 8+

### インストール

1. リポジトリをクローン:
```bash
git clone https://github.com/maxine-yang/blog.git
cd blog
```

2. データベースを作成:
```sql
CREATE DATABASE blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. データベースを設定:
   - `src/main/resources/application-dev.yml.example` を `src/main/resources/application-dev.yml` にコピー
   - `src/main/resources/application-pro.yml.example` を `src/main/resources/application-pro.yml` にコピー
   - これらのファイルのデータベース接続設定を更新:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

4. ビルドして実行:
```bash
mvn clean install
mvn spring-boot:run
```

5. アプリケーションにアクセス:
- フロントエンド: http://localhost:8080
- 管理パネル: http://localhost:8080/admin
  - デフォルトの認証情報は初回実行時に自動的に作成されます
  - **重要**: 初回ログイン後、すぐにデフォルトパスワードを変更してください！

## プロジェクト構造

```
blog/
├── src/
│   ├── main/
│   │   ├── java/com/lrm/
│   │   │   ├── po/          # エンティティクラス
│   │   │   ├── dao/         # リポジトリインターフェース
│   │   │   ├── service/     # ビジネスロジック
│   │   │   ├── web/         # コントローラー
│   │   │   ├── util/        # ユーティリティクラス
│   │   │   ├── config/      # 設定クラス
│   │   │   └── interceptor/ # インターセプター
│   │   └── resources/
│   │       ├── templates/   # Thymeleaf テンプレート
│   │       ├── static/      # 静的リソース
│   │       └── application*.yml.example  # 設定テンプレート
│   └── test/                # テストファイル
└── pom.xml                  # Maven 設定
```

## セキュリティに関する注意事項

⚠️ **重要**: GitHub にプッシュする前に、以下を確認してください:

1. **機密設定ファイルをコミットしない**:
   - `application-dev.yml` と `application-pro.yml` は既に `.gitignore` に含まれています
   - `.example` ファイルをテンプレートとして使用してください

2. **デフォルトの認証情報を変更**:
   - デフォルトの管理者パスワードは `DataInitializer.java` で設定されています
   - デプロイ後すぐに変更してください

3. **コードを確認**:
   - ハードコードされたパスワード、API キー、機密データがないか確認してください
   - 本番環境のデプロイには環境変数を使用してください

## 設定

このプロジェクトは、異なる環境に対して Spring プロファイルを使用します:
- `dev`: 開発環境（自動作成される管理者ユーザー、デバッグログ）
- `pro`: 本番環境（自動作成なし、警告レベルのログ）

サンプルファイルをコピーして、データベース接続を設定してください:
```bash
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
cp src/main/resources/application-pro.yml.example src/main/resources/application-pro.yml
```

## ライセンス

Copyright © 2026 maxine yang (Updated version)

## 連絡先

- **Email**: hamanomax01@gmail.com
- **GitHub**: https://github.com/maxine-yang
