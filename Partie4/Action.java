package partie4;

import java.util.Set;
import java.util.HashSet;

public class Action {
    private String name;
    private Set<String> preconditions;
    private Set<String> addEffects;    // positive postconditions
    private Set<String> deleteEffects; // negative postconditions
    private String assignedAgent;      // Tom or Bill

    public Action(String name, Set<String> preconditions,
                  Set<String> addEffects, Set<String> deleteEffects) {
        this.name = name;
        this.preconditions = preconditions;
        this.addEffects = addEffects;
        this.deleteEffects = deleteEffects;
    }

    public boolean isApplicable(State state) {
        return state.containsAll(preconditions);
    }

    public void apply(State state) {
        for (String del : deleteEffects) state.remove(del);
        for (String add : addEffects) state.add(add);
    }

    public String getName() { return name; }
    public Set<String> getPreconditions() { return preconditions; }
    public Set<String> getAddEffects() { return addEffects; }
    public Set<String> getDeleteEffects() { return deleteEffects; }
    public String getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(String agent) { this.assignedAgent = agent; }

    @Override
    public String toString() { return name + " [" + assignedAgent + "]"; }
}

