import java.util.ArrayList;
import java.util.Scanner;

public class AttendanceCalculator {

    static class StudentAttendance {
        String name;
        String subject;
        int    totalClasses;
        int    attendedClasses;
        int    absences;
        int    tardiness;       // partial credit: 2 tardies = 1 absent

        public StudentAttendance(String name, String subject,
                                  int totalClasses, int attended, int tardiness) {
            this.name           = name;
            this.subject        = subject;
            this.totalClasses   = totalClasses;
            this.attendedClasses = attended;
            this.tardiness      = tardiness;
            // Adjust: 2 tardies = 1 absent
            double effectiveAbsences = (totalClasses - attended) + (tardiness / 2.0);
            this.absences       = totalClasses - attended;
        }

        public double getRawPercentage() {
            if (totalClasses == 0) return 0;
            return (attendedClasses * 100.0) / totalClasses;
        }

        public double getAdjustedPercentage() {
            if (totalClasses == 0) return 0;
            double effectiveAttended = attendedClasses - (tardiness / 2.0);
            double pct = (effectiveAttended / totalClasses) * 100.0;
            return Math.max(pct, 0);
        }

        public String getRemarks() {
            double pct = getAdjustedPercentage();
            if (pct >= 98) return "Perfect / Near-Perfect Attendance";
            if (pct >= 95) return "Excellent Attendance";
            if (pct >= 90) return "Very Good Attendance";
            if (pct >= 85) return "Good Attendance";
            if (pct >= 80) return "Satisfactory";
            if (pct >= 75) return "Minimum Requirement Met ⚠";
            return "FAILED – Below Required Attendance ❌";
        }

        public String getStatus() {
            return getAdjustedPercentage() >= 75 ? "PASSED ✅" : "FAILED ❌";
        }

        public String getProgressBar(double pct) {
            int filled = (int)(pct / 5);
            filled = Math.min(filled, 20);
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < 20; i++) bar.append(i < filled ? "█" : "░");
            bar.append("]");
            return bar.toString();
        }
    }

    static ArrayList<StudentAttendance> records = new ArrayList<>();

    public static void computeAttendance(Scanner scanner) {
        System.out.println("\n  ── New Attendance Record ──");
        System.out.print("  Student Name   : ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("  Name required."); return; }

        System.out.print("  Subject        : ");
        String subject = scanner.nextLine().trim();
        if (subject.isEmpty()) subject = "General";

        System.out.print("  Total Classes  : ");
        int total;
        try {
            total = Integer.parseInt(scanner.nextLine().trim());
            if (total <= 0) { System.out.println("  Must be greater than 0."); return; }
        } catch (NumberFormatException e) { System.out.println("  Invalid number."); return; }

        System.out.print("  Classes Attended: ");
        int attended;
        try {
            attended = Integer.parseInt(scanner.nextLine().trim());
            if (attended < 0 || attended > total) {
                System.out.println("  Attended must be 0 to " + total + ".");
                return;
            }
        } catch (NumberFormatException e) { System.out.println("  Invalid number."); return; }

        System.out.print("  Times Tardy (late arrivals): ");
        int tardy;
        try {
            tardy = Integer.parseInt(scanner.nextLine().trim());
            if (tardy < 0) { System.out.println("  Cannot be negative."); return; }
        } catch (NumberFormatException e) { tardy = 0; }

        StudentAttendance sa = new StudentAttendance(name, subject, total, attended, tardy);
        records.add(sa);
        displayResult(sa);
    }

    public static void displayResult(StudentAttendance sa) {
        double rawPct = sa.getRawPercentage();
        double adjPct = sa.getAdjustedPercentage();

        System.out.println("\n  ╔═══════════════════════════════════════════════════╗");
        System.out.println("  ║          📊 ATTENDANCE REPORT CARD               ║");
        System.out.println("  ╠═══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Student : %-38s║%n", sa.name);
        System.out.printf("  ║  Subject : %-38s║%n", sa.subject);
        System.out.println("  ╠═══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Total Classes    : %-29d║%n", sa.totalClasses);
        System.out.printf("  ║  Classes Attended : %-29d║%n", sa.attendedClasses);
        System.out.printf("  ║  Absences         : %-29d║%n", sa.absences);
        System.out.printf("  ║  Times Tardy      : %-29d║%n", sa.tardiness);
        System.out.println("  ╠═══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Raw Attendance   : %6.2f%%%22s║%n", rawPct, "");
        System.out.printf("  ║  Adjusted (tardy) : %6.2f%%%22s║%n", adjPct, "");
        System.out.printf("  ║  Progress : %s %5.1f%%%n", sa.getProgressBar(adjPct), adjPct);
        System.out.println("  ╠═══════════════════════════════════════════════════╣");
        System.out.printf("  ║  Remarks : %-38s║%n", sa.getRemarks());
        System.out.printf("  ║  Status  : %-38s║%n", sa.getStatus());
        System.out.println("  ╚═══════════════════════════════════════════════════╝");

        // Advice
        if (adjPct < 75) {
            int needed = (int)Math.ceil((0.75 * sa.totalClasses) - sa.attendedClasses);
            System.out.printf("  ⚠ Needs %d more class attendance to reach 75%% requirement.%n", Math.max(needed, 0));
        } else if (adjPct < 80) {
            System.out.println("  ℹ️  Close to the minimum — try not to miss any more classes.");
        } else {
            System.out.println("  👏 Great job keeping up with attendance!");
        }
    }

    public static void viewAllRecords() {
        if (records.isEmpty()) { System.out.println("\n  No records yet."); return; }

        System.out.println("\n  ╔════╦══════════════════╦══════════════╦════════╦═════════╦══════════╗");
        System.out.println("  ║ No ║ Student          ║ Subject      ║ Total  ║ Attended║  Pct.   ║");
        System.out.println("  ╠════╬══════════════════╬══════════════╬════════╬═════════╬══════════╣");

        double sumPct = 0;
        for (int i = 0; i < records.size(); i++) {
            StudentAttendance sa = records.get(i);
            double pct = sa.getAdjustedPercentage();
            sumPct += pct;
            System.out.printf("  ║ %-2d ║ %-16s ║ %-12s ║  %3d   ║   %3d   ║ %6.2f%% ║%n",
                    i + 1, sa.name, sa.subject, sa.totalClasses, sa.attendedClasses, pct);
        }
        System.out.println("  ╚════╩══════════════════╩══════════════╩════════╩═════════╩══════════╝");
        System.out.printf("  Class Average Attendance: %.2f%%%n", sumPct / records.size());
    }

    public static void viewStudentReport(Scanner scanner) {
        System.out.print("\n  Enter student name: ");
        String query = scanner.nextLine().trim();
        boolean found = false;
        for (StudentAttendance sa : records) {
            if (sa.name.equalsIgnoreCase(query)) {
                displayResult(sa);
                found = true;
            }
        }
        if (!found) System.out.println("  No record found for: '" + query + "'");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║    🎓 STUDENT ATTENDANCE PERCENTAGE CALCULATOR    ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  Formula: (Attended / Total) × 100               ║");
        System.out.println("║  Note: 2 tardies = 1 absence deduction           ║");
        System.out.println("║  Passing Requirement: 75% attendance             ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println("\n  ┌──────────────────────────────────┐");
            System.out.println("  │             MENU                 │");
            System.out.println("  ├──────────────────────────────────┤");
            System.out.println("  │  1. Compute Student Attendance   │");
            System.out.println("  │  2. View All Records             │");
            System.out.println("  │  3. View Student Report          │");
            System.out.println("  │  4. Exit                         │");
            System.out.println("  └──────────────────────────────────┘");
            System.out.print("  Choice: ");
            switch (scanner.nextLine().trim()) {
                case "1": computeAttendance(scanner); break;
                case "2": viewAllRecords(); break;
                case "3": viewStudentReport(scanner); break;
                case "4": running = false; System.out.println("\n  Attendance matters! Keep showing up! 📚"); break;
                default:  System.out.println("  Invalid choice.");
            }
        }
        scanner.close();
    }
}
