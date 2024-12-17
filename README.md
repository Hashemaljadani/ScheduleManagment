# Schedule Management App

A simple and efficient Schedule Management App for Android that allows users to register, log in, and manage their tasks with reminders. Built with **Android Studio**, integrated with **Firebase Realtime Database** and **Firebase Authentication**.

---

## Features

- **User Authentication**:
  - Secure login and registration using Firebase Authentication.
  - Password visibility toggle for improved UX.

- **Task Management**:
  - Users can **add**, **edit**, and **delete** tasks.
  - Each task includes a name and a reminder time.

- **Task Reminders**:
  - Schedule exact task reminders using Android's `AlarmManager`.

- **Firebase Integration**:
  - Tasks are stored under each user's profile in **Firebase Realtime Database**.
  - User data is organized as:
    ```
    Users
     ├── sanitized_email
     │   ├── user_info
     │   └── Tasks
    ```

- **Modern UI**:
  - User-friendly interface with a clean layout.
  - Built using RecyclerView for displaying tasks dynamically.

---

## Tech Stack

- **Android**: Java and XML
- **Firebase**: 
  - Authentication
  - Realtime Database
- **AlarmManager**: Task reminders
- **Tools**: Android Studio

---

## Setup and Installation

### Prerequisites
- **Android Studio** installed.
- Firebase project set up.

### Steps:
1. Clone the repository:
   ```bash
   git clone https://github.com/Hashemaljadani/ScheduleManagment.git

![Register](https://github.com/user-attachments/assets/d736432f-499c-4061-bc52-538947e60ae9)

