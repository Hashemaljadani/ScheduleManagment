# Schedule Management App

A simple and efficient **Schedule Management App** for Android that allows users to register, log in, and manage their daily tasks with reminders. This app is built using **Android Studio** with integration of **Firebase Realtime Database** and **Firebase Authentication**.

---

## Features

### **User Authentication**
- Secure user registration and login with Firebase Authentication.
- Password visibility toggle for better user experience.

### **Task Management**
- Users can **add**, **edit**, and **delete** tasks.
- Task reminders can be set using Android's `AlarmManager`.

### **Firebase Integration**
- User information and tasks are stored in a secure Firebase Realtime Database.
- Data structure follows a user-centric approach:
Users ├── sanitized_email │ ├── user_info │ └── Tasks

yaml
Copy code

### **Modern User Interface**
- Clean and intuitive UI designed for a seamless task management experience.
- Tasks are displayed using a dynamic RecyclerView.

---

## Screenshots

### Register Page  
Users can register by providing their full name, email, phone number, and password.  

![Register](https://github.com/user-attachments/assets/d736432f-499c-4061-bc52-538947e60ae9)

---

### Login Page  
Secure login for registered users with Firebase Authentication.  

![Login](https://github.com/user-attachments/assets/1a4c8511-4ab7-46de-925e-0fd243bba771)

---

### Home Page  
Tasks are displayed in a RecyclerView with options to **edit** or **delete** tasks.  

![Home](https://github.com/user-attachments/assets/459233ff-0910-4c78-b7c3-4f74c05ccef7)

---

### Add a Task  
Users can add tasks with a name and a reminder time.  

![Add task](https://github.com/user-attachments/assets/f314edc2-3342-42b8-a23b-1685c1eb51a7)

---

### Home Page After Adding a Task  
The newly added task is displayed dynamically in the RecyclerView.  

![Home after](https://github.com/user-attachments/assets/551c59a8-54da-4e36-8916-630935695d7e)

---

## Tech Stack

- **Language**: Java
- **IDE**: Android Studio
- **Backend**: Firebase Realtime Database, Firebase Authentication
- **Task Reminders**: AlarmManager

---

## Setup and Installation

### Prerequisites
- Android Studio installed.
- A Firebase project with Authentication and Realtime Database enabled.

### Steps:
1. Clone the repository:
```bash
git clone https://github.com/Hashemaljadani/ScheduleManagment.git
