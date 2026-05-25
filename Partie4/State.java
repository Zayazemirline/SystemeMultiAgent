package partie4;

import java.util.HashSet;
import java.util.Set;

public class State {
    private Set<String> predicates;

    public State() {
        this.predicates = new HashSet<>();
    }

    public State(Set<String> predicates) {
        this.predicates = new HashSet<>(predicates);
    }

    public void add(String pred) { predicates.add(pred); }
    public void remove(String pred) { predicates.remove(pred); }
    public boolean contains(String pred) { return predicates.contains(pred); }
    public boolean containsAll(Set<String> preds) { return predicates.containsAll(preds); }

    public Set<String> getPredicates() { return new HashSet<>(predicates); }

    @Override
    public String toString() { return predicates.toString(); }
}

