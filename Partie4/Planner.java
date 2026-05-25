package partie4;

import java.util.*;

/**
 * Centralized Planner for Distributed Plans
 * 
 * Initial state: on(A,B) on(B,T) on(C,D) on(D,T) on(E,F) on(F,T)
 *                clear(A) clear(C) clear(E)
 * 
 * Goal: on(B,A) on(F,D) on(E,T)
 * 
 * Generated plan (6 actions):
 *  S1: move(B,T,A)
 *  S2: move(A,B,E)
 *  S3: movetable(E,F)
 *  S4: move(F,T,D)
 *  S5: move(D,T,C)
 *  S6: movetable(C,D)
 *
 * Partial ordering:
 *  S3 < S2 < S1   (Subplan1 → Agent1/Tom)
 *  S6 < S5 < S4   (Subplan2 → Agent2/Bill)
 *  S3 < S4         (synchronization: send/wait clear(F))
 */
public class Planner {

    public static State buildInitialState() {
        State s = new State();
        s.add("on(A,B)"); s.add("on(B,T)"); s.add("on(C,D)");
        s.add("on(D,T)"); s.add("on(E,F)"); s.add("on(F,T)");
        s.add("clear(A)"); s.add("clear(C)"); s.add("clear(E)");
        return s;
    }

    public static Set<String> buildGoal() {
        Set<String> goal = new LinkedHashSet<>();
        goal.add("on(B,A)");
        goal.add("on(F,D)");
        goal.add("on(E,T)");
        return goal;
    }

    /**
     * Build the 6 actions exactly as in the slide.
     * move(b,x,y): Precond on(b,x)∧clear(b)∧clear(y), Postcond on(b,y)∧clear(x)∧¬on(b,x)∧¬clear(y)
     * movetable(b,x): Precond on(b,x)∧clear(b), Postcond on(b,T)∧clear(x)∧¬on(b,x)
     */
    public static List<Action> generatePlan() {
        List<Action> plan = new ArrayList<>();

        // S3: movetable(E,F) — must go first (frees clear(F) for S4, frees E for S2)
        Action s3 = new Action("S3: movetable(E,F)",
            setOf("on(E,F)", "clear(E)"),
            setOf("on(E,T)", "clear(F)"),
            setOf("on(E,F)"));
        s3.setAssignedAgent("Agent1");
        plan.add(s3);

        // S2: move(A,B,E) — A must be moved to free B
        Action s2 = new Action("S2: move(A,B,E)",
            setOf("on(A,B)", "clear(A)", "clear(E)"),
            setOf("on(A,E)", "clear(B)"),
            setOf("on(A,B)", "clear(E)"));
        s2.setAssignedAgent("Agent1");
        plan.add(s2);

        // S1: move(B,T,A) — goal on(B,A)
        Action s1 = new Action("S1: move(B,T,A)",
            setOf("on(B,T)", "clear(B)", "clear(A)"),
            setOf("on(B,A)"),
            setOf("on(B,T)", "clear(A)"));
        s1.setAssignedAgent("Agent1");
        plan.add(s1);

        // S6: movetable(C,D) — frees D for S4
        Action s6 = new Action("S6: movetable(C,D)",
            setOf("on(C,D)", "clear(C)"),
            setOf("on(C,T)", "clear(D)"),
            setOf("on(C,D)"));
        s6.setAssignedAgent("Agent2");
        plan.add(s6);

        // S5: move(D,T,C) — moves D (needed as base for F)
        Action s5 = new Action("S5: move(D,T,C)",
            setOf("on(D,T)", "clear(D)", "clear(C)"),
            setOf("on(D,C)", "clear(T)"),
            setOf("on(D,T)", "clear(C)"));
        s5.setAssignedAgent("Agent2");
        plan.add(s5);

        // S4: move(F,T,D) — goal on(F,D), needs clear(F) from S3
        Action s4 = new Action("S4: move(F,T,D)",
            setOf("on(F,T)", "clear(F)", "clear(D)"),
            setOf("on(F,D)"),
            setOf("on(F,T)", "clear(D)"));
        s4.setAssignedAgent("Agent2");
        plan.add(s4);

        return plan;
    }

    /**
     * Returns the partial ordering constraints as strings for display.
     * Ordering:
     *   S2 < S1 (satisfy precond of S1)
     *   S3 < S2 (satisfy precond of S2: clear(E))
     *   S6 < S5 (satisfy precond of S5: clear(C) — wait, S6 frees D actually)
     *   S3 < S4 (S3 produces clear(F) needed by S4) ← SYNCHRONIZATION POINT
     *   S5 < S4 (S5 produces clear(D) needed by S4... wait S6 frees D)
     *   Slide says: S6<S5<S4 and S3<S2<S1 and S3<S4
     */
    public static List<String> getPartialOrderConstraints() {
        List<String> constraints = new ArrayList<>();
        constraints.add("S3 < S2 < S1  (Subplan1 - Agent1/Tom)");
        constraints.add("S6 < S5 < S4  (Subplan2 - Agent2/Bill)");
        constraints.add("S3 < S4        (Synchronization: Agent1 sends clear(F) → Agent2 waits)");
        return constraints;
    }

    private static Set<String> setOf(String... items) {
        return new HashSet<>(Arrays.asList(items));
    }
}

