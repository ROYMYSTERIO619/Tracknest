import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

interface Trackable {
    boolean checkCompletion();
}

// Abstract User
abstract class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    protected List<Goal> goals;
    protected List<Habit> habits;
    protected List<Task> tasks;
    protected List<String> habitHistory;
    protected Set<String> badges;
    protected int rewardPoints;
    protected int dailyTaskTarget;
    protected LocalDate lastLoginDate;
    protected List<String> recentCompleted;
    protected String theme; // Example: console "theme"
    protected LocalDate registrationDate;
    private String securityQuestion;
    private String securityAnswerHash;
    private String profileDescription;
    private String avatar;
    private String reminderFrequency; // "None", "Daily", "Weekly"
    private String language; // "EN", "ES"
    private boolean accessibilityMode;
    private String friendEmail;
    private boolean active = true;
    public User(int id, String name, String email, String passwordHash, String securityQuestion, String securityAnswerHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.registrationDate = LocalDate.now();
        this.securityQuestion = securityQuestion;
        this.securityAnswerHash = securityAnswerHash;
        this.goals = new ArrayList<>();
        this.habits = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.habitHistory = new ArrayList<>();
        this.badges = new HashSet<>();
        this.rewardPoints = 0;
        this.dailyTaskTarget = 0;
        this.lastLoginDate = null;
        this.recentCompleted = new LinkedList<>();
        this.theme = "Light";
    }

    public String getName() { return name; }
    public List<Goal> getGoals() { return goals; }
    public List<Habit> getHabits() { return habits; }
    public List<Task> getTasks() { return tasks; }
    public List<String> getHabitHistory() { return habitHistory; }
    public int getRewardPoints() { return rewardPoints; }
    public void addPoints(int pt) { rewardPoints += pt; }
    public void addBadge(String badge) {
        if (badges.add(badge)) System.out.println("🏅 Achievement unlocked: " + badge);
    }
    public void showBadges() {
        System.out.println("Your Badges: " + (badges.isEmpty() ? "None yet." : badges));
    }
    public void setTheme(String th) { this.theme = th; }
    public String getTheme() { return theme; }
    public String getEmail() { return email; }
    public int getId() { return id; }
    public void addRecentCompleted(String entry) {
        recentCompleted.add(0, entry);
        if (recentCompleted.size() > 3) recentCompleted.remove(3);
    }
    public void viewRecentCompleted() {
        System.out.println("Recently completed:");
        if (recentCompleted.isEmpty())
            System.out.println("None yet.");
        else
            for (String s : recentCompleted) System.out.println("- " + s);
    }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate date) { this.registrationDate = date; }
    public String getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswerHash() { return securityAnswerHash; }
    public void setSecurityQuestion(String q) { this.securityQuestion = q; }
    public void setSecurityAnswerHash(String h) { this.securityAnswerHash = h; }
    public String getProfileDescription() { return profileDescription; }
    public void setProfileDescription(String desc) { this.profileDescription = desc; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String av) { this.avatar = av; }
    public String getReminderFrequency() { return reminderFrequency; }
    public void setReminderFrequency(String freq) { this.reminderFrequency = freq; }
    public String getLanguage() { return language == null ? "EN" : language; }
    public void setLanguage(String lang) { this.language = lang; }
    public boolean isAccessibilityMode() { return accessibilityMode; }
    public void setAccessibilityMode(boolean mode) { this.accessibilityMode = mode; }
    public String getFriendEmail() { return friendEmail; }
    public void setFriendEmail(String email) { this.friendEmail = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }

    // Pretty print tasks
    public void printTasks() {
        if (tasks.isEmpty()) { System.out.println("No tasks."); return; }
        System.out.printf("%-3s %-25s %-12s %-8s %-10s %-5s\n", "#", "Task Name", "Due Date", "Priority", "Status", "Pin");
        int i = 1;
        for (Task t : tasks) {
            String status = t.isDone() ? "✔" : (t.isOverdue() ? "⏰" : "✗");
            String pin = t.isPinned() ? "⭐" : "";
            System.out.printf("%-3d %-25s %-12s %-8s %-10s %-5s\n", i++, t.getTaskName(), t.getDueDate(), t.getPriority(), status, pin);
        }
    }
    public void printHabits() {
        if (habits.isEmpty()) { System.out.println("No habits."); return; }
        System.out.printf("%-3s %-20s %-10s %-8s %-12s %-5s\n", "#", "Habit Name", "Freq", "Streak", "Last Log", "Pin");
        int i = 1;
        for (Habit h : habits) {
            String pin = h.isPinned() ? "⭐" : "";
            System.out.printf("%-3d %-20s %-10s %-8d %-12s %-5s\n", i++, h.getName(), h.getFrequency(), h.getStreak(), h.getLastLoggedDate()==null?"Never":h.getLastLoggedDate(), pin);
        }
    }
    public void printGoals() {
        if (goals.isEmpty()) { System.out.println("No goals."); return; }
        System.out.printf("%-3s %-25s %-12s %-10s %-5s\n", "#", "Goal Title", "Deadline", "Status", "Pin");
        int i = 1;
        for (Goal g : goals) {
            String pin = g.isPinned() ? "⭐" : "";
            System.out.printf("%-3d %-25s %-12s %-10s %-5s\n", i++, g.getTitle(), g.getDeadline(), g.getStatus(), pin);
        }
    }

    public void viewProgress() {
        UI.section("Progress Report for " + name + " [Theme: "+theme+"]");
        System.out.println("Reward Points: " + rewardPoints);
        System.out.println("Daily Target: " + (dailyTaskTarget == 0 ? "none" : dailyTaskTarget + " tasks/day"));
        System.out.println("Goals:");
        printGoals();
        System.out.println("Habits:");
        printHabits();
        System.out.println("Tasks:");
        printTasks();
        System.out.println("--------------------------------------------");
    }
    public void viewCalendar(LocalDate month) {
        System.out.println("====== Calendar deadlines for " + month.getMonth() + " ======");
        for (Goal g : goals) {
            if (g.getDeadline().getYear() == month.getYear() && g.getDeadline().getMonth() == month.getMonth())
                System.out.println("Goal: " + g.getTitle() + " - " + g.getDeadline());
        }
        for (Task t : tasks) {
            if (t.getDueDate().getYear() == month.getYear() && t.getDueDate().getMonth() == month.getMonth())
                System.out.println("Task: " + t.getTaskName() + " - " + t.getDueDate());
        }
        System.out.println("=============================================");
    }
    public void showHabitHistory() {
        if (habitHistory.isEmpty()) {
            System.out.println("No habit logs yet.");
            return;
        }
        System.out.println("--- Habit Log History ---");
        for (String entry : habitHistory) System.out.println(entry);
    }
    public void exportDataToCSV() {
        System.out.println("---- CSV Export START ----");
        System.out.println("Goals:\nTitle,Description,Deadline,Status");
        for (Goal g : goals)
            System.out.println(g.getTitle() + "," + g.getDescription() + "," + g.getDeadline() + "," + g.getStatus());
        System.out.println("Habits:\nName,Frequency,Streak,LastLogged");
        for (Habit h : habits)
            System.out.println(h.getName() + "," + h.getFrequency() + "," + h.getStreak()+","+h.getLastLoggedDate());
        System.out.println("Tasks:\nTaskName,Due,Priority,Status");
        for (Task t : tasks)
            System.out.println(t.getTaskName() + "," + t.getDueDate() + "," + t.getPriority() + "," + (t.isDone()?"DONE":t.isOverdue()?"OVERDUE":"PENDING"));
        System.out.println("---- CSV Export END ----");
    }
    public abstract void showMenu(Scanner sc, TrackNestSystem sys);
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String hash) { this.passwordHash = hash; }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available");
        }
    }

    public void showProgressGraph() {
        // Simple ASCII bar chart for last 7 days
        Map<LocalDate, Integer> dayCounts = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) dayCounts.put(today.minusDays(i), 0);
        for (Task t : tasks) {
            if (t.isDone() && dayCounts.containsKey(t.getDueDate()))
                dayCounts.put(t.getDueDate(), dayCounts.get(t.getDueDate()) + 1);
        }
        for (Habit h : habits) {
            if (h.getLastLoggedDate() != null && dayCounts.containsKey(h.getLastLoggedDate()))
                dayCounts.put(h.getLastLoggedDate(), dayCounts.get(h.getLastLoggedDate()) + 1);
        }
        UI.section("Progress (last 7 days)");
        for (Map.Entry<LocalDate, Integer> e : dayCounts.entrySet()) {
            System.out.printf("%s: %s\n", e.getKey(), "#".repeat(e.getValue()));
        }
    }
}

// Admin
class Admin extends User {
    public Admin(int id, String name, String email, String passwordHash, String securityQuestion, String securityAnswerHash) {
        super(id, name, email, passwordHash, securityQuestion, securityAnswerHash);
    }
    @Override
    public void showMenu(Scanner sc, TrackNestSystem sys) {
        UI.setTheme(getTheme());
        List<String> options = Arrays.asList(
            "View All Users",
            "Broadcast Productivity Tip",
            "Create Habit Templates",
            "View All Productivity Tips Sent",
            "Create Goal Template",
            "System Stats",
            "Change Password",
            "User Activity Log",
            "Deactivate/Reactivate User",
            "Broadcast Announcement",
            "Export All Data (CSV)",
            "Set Language",
            "Toggle Accessibility Mode",
            "Help",
            "Back/Logout"
        );
        while (true) {
            int ch = UI.menuSelect(sc, "Admin Menu", options);
            switch (ch) {
                case 1: sys.showAllUsers(); break;
                case 2: UI.prompt("Enter productivity tip: "); sys.broadcastTip(UI.getNonEmptyInput(sc)); break;
                case 3: sys.createHabitTemplate(sc); break;
                case 4: sys.viewAllTips(); break;
                case 5: sys.createGoalTemplate(sc); break;
                case 6: sys.showSystemStats(); break;
                case 7: sys.changePassword(this, sc); break;
                case 8: sys.viewActivityLog(); break;
                case 9: sys.deactivateUser(sc); break;
                case 10: sys.broadcastAnnouncement(sc); break;
                case 11: sys.exportAllDataCSV(); break;
                case 12:
                    UI.prompt("Set language (EN/ES): ");
                    String lang = sc.nextLine();
                    if (!lang.trim().isEmpty()) { setLanguage(lang); UI.setLanguage(lang); }
                    UI.info("Language: " + getLanguage());
                    break;
                case 13:
                    setAccessibilityMode(!isAccessibilityMode());
                    UI.setAccessibilityMode(isAccessibilityMode());
                    UI.info("Accessibility mode: " + (isAccessibilityMode() ? "ON" : "OFF"));
                    break;
                case 14:
                    UI.section("Help");
                    System.out.println("- View users, create templates, and broadcast tips.\n- Use 'Back/Logout' to return to main menu.");
                    break;
                case 15: return;
            }
        }
    }
}

// Normal User
class NormalUser extends User {
    public NormalUser(int id, String name, String email, String passwordHash, String securityQuestion, String securityAnswerHash) {
        super(id, name, email, passwordHash, securityQuestion, securityAnswerHash);
    }
    @Override
    public void showMenu(Scanner sc, TrackNestSystem sys) {
        UI.setTheme(getTheme());
        sys.showOverdueTasks(this);
        sys.autoCloseOverdueGoals(this);
        List<String> options = Arrays.asList(
            "Add New Goal",
            "Track Daily Habit",
            "View Streaks",
            "Log Task Completion",
            "View Progress Report",
            "Add Task",
            "Add Habit",
            "Edit/Delete Goal",
            "Edit/Delete Task",
            "Edit/Delete Habit",
            "Search/Filter Goals",
            "Sort Tasks",
            "View Calendar Deadlines",
            "View Habit History",
            "View Productivity Tips",
            "Instantiate Habit Template",
            "Instantiate Goal Template",
            "Export Data to CSV",
            "Delete Account",
            "Set/View Daily Task Target",
            "Quick Log All Daily Habits",
            "Pin/Unpin Task or Goal",
            "Show Achievement Badges",
            "Add Note to Habit or Goal",
            "View Recently Completed Items",
            "Switch Theme",
            "Change Password",
            "View My Email & Registration Date",
            "Help",
            "Back/Logout",
            "Set/View Profile Description",
            "Set/View Avatar",
            "Set/View Reminder Preference",
            "Preview Theme",
            "Set Language",
            "Toggle Accessibility Mode"
        );
        while (true) {
            int ch = UI.menuSelect(sc, "User Menu", options);
            switch (ch) {
                case 1: sys.addGoal(this, sc); break;
                case 2: sys.trackHabit(this, sc); break;
                case 3: sys.showStreaks(this); break;
                case 4: sys.logTaskCompletion(this, sc); break;
                case 5: viewProgress(); break;
                case 6: sys.addTask(this, sc); break;
                case 7: sys.addHabit(this, sc); break;
                case 8: sys.editDeleteGoal(this, sc); break;
                case 9: sys.editDeleteTask(this, sc); break;
                case 10: sys.editDeleteHabit(this, sc); break;
                case 11: sys.searchFilterGoals(this, sc); break;
                case 12: sys.sortTasks(this, sc); break;
                case 13: UI.prompt("Enter year and month (YYYY-MM): ");
                    try { viewCalendar(LocalDate.parse(UI.getNonEmptyInput(sc)+"-01")); }
                    catch(Exception e) { UI.error("Invalid format!"); } break;
                case 14: showHabitHistory(); break;
                case 15: sys.viewAllTips(); break;
                case 16: sys.instantiateHabitTemplate(this, sc); break;
                case 17: sys.instantiateGoalTemplate(this, sc); break;
                case 18: exportDataToCSV(); break;
                case 19: UI.prompt("Are you sure? Type YES to confirm: ");
                    if (UI.getNonEmptyInput(sc).equals("YES")) { sys.deleteAccount(this); UI.success("Account deleted."); return; } break;
                case 20:
                    UI.prompt("Enter new daily task target (0 to view only): ");
                    int t = UI.getIntInput(sc, 0, 100);
                    if (t > 0) {
                        dailyTaskTarget = t;
                        UI.success("Target set: " + t + " tasks/day");
                    } else {
                        long count = getTasks().stream().filter(Task::isDone).filter(task -> task.getDueDate().equals(LocalDate.now())).count();
                        UI.info("Today's completed tasks: " + count + " / " + dailyTaskTarget);
                    }
                    break;
                case 21:
                    int n = 0;
                    for (Habit h : getHabits()) {
                        if ("Daily".equalsIgnoreCase(h.getFrequency())) {
                            h.logToday();
                            habitHistory.add("Quick-log " + h.getName() + " on "+LocalDate.now());
                            n++; addPoints(1);
                        }
                    }
                    UI.success("Quick-logged "+n+" daily habits for today.");
                    break;
                case 22:
                    UI.prompt("Pin (1) Task or (2) Goal or (3) Habit? ");
                    String pinWhat = UI.getNonEmptyInput(sc);
                    try {
                        if ("1".equals(pinWhat)) {
                            for (int i=0; i<getTasks().size(); i++) System.out.println((i+1)+". "+getTasks().get(i));
                            UI.prompt("Pick task: ");
                            int idx = UI.getIntInput(sc, 1, getTasks().size())-1;
                            getTasks().get(idx).setPinned(!getTasks().get(idx).isPinned());
                            UI.info("Pinned?: "+getTasks().get(idx).isPinned());
                        } else if ("2".equals(pinWhat)) {
                            for (int i=0; i<getGoals().size(); i++) System.out.println((i+1)+". "+getGoals().get(i));
                            UI.prompt("Pick goal: ");
                            int idx = UI.getIntInput(sc, 1, getGoals().size())-1;
                            getGoals().get(idx).setPinned(!getGoals().get(idx).isPinned());
                            UI.info("Pinned?: "+getGoals().get(idx).isPinned());
                        } else if ("3".equals(pinWhat)) {
                            for (int i=0; i<getHabits().size(); i++) System.out.println((i+1)+". "+getHabits().get(i));
                            UI.prompt("Pick habit: ");
                            int idx = UI.getIntInput(sc, 1, getHabits().size())-1;
                            getHabits().get(idx).setPinned(!getHabits().get(idx).isPinned());
                            UI.info("Pinned?: "+getHabits().get(idx).isPinned());
                        }
                    } catch(Exception e) { UI.error("Invalid!"); }
                    break;
                case 23: showBadges(); break;
                case 24:
                    UI.prompt("Add note for (1) Goal or (2) Habit? ");
                    String noteType = UI.getNonEmptyInput(sc);
                    if ("1".equals(noteType)) {
                        for (int i=0; i<getGoals().size(); i++) System.out.println((i+1)+". "+getGoals().get(i));
                        UI.prompt("Pick goal: ");
                        int idx = UI.getIntInput(sc, 1, getGoals().size())-1;
                        UI.prompt("Note text: "); getGoals().get(idx).setNote(UI.getNonEmptyInput(sc));
                        UI.success("Note added.");
                    } else if ("2".equals(noteType)) {
                        for (int i=0; i<getHabits().size(); i++) System.out.println((i+1)+". "+getHabits().get(i));
                        UI.prompt("Pick habit: ");
                        int idx = UI.getIntInput(sc, 1, getHabits().size())-1;
                        UI.prompt("Note text: "); getHabits().get(idx).setNote(UI.getNonEmptyInput(sc));
                        UI.success("Note added.");
                    }
                    break;
                case 25: viewRecentCompleted(); break;
                case 26:
                    UI.prompt("Theme (Light/Dark): ");
                    String th = UI.getNonEmptyInput(sc);
                    setTheme(th);
                    UI.setTheme(th);
                    UI.success("Theme set.");
                    break;
                case 27: sys.changePassword(this, sc); break;
                case 28:
                    UI.section("My Info");
                    System.out.println("Email: " + getEmail());
                    System.out.println("Registration Date: " + getRegistrationDate());
                    break;
                case 29:
                    UI.section("Help");
                    System.out.println("- Use the menu numbers to navigate.\n- 'Back/Logout' returns to the main menu.\n- Most actions can be cancelled by typing 'back' at a prompt.\n- For more info, see the documentation.");
                    break;
                case 30: return;
                case 31:
                    UI.prompt("Profile description (leave blank to view): ");
                    String desc = sc.nextLine();
                    if (!desc.trim().isEmpty()) setProfileDescription(desc);
                    UI.info("Profile: " + (getProfileDescription() == null ? "None" : getProfileDescription()));
                    break;
                case 32:
                    UI.prompt("Avatar (emoji or text, leave blank to view): ");
                    String av = sc.nextLine();
                    if (!av.trim().isEmpty()) setAvatar(av);
                    UI.info("Avatar: " + (getAvatar() == null ? "None" : getAvatar()));
                    break;
                case 33:
                    UI.prompt("Reminder preference (None/Daily/Weekly, leave blank to view): ");
                    String rem = sc.nextLine();
                    if (!rem.trim().isEmpty()) setReminderFrequency(rem);
                    UI.info("Reminder: " + (getReminderFrequency() == null ? "None" : getReminderFrequency()));
                    break;
                case 34:
                    UI.prompt("Preview which theme? (Light/Dark): ");
                    String thPreview = sc.nextLine();
                    UI.previewTheme(thPreview);
                    break;
                case 35:
                    UI.prompt("Set language (EN/ES): ");
                    String lang = sc.nextLine();
                    if (!lang.trim().isEmpty()) { setLanguage(lang); UI.setLanguage(lang); }
                    UI.info("Language: " + getLanguage());
                    break;
                case 36:
                    setAccessibilityMode(!isAccessibilityMode());
                    UI.setAccessibilityMode(isAccessibilityMode());
                    UI.info("Accessibility mode: " + (isAccessibilityMode() ? "ON" : "OFF"));
                    break;
            }
        }
    }
}

// Goal
class Goal implements Trackable {
    private String title;
    private String description;
    private LocalDate deadline;
    private String status;
    private boolean pinned;
    private String note;
    private LocalDate reminderDate;
    private boolean archived;
    public Goal(String title, String description, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = "Active";
        this.pinned = false;
        this.note = null;
    }
    @Override public boolean checkCompletion() { return "Complete".equalsIgnoreCase(status); }
    public void markComplete() { status = "Complete"; }
    public void setTitle(String t) { title = t; }
    public void setDescription(String d) { description = d; }
    public void setDeadline(LocalDate d) { deadline = d; }
    public void setStatus(String st) { status = st; }
    public void setPinned(boolean p) { this.pinned = p; }
    public boolean isPinned() { return pinned; }
    public void setNote(String nt) { this.note = nt; }
    public String getNote() { return note; }
    public String getStatus() { return status; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDeadline() { return deadline; }
    public void setReminderDate(LocalDate d) { reminderDate = d; }
    public LocalDate getReminderDate() { return reminderDate; }
    public void setArchived(boolean a) { archived = a; }
    public boolean isArchived() { return archived; }
    public String toString() {
        return String.format("%s [Status:%s] (Deadline: %s)", title, status, deadline);
    }
}

// Habit
class Habit {
    private String name;
    private String frequency;
    private int streakCount;
    private LocalDate lastLoggedDate;
    private boolean pinned;
    private String note;
    private LocalDate reminderDate;
    private boolean archived;
    public Habit(String name, String frequency) {
        this.name = name;
        this.frequency = frequency;
        this.streakCount = 0;
        this.lastLoggedDate = null;
        this.pinned = false;
        this.note = null;
    }
    public String getName() { return name; }
    public int getStreak() { return streakCount; }
    public String getFrequency() { return frequency; }
    public LocalDate getLastLoggedDate() { return lastLoggedDate; }
    public void setName(String n) { name = n; }
    public void setFrequency(String f) { frequency = f; }
    public void setPinned(boolean p) { this.pinned = p; }
    public boolean isPinned() { return pinned; }
    public void setNote(String nt) { this.note = nt; }
    public String getNote() { return note; }
    public void setStreak(int streak) { this.streakCount = streak; }
    public void setLastLoggedDate(LocalDate date) { this.lastLoggedDate = date; }
    public void logToday() {
        LocalDate today = LocalDate.now();
        if(lastLoggedDate != null) {
            if(frequency.equalsIgnoreCase("Daily") && lastLoggedDate.plusDays(1).equals(today)) {
                streakCount++;
            } else if(frequency.equalsIgnoreCase("Weekly") && lastLoggedDate.plusWeeks(1).equals(today)) {
                streakCount++;
            } else if (lastLoggedDate.equals(today)) {
                // already logged today
            } else {
                streakCount = 1;
            }
        } else { streakCount = 1; }
        lastLoggedDate = today;
    }
    public void setReminderDate(LocalDate d) { reminderDate = d; }
    public LocalDate getReminderDate() { return reminderDate; }
    public void setArchived(boolean a) { archived = a; }
    public boolean isArchived() { return archived; }
    public String toString() {
        return name + " [" + frequency + "] (Last: " + (lastLoggedDate==null?"Never":lastLoggedDate) + ")";
    }
}

// Task
class Task {
    private String taskName;
    private LocalDate dueDate;
    private String priority;
    private boolean isDone;
    private boolean pinned;
    private LocalDate reminderDate;
    private boolean archived;
    private List<String> comments = new ArrayList<>();
    public Task(String taskName, LocalDate dueDate, String priority) {
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isDone = false;
        this.pinned = false;
    }
    public void markComplete() { isDone = true; }
    public boolean isDone() { return isDone; }
    public boolean isOverdue() { return LocalDate.now().isAfter(dueDate) && !isDone; }
    public String getTaskName() { return taskName; }
    public LocalDate getDueDate() { return dueDate; }
    public String getPriority() { return priority; }
    public void setTaskName(String n) { taskName = n; }
    public void setDueDate(LocalDate d) { dueDate = d; }
    public void setPriority(String p) { priority = p; }
    public void setPinned(boolean p) { this.pinned = p; }
    public boolean isPinned() { return pinned; }
    public void setReminderDate(LocalDate d) { reminderDate = d; }
    public LocalDate getReminderDate() { return reminderDate; }
    public void setArchived(boolean a) { archived = a; }
    public boolean isArchived() { return archived; }
    public void addComment(String c) { comments.add(c); }
    public List<String> getComments() { return comments; }
    public String toString() {
        return String.format("%s [%s] - Due: %s, %s", taskName, priority, dueDate, isDone ? "DONE" : (isOverdue()?"OVERDUE":"PENDING"));
    }
}

// Singleton Logger
class Logger {
    private static Logger instance = null;
    private Logger() {}
    public static Logger getInstance() {
        if(instance == null) instance = new Logger();
        return instance;
    }
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

// Templates + Core
class TrackNestSystem {
    private static int userCounter = 1;
    private Map<String, User> users;
    private List<String> habitTemplates;
    private List<GoalTemplate> goalTemplates;
    private List<String> tips;
    private Logger logger = Logger.getInstance();
    private String[] motivationalQuotes = {
        "Success is the sum of small efforts repeated day in and day out.",
        "The secret of getting ahead is getting started.",
        "Don’t watch the clock; do what it does. Keep going.",
        "Action is the foundational key to all success.",
        "One day or day one. You decide."
    };
    private static final String DATA_FILE = "tracknest_data.json";
    private List<String> activityLog = new ArrayList<>();
    private String pendingAnnouncement = null;
    public TrackNestSystem() {
        users = new HashMap<>();
        habitTemplates = new ArrayList<>();
        goalTemplates = new ArrayList<>();
        tips = new ArrayList<>();
    }
    // Motivational quote
    public void showRandomQuote() {
        Random r = new Random();
        System.out.println("Quote of the Day: \"" + motivationalQuotes[r.nextInt(motivationalQuotes.length)] + "\"");
    }
    public void registerUser(String role, String name, String email, String password, String secQ, String secA) {
        if(users.containsKey(email))
            throw new IllegalArgumentException("User already exists!");
        if ("admin".equalsIgnoreCase(role)) {
            for (User u : users.values()) if (u instanceof Admin) throw new IllegalArgumentException("Only one admin allowed!");
        }
        String hash = User.hashPassword(password);
        String secAHash = User.hashPassword(secA);
        User user = "admin".equalsIgnoreCase(role) ? new Admin(userCounter++, name, email, hash, secQ, secAHash) : new NormalUser(userCounter++, name, email, hash, secQ, secAHash);
        users.put(email, user);
        logger.log("New user registered: " + role + " - " + name + " (" + email + ")");
        saveData();
    }
    public User login(String email, String password) {
        if(!users.containsKey(email)) throw new NoSuchElementException("No such user!");
        User user = users.get(email);
        if (!user.isActive()) throw new SecurityException("Account is deactivated. Contact admin.");
        String hash = User.hashPassword(password);
        if (!user.getPasswordHash().equals(hash)) throw new SecurityException("Incorrect password!");
        logActivity("Login: " + email);
        return user;
    }
    public void deleteAccount(User user) {
        UI.prompt("Are you sure you want to delete your account? Type YES to confirm: ");
        if (UI.getNonEmptyInput(new Scanner(System.in)).equals("YES")) {
            users.remove(user.getEmail()); logger.log("Deleted user: "+user.getEmail());
            UI.success("Account deleted.");
            saveData();
        } else {
            UI.info("Account deletion cancelled.");
        }
    }
    public void showAllUsers() {
        System.out.println("All Users:");
        for (User u : users.values()) System.out.println(u.getName() + " (" + u.getEmail() + ")");
    }
    public void broadcastTip(String tip) { tips.add(tip); System.out.println("BROADCASTING: " + tip); }
    public void viewAllTips() {
        System.out.println("--- Productivity Tips log ---");
        for (String t : tips) System.out.println("- " + t);
    }
    public void createHabitTemplate(Scanner sc) {
        System.out.print("Habit template name: ");
        habitTemplates.add(sc.nextLine());
        logger.log("Habit template added.");
    }
    public void instantiateHabitTemplate(User user, Scanner sc) {
        if (habitTemplates.isEmpty()) { System.out.println("No habit templates available."); return; }
        for (int i=0;i<habitTemplates.size();i++)
            System.out.println((i+1)+". "+habitTemplates.get(i));
        System.out.print("Pick template number: ");
        try {
            int idx = Integer.parseInt(sc.nextLine()) - 1;
            if (idx<0 || idx>=habitTemplates.size()) throw new Exception();
            System.out.print("Frequency (Daily/Weekly): ");
            String f = sc.nextLine();
            Habit h = new Habit(habitTemplates.get(idx), f);
            user.getHabits().add(h);
            logger.log("Habit template instantiated.");
        } catch(Exception e) { System.out.println("Invalid selection!"); }
    }
    public void createGoalTemplate(Scanner sc) {
        System.out.print("Goal template title: ");
        String t = sc.nextLine();
        System.out.print("Description: ");
        String d = sc.nextLine();
        System.out.print("Recommended duration in days: ");
        int dur = Integer.parseInt(sc.nextLine());
        goalTemplates.add(new GoalTemplate(t, d, dur));
        logger.log("Goal template created.");
    }
    public void instantiateGoalTemplate(User user, Scanner sc) {
        if (goalTemplates.isEmpty()) { System.out.println("No goal templates."); return; }
        for (int i=0;i<goalTemplates.size();i++)
            System.out.println((i+1)+". "+goalTemplates.get(i));
        System.out.print("Pick template number: ");
        try {
            int idx = Integer.parseInt(sc.nextLine())-1;
            if (idx<0 || idx>=goalTemplates.size()) throw new Exception();
            GoalTemplate gt = goalTemplates.get(idx);
            LocalDate deadline = LocalDate.now().plusDays(gt.recommendedDurationDays);
            Goal g = new Goal(gt.title, gt.desc, deadline);
            user.getGoals().add(g);
            logger.log("Goal template instantiated.");
        } catch(Exception e) { System.out.println("Invalid selection!"); }
    }
    public void addGoal(User user, Scanner sc) {
        System.out.print("Goal title: "); String title = sc.nextLine();
        System.out.print("Description: "); String desc = sc.nextLine();
        LocalDate deadline = null;
        while(true) {
            try { System.out.print("Deadline (YYYY-MM-DD): ");
                deadline = LocalDate.parse(sc.nextLine()); break;
            } catch(Exception e) { System.out.println("Invalid date!"); }
        }
        Goal goal = new Goal(title, desc, deadline);
        user.getGoals().add(goal);
        logger.log("Goal added for " + user.getName());
        saveData();
    }
    public void editDeleteGoal(User user, Scanner sc) {
        if(user.getGoals().isEmpty()) { System.out.println("No goals."); return; }
        int c=1;
        for (Goal g : user.getGoals()) System.out.println((c++)+". "+g);
        UI.prompt("Edit or Delete? (E/D): ");
        String op = UI.getNonEmptyInput(sc).toUpperCase();
        UI.prompt("Pick goal number: ");
        try {
            int idx = UI.getIntInput(sc, 1, user.getGoals().size())-1;
            if (idx<0 || idx>=user.getGoals().size()) throw new Exception();
            if (op.equals("D")) {
                UI.prompt("Are you sure you want to delete this goal? Type YES to confirm: ");
                if (UI.getNonEmptyInput(sc).equals("YES")) {
                    user.getGoals().remove(idx); logger.log("Goal deleted.");
                    UI.success("Goal deleted.");
                    saveData();
                } else {
                    UI.info("Delete cancelled.");
                }
            }
            else {
                Goal g = user.getGoals().get(idx);
                System.out.println("Edit fields: 1.Title 2.Description 3.Deadline 4.Status");
                String f = UI.getNonEmptyInput(sc);
                switch(f) {
                    case "1": UI.prompt("New title: "); g.setTitle(UI.getNonEmptyInput(sc)); break;
                    case "2": UI.prompt("New desc: "); g.setDescription(UI.getNonEmptyInput(sc)); break;
                    case "3": UI.prompt("New deadline YYYY-MM-DD: "); g.setDeadline(UI.getDateInput(sc)); break;
                    case "4": UI.prompt("Set status (Active/Complete): "); g.setStatus(UI.getNonEmptyInput(sc)); break;
                    default: UI.error("Unknown field.");
                }
                UI.success("Goal edited.");
                logger.log("Goal edited.");
                saveData();
            }
        } catch(Exception e) { UI.error("Invalid selection!"); }
    }
    public void addTask(User user, Scanner sc) {
        System.out.print("Task Name: "); String name = sc.nextLine();
        LocalDate due = null;
        while(true) {
            try { System.out.print("Due Date (YYYY-MM-DD): "); due = LocalDate.parse(sc.nextLine()); break; }
            catch(Exception e) { System.out.println("Invalid date!"); }
        }
        System.out.print("Priority (High/Medium/Low): "); String prio = sc.nextLine();
        Task t = new Task(name, due, prio);
        user.getTasks().add(t);
        logger.log("Task added.");
        saveData();
    }
    public void editDeleteTask(User user, Scanner sc) {
        if(user.getTasks().isEmpty()) { System.out.println("No tasks."); return; }
        int c=1; for (Task t : user.getTasks()) System.out.println((c++)+". "+t);
        UI.prompt("Edit or Delete? (E/D): ");
        String op = UI.getNonEmptyInput(sc).toUpperCase();
        UI.prompt("Pick task number: ");
        try {
            int idx = UI.getIntInput(sc, 1, user.getTasks().size())-1;
            if (idx<0 || idx>=user.getTasks().size()) throw new Exception();
            if (op.equals("D")) {
                UI.prompt("Are you sure you want to delete this task? Type YES to confirm: ");
                if (UI.getNonEmptyInput(sc).equals("YES")) {
                    user.getTasks().remove(idx); logger.log("Task deleted.");
                    UI.success("Task deleted.");
                    saveData();
                } else {
                    UI.info("Delete cancelled.");
                }
            }
            else {
                Task t = user.getTasks().get(idx);
                System.out.println("Edit fields: 1.Name 2.DueDate 3.Priority");
                String f = UI.getNonEmptyInput(sc);
                switch(f) {
                    case "1": UI.prompt("New name: "); t.setTaskName(UI.getNonEmptyInput(sc)); break;
                    case "2": UI.prompt("New DueDate YYYY-MM-DD: "); t.setDueDate(UI.getDateInput(sc)); break;
                    case "3": UI.prompt("New priority: "); t.setPriority(UI.getNonEmptyInput(sc)); break;
                    default: UI.error("Unknown field.");
                }
                UI.success("Task edited.");
                logger.log("Task edited.");
                saveData();
            }
        } catch(Exception e) { UI.error("Invalid selection!"); }
    }
    public void addHabit(User user, Scanner sc) {
        System.out.print("Habit name: "); String name = sc.nextLine();
        System.out.print("Frequency (Daily/Weekly): "); String freq = sc.nextLine();
        Habit h = new Habit(name, freq);
        user.getHabits().add(h);
        logger.log("Habit added for " + user.getName());
        saveData();
    }
    public void editDeleteHabit(User user, Scanner sc) {
        if(user.getHabits().isEmpty()) { System.out.println("No habits."); return; }
        int c=1; for (Habit h : user.getHabits()) System.out.println((c++)+". "+h);
        UI.prompt("Edit or Delete? (E/D): ");
        String op = UI.getNonEmptyInput(sc).toUpperCase();
        UI.prompt("Pick habit number: ");
        try {
            int idx = UI.getIntInput(sc, 1, user.getHabits().size())-1;
            if (idx<0 || idx>=user.getHabits().size()) throw new Exception();
            if (op.equals("D")) {
                UI.prompt("Are you sure you want to delete this habit? Type YES to confirm: ");
                if (UI.getNonEmptyInput(sc).equals("YES")) {
                    user.getHabits().remove(idx); logger.log("Habit deleted.");
                    UI.success("Habit deleted.");
                    saveData();
                } else {
                    UI.info("Delete cancelled.");
                }
            }
            else {
                Habit h = user.getHabits().get(idx);
                System.out.println("Edit fields: 1.Name 2.Frequency");
                String f = UI.getNonEmptyInput(sc);
                switch(f) {
                    case "1": UI.prompt("New name: "); h.setName(UI.getNonEmptyInput(sc)); break;
                    case "2": UI.prompt("New Frequency: "); h.setFrequency(UI.getNonEmptyInput(sc)); break;
                    default: UI.error("Unknown field.");
                }
                UI.success("Habit edited.");
                logger.log("Habit edited.");
                saveData();
            }
        } catch(Exception e) { UI.error("Invalid selection!"); }
    }
    public void trackHabit(User user, Scanner sc) {
        List<Habit> habits = user.getHabits();
        if(habits.isEmpty()) { System.out.println("No habits!"); return;}
        System.out.println("Your Habits:"); for(int i=0; i<habits.size(); i++)
            System.out.println((i+1)+". "+habits.get(i));
        System.out.print("Pick habit number to log: ");
        try {
            int idx = Integer.parseInt(sc.nextLine()) - 1;
            if(idx<0 || idx>=habits.size()) throw new Exception();
            Habit h = habits.get(idx);
            h.logToday();
            user.habitHistory.add("Logged "+h.getName()+" on "+LocalDate.now());
            user.addPoints(5);
            if (h.getStreak()==1) user.addBadge("First Habit Logged!");
            if (h.getStreak()>=7) user.addBadge("Weekly Streak Master");
            logger.log("Habit logged. +" + 5 + " pts!");
        } catch(Exception e) { System.out.println("Invalid selection!"); }
    }
    public void logTaskCompletion(User user, Scanner sc) {
        List<Task> tasks = user.getTasks();
        int count=1; boolean found=false;
        for(Task t : tasks) { if (!t.isDone()) {
                System.out.println(count+". "+t); found=true; }
            count++;
        }
        if(!found) { System.out.println("No pending tasks."); return; }
        System.out.print("Pick number to mark task complete: ");
        try {
            int idx = Integer.parseInt(sc.nextLine())-1;
            Task t = tasks.get(idx);
            t.markComplete();
            user.addPoints(10);
            if (tasks.stream().filter(Task::isDone).count() == 1) user.addBadge("First Task Complete");
            user.addRecentCompleted("Task: "+t.getTaskName());
            logger.log("Task marked complete. +10 pts!");
        } catch(Exception e) { System.out.println("Invalid selection!"); }
    }
    public void showStreaks(User user) {
        System.out.println("-- Habit Streaks --");
        for(Habit h : user.getHabits()) {
            System.out.println(h.getName() + ": " + h.getStreak() + " (Last: " + h.getLastLoggedDate() + ")");
            if (h.getStreak() > 0 && h.getStreak() % 7 == 0) {
                System.out.println("🏆 WOW! 7-day streak! +50 pts!");
                user.addPoints(50);
                user.addBadge("Weekly Streak Master");
            }
        }
    }
    public void searchFilterGoals(User user, Scanner sc) {
        System.out.println("Search by: 1.Status 2.Deadline before date");
        String opt = sc.nextLine();
        List<Goal> filtered = new ArrayList<>();
        if (opt.equals("1")) {
            System.out.print("Enter status (Active/Complete/Failed): ");
            String s = sc.nextLine();
            for(Goal g : user.getGoals()) if(g.getStatus().equalsIgnoreCase(s)) filtered.add(g);
        } else if (opt.equals("2")) {
            System.out.print("Enter deadline date (YYYY-MM-DD): ");
            try { LocalDate d = LocalDate.parse(sc.nextLine());
                for(Goal g : user.getGoals()) if(g.getDeadline().isBefore(d)) filtered.add(g);
            } catch(Exception e) { System.out.println("Invalid date!"); return; }
        }
        System.out.println("Filtered goals:");
        for(Goal g : filtered) System.out.println("- "+g);
    }
    public void sortTasks(User user, Scanner sc) {
        System.out.println("Sort by: 1.Due Date 2.Priority");
        String op = sc.nextLine();
        List<Task> sorted = new ArrayList<>(user.getTasks());
        if(op.equals("1")) Collections.sort(sorted, Comparator.comparing(Task::getDueDate));
        else if(op.equals("2")) {
            List<String> prioList = Arrays.asList("High", "Medium", "Low");
            Collections.sort(sorted, Comparator.comparing(a->prioList.indexOf(a.getPriority())));
        }
        System.out.println("--- Sorted Tasks ---");
        for(Task t : sorted) System.out.println(t);
    }
    // Overdue warnings
    public void showOverdueTasks(User user) {
        boolean overdue = false;
        for(Task t : user.getTasks())
            if(t.isOverdue() && !t.isDone()) {
                if (!overdue) {
                    System.out.println("\n** Reminder **: You have overdue tasks!");
                    overdue = true;
                }
                System.out.println("- "+t);
            }
    }
    // Auto-close past due goals
    public void autoCloseOverdueGoals(User user) {
        for (Goal g : user.getGoals()) {
            if (!g.checkCompletion() && g.getDeadline().plusDays(3).isBefore(LocalDate.now())
                && !"Failed".equalsIgnoreCase(g.getStatus())) {
                g.setStatus("Failed");
                System.out.println("Goal auto-set as Failed (overdue 3+ days): " + g.getTitle());
            }
        }
    }
    public void saveData() {
        try (PrintWriter out = new PrintWriter(new FileWriter(DATA_FILE))) {
            out.println("[");
            int uCount = 0;
            for (User u : users.values()) {
                if (uCount++ > 0) out.println(",");
                out.print("  {\"id\":"+u.getId()+",\"name\":\""+u.getName()+"\",\"email\":\""+u.getEmail()+"\",\"role\":\"");
                out.print((u instanceof Admin) ? "admin" : "user");
                out.print("\",\"passwordHash\":\""+u.getPasswordHash()+"\"");
                out.print(",\"securityQuestion\":\""+u.getSecurityQuestion()+"\"");
                out.print(",\"securityAnswerHash\":\""+u.getSecurityAnswerHash()+"\"");
                out.print(",\"registrationDate\":\""+u.getRegistrationDate()+"\"");
                out.print(",\"goals\":[");
                for (int i=0;i<u.getGoals().size();i++) {
                    Goal g = u.getGoals().get(i);
                    if (i>0) out.print(",");
                    out.print("{\"title\":\""+g.getTitle()+"\",\"desc\":\""+g.getDescription()+"\",\"deadline\":\""+g.getDeadline()+"\",\"status\":\""+g.getStatus()+"\",\"pinned\":"+g.isPinned()+",\"note\":\""+(g.getNote()==null?"":g.getNote())+"\",\"reminderDate\":\""+(g.getReminderDate()==null?"":g.getReminderDate())+"\",\"archived\":"+g.isArchived()+"}");
                }
                out.print("],\"habits\":[");
                for (int i=0;i<u.getHabits().size();i++) {
                    Habit h = u.getHabits().get(i);
                    if (i>0) out.print(",");
                    out.print("{\"name\":\""+h.getName()+"\",\"freq\":\""+h.getFrequency()+"\",\"streak\":"+h.getStreak()+",\"last\":\""+(h.getLastLoggedDate()==null?"":h.getLastLoggedDate())+"\",\"pinned\":"+h.isPinned()+",\"note\":\""+(h.getNote()==null?"":h.getNote())+"\",\"reminderDate\":\""+(h.getReminderDate()==null?"":h.getReminderDate())+"\",\"archived\":"+h.isArchived()+"}");
                }
                out.print("],\"tasks\":[");
                for (int i=0;i<u.getTasks().size();i++) {
                    Task t = u.getTasks().get(i);
                    if (i>0) out.print(",");
                    out.print("{\"name\":\""+t.getTaskName()+"\",\"due\":\""+t.getDueDate()+"\",\"prio\":\""+t.getPriority()+"\",\"done\":"+t.isDone()+",\"pinned\":"+t.isPinned()+",\"reminderDate\":\""+(t.getReminderDate()==null?"":t.getReminderDate())+"\",\"archived\":"+t.isArchived()+"}");
                }
                out.print("],\"badges\":[");
                int b=0; for (String badge : u.badges) { if (b++>0) out.print(","); out.print("\""+badge+"\""); }
                out.print("],\"points\":"+u.getRewardPoints());
                out.print(",\"theme\":\""+u.getTheme()+"\"");
                out.print(",\"friendEmail\":\""+u.getFriendEmail()+"\"");
                out.print(",\"active\":"+u.isActive());
                out.print("}");
            }
            out.println();
            out.println("]");
        } catch(Exception e) { System.out.println("[Error] Could not save data: "+e.getMessage()); }
    }
    public void loadData() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (Scanner sc = new Scanner(f)) {
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) sb.append(sc.nextLine());
            String json = sb.toString();
            json = json.replaceAll("\\s+", "");
            if (!json.startsWith("[")) return;
            String[] userBlocks = json.substring(1, json.length()-1).split("},");
            for (String userBlock : userBlocks) {
                if (!userBlock.endsWith("}")) userBlock += "}";
                int id = Integer.parseInt(userBlock.replaceAll(".*\"id\":(\\d+).*", "$1"));
                String name = userBlock.replaceAll(".*\"name\":\"(.*?)\".*", "$1");
                String email = userBlock.replaceAll(".*\"email\":\"(.*?)\".*", "$1");
                String role = userBlock.replaceAll(".*\"role\":\"(.*?)\".*", "$1");
                String passwordHash = userBlock.replaceAll(".*\"passwordHash\":\"(.*?)\".*", "$1");
                String secQ = userBlock.replaceAll(".*\"securityQuestion\":\"(.*?)\".*", "$1");
                String secAHash = userBlock.replaceAll(".*\"securityAnswerHash\":\"(.*?)\".*", "$1");
                String regDateStr = userBlock.replaceAll(".*\"registrationDate\":\"(.*?)\".*", "$1");
                User u = "admin".equals(role) ? new Admin(id, name, email, passwordHash, secQ, secAHash) : new NormalUser(id, name, email, passwordHash, secQ, secAHash);
                if (regDateStr != null && !regDateStr.isEmpty() && !regDateStr.equals("registrationDate")) {
                    try { u.setRegistrationDate(LocalDate.parse(regDateStr)); } catch(Exception ignore){}
                }
                // Points
                try { u.rewardPoints = Integer.parseInt(userBlock.replaceAll(".*\"points\":(\\d+).*", "$1")); } catch(Exception ignore){}
                // Theme
                try { u.theme = userBlock.replaceAll(".*\"theme\":\"(.*?)\".*", "$1"); } catch(Exception ignore){}
                // Badges
                if (userBlock.contains("\"badges\":[")) {
                    String badgeStr = userBlock.split("\"badges\":\\[")[1].split("]",2)[0];
                    if (!badgeStr.isEmpty()) for (String badge : badgeStr.split(",")) u.badges.add(badge.replaceAll("\"", ""));
                }
                // Profile Description
                try { u.setProfileDescription(userBlock.replaceAll(".*\"profileDescription\":\"(.*?)\".*", "$1")); } catch(Exception ignore){}
                // Avatar
                try { u.setAvatar(userBlock.replaceAll(".*\"avatar\":\"(.*?)\".*", "$1")); } catch(Exception ignore){}
                // Reminder Frequency
                try { u.setReminderFrequency(userBlock.replaceAll(".*\"reminderFrequency\":\"(.*?)\".*", "$1")); } catch(Exception ignore){}
                // Language
                try { u.setLanguage(userBlock.replaceAll(".*\"language\":\"(.*?)\".*", "$1")); } catch(Exception ignore){}
                // Accessibility Mode
                try { u.setAccessibilityMode(userBlock.contains("\"accessibilityMode\":true")); } catch(Exception ignore){}
                // Friend Email
                try { u.setFriendEmail(userBlock.replaceAll(".*\"friendEmail\":\"(.*?)\".*", "$1")); } catch(Exception ignore){}
                // Active Status
                try { u.setActive(userBlock.contains("\"active\":true")); } catch(Exception ignore){}
                // Goals
                if (userBlock.contains("\"goals\":[")) {
                    String goalsStr = userBlock.split("\"goals\":\\[")[1].split("]",2)[0];
                    if (!goalsStr.isEmpty()) for (String gBlock : goalsStr.split("},")) {
                        if (!gBlock.endsWith("}")) gBlock += "}";
                        String title = gBlock.replaceAll(".*\"title\":\"(.*?)\".*", "$1");
                        String desc = gBlock.replaceAll(".*\"desc\":\"(.*?)\".*", "$1");
                        String deadline = gBlock.replaceAll(".*\"deadline\":\"(.*?)\".*", "$1");
                        String status = gBlock.replaceAll(".*\"status\":\"(.*?)\".*", "$1");
                        boolean pinned = gBlock.contains("\"pinned\":true");
                        String note = gBlock.replaceAll(".*\"note\":\"(.*?)\".*", "$1");
                        LocalDate reminderDate = gBlock.contains("\"reminderDate\":\"") ? LocalDate.parse(gBlock.replaceAll(".*\"reminderDate\":\"(.*?)\".*", "$1")) : null;
                        boolean archived = gBlock.contains("\"archived\":true");
                        Goal goal = new Goal(title, desc, LocalDate.parse(deadline));
                        goal.setStatus(status); goal.setPinned(pinned); goal.setNote(note);
                        goal.setReminderDate(reminderDate); goal.setArchived(archived);
                        u.goals.add(goal);
                    }
                }
                // Habits
                if (userBlock.contains("\"habits\":[")) {
                    String habitsStr = userBlock.split("\"habits\":\\[")[1].split("]",2)[0];
                    if (!habitsStr.isEmpty()) for (String hBlock : habitsStr.split("},")) {
                        if (!hBlock.endsWith("}")) hBlock += "}";
                        String nameH = hBlock.replaceAll(".*\"name\":\"(.*?)\".*", "$1");
                        String freq = hBlock.replaceAll(".*\"freq\":\"(.*?)\".*", "$1");
                        int streak = Integer.parseInt(hBlock.replaceAll(".*\"streak\":(\\d+).*", "$1"));
                        String last = hBlock.replaceAll(".*\"last\":\"(.*?)\".*", "$1");
                        boolean pinned = hBlock.contains("\"pinned\":true");
                        String note = hBlock.replaceAll(".*\"note\":\"(.*?)\".*", "$1");
                        LocalDate reminderDate = hBlock.contains("\"reminderDate\":\"") ? LocalDate.parse(hBlock.replaceAll(".*\"reminderDate\":\"(.*?)\".*", "$1")) : null;
                        boolean archived = hBlock.contains("\"archived\":true");
                        Habit habit = new Habit(nameH, freq); habit.setPinned(pinned); habit.setNote(note);
                        habit.setStreak(streak);
                        if (!last.isEmpty()) habit.setLastLoggedDate(LocalDate.parse(last));
                        habit.setReminderDate(reminderDate); habit.setArchived(archived);
                        u.habits.add(habit);
                    }
                }
                // Tasks
                if (userBlock.contains("\"tasks\":[")) {
                    String tasksStr = userBlock.split("\"tasks\":\\[")[1].split("]",2)[0];
                    if (!tasksStr.isEmpty()) for (String tBlock : tasksStr.split("},")) {
                        if (!tBlock.endsWith("}")) tBlock += "}";
                        String nameT = tBlock.replaceAll(".*\"name\":\"(.*?)\".*", "$1");
                        String due = tBlock.replaceAll(".*\"due\":\"(.*?)\".*", "$1");
                        String prio = tBlock.replaceAll(".*\"prio\":\"(.*?)\".*", "$1");
                        boolean done = tBlock.contains("\"done\":true");
                        boolean pinned = tBlock.contains("\"pinned\":true");
                        LocalDate reminderDate = tBlock.contains("\"reminderDate\":\"") ? LocalDate.parse(tBlock.replaceAll(".*\"reminderDate\":\"(.*?)\".*", "$1")) : null;
                        boolean archived = tBlock.contains("\"archived\":true");
                        Task task = new Task(nameT, LocalDate.parse(due), prio); task.setPinned(pinned);
                        if (done) task.markComplete();
                        task.setReminderDate(reminderDate); task.setArchived(archived);
                        u.tasks.add(task);
                    }
                }
                users.put(email, u);
            }
        } catch(Exception e) { System.out.println("[Error] Could not load data: "+e.getMessage()); }
    }
    public boolean hasUser(String email) {
        return users.containsKey(email);
    }
    public void showSystemStats() {
        int totalUsers = users.size();
        int totalAdmins = (int) users.values().stream().filter(u -> u instanceof Admin).count();
        int totalGoals = users.values().stream().mapToInt(u -> u.getGoals().size()).sum();
        int totalHabits = users.values().stream().mapToInt(u -> u.getHabits().size()).sum();
        int totalTasks = users.values().stream().mapToInt(u -> u.getTasks().size()).sum();
        UI.section("System Stats");
        System.out.println("Total Users: " + totalUsers);
        System.out.println("Total Admins: " + totalAdmins);
        System.out.println("Total Goals: " + totalGoals);
        System.out.println("Total Habits: " + totalHabits);
        System.out.println("Total Tasks: " + totalTasks);
    }
    public void changePassword(User user, Scanner sc) {
        UI.prompt("Enter current password: ");
        String current = UI.getNonEmptyInput(sc);
        if (!user.getPasswordHash().equals(User.hashPassword(current))) {
            UI.error("Incorrect current password!");
            return;
        }
        UI.prompt("Enter new password: ");
        String newPass = UI.getNonEmptyInput(sc);
        user.setPasswordHash(User.hashPassword(newPass));
        UI.success("Password changed successfully.");
        saveData();
    }
    public boolean resetPassword(String email, Scanner sc) {
        if (!users.containsKey(email)) { UI.error("No such user!"); return false; }
        User user = users.get(email);
        UI.section("Password Reset");
        System.out.println("Security Question: " + user.getSecurityQuestion());
        UI.prompt("Answer: ");
        String ans = UI.getNonEmptyInput(sc);
        if (!user.getSecurityAnswerHash().equals(User.hashPassword(ans))) {
            UI.error("Incorrect answer!");
            return false;
        }
        UI.prompt("Enter new password: ");
        String newPass = UI.getNonEmptyInput(sc);
        user.setPasswordHash(User.hashPassword(newPass));
        UI.success("Password reset successful.");
        saveData();
        return true;
    }
    public void logActivity(String msg) {
        activityLog.add(LocalDateTime.now() + ": " + msg);
    }
    public void viewActivityLog() {
        UI.section("User Activity Log");
        for (String entry : activityLog) System.out.println(entry);
    }
    public void deactivateUser(Scanner sc) {
        showAllUsers();
        UI.prompt("Enter email to deactivate/reactivate: ");
        String email = sc.nextLine();
        User u = users.get(email);
        if (u == null) { UI.error("No such user!"); return; }
        u.setActive(!u.isActive());
        UI.info("User " + email + (u.isActive() ? " reactivated." : " deactivated."));
        logActivity((u.isActive() ? "Reactivated" : "Deactivated") + " user: " + email);
        saveData();
    }
    public void broadcastAnnouncement(Scanner sc) {
        UI.prompt("Enter announcement: ");
        pendingAnnouncement = sc.nextLine();
        UI.success("Announcement will be shown to all users on next login.");
        logActivity("Broadcast announcement: " + pendingAnnouncement);
    }
    public String consumeAnnouncement() {
        String msg = pendingAnnouncement;
        pendingAnnouncement = null;
        return msg;
    }
    public void exportAllDataCSV() {
        try (PrintWriter out = new PrintWriter(new FileWriter("tracknest_export.csv"))) {
            out.println("Email,Name,Role,Active,Goals,Habits,Tasks,Points,RegistrationDate");
            for (User u : users.values()) {
                out.print(u.getEmail()+","+u.getName()+","+(u instanceof Admin?"Admin":"User")+","+u.isActive()+","+u.getGoals().size()+","+u.getHabits().size()+","+u.getTasks().size()+","+u.getRewardPoints()+","+u.getRegistrationDate());
                out.println();
            }
            UI.success("All data exported to tracknest_export.csv");
            logActivity("Exported all data to CSV");
        } catch (Exception e) {
            UI.error("Failed to export CSV: " + e.getMessage());
        }
    }
}

// Goal template
class GoalTemplate {
    String title;
    String desc;
    int recommendedDurationDays;
    public GoalTemplate(String t, String d, int dur) {
        title = t; desc = d; recommendedDurationDays = dur;
    }
    public String toString() {
        return String.format("%s (%s) [%d days]", title, desc, recommendedDurationDays);
    }
}

// --- UI Helper Class ---
class UI {
    // ANSI escape codes for color (works on most terminals)
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
    // Dark theme colors
    public static final String DARK_BG = "\u001B[40m";
    public static final String LIGHT_BG = "\u001B[47m";
    private static String theme = "Light";
    private static boolean accessibilityMode = false;
    private static String language = "EN";
    static {
        // Obfuscated credit string
        String[] parts = {"Created", "by", "Deepta", "Roy"};
        StringBuilder sb = new StringBuilder();
        for (String p : parts) sb.append(p).append(" ");
        System.out.println(themed(CYAN + sb.toString().trim() + RESET));
    }
    public static void setTheme(String th) { theme = th; }
    public static String getTheme() { return theme; }
    public static void setAccessibilityMode(boolean mode) { accessibilityMode = mode; }
    public static boolean isAccessibilityMode() { return accessibilityMode; }
    public static void setLanguage(String lang) { language = lang; }
    public static String getLanguage() { return language; }
    public static void previewTheme(String theme) {
        System.out.println("Previewing theme: " + theme);
        if ("Dark".equalsIgnoreCase(theme)) {
            System.out.println("[Dark background, light text]");
        } else {
            System.out.println("[Light background, dark text]");
        }
    }
    private static String themed(String s) {
        if ("Dark".equalsIgnoreCase(theme)) return DARK_BG + s + RESET;
        return s;
    }
    public static void header(String text) {
        System.out.println(themed(BLUE + "\n==== " + text + " ====\n" + RESET));
    }
    public static void section(String text) {
        System.out.println(themed(CYAN + "--- " + text + " ---" + RESET));
    }
    public static void error(String text) {
        System.out.println(themed(RED + "[Error] " + text + RESET));
    }
    public static void success(String text) {
        System.out.println(themed(GREEN + text + RESET));
    }
    public static void info(String text) {
        System.out.println(themed(YELLOW + text + RESET));
    }
    public static void prompt(String text) {
        System.out.print(themed(BOLD + text + RESET));
    }
    public static void printMenu(List<String> options) {
        for (int i = 0; i < options.size(); i++) {
            System.out.printf(themed("  %2d. %s\n"), i + 1, options.get(i));
        }
    }
    public static int getIntInput(Scanner sc, int min, int max) {
        while (true) {
            String input = sc.nextLine();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) return val;
            } catch (Exception ignored) {}
            error("Please enter a number between " + min + " and " + max + ".");
            prompt("Try again: ");
        }
    }
    public static String getNonEmptyInput(Scanner sc) {
        while (true) {
            String input = sc.nextLine();
            if (!input.trim().isEmpty()) return input.trim();
            error("Input cannot be empty. Try again:");
        }
    }
    public static LocalDate getDateInput(Scanner sc) {
        while (true) {
            String input = sc.nextLine();
            try {
                return LocalDate.parse(input);
            } catch (Exception e) {
                error("Invalid date format. Use YYYY-MM-DD:");
                prompt("");
            }
        }
    }
    public static int menuSelect(Scanner sc, String headerText, List<String> options) {
        header(headerText);
        printMenu(options);
        prompt("Select option: ");
        return getIntInput(sc, 1, options.size());
    }
    public static void showLogo() {
        System.out.println(themed(BLUE + BOLD + "\n   T N   \n" + RESET));
    }
    public static void printCredit() {
        String[] parts = {"Created", "by", "Deepta", "Roy"};
        StringBuilder sb = new StringBuilder();
        for (String p : parts) sb.append(p).append(" ");
        System.out.println(themed(CYAN + sb.toString().trim() + RESET));
    }
}

// Main
public class TrackNestApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TrackNestSystem system = new TrackNestSystem();
        system.loadData();
        if (!system.hasUser("admin@nest.com")) {
            System.out.print("Set admin password: ");
            String adminPass = sc.nextLine();
            System.out.print("Set admin security question: ");
            String adminQ = sc.nextLine();
            System.out.print("Set admin security answer: ");
            String adminA = sc.nextLine();
            system.registerUser("admin", "Admin", "admin@nest.com", adminPass, adminQ, adminA);
        }
        while (true) {
            UI.showLogo();
            List<String> mainMenu = Arrays.asList("Register", "Login", "Forgot Password", "Help", "Exit");
            int ch = UI.menuSelect(sc, "TrackNest", mainMenu);
            try {
                switch (ch) {
                    case 1:
                        UI.section("Register New User");
                        UI.prompt("Name: "); String name = UI.getNonEmptyInput(sc);
                        UI.prompt("Email: "); String email = UI.getNonEmptyInput(sc);
                        UI.prompt("Role (admin/user): "); String role = UI.getNonEmptyInput(sc);
                        UI.prompt("Password: "); String password = UI.getNonEmptyInput(sc);
                        UI.prompt("Security question: "); String secQ = UI.getNonEmptyInput(sc);
                        UI.prompt("Security answer: "); String secA = UI.getNonEmptyInput(sc);
                        system.registerUser(role, name, email, password, secQ, secA);
                        UI.success("Registered! You can now login."); break;
                    case 2:
                        UI.section("Login");
                        UI.prompt("Email: "); String loginEmail = UI.getNonEmptyInput(sc);
                        UI.prompt("Password: "); String loginPassword = UI.getNonEmptyInput(sc);
                        User user = system.login(loginEmail, loginPassword);
                        String announcement = system.consumeAnnouncement();
                        if (announcement != null) {
                            UI.section("Announcement");
                            System.out.println(announcement);
                        }
                        system.showRandomQuote();
                        if (user.lastLoginDate != null)
                            UI.info("Welcome back! Last login: "+user.lastLoginDate);
                        user.lastLoginDate = LocalDate.now();
                        UI.success("Welcome, " + user.getName() + " ("+user.getRewardPoints()+" pts)!");
                        // --- DASHBOARD ---
                        UI.section("Your Dashboard");
                        System.out.println("Points: " + user.getRewardPoints());
                        System.out.println("Badges: " + (user.badges.isEmpty() ? "None yet." : user.badges));
                        long streaks = user.getHabits().stream().mapToInt(Habit::getStreak).max().orElse(0);
                        System.out.println("Longest Habit Streak: " + streaks);
                        long overdue = user.getTasks().stream().filter(t -> t.isOverdue() && !t.isDone()).count();
                        System.out.println("Overdue Tasks: " + overdue);
                        System.out.println("Pinned Goals: " + user.getGoals().stream().filter(Goal::isPinned).count());
                        System.out.println("Pinned Tasks: " + user.getTasks().stream().filter(Task::isPinned).count());
                        System.out.println("----------------------");
                        user.showMenu(sc, system);
                        break;
                    case 3:
                        UI.section("Forgot Password");
                        UI.prompt("Email: "); String forgotEmail = UI.getNonEmptyInput(sc);
                        system.resetPassword(forgotEmail, sc);
                        break;
                    case 4:
                        UI.section("Help");
                        System.out.println("- Register: Create a new account (admin or user)\n- Login: Access your account\n- Forgot Password: Reset your password using your security question.\n- Exit: Quit the app\nNavigate menus by entering the number next to your choice.\nYou can type 'back' at most prompts to return to the previous menu.");
                        break;
                    case 5:
                        UI.info("Goodbye!");
                        return;
                }
            } catch(Exception e) {
                UI.error(e.getMessage());
            }
            UI.printCredit();
        }
    }
}
