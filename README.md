# Video Streaming Application

## Overview
The Video Streaming Application is a Spring Boot-based backend service that supports uploading, managing, and streaming video files. It offers features like video metadata management, full video retrieval, and chunked streaming via byte-range requests, optimizing streaming for large files.
This application supports partial content requests through the Range HTTP header, allowing large videos to be streamed in smaller chunks. This optimizes bandwidth usage and provides a smooth streaming experience for users.

## Features
- **Upload Video:** Allows users to upload videos along with metadata (title, description, content type).
- **Retrieve Video Information:** Fetch video details using video ID or title.
- **Stream Video:** Supports video streaming via HTTP byte-range requests, allowing for partial content delivery and progressive video streaming.
- **Update Video Metadata:** Allows updating video metadata.
- **In-Memory Database:** Uses H2 in-memory database to store video metadata during application runtime.
- **Multipart File Upload Support:** Supports large video uploads up to 1GB.

## Tech Stack
- **Backend Framework:** Spring Boot
- **Database:** H2 (in-memory)
- **Persistence:** JPA with Hibernate
- **Logging:** SLF4J with Logback
- **File Management:** Spring MultipartFile, FileSystemResource
- **HTTP Streaming:** Supports byte-range requests for efficient media streaming

## Installation and Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/video-streaming-app.git
   cd video-streaming-app

2.  **Run the Application:**
   ```bash
   mvn spring-boot:run

