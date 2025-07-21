TrackNest is a personal command-line productivity app, designed to help you track your goals, habits, tasks, and progress—securely. It features password-protected accounts for users and admins, habit/goal/task management, daily streaks, categories, onboarding wizard, data persistence, audit logs, and a host of professional admin controls.

✨ Features
User/Password Security: SHA-256 password hashing, security questions, password hints, password change & recovery, lockout after failed logins, admin password reset/unlock.

Data Persistence: All users, goals, habits, and logs are saved between sessions.

Admin & User Roles: Admin features include user overview, password management, log export, and more.

Productivity Management: Create/manage tasks (with priority and due date), goals (with deadline and status), and habits (with streak tracking).

Onboarding Wizard: Friendly setup for new users.

Progress & Stats: Quickly view your completed items, streaks, recent accolades, and points.

Search & Filter: Find items by keyword, organize with categories.

Stylized Menus: Clean, clearly marked, Unicode-enhanced console navigation with hotkeys.

Reminders & Tips: Get notifications on deadlines, motivational tips from admins, and daily summaries.

Activity/Audit Log: Tracks sign-in/out and admin actions, exportable to CSV for auditing.

Quick Command Support: Use /search and /info commands anywhere.

Safe Actions: Requires confirmation before any destructive steps (deletion, reset, logout).

CLI Automation: Supports command-line flags for exporting logs, resetting admin, etc.

User Experience: Hints, theming, and navigation tips always available.

Modular Structure: Ready for future expansion (calendar, AI/ML, web).

🚀 Getting Started
Requirements
Java 8 or newer (JDK)

Command-line/terminal access

Setup & Run
Clone or Save the Source

Save TrackNestApp.java in a directory of your choosing.

Compile

text
javac TrackNestApp.java
Run

text
java TrackNestApp
(Optional) Command-Line Automation

Export audit log:

text
java TrackNestApp --export
Reset Admin Password:

text
java TrackNestApp --reset-admin
🗝️ First Login
A default admin user is created on first launch:

Email: admin@nest.com

Password: Admin@123

Security Q: Admin's favorite project?

Security A: TrackNest

Please log in and reset the password after setup.

📚 Usage Overview
Register: Input your name, email, strong password, password hint, and security question/answer.

Login: Use credentials to access your dashboard.

User Dashboard:

Add/view/complete tasks, habits, and goals.

Log routines; track streaks and points.

Search or filter with /search keyword.

View recent activities, badges, and stats.

Change password, set reminders, manage categories.

Admin Dashboard:

View all users and statuses.

Reset or unlock accounts.

Broadcast productivity tips.

Audit logs and export.

Change admin password.

Quick user info with /info user@email.com.

Quick Commands:

Type /search keyword in any menu to jump to items.

Type logout or exit in any menu to sign out.

🛡️ Security/Account Features
SHA-256 password hashing (never stored as clear-text).

Account lockout after multiple failed logins.

Password hints (never shows actual password).

Security question/answer for password recovery.

Admin resets for forgotten/locked accounts.

💾 Data Storage
All data (users, goals, habits, stats, logs) persist in tracknest_data.ser (serialized object).

No external database required.

🛠️ Advanced/Admin Features
Audit log: usage and actions are tracked by session.

Admins can export logs to CSV for auditing.

CLI flags support admin automation (--export/--reset-admin).

User and admin menus are stylized and navigable via hotkeys.

🌱 Extensibility
Expand to web/mobile interface.

Add calendar or AI analysis modules.

Connect to Google Tasks/Trello or calendar APIs.

Plug in notification/email/2FA modules for real-world deployment.

❤️ Contributing
Fork, star, or suggest issues/PRs for enhancements (if on a git repo).

Ideas: habit reinforcement machine learning, weekly PDF reports, dark mode, email notifications.

💬 Help & Support
For password resets, use option 3 at main screen (need your security answer).

See console for all navigation tips and guidance.

For any app crash or bug, delete tracknest_data.ser to reset all data.

📄 License
This project is open for educational use. For commercial use or advanced integrations, please contact the author.

Made with dedication for personal growth and productivity! 🚀

Enjoy boosting your productivity and building lasting habits with TrackNest!
