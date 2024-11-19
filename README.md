# YouTube Project - Full-Stack Video Sharing Platform

## Overview

The **YouTube Project** is a full-stack video-sharing platform that integrates a web application, Android app, and server-side architecture. It enables users to upload, watch, comment on, and share videos seamlessly. The platform leverages modern technologies such as Node.js, React, Android SDK, and a C++-powered TCP server for optimal performance.

## Features

### Shared Features (Across Web and Android)

- **Video Playback**: Stream videos with a responsive, easy-to-use player.
- **Video Management**: View, edit, and manage video titles, descriptions, and metadata.
- **Video Sharing**: Share videos with other users through links or social media.
- **Comments Section**: Users can interact and engage by commenting on videos.
- **Related Videos**: Discover related videos based on your current viewing preferences.
- **User Profiles**: Create and manage profiles, including likes, uploads, and comments.
- **Dark Mode**: Switch between light and dark themes for a personalized experience.

### TCP Server

The **TCP server** is a critical component that enhances the platform's performance by handling user data efficiently.

- **User Watch History**: Tracks video-watching activity and stores it in the MongoDB database.
- **Video Recommendations**: Suggests videos based on user interactions and watching history.
- **Multithreading**: Efficiently handles multiple simultaneous client connections.
- **Graceful Shutdown**: Ensures proper server shutdown by listening to termination signals.

## Technologies Used

### Web Frontend

- **Framework**: React.js
- **State Management**: Context API
- **Styling**: CSS Modules
- **Routing**: React Router

### Android Frontend

- **Framework**: Android SDK
- **Styling**: XML with Material Components

### Backend

- **Environment**: Node.js
- **Framework**: Express.js
- **Database**: MongoDB (using Mongoose for object modeling)
- **Authentication**: JWT (JSON Web Tokens)
- **File Handling**: Multer (for video and thumbnail uploads)

### TCP Server

- **Programming Language**: C++
- **Database**: MongoDB
- **Multithreading**: Handles concurrent client connections.
- **Socket Programming**: Communication is handled through sockets.
- **Signal Handling**: Gracefully shuts down server processes using signals.

## Development Process

### Web Project

- **Task Assignment**: Team members were responsible for different pages, balancing design and logic.
- **Design & Implementation**: Focused on building intuitive UI and clear navigation.
- **Logical Infrastructure**: Managed state with Context API to streamline data flow.
- **Coordination & Integration**: Ensured consistent integration across components.
- **Testing & Refinement**: Conducted user testing and iterations based on feedback.

### Android Project

- **Task Assignment**: Development was divided into modules such as video playback, user management, and community interactions.
- **Design & Implementation**: Mobile-friendly designs were prioritized for smooth UX.
- **Logical Infrastructure**: Used Android architecture components for efficient state management.
- **Coordination & Integration**: Frequent syncing ensured smooth integration and stability.
- **Testing & Refinement**: Comprehensive testing ensured app stability across various devices.

### Server-Side Project

- **Task Assignment**: Each team member took responsibility for different backend functionalities such as user authentication and video management.
- **Implementation**: The backend uses Express.js for API creation and MongoDB for storing data.
- **Integration**: Designed RESTful APIs for smooth interaction between frontend and backend.
- **Testing & Refinement**: API endpoints were rigorously tested for security and performance.

## Development Team

- **Hodaya Barak**
- **Ziv Olevsky**
- **Guy Rosental**

## Project Management

We used **Jira** to manage the project using **Agile methodologies**. Our workflow consisted of organizing work into **sprints**, tracking tasks with **user stories**, and adjusting priorities to ensure timely deliveries.

### Agile Methodology in Jira

- **Sprint Planning**: Defined and prioritized user stories at the start of each sprint.
- **Daily Standups**: Held daily meetings to track progress, resolve blockers, and adjust tasks.
- **Sprint Reviews & Retrospectives**: After each sprint, we reviewed completed work, demonstrated features, and identified areas for improvement.

### Android Development Approach

We used the **MVVM (Model-View-ViewModel)** pattern for the Android app, promoting modularity, maintainability, and testability.

- **Model**: Manages data and business logic, interfacing with backend services (e.g., APIs).
- **View**: Displays UI elements and captures user interactions.
- **ViewModel**: Acts as a bridge between the Model and View, providing data for the UI and handling user inputs.

### Server-Side Development Approach

The backend utilizes the **MVC (Model-View-Controller)** pattern for clear separation of concerns and scalability.

- **Model**: Defines data structures and handles database operations.
- **View**: In a server-side context, the "view" refers to the data returned as JSON responses.
- **Controller**: Manages incoming requests, processes data, and sends responses.

## Installation and Setup

### Web Project

#### Prerequisites

- Node.js (version 14.x or later)
- npm (Node Package Manager)

#### Installation

1. Clone the repository:
   ```bash
     git clone https://github.com/HodayaBarak/YouTube-project/youtube
   cd youtube
   npm install

