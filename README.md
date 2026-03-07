# itookthese

A full-stack photography portfolio website for showcasing and managing photos with EXIF metadata, categories, and a built-in admin dashboard.

**Live:** [itookthese.app](https://itookthese.app)

## Tech Stack

| Layer          | Technology                                  |
| -------------- | ------------------------------------------- |
| Frontend       | Angular 19, Tailwind CSS 4, TypeScript      |
| Backend        | Spring Boot 4, Java 21, Hibernate, Flyway   |
| Database       | PostgreSQL 16                               |
| Infrastructure | Docker Compose, Nginx, Let's Encrypt        |

## Features

- **Photo Gallery** — Responsive grid with category filtering and lightbox modal
- **Image Processing** — Automatic thumbnail (400px), medium (1200px), and full (2400px) generation with 20px base64 placeholders for progressive loading
- **EXIF Extraction** — Camera model, aperture, ISO, shutter speed, and focal length parsed from uploads
- **Admin Dashboard** — Upload, inline-edit, delete, and drag-and-drop reorder photos; manage categories, contact submissions, and site settings
- **Contact Form** — Public submissions with optional email notifications
- **Light/Dark Theme** — Toggle with system preference detection and localStorage persistence
- **JWT Authentication** — Stateless auth protecting all admin endpoints

## Project Structure

```
.
├── api/            # Spring Boot backend
├── client/         # Angular frontend
├── docker/
│   └── nginx/      # Nginx configs (dev & prod)
├── scripts/        # Deployment & SSL renewal scripts
├── docker-compose.yml       # Development
└── docker-compose.prod.yml  # Production
```

## Testing

The project includes 81 backend tests and 179 frontend tests covering services, controllers, components, and guards.
