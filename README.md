# maxine's blog

> 🌐 **Languages**: [English](README.md) | [日本語](README_ja.md)

Personal blog project built with Spring Boot 3.2.0 and Java 17.

## About

This is maxine's personal blog, where I share thoughts about programming, writing, and learning. The project features a minimalist design inspired by modern web applications like Linear and Apple.

## Features

- **Modern Tech Stack**: Spring Boot 3.2.0, Java 17, MySQL
- **Minimalist UI**: Clean and modern design using Tailwind CSS
- **Blog Management**: Full-featured admin panel for managing blogs, categories, and tags
- **Markdown Support**: Rich text editing with Markdown support
- **Responsive Design**: Mobile-friendly interface
- **Image Upload**: Support for local image uploads
- **Smart Featured Image**: Automatically extracts first image from content if not provided
- **Publishing Heatmap**: Visual statistics of blog publishing activity

## Tech Stack

- **Backend**: Spring Boot 3.2.0, Spring Data JPA, Hibernate 6
- **Frontend**: Thymeleaf, Tailwind CSS, JavaScript, ECharts
- **Database**: MySQL 8+
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/maxine-yang/blog.git
cd blog
```

2. Create database:
```sql
CREATE DATABASE blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Configure database:
   - Copy `src/main/resources/application-dev.yml.example` to `src/main/resources/application-dev.yml`
   - Copy `src/main/resources/application-pro.yml.example` to `src/main/resources/application-pro.yml`
   - Update the database connection settings in these files:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

4. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

5. Access the application:
- Frontend: http://localhost:8080
- Admin Panel: http://localhost:8080/admin
  - Default credentials will be created automatically on first run
  - **Important**: Change the default password immediately after first login!

## Project Structure

```
blog/
├── src/
│   ├── main/
│   │   ├── java/com/lrm/
│   │   │   ├── po/          # Entity classes
│   │   │   ├── dao/         # Repository interfaces
│   │   │   ├── service/     # Business logic
│   │   │   ├── web/         # Controllers
│   │   │   ├── util/        # Utility classes
│   │   │   ├── config/      # Configuration classes
│   │   │   └── interceptor/ # Interceptors
│   │   └── resources/
│   │       ├── templates/   # Thymeleaf templates
│   │       ├── static/      # Static resources
│   │       └── application*.yml.example  # Configuration templates
│   └── test/                # Test files
└── pom.xml                  # Maven configuration
```

## Security Notes

⚠️ **Important**: Before pushing to GitHub, make sure to:

1. **Never commit sensitive configuration files**:
   - `application-dev.yml` and `application-pro.yml` are already in `.gitignore`
   - Use the `.example` files as templates

2. **Change default credentials**:
   - The default admin password is set in `DataInitializer.java`
   - Change it immediately after deployment

3. **Review your code**:
   - Check for any hardcoded passwords, API keys, or sensitive data
   - Use environment variables for production deployments

## Configuration

The project uses Spring profiles for different environments:
- `dev`: Development environment (auto-created admin user, debug logging)
- `pro`: Production environment (no auto-creation, warning-level logging)

Copy the example files and configure your database connection:
```bash
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
cp src/main/resources/application-pro.yml.example src/main/resources/application-pro.yml
```

## License

Copyright © 2026 maxine yang (Updated version)

## Contact

- **Email**: hamanomax01@gmail.com
- **GitHub**: https://github.com/maxine-yang
